package org.scalastuff.scalacas
import org.scale7.cassandra.pelops.Mutator
import org.apache.cassandra.thrift.Column

/**
 * Maps objects to SuperColumn.
 * 
 * Provides SuperColumn name and list of columns, representing object properties. 
 * Provides also back conversion from list of columns to object instance. 
 * Subclass to provide your own mapping to your domain objects of given class.
 * Must be thread-safe.
 * 
 * @author Alexander Dvorkovyy
 */
abstract class Mapper[A <: AnyRef](val prefix:String) {
	def name(obj:A):String = {
		val sb = new StringBuilder(fullPrefix)
		sb ++= id(obj)
		sb.result
	}
	
	def name[P <: AnyRef](obj:A, parent:P)(implicit mP:Mapper[P]):String = {
		val sb = new StringBuilder(fullPrefix(parent))
		sb ++= id(obj)
		sb.result
	}
	
	val fullPrefix = prefix + " " 
		
	def fullPrefix[P <: AnyRef](parent:P)(implicit mP:Mapper[P]):String = {
		val sb = new StringBuilder(mP.name(parent))
		sb ++= "/"
		sb ++= fullPrefix
		sb.result
	}
	
	def id(obj:A):String
	def objectToColumns(mutator:Mutator, obj:A):Seq[Column]
	def columnsToObject(superColumnName: Array[Byte], subColumns:Seq[Column]):A
}