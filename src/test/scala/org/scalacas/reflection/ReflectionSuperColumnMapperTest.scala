package org.scalacas.reflection

import ScalaReflection._

import org.specs.SpecificationWithJUnit
import org.junit.runner.RunWith
import org.specs.runner.JUnitSuiteRunner

@RunWith(classOf[JUnitSuiteRunner])
class ReflectionSuperColumnMapperTest extends SpecificationWithJUnit {

	class ReflectionTest {
		val i:Int = 1
		val i2:Integer = 2
	}
	
	"" in {
		val rt = new ReflectionTest
	
		printType(rt.getClass.properties.find(_.name == "i").get.underlyingType)
		printType(rt.getClass.properties.find(_.name == "i2").get.underlyingType)
	}
	
	private def printType(c:Class[_]):Unit = {
		if (c.isAssignableFrom(classOf[Int])) println("Int")
		else if (c.isAssignableFrom(classOf[Integer])) println("Integer")
//		case Integer => println("Integer")
//		case AnyRef => println("AnyRef")
	}
}