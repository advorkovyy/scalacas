package org.scalacas.json.scala.deser

import org.codehaus.jackson._
import org.codehaus.jackson.map._
import org.codehaus.jackson.map.deser._

object BigDecimalDeserializer extends JsonDeserializer[BigDecimal] {
  val deserializer = new StdDeserializer.BigDecimalDeserializer
  def deserialize(jp: JsonParser, ctxt: DeserializationContext): BigDecimal = {
    val bd = deserializer.deserialize(jp, ctxt)
    if (bd == null) null
    else new BigDecimal(bd)
  }

}

object BigIntegerDeserializer extends JsonDeserializer[BigInt] {
  val deserializer = new StdDeserializer.BigIntegerDeserializer
  def deserialize(jp: JsonParser, ctxt: DeserializationContext): BigInt = {
    val bi = deserializer.deserialize(jp, ctxt)
    if (bi == null) null
    else new BigInt(bi)
  }

}