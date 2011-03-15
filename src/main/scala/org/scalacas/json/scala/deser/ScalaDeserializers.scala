package org.scalacas.json.scala.deser

import org.codehaus.jackson.`type`.JavaType
import org.codehaus.jackson._
import org.codehaus.jackson.map._
import org.codehaus.jackson.map.`type`._

object ScalaDeserializers extends Deserializers {
  def findBeanDeserializer(javaType: JavaType, config: DeserializationConfig, provider: DeserializerProvider, beanDesc: BeanDescription, property: BeanProperty) = {
    if (classOf[Option[_]].isAssignableFrom(javaType.getRawClass))
      new OptionDeserializer(provider.findValueDeserializer(config, javaType.containedType(0), property))
    else if (classOf[BigDecimal].isAssignableFrom(javaType.getRawClass))
      BigDecimalDeserializer
    else if (classOf[BigInt].isAssignableFrom(javaType.getRawClass))
      BigIntegerDeserializer
    else if (classOf[collection.Traversable[_]].isAssignableFrom(javaType.getRawClass)) {
      val companionClassName = javaType.getRawClass + "$"

      try {
        val compField = Class.forName(companionClassName).getDeclaredField("MODULE$")
        compField.setAccessible(true)
        val comp = compField.get(null).asInstanceOf[collection.generic.GenericCompanion[Traversable]]

        new TraversableDeserializer(
          provider.findValueDeserializer(config, javaType.containedType(0), property),
          javaType.containedType(0).getRawClass,
          comp)
        
      } catch {
        case e: Exception => null
      }

    } else null
  }

  def findTreeNodeDeserializer(nodeType: Class[_ <: JsonNode], config: DeserializationConfig, property: BeanProperty) = {
    null
  }

  def findMapDeserializer(mapType: MapType, config: DeserializationConfig, provider: DeserializerProvider, beanDesc: BeanDescription, property: BeanProperty, keyDeserializer: KeyDeserializer, elementTypeDeserializer: TypeDeserializer, elementDeserializer: JsonDeserializer[_]) = {
    null
  }

  def findEnumDeserializer(clazz: Class[_], config: DeserializationConfig, beanDesc: BeanDescription, property: BeanProperty) = {
    null
  }

  def findCollectionDeserializer(collectionType: CollectionType, config: DeserializationConfig, provider: DeserializerProvider, beanDesc: BeanDescription, property: BeanProperty, elementTypeDeserializer: TypeDeserializer, elementDeserializer: JsonDeserializer[_]) = {
    null
  }

  def findArrayDeserializer(arrayType: ArrayType, config: DeserializationConfig, provider: DeserializerProvider, property: BeanProperty,
                            elementTypeDeserializer: TypeDeserializer, elementDeserializer: JsonDeserializer[_]) = {
    null
  }

}
