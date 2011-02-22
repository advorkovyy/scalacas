package org.scalacas.reflection

import org.scale7.cassandra.pelops.Bytes

/**
 * Implement this trait for your custom types and mixin to your {@link org.scalacas.Mapper}.
 * 
 * Use 'abstract override' feature to allow stacking of multiple Serializer2s. See StandardSerializer2
 * implementation for the reference.
 * 
 * @author Alexander Dvorkovyy
 *
 */
trait Serializer2 {
	def canSerialize(c:Class[_]) = false
	def serialize(obj:AnyRef):Bytes = throw new IllegalArgumentException("Cannot serialize " + obj + (if ( obj != null ) " of class " + obj.getClass))
	def deserialize(c:Class[_], buffer:Bytes):AnyRef = throw new IllegalArgumentException("Cannot deserialize instance of class " + c.getName)
}

/**
 * Supports almost all simple data types, including scala.Option<p>
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
 * BigDecimal is serialized as String.<p>
 * 
 * Arrays and collections are not supported. Redefining serialization for implemented
 * types is not supported for performance reasons.
 *  
 * @author Alexander Dvorkovyy
 *
 */
trait StandardSerializer extends Serializer2 {
	abstract override def canSerialize(c:Class[_]) = 
		(c == classOf[java.lang.String]) || 
		(c == classOf[java.lang.Boolean]) || (c == classOf[Boolean]) ||
		(c == classOf[java.util.UUID]) ||
		(c == classOf[com.eaio.uuid.UUID]) ||		
		(c == classOf[java.util.Date])|| (c == classOf[java.sql.Date])|| (c == classOf[java.sql.Timestamp])||		
		(c == classOf[java.lang.Long]) || (c == classOf[Long]) ||		
		(c == classOf[Integer]) || (c == classOf[Int]) ||
		(c == classOf[scala.math.BigDecimal]) || (c == classOf[java.math.BigDecimal]) ||
		(c == classOf[java.lang.Short]) || (c == classOf[Short]) ||		
		(c == classOf[java.lang.Byte]) || (c == classOf[Byte]) ||		
		(c == classOf[java.lang.Character]) || (c == classOf[Char]) ||		
		(c == classOf[java.lang.Double]) || (c == classOf[Double]) ||		
		(c == classOf[java.lang.Float]) || (c == classOf[Float]) ||		
		super.canSerialize(c)
	
	abstract override def serialize(obj:AnyRef):Bytes = obj match {
		case null => Bytes.NULL
		case None => Bytes.NULL
		case Some(x) => serialize(x.asInstanceOf[AnyRef])		
		case s:String => Bytes.fromUTF8(s)
		case b:java.lang.Boolean => Bytes.fromBoolean(b)
		case uuid:java.util.UUID => Bytes.fromUuid(uuid)
		case uuid:com.eaio.uuid.UUID => Bytes.fromTimeUuid(uuid)
		case timestamp:java.sql.Timestamp => Bytes.fromUTF8(timestamp.toString)
		case date:java.sql.Date => Bytes.fromUTF8(date.toString)
		case dateTime:java.util.Date => Bytes.fromLong(dateTime.getTime)
		case l:java.lang.Long => Bytes.fromLong(l)
		case i:Integer => Bytes.fromInt(i)
		case bd:java.math.BigDecimal => Bytes.fromUTF8(bd.toString)
		case bd:scala.math.BigDecimal => Bytes.fromUTF8(bd.toString)
		case sh:java.lang.Short => Bytes.fromShort(sh)
		case bt:java.lang.Byte => Bytes.fromByte(bt)
		case ch:java.lang.Character => Bytes.fromChar(ch)		
		case d:java.lang.Double => Bytes.fromDouble(d)
		case f:java.lang.Float => Bytes.fromFloat(f)
		case _ => 
			if (super.canSerialize(obj.getClass)) super.serialize(obj)
			else throw new IllegalArgumentException("Cannot serialize " + obj + " of class " + obj.getClass)
	}
	
	abstract override def deserialize(c:Class[_], buffer:Bytes):AnyRef = {
		if (buffer.isNull) null		
		
		else if (c == classOf[String]) buffer.toUTF8	
		
		else if (c == classOf[java.lang.Boolean]) buffer.toBoolean.asInstanceOf[AnyRef]
		else if (c == classOf[Boolean]) buffer.toBoolean.asInstanceOf[AnyRef]
		
		else if (c == classOf[java.util.UUID]) buffer.toUuid
		else if (c == classOf[com.eaio.uuid.UUID]) buffer.toTimeUuid
		
		else if (c == classOf[java.sql.Timestamp]) java.sql.Timestamp.valueOf(buffer.toUTF8)
		else if (c == classOf[java.sql.Date]) java.sql.Date.valueOf(buffer.toUTF8)
		else if (c == classOf[java.util.Date]) new java.util.Date(buffer.toLong)
		
		else if (c == classOf[java.lang.Long]) buffer.toLong.asInstanceOf[AnyRef]
		else if (c == classOf[Long]) buffer.toLong.asInstanceOf[AnyRef]
		
		else if (c == classOf[Integer]) buffer.toInt.asInstanceOf[AnyRef]
		else if (c == classOf[Int]) buffer.toInt.asInstanceOf[AnyRef]
		
		else if (c == classOf[java.math.BigDecimal]) new java.math.BigDecimal(buffer.toUTF8)
		else if (c == classOf[scala.math.BigDecimal]) scala.math.BigDecimal(buffer.toUTF8)
		
		else if (c == classOf[java.lang.Short]) buffer.toShort.asInstanceOf[AnyRef]
		else if (c == classOf[Short]) buffer.toShort.asInstanceOf[AnyRef]
		
		else if (c == classOf[java.lang.Byte]) buffer.toByte.asInstanceOf[AnyRef]
		else if (c == classOf[Byte]) buffer.toByte.asInstanceOf[AnyRef]
		
		else if (c == classOf[java.lang.Character]) buffer.toChar.asInstanceOf[AnyRef]		
		else if (c == classOf[Char]) buffer.toChar.asInstanceOf[AnyRef]
		
		else if (c == classOf[java.lang.Double]) buffer.toDouble.asInstanceOf[AnyRef]
		else if (c == classOf[Double]) buffer.toDouble.asInstanceOf[AnyRef]
		
		else if (c == classOf[java.lang.Float]) buffer.toFloat.asInstanceOf[AnyRef]
		else if (c == classOf[Float]) buffer.toFloat.asInstanceOf[AnyRef]
		
		else if (super.canSerialize(c)) super.deserialize(c, buffer)
		
		else throw new IllegalArgumentException("Cannot deserialize instance of class " + c.getName)
	}
}

object StandardSerializer extends StandardSerializer