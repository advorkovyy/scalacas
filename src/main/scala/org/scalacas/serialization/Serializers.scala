package org.scalacas.serialization

import java.nio.ByteBuffer
import java.lang.reflect.{ Type, ParameterizedType }
import org.scale7.cassandra.pelops.Bytes

trait Serializer[A] {
  def serialize(obj: A): Bytes
  def deserialize(buffer: Bytes): A
  def deserialize(buffer: ByteBuffer): A = deserialize(Bytes.fromByteBuffer(buffer))
  def deserialize(buffer: Array[Byte]): A = deserialize(Bytes.fromByteArray(buffer))
}

/**
 * Supports serialization of simple data types, including scala.Option<p>
 * 
 * Dates are serialized as following:
 * <ul>
 *   <li>java.util.Date : long millis are serialized</li>
 *   <li>java.sql.Date : yyyy-MM-dd string serialized, see {@link java.sql.Date}</li>
 *   <li>java.sql.Timestamp : yyyy-MM-dd HH:mm.SSS string serialized,  see {@link java.sql.Timestamp}</li>
 * </ul>
 * 
 * Pay attention to this if your application servers located in different time zones,
 * because deserialization may give unexpected results otherwise.<p>
 * 
 * BigDecimal and BigInt are serialized as String.<p>
 * 
 * @author Alexander Dvorkovyy
 *
 */
object Serializers extends NumericTypesSerializers with CharTypesSerializers with UuidTypesSerializers with DateTypesSerializers {
  private[this] val serializers = Map[Class[_], Serializer[_ <: AnyRef]](
    classOf[String] -> UTF8Serializer,
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
    classOf[java.sql.Date] -> SqlDateSerializer,
    classOf[java.sql.Timestamp] -> SqlTimestampSerializer,
    classOf[scala.math.BigDecimal] -> ScalaBigDecimalSerializer,
    classOf[java.math.BigDecimal] -> JavaBigDecimalSerializer,
    classOf[scala.math.BigInt] -> ScalaBigDecimalSerializer,
    classOf[java.math.BigInteger] -> JavaBigDecimalSerializer)

  def findSerializerFor(t: Type):Option[Serializer[_ <: AnyRef]] = t match {
    case c: Class[_] => serializers.get(c)
    case pt: ParameterizedType if (pt.getRawType == classOf[Option[_]]) =>
      pt.getActualTypeArguments()(0) match {
        case c: java.lang.Class[_] => serializers.get(c).map(new OptionSerializer(_))
        case _ => None
      }
    case _ => None
  }

  def toBytes[A](v: A)(implicit s: Serializer[A]) = s.serialize(v)

  def fromBytes[A](buffer: ByteBuffer)(implicit s: Serializer[A]): A = s.deserialize(buffer)
  def fromBytes[A](buffer: Array[Byte])(implicit s: Serializer[A]): A = s.deserialize(buffer)
  def fromBytes[A](buffer: Bytes)(implicit s: Serializer[A]): A = s.deserialize(buffer)

  class OptionSerializer[A](s: Serializer[A]) extends Serializer[Option[A]] {
    def serialize(obj: Option[A]): Bytes = obj match {
      case null => Bytes.NULL
      case None => Bytes.NULL
      case Some(x) => s.serialize(x)
    }

    def deserialize(buffer: Bytes): Option[A] = {
      if (buffer == null) None
      else Option(s.deserialize(buffer))
    }
  }  
}