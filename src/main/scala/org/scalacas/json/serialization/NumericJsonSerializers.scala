package org.scalacas.json.serialization

import org.codehaus.jackson.{JsonParser, JsonGenerator}

class ByteJsonSerializer extends JsonSerializer[java.lang.Byte] {
  def serialize(v: java.lang.Byte, generator: JsonGenerator) { generator.writeNumber(v.intValue) }
  def deserialize(parser:JsonParser) = parser.getIntValue.asInstanceOf[java.lang.Byte]
}

class ShortJsonSerializer extends JsonSerializer[java.lang.Short] {
  def serialize(v: java.lang.Short, generator: JsonGenerator) { generator.writeNumber(v.intValue) }
  def deserialize(parser:JsonParser) = parser.getIntValue.asInstanceOf[java.lang.Short]
}

class IntegerJsonSerializer extends JsonSerializer[java.lang.Integer] {
  def serialize(v: java.lang.Integer, generator: JsonGenerator) { generator.writeNumber(v.intValue) }
  def deserialize(parser:JsonParser) = parser.getIntValue
}

class LongJsonSerializer extends JsonSerializer[java.lang.Long] {
  def serialize(v: java.lang.Long, generator: JsonGenerator) { generator.writeNumber(v.longValue) }
  def deserialize(parser:JsonParser) = parser.getLongValue
}

class FloatJsonSerializer extends JsonSerializer[java.lang.Float] {
  def serialize(v: java.lang.Float, generator: JsonGenerator) { generator.writeNumber(v.floatValue) }
  def deserialize(parser:JsonParser) = parser.getFloatValue
}

class DoubleJsonSerializer extends JsonSerializer[java.lang.Double] {
  def serialize(v: java.lang.Double, generator: JsonGenerator) { generator.writeNumber(v.doubleValue) }
  def deserialize(parser:JsonParser) = parser.getDoubleValue
}

class ScalaBigDecimalJsonSerializer extends JsonSerializer[BigDecimal] {
  def serialize(v: BigDecimal, generator: JsonGenerator) { generator.writeNumber(v.bigDecimal) }
  def deserialize(parser:JsonParser) = BigDecimal(parser.getDecimalValue)
}

class JavaBigDecimalJsonSerializer extends JsonSerializer[java.math.BigDecimal] {
  def serialize(v: java.math.BigDecimal, generator: JsonGenerator) { generator.writeNumber(v) }
  def deserialize(parser:JsonParser) = parser.getDecimalValue
}

class ScalaBigIntJsonSerializer extends JsonSerializer[BigInt] {
  def serialize(v: BigInt, generator: JsonGenerator) { generator.writeNumber(v.bigInteger) }
  def deserialize(parser:JsonParser) = BigInt(parser.getText)
}

class JavaBigIntegerJsonSerializer extends JsonSerializer[java.math.BigInteger] {
  def serialize(v: java.math.BigInteger, generator: JsonGenerator) { generator.writeNumber(v) }
  def deserialize(parser:JsonParser) = new java.math.BigInteger(parser.getText)
}