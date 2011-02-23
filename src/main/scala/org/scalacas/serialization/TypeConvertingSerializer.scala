package org.scalacas.serialization

import org.scale7.cassandra.pelops.Bytes
import java.nio.ByteBuffer

/**
 * Provides type conversion A -> B before serialization and B -> A after serialization.<p>
 * 
 * Null-safe. A and B must be AnyRef.
 * 
 * @author Alexander Dvorkovyy
 *
 */
abstract class TypeConvertingSerializer[A <: AnyRef, B <: AnyRef](implicit bytesSerializer: Serializer[B]) extends Serializer[A] {
  
  def serialize(obj: A): Bytes = if (obj == null) Bytes.NULL else bytesSerializer.serialize(convertTo(obj))
  
  def deserialize(buffer: Bytes): A = {
    val b: B = bytesSerializer.deserialize(buffer)
    if (b == null) null.asInstanceOf[A]
    else convertFrom(b)
  }

  protected def convertTo(obj: A): B
  protected def convertFrom(obj: B): A
}