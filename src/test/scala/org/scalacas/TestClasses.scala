package org.scalacas

import scala.collection.JavaConversions._
import org.scale7.cassandra.pelops._
import org.apache.cassandra.thrift._

object TestClasses {
  case class A(id: Int)
  case class B(id: Int)
  case class C(id: Int)

  implicit val scmA = new SuperColumnMapper[A]("A") {
	  def toSubColumnsList(mutator:Mutator, obj:A) = Nil
	  def fromSubColumnsList(subColumns:List[Column]) = A(id = Selector.getColumnValue(subColumns, "id").toInt)
	  def id(obj:A) = obj.id.toString
  }
  
  implicit val scmB = new SuperColumnMapper[B]("B") {
	  def toSubColumnsList(mutator:Mutator, obj:B) = Nil
	  def fromSubColumnsList(subColumns:List[Column]) = B(id = Selector.getColumnValue(subColumns, "id").toInt)
	  def id(obj:B) = obj.id.toString
  }
  
  implicit val scmC = new SuperColumnMapper[C]("C") {
	  def toSubColumnsList(mutator:Mutator, obj:C) = Nil
	  def fromSubColumnsList(subColumns:List[Column]) = C(id = Selector.getColumnValue(subColumns, "id").toInt)
	  def id(obj:C) = obj.id.toString
  }
}