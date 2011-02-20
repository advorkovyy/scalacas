package org.scalacas.reflection

import org.scalacas.{Mapper, HasId, HasIdSupport}
import org.scale7.cassandra.pelops._
import org.apache.cassandra.thrift._
import ScalaReflection._

/**
 * Mapper implementation based on Java reflection. 
 * 
 * Supports polymorphism by adding extra column with class name. While property access
 * using reflection seems to be very fast, constructor calls are very slow. Columns to
 * object conversion can be 2-3 times slower compared to {@link ReflectionMapper}.
 * 
 * If performance is an issue, implement your own polymorphic mapper. General idea is the same:
 * add column with the name starting with '-' (ignored by ReflectionMapper) and use it's
 * value to decide which subclass instance you want to create.
 * 
 * @author Alexander Dvorkovyy
 *
 */
class PolymorphicReflectionMapper[A <: HasId](prefix:String) extends Mapper[A](prefix) with PolymorphicReflectionSupport[A] with HasIdSupport[A]

trait PolymorphicReflectionSupport[A <: AnyRef] extends ReflectionSupport[A] { self:Mapper[A] =>
	override def objectToColumns(mutator:Mutator, obj:A):List[Column] = {		
		mutator.newColumn("-concrete class-", Bytes.fromUTF8(obj.getClass.getName)) ::
		super.objectToColumns(mutator, obj)
	}
	
	protected def createObjectInstance(colums:Seq[(String, Bytes)]):A = {
		val c = Class.forName(colums.find(_._1 == "-concrete class-").get._2.toUTF8)
		c.create.asInstanceOf[A]
	}
}