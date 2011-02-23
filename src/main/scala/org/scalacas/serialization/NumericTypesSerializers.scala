package org.scalacas.serialization

import org.scale7.cassandra.pelops.Bytes
import java.nio.ByteBuffer

trait NumericTypesSerializers extends CharTypesSerializers {

  implicit object LongSerializer extends Serializer[java.lang.Long] {
    @inline
    def serialize(v: java.lang.Long) = Bytes.fromLong(v)
    @inline
    def deserialize(buffer: Bytes) = buffer.toLong(null)
  }

  implicit object PrimitiveLongSerializer extends Serializer[Long] {
    @inline
    def serialize(v: Long) = Bytes.fromLong(v)
    @inline
    def deserialize(buffer: Bytes) = buffer.toLong
  }

  implicit object IntSerializer extends Serializer[java.lang.Integer] {
    @inline
    def serialize(v: java.lang.Integer) = Bytes.fromInt(v)
    @inline
    def deserialize(buffer: Bytes) = buffer.toInt(null)
  }

  implicit object PrimitiveIntSerializer extends Serializer[Int] {
    @inline
    def serialize(v: Int) = Bytes.fromInt(v)
    @inline
    def deserialize(buffer: Bytes) = buffer.toInt
  }

  implicit object ShortSerializer extends Serializer[java.lang.Short] {
    @inline
    def serialize(v: java.lang.Short) = Bytes.fromShort(v)
    @inline
    def deserialize(buffer: Bytes) = buffer.toShort(null)
  }

  implicit object PrimitiveShortSerializer extends Serializer[Short] {
    @inline
    def serialize(v: Short) = Bytes.fromShort(v)
    @inline
    def deserialize(buffer: Bytes) = buffer.toShort
  }

  implicit object ByteSerializer extends Serializer[java.lang.Byte] {
    @inline
    def serialize(v: java.lang.Byte) = Bytes.fromByte(v)
    @inline
    def deserialize(buffer: Bytes) = buffer.toByte(null)
  }

  implicit object PrimitiveByteSerializer extends Serializer[Byte] {
    @inline
    def serialize(v: Byte) = Bytes.fromByte(v)
    @inline
    def deserialize(buffer: Bytes) = buffer.toByte
  }

  
  implicit object DoubleSerializer extends Serializer[java.lang.Double] {
    @inline
    def serialize(v: java.lang.Double) = Bytes.fromDouble(v)
    @inline
    def deserialize(buffer: Bytes) = buffer.toDouble(null)
  }

  implicit object PrimitiveDoubleSerializer extends Serializer[Double] {
    @inline
    def serialize(v: Double) = Bytes.fromDouble(v)
    @inline
    def deserialize(buffer: Bytes) = buffer.toDouble
  }

  implicit object FloatSerializer extends Serializer[java.lang.Float] {
    @inline
    def serialize(v: java.lang.Float) = Bytes.fromFloat(v)
    @inline
    def deserialize(buffer: Bytes) = buffer.toFloat(null)
  }

  implicit object PrimitiveFloatSerializer extends Serializer[Float] {
    @inline
    def serialize(v: Float) = Bytes.fromFloat(v)
    @inline
    def deserialize(buffer: Bytes) = buffer.toFloat
  }
  
  implicit object ScalaBigDecimalSerializer extends TypeConvertingSerializer[scala.math.BigDecimal, String] {
    protected def convertTo(v: scala.math.BigDecimal) = v.toString
    protected def convertFrom(s:String) = scala.math.BigDecimal(s)
  }
  
  implicit object JavaBigDecimalSerializer extends TypeConvertingSerializer[java.math.BigDecimal, String] {
    protected def convertTo(v: java.math.BigDecimal) = v.toString
    protected def convertFrom(s:String) = new java.math.BigDecimal(s)
  }
  
  implicit object ScalaBigIntSerializer extends TypeConvertingSerializer[scala.math.BigInt, String] {
    protected def convertTo(v: scala.math.BigInt) = v.toString
    protected def convertFrom(s:String) = scala.math.BigInt(s)
  }
  
  implicit object JavaBigIntegerSerializer extends TypeConvertingSerializer[java.math.BigInteger, String] {
    protected def convertTo(v: java.math.BigInteger) = v.toString
    protected def convertFrom(s:String) = new java.math.BigInteger(s)
  }
}