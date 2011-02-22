package org.scalacas.serialization

import org.scale7.cassandra.pelops.Bytes
import java.nio.ByteBuffer

trait CharTypesSerializers {
  implicit object UTF8Serializer extends Serializer[String] {
    @inline
    def serialize(s: String) = Bytes.fromUTF8(s)
    @inline
    def deserialize(buffer: ByteBuffer) = Bytes.toUTF8(buffer)
  }


  implicit object CharSerializer extends Serializer[java.lang.Character] {
    @inline
    def serialize(v: java.lang.Character) = Bytes.fromChar(v)
    @inline
    def deserialize(buffer: ByteBuffer) = Bytes.fromByteBuffer(buffer).toChar(null)
  }

  implicit object PrimitiveCharSerializer extends Serializer[Char] {
    @inline
    def serialize(v: Char) = Bytes.fromChar(v)
    @inline
    def deserialize(buffer: ByteBuffer) = Bytes.fromByteBuffer(buffer).toChar
  }
}