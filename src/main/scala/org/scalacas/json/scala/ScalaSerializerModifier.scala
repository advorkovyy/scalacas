package org.scalacas.json.scala

import org.codehaus.jackson.map.introspect.BasicBeanDescription
import scala.collection.JavaConversions._
import org.codehaus.jackson.map._
import org.codehaus.jackson.map.ser._

object ScalaSerializerModifier extends BeanSerializerModifier {
  override def changeProperties(config: SerializationConfig,
    beanDesc: BasicBeanDescription, beanProperties: java.util.List[BeanPropertyWriter]) = {
	  //println(beanDesc.getClassInfo.memberMethods map (_.getName) mkString ",")
    for {
      getter <- beanDesc.getClassInfo.memberMethods
      name = getter.getName
      if !beanProperties.exists(_.getName == name) // check no JavaBean property
      if beanDesc.getClassInfo.getAnnotated.getMethods.exists(_.getName == name + "_$eq") // check not read-only      
    } beanProperties add new BeanPropertyWriter(
      getter /* member */ ,
      beanDesc.getClassAnnotations /* contextAnnotations */ ,
      name /* name */ ,
      getter.getType(beanDesc.bindingsForBeanType) /* declaredType */ ,
      null /* ser */ , null /* typeSer */ , 
      getter.getType(beanDesc.bindingsForBeanType) /* serType */ ,
      getter.getAnnotated /* m */ ,
      null /* f */ , false /* suppressNulls */ , null /* suppressableValue */ )

    beanProperties
  }

}