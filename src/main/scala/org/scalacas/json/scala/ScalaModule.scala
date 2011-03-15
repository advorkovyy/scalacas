package org.scalacas.json.scala

import org.codehaus.jackson._
import org.codehaus.jackson.map._

class ScalaModule extends Module {

  def getModuleName = "ScalaModule"

  def version = new Version(0, 0, 1, "SNAPSHOT")

  def setupModule(context: Module.SetupContext) {
	  context.addSerializers(ser.ScalaSerializers)
	  context.addDeserializers(deser.ScalaDeserializers)
	  
	  context.addBeanSerializerModifier(ScalaSerializerModifier)
	  context.addBeanDeserializerModifier(ScalaDeserializerModifier)
  }
}