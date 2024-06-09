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
import sttp.tapir.SchemaType.{SProduct, SString, SInteger}
import sttp.tapir.json.circe.jsonBody

import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.apispec.openapi.circe.yaml.*

type Person = % { val name: String; val age: Int }

// 個別の Schema を定義してみる
given Schema[Person] = Schema(
  schemaType = SProduct(
    List(
      SchemaType.SProductField(FieldName("name"), Schema(SString()), _.name.some),
      SchemaType.SProductField(FieldName("age"), Schema(SInteger()), _.age.some)
    )
  )
)

val getRecord = endpoint
  .get
  .in("record")
  .out(
    jsonBody[% { val name: String; val age: Int }]
      .example(%(name = "taretmch", age = 20))
  )

println(getRecord.show)
println(summon[Schema[Person]].show)

// OpenAPI 仕様書を出力してみる
val endpoints = List(getRecord)
val docs = OpenAPIDocsInterpreter().toOpenAPI(endpoints, "My Bookshop", "1.0")

println("----------")
println(docs.toYaml)
