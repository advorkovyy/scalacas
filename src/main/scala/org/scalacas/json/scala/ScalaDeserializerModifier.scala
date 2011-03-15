package org.scalacas.json.scala

import collection.JavaConversions._
import org.codehaus.jackson.map._
import org.codehaus.jackson.map.deser._
import org.codehaus.jackson.map.introspect.BasicBeanDescription
import org.codehaus.jackson.map.`type`._

object ScalaDeserializerModifier extends BeanDeserializerModifier {
  override def updateBuilder(config: DeserializationConfig, beanDesc: BasicBeanDescription, builder: BeanDeserializerBuilder) = {
//	println(beanDesc.getClassInfo.memberMethods map (_.getName) mkString ",")
//	println(beanDesc.getClassInfo.getAnnotated.getMethods.exists(_.getName == "s"))
	
	for {
	  setter <- beanDesc.getClassInfo.memberMethods
	  if (setter.getName endsWith "_$eq")
	  name = setter.getName dropRight 4
	  if !builder.hasProperty(name)
      if beanDesc.getClassInfo.getAnnotated.getMethods.exists(_.getName == name)
    } { 
    	val propertyType = TypeFactory.`type`(setter.getParameterType(0), beanDesc.bindingsForBeanType())
		  builder.addProperty(
				new SettableBeanProperty.MethodProperty(
						name, 
						propertyType, 
						propertyType.getTypeHandler[TypeDeserializer],
						beanDesc.getClassAnnotations,
						setter))
	}
    
    builder
  }
}