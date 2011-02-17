package org.scalacas

import org.scale7.cassandra.pelops._
import org.apache.cassandra.thrift._
import org.specs.SpecificationWithJUnit

import org.junit.runner.RunWith
import org.specs.runner.JUnitSuiteRunner

@RunWith(classOf[JUnitSuiteRunner])
class SuperColumnMapperTest extends SpecificationWithJUnit {

  import TestClasses._

  "test name" in {
	scmA.name(A(1)) must be equalTo("A 1")
	scmB.name(B(2)) must be equalTo("B 2")
	scmB.name(B(3), C(4)) must be equalTo("C 4/B 3")
  }
  
  "test full prefix" in {
	scmA.fullPrefix must be equalTo("A ")
	scmB.fullPrefix(C(4)) must be equalTo("C 4/B ")
  }

}