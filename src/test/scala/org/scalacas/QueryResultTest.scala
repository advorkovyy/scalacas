package org.scalacas

import scala.collection.JavaConversions._
import org.scale7.cassandra.pelops._
import org.apache.cassandra.thrift._
import org.specs.SpecificationWithJUnit

import org.junit.runner.RunWith
import org.specs.runner.JUnitSuiteRunner

@RunWith(classOf[JUnitSuiteRunner])
class QueryResultTest extends SpecificationWithJUnit {
  import TestClasses._
  
  val qr = new QueryResult(List(
      new SuperColumn(toBytes("A 1"), List(new Column(toBytes("id"), toBytes(1), 0))),
      new SuperColumn(toBytes("B 2"), List(new Column(toBytes("id"), toBytes(2), 0))),
      new SuperColumn(toBytes("B 3"), List(new Column(toBytes("id"), toBytes(3), 0)))))

  "test filter" in {    
    val a = qr.filter[A]
    a.size must be equalTo(1)
    a.head.id must be equalTo(1)

    val b = qr.filter[B] toList;
    b.size must be equalTo(2)
    b(0).id must be equalTo(2)
    b(1).id must be equalTo(3)
    
    val c = qr.filter[C]
    c.size must be equalTo(0)
  }
  
  "test find" in {    
    val a = qr.find[A]
    a must be equalTo(Some(A(1)))

    val b = qr.find[B]
    b must be equalTo(Some(B(2)))
    
    val c = qr.find[C]
    c must be equalTo(None)
  }

  private def toBytes(str: String) = Bytes.fromUTF8(str).getBytes
  private def toBytes(i: Int) = Bytes.fromInt(i).getBytes
}