import com.github.tarao.record4s.*

import sttp.tapir.*
import sttp.tapir.SchemaType.*

import scala.compiletime.{constValue, erasedValue, summonInline}
import scala.collection.mutable.Builder

object SchemaDerivation {

  private inline def encodeSchemaFields[Types, Labels, R](
    res: Builder[SProductField[R], List[SProductField[R]]] = List.newBuilder[SProductField[R]]
  ): Builder[SProductField[R], List[SProductField[R]]] =
    inline (erasedValue[Types], erasedValue[Labels]) match
      case _: (EmptyTuple, EmptyTuple) =>
        res
      case _: (tpe *: types, label *: labels) =>
        val labelStr = constValue[label & String]
        val schema = summonInline[Schema[tpe]]
        res += SProductField(FieldName(labelStr), schema, r => Some(r.asInstanceOf[Map[String, Any]](labelStr).asInstanceOf[tpe]))
        encodeSchemaFields[types, labels, R](res)
  
  inline given schema[R <: %](using r: RecordLike[R]): Schema[R] =
    Schema(
      schemaType = SProduct(encodeSchemaFields[r.ElemTypes, r.ElemLabels, R]().result())
    )
}
