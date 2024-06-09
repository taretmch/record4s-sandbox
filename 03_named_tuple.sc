//> using scala "3.5.0-RC1"
//> using options "-experimental"

import scala.language.experimental.namedTuples

import NamedTuple.*

val p: NamedTuple[("name", "age"), (String, Int)] = (name = "taretmch", age = 20)

println(p)
println(p.name)
println(p.age)

