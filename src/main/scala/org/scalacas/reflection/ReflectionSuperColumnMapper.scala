package org.scalacas.reflection

import org.scalacas.SuperColumnMapper
import org.scale7.cassandra.pelops._
import org.apache.cassandra.thrift._
import ScalaReflection._

trait ReflectionSupport[A <: AnyRef] { self:SuperColumnMapper[A] =>
	def toSubColumnsList(mutator:Mutator, obj:A):List[Column] = {
		val properties = obj.getClass.properties.filter(_.hasSimpleType).toList
		val concreteClassColumn = mutator.newColumn("-concrete class-", Bytes.fromUTF8(obj.getClass.getName))
		val propertyColumns = 
			for (p <- properties if !p.isReadOnly) 
			yield mutator.newColumn(p.name, p.get(obj) match {
				case None => Bytes.NULL
				case Some(x) => serialize(x)
				case x @ _ => serialize(x)
			})
		
		concreteClassColumn :: propertyColumns
	}
	
	def fromSubColumnsList(subColumns:List[Column]):A
	
	def serialize(obj:Any) = obj match {
		case s:String => Bytes.fromUTF8(s)
		case i:Int => Bytes.fromInt(i)
		case i:Integer => Bytes.fromInt(i)
	}
}