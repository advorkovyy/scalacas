package org.scalacas.json.scala

import org.codehaus.jackson.map._

import org.specs.SpecificationWithJUnit
import org.junit.runner.RunWith
import org.specs.runner.JUnitSuiteRunner

class TestOptionObj {
	var x:Option[String] = None
}

@RunWith(classOf[JUnitSuiteRunner])
class OptionSerializationTest extends SpecificationWithJUnit {
	val mapper = new ObjectMapper()
	mapper.registerModule(new ScalaModule())
	
	"test serialize Some" in {
		val t = new TestOptionObj
		
		t.x = Some("str")
		mapper.writeValueAsString(t) must_== """{"x":"str"}"""
		
		t.x = Some(null) // never try it at home ;)
		mapper.writeValueAsString(t) must_== """{"x":null}"""
	}
	
	"test serialize None" in {
		val t = new TestOptionObj
		t.x = None
		mapper.writeValueAsString(t) must_== """{"x":null}"""
	}
	
	"test deserialize Some" in {
		val t = mapper.readValue("""{"x":"str"}""", classOf[TestOptionObj])		
		t.x must_== Some("str")
	}
	
	"test deserialize None" in {
		val t = mapper.readValue("""{"x":null}""", classOf[TestOptionObj])		
		t.x must_== None
	}
}