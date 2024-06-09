//> using scala "3.3.3"
//> using dep "com.github.tarao::record4s:0.12.0"
//> using dep "com.github.tarao::record4s-circe:0.12.0"
//> using dep "com.softwaremill.sttp.tapir:tapir-core_3:1.10.8"
//> using dep "com.softwaremill.sttp.tapir:tapir-json-circe_3:1.10.8"
//> using dep "com.softwaremill.sttp.tapir:tapir-openapi-docs_3:1.10.8"
//> using dep "com.softwaremill.sttp.apispec:openapi-circe-yaml_3:0.10.0"
//> using file SchemaDerivation.scala

import com.github.tarao.record4s.*
import com.github.tarao.record4s.circe.Codec.encoder
import com.github.tarao.record4s.circe.Codec.decoder

import cats.syntax.option.*

import sttp.tapir.*
import sttp.tapir.json.circe.jsonBody

import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.apispec.openapi.circe.yaml.*

import SchemaDerivation.schema

// ちょっと微妙なのが、OpenAPI yaml には Schema の定義が出力されず、リクエスト/レスポンスに JSON 構造だけが出力される。
// これだと API クライアントが型情報を共有できないので、Schema の定義も出力されると良さそう。
// ただ、フロントエンドとバックエンドで Schema の名前を共有して密結合にしない方が良いのかも？
// この yaml だとどんなクライアントが自動生成されるのか気になる。
type RecordRequest = % { val name: String }
type RecordResponse1 = % { val name: String; val age: Int }
type RecordResponse2 = % { val name: String; val age: Int; val work: % { val company: String; val position: String }; val isStudent: Boolean }

val getRecord = endpoint
  .get
  .in("record")
  .in(jsonBody[RecordRequest])
  .out(jsonBody[RecordResponse1].example(%(name = "taretmch", age = 20)))

val getNestedRecord = endpoint
  .get
  .in("record" / "nested")
  .out(
    jsonBody[RecordResponse2]
      .example(%(name = "taretmch", age = 20, work = %(company = "taretmch", position = "developer"), isStudent = false))
  )

// OpenAPI 仕様書を出力してみる
val endpoints = List(getRecord, getNestedRecord)
val docs = OpenAPIDocsInterpreter().toOpenAPI(endpoints, "Test Endpoints", "1.0")

println("----------")
println(docs.toYaml)

//openapi: 3.1.0
//info:
//  title: Test Endpoints
//  version: '1.0'
//paths:
//  /record:
//    get:
//      operationId: getRecord
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
//          description: 'Invalid value for: body'
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
