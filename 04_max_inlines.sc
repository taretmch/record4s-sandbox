//> using scala "3.3.3"
//> using dep "com.github.tarao::record4s:0.12.0"
//> using dep "com.github.tarao::record4s-circe:0.12.0"
//> using dep "com.softwaremill.sttp.tapir:tapir-core_3:1.10.8"
//> using dep "com.softwaremill.sttp.tapir:tapir-json-circe_3:1.10.8"
//> using dep "com.softwaremill.sttp.tapir:tapir-openapi-docs_3:1.10.8"
//> using dep "com.softwaremill.sttp.apispec:openapi-circe-yaml_3:0.10.0"
//> using file SchemaDerivation.scala
//
import com.github.tarao.record4s.*
import com.github.tarao.record4s.circe.Codec.encoder
import com.github.tarao.record4s.circe.Codec.decoder

import cats.syntax.option.*

import sttp.tapir.*
import sttp.tapir.json.circe.jsonBody

import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.apispec.openapi.circe.yaml.*

import SchemaDerivation.schema

type R = % {
  val t1  : String
  val t2  : String
  val t3  : String
  val t4  : String
  val t5  : String
  val t6  : String
  val t7  : String
  val t8  : String
  val t9  : String
  val t10 : String
  val t11 : String
  val t12 : String
  val t13 : String
  val t14 : String
  val t15 : String
  val t16 : String
  val t17 : String
  val t18 : String
  val t19 : String
  val t20 : String
  val t21 : String
  val t22 : String
  val t23 : String
  val t24 : String
  val t25 : String
  val t26 : String
  val t27 : String
// 28 カラム以上だと max-inlines(32) でエラーになる
//  val t28 : String
//  val t29 : String
//  val t30 : String
}

val r = %(
  t1  = "t1",
  t2  = "t2",
  t3  = "t3",
  t4  = "t4",
  t5  = "t5",
  t6  = "t6",
  t7  = "t7",
  t8  = "t8",
  t9  = "t9",
  t10 = "t10",
  t11 = "t11",
  t12 = "t12",
  t13 = "t13",
  t14 = "t14",
  t15 = "t15",
  t16 = "t16",
  t17 = "t17",
  t18 = "t18",
  t19 = "t19",
  t20 = "t20",
  t21 = "t21",
  t22 = "t22",
  t23 = "t23",
  t24 = "t24",
  t25 = "t25",
  t26 = "t26",
  t27 = "t27",
//  t28 = "t28",
//  t29 = "t29",
//  t30 = "t30",
)

val getRecord = endpoint.get.in("record").out(jsonBody[R].example(r))

println(OpenAPIDocsInterpreter().toOpenAPI(List(getRecord), "Test Endpoints", "1.0").toYaml)
