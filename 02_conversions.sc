//> using scala "3.3.3"
//> using dep "com.github.tarao::record4s:0.12.0"

import com.github.tarao.record4s.{ RecordLike, Record, % }

class Person(val name: String, val age: Int):
  override def toString: String = s"Person(${name} (${age}))"

val person = Person("taretmch", 20)

object recordLike1 extends RecordLike[Person]:
  type FieldTypes = (("name", String), ("age", Int))
  type ElemLabels = ("name", "age")
  type ElemTypes = (String, Int)

  def iterableOf(p: Person): Iterable[(String, Any)] =
    Iterable(("name", p.name), ("age", p.age))

object scope:
  given RecordLike[Person] = recordLike1
  val r = Record.from(person)
  println(r)

