package org.scalastuff.scalacas

import org.scale7.cassandra.pelops._
import org.apache.cassandra.thrift._
import org.specs.SpecificationWithJUnit
import org.junit.runner.RunWith
import org.specs.runner.JUnitSuiteRunner

@RunWith(classOf[JUnitSuiteRunner])
class QueryTest extends SpecificationWithJUnit {
	import TestClasses._
	
	"test objectOfClass" in {
		val qry = new Query(List("1")).objectsOfClass[A] 
		qry.fromSuperColumnName must be equalTo(Some("A "))
		qry.toSuperColumnName must be equalTo(Some("A ~"))
	}
	
	"test startWithClass" in {
		val qry = new Query(List("1")).startWithClass[A] 
		qry.fromSuperColumnName must be equalTo(Some("A "))
	}
	
	"test startWith" in {
		val qry = new Query(List("1")).startWith(A(1)) 
		qry.fromSuperColumnName must be equalTo(Some("A 1"))
	}
	
	"test startWith parent" in {
		val qry = new Query(List("1")).startWith(A(1), C(1)) 
		qry.fromSuperColumnName must be equalTo(Some("C 1/A 1"))
	}
	
	"test endWithClass" in {
		val qry = new Query(List("1")).endWithClass[A] 
		qry.toSuperColumnName must be equalTo(Some("A ~"))
	}
	
	"test endWith" in {
		val qry = new Query(List("1")).endWith(A(10)) 
		qry.toSuperColumnName must be equalTo(Some("A 10"))
	}
	
	"test endWith parent" in {
		val qry = new Query(List("1")).endWith(A(10), C(1)) 
		qry.toSuperColumnName must be equalTo(Some("C 1/A 10"))
	}
	
	"test limit" in {
		val qry = new Query(List("1")) limit 100 
		qry.maxColumnCount must be equalTo(100)
	}
}