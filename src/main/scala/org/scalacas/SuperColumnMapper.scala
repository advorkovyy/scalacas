package org.scalacas
import org.scale7.cassandra.pelops.Mutator
import org.apache.cassandra.thrift.Column

abstract class SuperColumnMapper[A <: AnyRef](val prefix:String) {
	def name(obj:A):String = {
		val sb = new StringBuilder(fullPrefix)
		sb ++= id(obj)
		sb.result
	}
	
	def name[P <: AnyRef](obj:A, parent:P)(implicit mP:SuperColumnMapper[P]):String = {
		val sb = new StringBuilder(fullPrefix(parent))
		sb ++= id(obj)
		sb.result
	}
	
	lazy val fullPrefix = prefix + " " 
		
	def fullPrefix[P <: AnyRef](parent:P)(implicit mP:SuperColumnMapper[P]):String = {
		val sb = new StringBuilder(mP.name(parent))
		sb ++= "/"
		sb ++= fullPrefix
		sb.result
	}
	
	def id(obj:A):String
	def toSubColumnsList(mutator:Mutator, obj:A):List[Column]
	def fromSubColumnsList(subColumns:List[Column]):A
}

object NullSuperColumnMapper extends SuperColumnMapper[Null]("~Null~") {
	def id(obj:Null):String = throw new IllegalArgumentException
	def toSubColumnsList(mutator:Mutator, obj:Null):List[Column] = throw new IllegalArgumentException
	def fromSubColumnsList(subColumns:List[Column]):Null = throw new IllegalArgumentException
}