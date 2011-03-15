package org.scalacas.json.serialization
import org.codehaus.jackson.{JsonGenerator, JsonParser}

class CharJsonSerializer extends JsonSerializer[java.lang.Character] {
  def serialize(v: java.lang.Character, generator: JsonGenerator) { generator.writeString(v.toString) }
  def deserialize(parser:JsonParser) = parser.getText.head
}

class StringJsonSerializer extends JsonSerializer[String] {
  def serialize(v: String, generator: JsonGenerator) { generator.writeString(v) }
  def deserialize(parser:JsonParser) = parser.getText
}

class BooleanJsonSerializer extends JsonSerializer[java.lang.Boolean] {
  def serialize(v: java.lang.Boolean, generator: JsonGenerator) { generator.writeBoolean(v.booleanValue) }
  def deserialize(parser:JsonParser) = parser.getBooleanValue
}