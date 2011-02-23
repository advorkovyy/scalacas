package org.scalacas.serialization

import org.scale7.cassandra.pelops.Bytes
import java.nio.ByteBuffer

trait CharTypesSerializers {
  implicit object UTF8Serializer extends Serializer[String] {
    def serialize(s: String) = Bytes.fromUTF8(s)
    def deserialize(buffer: Bytes) = buffer.toUTF8
    override def deserialize(buffer: ByteBuffer) = Bytes.toUTF8(buffer)
  }


  implicit object CharSerializer extends Serializer[java.lang.Character] {
    def serialize(v: java.lang.Character) = Bytes.fromChar(v)
    def deserialize(buffer: Bytes) = buffer.toChar(null)
  }

  implicit object PrimitiveCharSerializer extends Serializer[Char] {
    def serialize(v: Char) = Bytes.fromChar(v)
    def deserialize(buffer: Bytes) = buffer.toChar
  }
}