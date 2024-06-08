//> using scala "3.3.3"
//> using dep "com.github.tarao::record4s:0.12.0"

import com.github.tarao.record4s.{ %, Tag, select }

val r = %(name = "taretmch", age = 20)
val r2 = %(email = "taretmch@example.com", occupation = "engineer")

println(r)
println(r.name)
println(r.age)
println(r + (email = "taretmch@example.com"))
println(r ++ r2)
println(r concat r2)
println(r + (age = r.age + 1))
println(r + (age2 = r.age + 1))

extension (r: % { val name: String; val age: Int })
  def greet = s"Hello, ${r.name}! You are ${r.age} years old."

println(r.greet)

trait Person:
  extension (p: % { val name: String; val age: Int } & Tag[Person])
    def greet = s"Hello, ${p.name}! You are ${p.age} years old."

val p = r.tag[Person]
println(p.greet)

val p2 = (r ++ r2).tag[Person]
println(p2.greet)
println(p2.email)
println(p2.occupation)

val p3 = %(name = "taretmch").tag[Person]
println(p3.name)
// println(p3.greet) - compile error

p match
  case select.name.age(name, age) => println(s"$name is $age years old.")

p2 match
  case select.name.age.email.occupation(name, age, email, occupation) =>
    println(s"$name is $age years old. Email: $email, Occupation: $occupation.")

val regex = "(.*)@(.*)".r
p2 match
  case select.name.email(name, regex(user, domain)) =>
    val isSame = if (name == user) "same" else "different"
    println(s"$name is $isSame as $user in $domain.")
