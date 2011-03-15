package org.scalacas.json.scala.deser

import org.codehaus.jackson._
import org.codehaus.jackson.map._

class OptionDeserializer[T](valueDeserializer: JsonDeserializer[T]) extends JsonDeserializer[Option[T]] {
  def deserialize(jp: JsonParser, ctxt: DeserializationContext): Option[T] = {
    valueDeserializer.deserialize(jp, ctxt) match {
      case null => None
      case x@_ => Some(x)
    }
  }

  override def getNullValue() = None
}