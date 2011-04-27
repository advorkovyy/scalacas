package org.scalastuff.scalacas.beans

import org.scalastuff.scalacas.{ Mapper, HasIdSupport, HasId }
import org.scalastuff.scalacas.serialization.Serializers._
import org.apache.cassandra.thrift.Column
import org.scale7.cassandra.pelops.{ Bytes, Mutator }
import java.util.Arrays
import org.scalastuff.proto.Preamble._
import org.scalastuff.proto._

/**
 * Maps JavaBeans of given class to protobuf byte array and saves it in '-protobuf' column. <p>
 *
 * @author Alexander Dvorkovyy
 */
class ProtobufMapper[A <: HasId](prefix: String)(implicit mf: Manifest[A]) extends AbstractProtobufMapper[A](prefix) with HasIdSupport[A]

abstract class AbstractProtobufMapper[A <: AnyRef](prefix: String)(implicit mf: Manifest[A]) extends Mapper[A](prefix) {
  import ProtobufMapper._

  val reader = readerOf[A]
  val writer = writerOf[A]
  val format = ProtobufFormat

  def objectToColumns(mutator: Mutator, obj: A): Seq[Column] = {
    Seq(mutator.newColumn(PROTOBUF_BYTES, Bytes.fromByteArray(writer.toByteArray(obj, format))))
  }

  def columnsToObject(superColumnName: Array[Byte], subColumns: Seq[Column]): A = {
    subColumns find (col => Arrays.equals(PROTOBUF_BYTES.getBytes.array, col.getName)) match {
      case Some(col) =>
        reader.readFrom(col.getValue, ProtobufFormat)
      case None => 
        throw new RuntimeException("Cannot deserialize object: column with name '%s' not found".format(PROTOBUF))
    }
  }
}

object ProtobufMapper {
  val PROTOBUF = "-protobuf"
  val PROTOBUF_BYTES = toBytes(PROTOBUF)
}