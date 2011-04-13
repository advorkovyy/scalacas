package org.scalastuff.scalacas

import scala.collection.JavaConversions._
import org.scale7.cassandra.pelops._
import org.apache.cassandra.thrift._

object TestClasses {
  case class A(id: Int)
  case class B(id: Int)
  case class C(id: Int)

  implicit val scmA = new Mapper[A]("A") {
	  def objectToColumns(mutator:Mutator, obj:A) = Nil
	  def columnsToObject(subColumns:Seq[Column]) = A(id = Selector.getColumnValue(subColumns, "id").toInt)
	  def id(obj:A) = obj.id.toString
  }
  
  implicit val scmB = new Mapper[B]("B") {
	  def objectToColumns(mutator:Mutator, obj:B) = Nil
	  def columnsToObject(subColumns:Seq[Column]) = B(id = Selector.getColumnValue(subColumns, "id").toInt)
	  def id(obj:B) = obj.id.toString
  }
  
  implicit val scmC = new Mapper[C]("C") {
	  def objectToColumns(mutator:Mutator, obj:C) = Nil
	  def columnsToObject(subColumns:Seq[Column]) = C(id = Selector.getColumnValue(subColumns, "id").toInt)
	  def id(obj:C) = obj.id.toString
  }
}