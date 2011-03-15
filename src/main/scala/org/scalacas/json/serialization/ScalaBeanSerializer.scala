package org.scalacas.json.serialization
import scala.collection.immutable.TreeMap
import scala.collection.mutable.HashMap
import org.codehaus.jackson.`type`.JavaType
import org.codehaus.jackson.map.`type`.TypeFactory
import java.lang.reflect.{ Method, Type }
import org.codehaus.jackson.{ SerializableString, JsonToken, JsonParser, JsonGenerator }
import org.codehaus.jackson.io.SerializedString
import org.scalacas.reflection.ScalaReflection._

class ScalaBeanSerializer(c: Class[_]) extends JsonSerializer[AnyRef] {

  private val properties = HashMap[String, ScalaProperty]( 
    (for {
      p <- c.properties
      setter <- p.setter
    } yield (p.getter.getName, new ScalaProperty(p.getter, setter))) :_* )
  

  private val ctor = c.getConstructor()
  ctor.setAccessible(true)

  def serialize(obj: AnyRef, generator: JsonGenerator) {
    generator.writeStartObject()
    for (property <- properties.values) {
      generator.writeFieldName(property.name)
      property.valueSerializer serialize (property.get(obj), generator)
    }
    generator.writeEndObject()
  }
  
  private val fun = { p: ScalaProperty => p.name.getValue }

  def deserialize(parser: JsonParser): AnyRef = {
    require(parser.getCurrentToken() == JsonToken.START_OBJECT)
    val res = ctor.newInstance().asInstanceOf[AnyRef]

    while (parser.nextToken() != JsonToken.END_OBJECT) {
      val propertyName = parser.getCurrentName
      parser.nextToken

      properties.get(propertyName) match {
        case Some(property) =>
          property.set(res, property.valueSerializer.deserialize(parser))
        case _ =>
          parser.skipChildren
      }
    }

    res
  }

  private class ScalaProperty(getter: Method, setter: Method) {
    implicit def toJavaType(t: Type): JavaType = TypeFactory.`type`(t)

    val name: SerializableString = new SerializedString(getter.getName)
    val valueSerializer: JsonSerializer[AnyRef] = JsonSerializers.findJsonSerializerFor(getter.getGenericReturnType)
    
    def get(obj:AnyRef) = getter.invoke(obj)
    def set(obj:AnyRef, v:AnyRef) { setter.invoke(obj, v) }
  }

}