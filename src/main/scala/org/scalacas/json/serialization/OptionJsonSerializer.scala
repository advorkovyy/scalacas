package org.scalacas.json.serialization

import org.codehaus.jackson.`type`.JavaType
import org.codehaus.jackson.{JsonParser, JsonGenerator, JsonToken}

object OptionJsonSerializerFactory extends JsonSerializerFactory[Option[Any]] {
  def createSerializerFor(t: JavaType) = new OptionJsonSerializer(JsonSerializers.findJsonSerializerFor(t.containedType(0)))
}

class OptionJsonSerializer[A](valueSerializer: JsonSerializer[A]) extends JsonSerializer[Option[A]] {
  def serialize(obj: Option[A], generator: JsonGenerator) = obj match {
	case Some(value) => valueSerializer.serialize(value, generator)
	case None => generator.writeNull()
  }
  
  def deserialize(parser:JsonParser):Option[A] = {
	if (parser.getCurrentToken == JsonToken.VALUE_NULL) None
	else Some(valueSerializer.deserialize(parser))
  }
}