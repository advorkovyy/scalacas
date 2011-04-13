package org.scalastuff.scalacas.beans

import org.scalastuff.scalacas.{Mapper, HasIdSupport, HasId}
import org.scalastuff.scalacas.serialization.Serializers._
import org.apache.cassandra.thrift.Column
import org.scale7.cassandra.pelops.{Bytes, Mutator}
import java.util.Arrays
import com.dyuproject.protostuff.{Schema, LinkedBuffer, ProtobufIOUtil}
import org.scalastuff.proto.MirrorSchema

/**
 * Maps JavaBeans of given class to JSON string and saves it in '-json' column. <p>
 *
 * Doesn't support Scala types like Option, BigDecimal etc. Doesn't
 * support Scala properties, use {@code @BeanProperty}<p>
 *
 * For further details see http://jackson.codehaus.org/
 *
 * @author Alexander Dvorkovyy
 * @see <a href="http://jackson.codehaus.org/">http://jackson.codehaus.org/</a>
 *
 */
class ProtobufMapper[A <: HasId](prefix:String)(implicit mf:Manifest[A]) extends AbstractProtobufMapper[A](prefix) with HasIdSupport[A]

abstract class AbstractProtobufMapper[A <: AnyRef](prefix:String)(implicit mf:Manifest[A]) extends Mapper[A](prefix) {
	import ProtobufMapper._

  val schema: Schema[A] = MirrorSchema.schemaOf[A]
  // TODO: must be thread local!!!!
  val linkedBuffer = LinkedBuffer.allocate(512)

	def objectToColumns(mutator:Mutator, obj:A):Seq[Column] = {
		Seq(mutator.newColumn(PROTOBUF_BYTES, Bytes.fromByteArray(ProtobufIOUtil.toByteArray(obj, schema, linkedBuffer))))
	}

	def columnsToObject(subColumns:Seq[Column]):A = {
		subColumns find ( col => Arrays.equals(PROTOBUF_BYTES.getBytes.array, col.getName) ) match {
			case Some(col) =>
				//println(fromBytes[String](col.getValue))
        val obj = schema.newMessage()
        ProtobufIOUtil.mergeFrom(col.getValue, obj, schema)
				obj
			case None => throw new RuntimeException("Cannot deserialize object: column with name '%s' not found".format(PROTOBUF))
		}
	}
}

object ProtobufMapper {
	val PROTOBUF = "-protobuf"
	val PROTOBUF_BYTES = toBytes(PROTOBUF)
}