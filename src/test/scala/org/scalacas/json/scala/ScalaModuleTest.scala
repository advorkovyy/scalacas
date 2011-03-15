package org.scalacas.json.scala

//import scala.collection.JavaConversions._
import org.codehaus.jackson.map._

import org.specs.SpecificationWithJUnit
import org.junit.runner.RunWith
import org.specs.runner.JUnitSuiteRunner

class TestScalaObj {
	var s:String = "whatever"
	var x:Int = 1
	var y:Double = 3.45
	var ar = Array(("a","b"),("c", "d"))
	def getWhatever = Array("sdf", "wer")
}

@RunWith(classOf[JUnitSuiteRunner])
class ScalaModuleTest  extends SpecificationWithJUnit {
	val mapper = new ObjectMapper()
	mapper.registerModule(new ScalaModule())
	
	"" in {
		println(mapper.writeValueAsString(new TestScalaObj))
		
		val to = mapper.readValue("""{"s":"whateverX","x":10,"y":34.5}""", classOf[TestScalaObj])
		to.s must_== "whateverX"
	}
}