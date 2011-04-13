package org.scalastuff.scalacas.serialization

import org.scale7.cassandra.pelops.Bytes
import java.nio.ByteBuffer

trait DateTypesSerializers extends NumericTypesSerializers with CharTypesSerializers {

  implicit object UtilDateSerializer extends TypeConvertingSerializer[java.util.Date, java.lang.Long] {
    protected def convertTo(v: java.util.Date) = v.getTime
    protected def convertFrom(l: java.lang.Long) = new java.util.Date(l.longValue)
  }

  implicit object SqlDateSerializer extends TypeConvertingSerializer[java.sql.Date, String] {
    protected def convertTo(v: java.sql.Date) = v.toString
    protected def convertFrom(s: String) = java.sql.Date.valueOf(s)
  }

  implicit object SqlTimestampSerializer extends TypeConvertingSerializer[java.sql.Timestamp, String] {
    protected def convertTo(v: java.sql.Timestamp) = v.toString
    protected def convertFrom(s: String) = java.sql.Timestamp.valueOf(s)
  }
}