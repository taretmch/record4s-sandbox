//> using scala "3.5.0-RC1"
//> using options "-experimental"

import scala.language.experimental.namedTuples

import NamedTuple.*

val p: NamedTuple[("name", "age"), (String, Int)] = (name = "taretmch", age = 20)

println(p)
println(p.name)
println(p.age)

extension (t: NamedTuple[("name", "age"), (String, Int)])
  def greet = s"Hello, ${t.name}! You are ${t.age} years old."

println(p.greet)

val p2 = (email = "taretmch@example.com", occupation = "developer")
println(p ++ p2)

val p3 = (name = "taretmch")
// 名前が被るとエラーになる。
//
// Cannot prove that Tuple.Disjoint[(("name" : String), ("age" : String)), Tuple1[("name" : String)]] =:= (true : Boolean).
// println(p ++ p3)

p match
  case (name = n, age = a) => println(s"$n is $a years old.")

p match
  case (name = n) => println(s"hello $n")
  // case (email = e) => println(s"email: $e") - これはコンパイルエラーになる
