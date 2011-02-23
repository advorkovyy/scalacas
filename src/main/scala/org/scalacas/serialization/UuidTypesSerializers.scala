package org.scalacas.serialization

import org.scale7.cassandra.pelops.Bytes
import java.nio.ByteBuffer

trait UuidTypesSerializers {
  implicit object UUIDSerializer extends Serializer[java.util.UUID] {
    @inline
    def serialize(v: java.util.UUID) = Bytes.fromUuid(v)
    @inline
    def deserialize(buffer: Bytes) = buffer.toUuid
  }

  implicit object TimeUUIDSerializer extends Serializer[com.eaio.uuid.UUID] {
    @inline
    def serialize(v: com.eaio.uuid.UUID) = Bytes.fromTimeUuid(v)
    @inline
    def deserialize(buffer: Bytes) = buffer.toTimeUuid
  }
}