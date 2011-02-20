package org.scalacas.reflection

import org.junit.runner.RunWith
import org.specs.SpecificationWithJUnit
import org.specs.runner.JUnitSuiteRunner
import ScalaReflection._

@RunWith(classOf[JUnitSuiteRunner])
class ScalaReflectionTest extends SpecificationWithJUnit {
  class SimpleProperties(
    var stringProp: String,
    var booleanOpt: Option[Boolean],
    var intArray: Array[Int],
    var longList: List[Long])

  "RichPropertyTest" should {
    "simple property type" in {
      val p: ScalaProperty = classOf[SimpleProperties].properties("stringProp").get

      (p.underlyingType == classOf[String]) must_== true
      (p.propertyType == classOf[String]) must_== true
      p.hasCollectionType must_== false
      p.hasArrayType must_== false
      p.hasOptionType must_== false
      p.isReadOnly must_== false
    }

    "option property type" in {
      val p: ScalaProperty = classOf[SimpleProperties].properties("booleanOpt").get

      (p.underlyingType == classOf[java.lang.Boolean]) must_== true
      (p.propertyType == classOf[Option[Boolean]]) must_== true
      p.hasCollectionType must_== false
      p.hasArrayType must_== false
      p.hasOptionType must_== true
      p.isReadOnly must_== false
    }

    "collection property type" in {
      val p: ScalaProperty = classOf[SimpleProperties].properties("longList").get

      (p.underlyingType == classOf[java.lang.Long]) must_== true
      (p.propertyType == classOf[List[Long]]) must_== true
      p.hasCollectionType must_== true
      p.hasArrayType must_== false
      p.hasOptionType must_== false
      p.isReadOnly must_== false
    }

    "array property type" in {
      val p: ScalaProperty = classOf[SimpleProperties].properties("intArray").get

      (p.underlyingType == classOf[Int]) must_== true
      (p.propertyType == classOf[Array[Int]]) must_== true
      p.hasCollectionType must_== false
      p.hasArrayType must_== true
      p.hasOptionType must_== false
      p.isReadOnly must_== false
    }
  }

}