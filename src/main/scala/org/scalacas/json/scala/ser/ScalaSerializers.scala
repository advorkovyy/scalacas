package org.scalacas.json.scala.ser

import org.codehaus.jackson.`type`.JavaType
import org.codehaus.jackson.map._

object ScalaSerializers extends Serializers {
  def findSerializer(config: SerializationConfig, javaType: JavaType, beanDesc: BeanDescription, property: BeanProperty) = {
	  // Scala BigDecimal and BigInt are covered by StdSerializers.NumberSerializer 
	  if (classOf[Option[_]].isAssignableFrom(javaType.getRawClass)) new OptionSerializer(property)
	  else null
  }
}
