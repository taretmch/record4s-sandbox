//> using scala "3.3.3"
//> using dep "com.github.tarao::record4s:0.12.0"
//> using dep "com.github.tarao::record4s-circe:0.12.0"
//> using dep "io.circe:circe-core_3:0.14.7"
//> using dep "io.circe:circe-generic_3:0.14.7"
//> using dep "io.circe:circe-parser_3:0.14.7"

import com.github.tarao.record4s.{ %, ArrayRecord }
import com.github.tarao.record4s.circe.Codec.encoder
import com.github.tarao.record4s.circe.Codec.decoder
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.circe.parser.parse

val r = %(name = "taretmch", age = 20)

val json = r.asJson.noSpaces
println(json)

val raw = """{"name":"taretmch","age":20}"""
val r2 = parse(raw).flatMap:
  _.as[% { val name: String; val age: Int }]
println(r2)

val r3 = ArrayRecord(name = "taretmch", age = 20)
println(r3)
println(r3.asJson.noSpaces)
println(parse(raw).flatMap(_.as[ArrayRecord[(("name", String), ("age", Int))]]))
