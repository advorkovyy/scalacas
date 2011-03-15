package org.scalacas.json.scala.ser

import org.codehaus.jackson._
import org.codehaus.jackson.map._

class OptionSerializer(property: BeanProperty) extends JsonSerializer[Option[Any]] {
  override def serialize(value: Option[Any], jgen: JsonGenerator, provider: SerializerProvider) {
	  value match {
	 	  case None => provider.defaultSerializeNull(jgen)
	 	  case Some(null) => provider.defaultSerializeNull(jgen) // just in case, should be None actually
	 	  case Some(x:AnyRef) => 
	 	    val ser = provider.findValueSerializer(x.getClass, property)
	 	   	ser.serialize(x, jgen, provider)
	  }
  }
}