package org.scalacas.json
import org.scalacas.json.serialization.JsonSerializers

import org.codehaus.jackson.util.ByteArrayBuilder
import org.codehaus.jackson.JsonEncoding
import org.codehaus.jackson.JsonFactory
import org.codehaus.jackson.map.ObjectMapper
import collection.mutable
import java.util.Arrays

import org.scalacas.{ Mapper, HasId, HasIdSupport }
import org.scale7.cassandra.pelops._
import org.apache.cassandra.thrift._
import org.scalacas.serialization.Serializers._

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
class JsonMapper[A <: HasId](prefix: String)(implicit mf:Manifest[A]) extends 
	AbstractJsonMapper[A](prefix) with HasIdSupport[A]

abstract class AbstractJsonMapper[A <: AnyRef](prefix: String)(implicit mf:Manifest[A]) extends Mapper[A](prefix) {
  import JsonMapper._
  
  private[this] val serializer = JsonSerializers.findJsonSerializerFor[A]

  def objectToColumns(mutator: Mutator, obj: A): Seq[Column] = {
	val buffer = new ByteArrayBuilder
    val generator = factory.createJsonGenerator(buffer, encoding)
    serializer.serialize(obj, generator)
    generator.close()
    Seq(mutator.newColumn(JSON_BYTES, Bytes.fromByteArray(buffer.toByteArray)))
  }

  def columnsToObject(subColumns: Seq[Column]): A = {
    subColumns find (col => Arrays.equals(JSON_BYTES.getBytes.array, col.getName)) match {
      case Some(col) =>
      	val parser = factory.createJsonParser(col.getValue)
      	parser.nextToken
      	serializer.deserialize(parser)      	
      case None => throw new RuntimeException("Cannot deserialize object: column with name '%s' not found".format(JSON))
    }
  }
}

object JsonMapper {
  val JSON = "-json"
  val JSON_BYTES = toBytes(JSON)

  val factory = new JsonFactory()
  val encoding = JsonEncoding.UTF8
}