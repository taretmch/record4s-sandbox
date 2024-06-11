//> using scala "3.3.3"
//> using dep "com.github.tarao::record4s:0.12.0"
//> using dep "com.github.tarao::record4s-circe:0.12.0"
//> using dep "com.softwaremill.sttp.tapir:tapir-core_3:1.10.8"
//> using dep "com.softwaremill.sttp.tapir:tapir-json-circe_3:1.10.8"
//> using dep "com.softwaremill.sttp.tapir:tapir-openapi-docs_3:1.10.8"
//> using dep "com.softwaremill.sttp.apispec:openapi-circe-yaml_3:0.10.0"
//> using file SchemaDerivation.scala

import io.circe.generic.auto._

import sttp.tapir.*
import sttp.tapir.json.circe._
import sttp.tapir.generic.auto._

case class JsValuePutRecord(name: String)
case class JsValueRecord(name: String, age: Int)

// PUT /record : id と name を受け取って、name と age を返すエンドポイント
val putRecord1 = endpoint
  .put
  .in("v1" / "record" / path[Long]("id"))
  .in(jsonBody[JsValuePutRecord])
  .out(jsonBody[JsValueRecord].example(JsValueRecord(name = "taretmch", age = 20)))

import com.github.tarao.record4s.*
import com.github.tarao.record4s.circe.Codec.encoder
import com.github.tarao.record4s.circe.Codec.decoder

import sttp.tapir.*
import sttp.tapir.json.circe._

import SchemaDerivation.schema

val putRecord2 = endpoint
  .put
  .in("v2" / "record" / path[Long]("id"))
  .in(
    jsonBody[% { val name: String }]
  )
  .out(
    jsonBody[% { val name: String; val age: Int }]
      .example(%(name = "taretmch", age = 20))
  )
