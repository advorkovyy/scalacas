package org.scalacas.json.serialization

import org.codehaus.jackson.`type`.JavaType
import org.codehaus.jackson.map.`type`.TypeFactory
import java.lang.reflect.{Method, Type}
import org.codehaus.jackson.{SerializableString, JsonToken, JsonParser, JsonGenerator}
import org.codehaus.jackson.io.SerializedString
import org.scalacas.reflection.ScalaReflection._

class ScalaBeanSerializer(c: Class[_]) extends JsonSerializer[AnyRef] {

  private val properties: Iterable[ScalaProperty] = 
	  for {
		p <- c.properties
	 	setter <- p.setter	 	   
	  }
	  yield new ScalaProperty(p.getter, setter)
	   
  private val ctor = c.getConstructor()
  ctor.setAccessible(true)

  implicit def toJavaType(t:Type):JavaType = TypeFactory.`type`(t)

  def serialize(obj: AnyRef, generator: JsonGenerator) {
    generator.writeStartObject()
    for (property <- properties) {
      generator.writeFieldName(property.name)
      property.valueSerializer serialize (obj #: property value, generator)
    }
    generator.writeEndObject()
  }

  def deserialize(parser:JsonParser):AnyRef = {
    require(parser.getCurrentToken() == JsonToken.START_OBJECT)
    val res = ctor.newInstance().asInstanceOf[AnyRef]
    while (parser.nextToken() != JsonToken.END_OBJECT) {
      val propertyName = parser.getCurrentName
      parser.nextToken

      properties find (_.name.getValue == propertyName) match {
        case Some(property) =>
          res #: property value = property.valueSerializer.deserialize(parser)
        case _ =>
          parser.skipChildren
      }
    }

    res
  }

  private class ScalaProperty(getter: Method, setter: Method) {

    val name: SerializableString = new SerializedString(getter.getName)
    val valueSerializer: JsonSerializer[AnyRef] = JsonSerializers.findJsonSerializerFor(getter.getGenericReturnType)

    def #: (obj:AnyRef) = new {
      def value = getter.invoke(obj)
      def value_= (value:AnyRef) { setter.invoke(obj, value) }
    }
  }

}