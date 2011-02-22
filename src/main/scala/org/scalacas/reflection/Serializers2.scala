package org.scalacas.reflection

import org.scale7.cassandra.pelops.Bytes

trait Serializer2[A <: AnyRef] {
	def serialize(obj:A):Bytes
	def deserialize(c:Class[_], buffer:Bytes):A
}

trait StandardSerializers2 extends Serializer {
	val serializers = Map[Class[_], Serializer2[_]](
		classOf[String] -> StringSerializer,
		classOf[Long] -> LongSerializer,
		classOf[java.lang.Long] -> LongSerializer,
		classOf[Int] -> IntSerializer,
		classOf[java.lang.Integer] -> IntSerializer,
		classOf[Short] -> ShortSerializer,
		classOf[java.lang.Short] -> ShortSerializer,
		classOf[Byte] -> ByteSerializer,
		classOf[java.lang.Byte] -> ByteSerializer,
		classOf[Char] -> CharSerializer,
		classOf[java.lang.Character] -> CharSerializer,
		classOf[Double] -> DoubleSerializer,
		classOf[java.lang.Double] -> DoubleSerializer,
		classOf[Float] -> FloatSerializer,
		classOf[java.lang.Float] -> FloatSerializer,
		classOf[java.util.UUID] -> UUIDSerializer,
		classOf[com.eaio.uuid.UUID] -> TimeUUIDSerializer,
		classOf[java.util.Date] -> UtilDateSerializer,
		classOf[BigDecimal] -> BigDecimalSerializer
			)
			
	override def canSerialize(c:Class[_]) = serializers.contains(c)
	
	override def serialize(obj:AnyRef):Bytes = obj match {
		case null => Bytes.NULL
		case None => Bytes.NULL
		case Some(x) => serialize(x.asInstanceOf[AnyRef])
		case _ => serializers(obj.getClass).asInstanceOf[Serializer2[AnyRef]].serialize(obj)
	}
	
	override def deserialize(c:Class[_], buffer:Bytes):AnyRef = {
		if (buffer.isNull) null
		else serializers(c).asInstanceOf[Serializer2[AnyRef]].deserialize(c, buffer)
	}
}
object StandardSerializer2 extends StandardSerializers2

object StringSerializer extends Serializer2[String] {
	def serialize(s:String) = Bytes.fromUTF8(s)
	def deserialize(c:Class[_], buffer:Bytes) = buffer.toUTF8 
}

object LongSerializer extends Serializer2[java.lang.Long] {
	def serialize(v:java.lang.Long) = Bytes.fromLong(v)
	def deserialize(c:Class[_], buffer:Bytes) = buffer.toLong(null) 
}

object IntSerializer extends Serializer2[java.lang.Integer] {
	def serialize(v:java.lang.Integer) = Bytes.fromInt(v)
	def deserialize(c:Class[_], buffer:Bytes) = buffer.toInt(null) 
}

object ShortSerializer extends Serializer2[java.lang.Short] {
	def serialize(v:java.lang.Short) = Bytes.fromShort(v)
	def deserialize(c:Class[_], buffer:Bytes) = buffer.toShort(null) 
}

object ByteSerializer extends Serializer2[java.lang.Byte] {
	def serialize(v:java.lang.Byte) = Bytes.fromByte(v)
	def deserialize(c:Class[_], buffer:Bytes) = buffer.toByte(null) 
}

object CharSerializer extends Serializer2[java.lang.Character] {
	def serialize(v:java.lang.Character) = Bytes.fromChar(v)
	def deserialize(c:Class[_], buffer:Bytes) = buffer.toChar(null) 
}

object DoubleSerializer extends Serializer2[java.lang.Double] {
	def serialize(v:java.lang.Double) = Bytes.fromDouble(v)
	def deserialize(c:Class[_], buffer:Bytes) = buffer.toDouble(null) 
}

object FloatSerializer extends Serializer2[java.lang.Float] {
	def serialize(v:java.lang.Float) = Bytes.fromFloat(v)
	def deserialize(c:Class[_], buffer:Bytes) = buffer.toFloat(null) 
}

object UUIDSerializer extends Serializer2[java.util.UUID] {
	def serialize(v:java.util.UUID) = Bytes.fromUuid(v)
	def deserialize(c:Class[_], buffer:Bytes) = buffer.toUuid 
}

object TimeUUIDSerializer extends Serializer2[com.eaio.uuid.UUID] {
	def serialize(v:com.eaio.uuid.UUID) = Bytes.fromTimeUuid(v)
	def deserialize(c:Class[_], buffer:Bytes) = buffer.toTimeUuid
}

object BigDecimalSerializer extends Serializer2[BigDecimal] {
	def serialize(v:BigDecimal) = Bytes.fromUTF8(v.toString)
	def deserialize(c:Class[_], buffer:Bytes) = BigDecimal(buffer.toUTF8) 
}

object UtilDateSerializer extends Serializer2[java.util.Date] {
	def serialize(v:java.util.Date) = Bytes.fromLong(v.getTime)
	def deserialize(c:Class[_], buffer:Bytes) = new java.util.Date(buffer.toLong) 
}