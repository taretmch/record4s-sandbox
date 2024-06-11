//> using scala "3.3.3"
//> using dep "com.github.tarao::record4s:0.12.0"
//> using dep "com.github.tarao::record4s-circe:0.12.0"
//> using dep "com.softwaremill.sttp.tapir:tapir-core_3:1.10.8"
//> using dep "com.softwaremill.sttp.tapir:tapir-json-circe_3:1.10.8"
//> using dep "com.softwaremill.sttp.tapir:tapir-openapi-docs_3:1.10.8"
//> using dep "com.softwaremill.sttp.apispec:openapi-circe-yaml_3:0.10.0"

import com.github.tarao.record4s.*
import com.github.tarao.record4s.circe.Codec.encoder
import com.github.tarao.record4s.circe.Codec.decoder

import cats.syntax.option.*

import sttp.tapir.*
import sttp.tapir.SchemaType.*
import sttp.tapir.json.circe.jsonBody

import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.apispec.openapi.circe.yaml.*

import scala.compiletime.{constValue, erasedValue, summonInline}
import scala.collection.mutable.Builder

// % に対して Schema を実装してみる
inline def encodeSchemaFields[Types, Labels, R](
  res: Builder[SProductField[R], List[SProductField[R]]] = List.newBuilder[SProductField[R]]
): Builder[SProductField[R], List[SProductField[R]]] =
  inline (erasedValue[Types], erasedValue[Labels]) match {
    case _: (EmptyTuple, EmptyTuple) =>
      res

    case _: (tpe *: types, label *: labels) =>
      val labelStr = constValue[label & String]
      val schema = summonInline[Schema[tpe]]
      res += SProductField(FieldName(labelStr), schema, r => Some(r.asInstanceOf[Map[String, Any]](labelStr).asInstanceOf[tpe]))
      encodeSchemaFields[types, labels, R](res)
  }

inline given schema[R <: %](using r: RecordLike[R]): Schema[R] = {
  type Types = r.ElemTypes
  type Labels = r.ElemLabels

  Schema(
    schemaType = SProduct(encodeSchemaFields[Types, Labels, R]().result())
  )
}

val getRecord = endpoint
  .get
  .in("record" / path[Long]("id"))
  .out(
    jsonBody[% { val name: String; val age: Int }]
      .example(%(name = "taretmch", age = 20))
  )

val getNestedRecord = endpoint
  .get
  .in("record" / "nested")
  .out(
    jsonBody[% { val name: String; val age: Int; val work: % { val company: String; val position: String }; val isStudent: Boolean }]
      .example(%(name = "taretmch", age = 20, work = %(company = "taretmch", position = "developer"), isStudent = false))
  )

val putRecord = endpoint
  .put
  .in("record" / path[Long]("id"))
  .in(
    jsonBody[% { val name: String }]
  )
  .out(
    jsonBody[% { val name: String; val age: Int }]
      .example(%(name = "taretmch", age = 20))
  )

// OpenAPI 仕様書を出力してみる
val endpoints = List(getRecord, getNestedRecord, putRecord)
val docs = OpenAPIDocsInterpreter().toOpenAPI(endpoints, "Test Endpoints", "1.0")

println("----------")
println(docs.toYaml)

//openapi: 3.1.0
//info:
//  title: Test Endpoints
//  version: '1.0'
//paths:
//  /record/{id}:
//    get:
//      operationId: getRecordId
//      parameters:
//      - name: id
//        in: path
//        required: true
//        schema:
//          type: integer
//          format: int64
//      responses:
//        '200':
//          description: ''
//          content:
//            application/json:
//              schema:
//                type: object
//                required:
//                - name
//                - age
//                properties:
//                  name:
//                    type: string
//                  age:
//                    type: integer
//                    format: int32
//              example:
//                name: taretmch
//                age: 20
//        '400':
//          description: 'Invalid value for: path parameter id'
//          content:
//            text/plain:
//              schema:
//                type: string
//    put:
//      operationId: putRecordId
//      parameters:
//      - name: id
//        in: path
//        required: true
//        schema:
//          type: integer
//          format: int64
//      requestBody:
//        content:
//          application/json:
//            schema:
//              type: object
//              required:
//              - name
//              properties:
//                name:
//                  type: string
//        required: true
//      responses:
//        '200':
//          description: ''
//          content:
//            application/json:
//              schema:
//                type: object
//                required:
//                - name
//                - age
//                properties:
//                  name:
//                    type: string
//                  age:
//                    type: integer
//                    format: int32
//              example:
//                name: taretmch
//                age: 20
//        '400':
//          description: 'Invalid value for: path parameter id, Invalid value for: body'
//          content:
//            text/plain:
//              schema:
//                type: string
//  /record/nested:
//    get:
//      operationId: getRecordNested
//      responses:
//        '200':
//          description: ''
//          content:
//            application/json:
//              schema:
//                type: object
//                required:
//                - name
//                - age
//                - work
//                - isStudent
//                properties:
//                  name:
//                    type: string
//                  age:
//                    type: integer
//                    format: int32
//                  work:
//                    type: object
//                    required:
//                    - company
//                    - position
//                    properties:
//                      company:
//                        type: string
//                      position:
//                        type: string
//                  isStudent:
//                    type: boolean
//              example:
//                name: taretmch
//                age: 20
//                work:
//                  company: taretmch
//                  position: developer
//                isStudent: false
