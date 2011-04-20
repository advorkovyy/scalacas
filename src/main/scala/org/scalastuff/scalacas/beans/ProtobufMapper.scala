package org.scalastuff.scalacas.beans

import org.scalastuff.scalacas.{ Mapper, HasIdSupport, HasId }
import org.scalastuff.scalacas.serialization.Serializers._
import org.apache.cassandra.thrift.Column
import org.scale7.cassandra.pelops.{ Bytes, Mutator }
import java.util.Arrays
import com.dyuproject.protostuff.{ Schema, LinkedBuffer, ProtobufIOUtil }
import org.scalastuff.scalabeans.Preamble._
import org.scalastuff.proto.value.BeanValueHandler
import com.dyuproject.protostuff.ProtobufOutput
import com.dyuproject.protostuff.ByteArrayInput

/**
 * Maps JavaBeans of given class to protobuf byte array and saves it in '-protobuf' column. <p>
 *
 * @author Alexander Dvorkovyy
 */
class ProtobufMapper[A <: HasId](prefix: String)(implicit mf: Manifest[A]) extends AbstractProtobufMapper[A](prefix) with HasIdSupport[A]

abstract class AbstractProtobufMapper[A <: AnyRef](prefix: String)(implicit mf: Manifest[A]) extends Mapper[A](prefix) {
  import ProtobufMapper._

  val beanValueHandler = BeanValueHandler(scalaTypeOf[A])
  // TODO: must be thread local!!!!
  val linkedBuffer = LinkedBuffer.allocate(512)

  def objectToColumns(mutator: Mutator, obj: A): Seq[Column] = {
    linkedBuffer.clear()
    val output = new ProtobufOutput(linkedBuffer)
    beanValueHandler.schemaWriteTo(output, obj)
    Seq(mutator.newColumn(PROTOBUF_BYTES, Bytes.fromByteArray(output.toByteArray)))
  }

  def columnsToObject(superColumnName: Array[Byte], subColumns: Seq[Column]): A = {
    subColumns find (col => Arrays.equals(PROTOBUF_BYTES.getBytes.array, col.getName)) match {
      case Some(col) =>
        //println(fromBytes[String](col.getValue))
      	// TODO: exception if cannot create instance without constructor parameters. cannot we use immutable schema here?
        val input = new ByteArrayInput(col.getValue, false)
        beanValueHandler.schemaReadFrom(input).asInstanceOf[A]
      case None => throw new RuntimeException("Cannot deserialize object: column with name '%s' not found".format(PROTOBUF))
    }
  }
}

object ProtobufMapper {
  val PROTOBUF = "-protobuf"
  val PROTOBUF_BYTES = toBytes(PROTOBUF)
}