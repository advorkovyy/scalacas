package org.scalacas.json

import org.codehaus.jackson.map.ObjectMapper

import collection.mutable
import java.util.Arrays

import org.scalacas.{Mapper, HasId, HasIdSupport}
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
class JsonMapper[A <: HasId](prefix:String, clazz:Class[A]) extends AbstractJsonMapper[A](prefix, clazz) with HasIdSupport[A]

abstract class AbstractJsonMapper[A <: AnyRef](prefix:String, clazz:Class[A]) extends Mapper[A](prefix) {
	import JsonMapper._
	
	def objectToColumns(mutator:Mutator, obj:A):Seq[Column] = {
		Seq(mutator.newColumn(JSON_BYTES, Bytes.fromByteArray(mapper.writeValueAsBytes(obj))))
	}
	
	def columnsToObject(subColumns:Seq[Column]):A = {
		subColumns find ( col => Arrays.equals(JSON_BYTES.getBytes.array, col.getName) ) match {
			case Some(col) =>
				//println(fromBytes[String](col.getValue))
				mapper.readValue(col.getValue, 0, col.getValue.size, clazz)
			case None => throw new RuntimeException("Cannot deserialize object: column with name '%s' not found".format(JSON))
		}
	}
}

object JsonMapper {
	val mapper = new ObjectMapper()
	mapper.registerModule(new scala.ScalaModule())
	
	val JSON = "-json"
	val JSON_BYTES = toBytes(JSON)
}