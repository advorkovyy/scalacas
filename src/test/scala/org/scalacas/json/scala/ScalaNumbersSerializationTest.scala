package org.scalacas.json.scala

import org.codehaus.jackson.map._

import org.specs.SpecificationWithJUnit
import org.junit.runner.RunWith
import org.specs.runner.JUnitSuiteRunner

class TestNumbersObj {
	var bd:BigDecimal = _
	var bi:BigInt = _
}

@RunWith(classOf[JUnitSuiteRunner])
class ScalaNumbersSerializationTest extends SpecificationWithJUnit {
	val mapper = new ObjectMapper()
	mapper.registerModule(new ScalaModule())
	
	"test serialize" in {
		val t = new TestNumbersObj
		
		t.bd = 456.123
		t.bi = 789
		mapper.writeValueAsString(t) must_== """{"bd":456.123,"bi":789}"""
	}
	
	"test serialize null" in {
		val t = new TestNumbersObj
		mapper.writeValueAsString(t) must_== """{"bd":null,"bi":null}"""
	}
	
	"test deserialize null" in {
		val t = mapper.readValue("""{"bd":456.123,"bi":789}""", classOf[TestNumbersObj])		
		t.bd must_== BigDecimal(456.123)
		t.bi must_== BigInt(789)
	}
	
	"test deserialize None" in {
		val t = mapper.readValue("""{"bd":null,"bi":null}""", classOf[TestNumbersObj])		
		t.bd must beNull
		t.bi must beNull
	}
}