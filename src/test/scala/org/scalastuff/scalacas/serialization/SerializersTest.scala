package org.scalastuff.scalacas.serialization

import org.specs.SpecificationWithJUnit
import org.junit.runner.RunWith
import org.specs.runner.JUnitSuiteRunner
import Serializers._
import org.scale7.cassandra.pelops.Bytes

@RunWith(classOf[JUnitSuiteRunner])
class SerializersTest extends SpecificationWithJUnit {
	"test isDefinedFor" in {
		findSerializerFor(classOf[String]) must haveClass[Some[_]]
		findSerializerFor(classOf[Long]) must haveClass[Some[_]]
		findSerializerFor(classOf[java.lang.Long]) must haveClass[Some[_]]
		findSerializerFor(classOf[BigDecimal]) must haveClass[Some[_]]
		findSerializerFor(this.getClass) must be(None)
	}
	
	"test Option" in {
		class TestOption {
			val i:Option[Int] = None
			val i2:Option[Integer] = None
		}
		val s = findSerializerFor(classOf[TestOption].getMethod("i").getGenericReturnType).get
		checkSerializer(s, Some(2))
		checkSerializer(s, Some(Int.box(2)))
		checkSerializer(s, None)
		
		val s2 = findSerializerFor(classOf[TestOption].getMethod("i2").getGenericReturnType).get
		checkSerializer(s2, Some(Int.box(3)))
		checkSerializer(s2, Some(3))
		checkSerializer(s2, None)
	}
	
	"test standard serializers" in {
		checkSerializer(classOf[String], "whatever")
		checkSerializer(classOf[String], null)
		
		checkSerializer(classOf[Long], Long.box(2))
		checkSerializer(classOf[java.lang.Long], Long.box(2))
		checkSerializer(classOf[java.lang.Long], null)
		
		checkSerializer(classOf[Int], Int.box(2))
		checkSerializer(classOf[java.lang.Integer], Int.box(2))
		checkSerializer(classOf[java.lang.Integer], null)
		
		checkSerializer(classOf[BigDecimal], BigDecimal(4.56))
		checkSerializer(classOf[BigDecimal], null)
	}
	
	
	private def checkSerializer(c:Class[_], obj:AnyRef) {
		val s = findSerializerFor(c).get.asInstanceOf[Serializer[AnyRef]]
		checkSerializer(s, obj)
	}
	
	private def checkSerializer(serializer:Serializer[_], obj:Any) {
		val s = serializer.asInstanceOf[Serializer[Any]]
		val buffer = s.serialize(obj)
		val deser = s.deserialize(buffer.getBytes)
		
		if (obj == null) deser must beNull
		else deser must_==(obj)
	}

}