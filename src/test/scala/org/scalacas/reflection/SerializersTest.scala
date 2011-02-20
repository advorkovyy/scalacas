package org.scalacas.reflection

import org.specs.SpecificationWithJUnit
import org.junit.runner.RunWith
import org.specs.runner.JUnitSuiteRunner

@RunWith(classOf[JUnitSuiteRunner])
class SerializersTest extends SpecificationWithJUnit {
	"test isDefinedFor" in {
		StandardSerializer.canSerialize(classOf[String]) must be(true)
		StandardSerializer.canSerialize(classOf[Long]) must be(true)
		StandardSerializer.canSerialize(classOf[java.lang.Long]) must be(true)
		StandardSerializer.canSerialize(classOf[BigDecimal]) must be(true)
		StandardSerializer.canSerialize(this.getClass) must be(false)
	}
	
	"test null" in {
		val buffer = StandardSerializer.serialize(null)
		buffer.isNull must be(true)
		StandardSerializer.deserialize(classOf[Long], buffer) must be(null)
	}
	
	"test standard StandardSerializer" in {
		checkSerializer(classOf[String], "whatever")
		checkSerializer(classOf[Long], Long.box(2))
		checkSerializer(classOf[java.lang.Long], Long.box(2))
		checkSerializer(classOf[Int], Int.box(2))
		checkSerializer(classOf[java.lang.Integer], Int.box(2))
		checkSerializer(classOf[BigDecimal], BigDecimal(4.56))
	}
	
	
	private def checkSerializer(c:Class[_], obj:AnyRef) {
		val buffer = StandardSerializer.serialize(obj)
		StandardSerializer.deserialize(c, buffer) must be equalTo(obj)
	}

}