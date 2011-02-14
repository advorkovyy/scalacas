package org.scalacas

import org.scale7.cassandra.pelops._
import org.apache.cassandra.thrift._

class Query(val keys:List[String]) {
	var fromSuperColumnName:Option[String] = None
	var toSuperColumnName:Option[String] = None
	var rev = false
	var maxColumnCount = Integer.MAX_VALUE
	var cl = ConsistencyLevel.ONE
	
	def from[O <: AnyRef, P <: AnyRef](obj:O, parent : Option[P] = None)(implicit mapperO:SuperColumnMapper[O], mapperP:SuperColumnMapper[P]):Query = {
		fromSuperColumnName = Some(mapperO.name(obj, parent))
		this
	}
	
	def to[O <: AnyRef, P <: AnyRef](obj:O, parent : Option[P] = None)(implicit mapperO:SuperColumnMapper[O], mapperP:SuperColumnMapper[P]):Query = {
		toSuperColumnName = Some(mapperO.name(obj, parent))
		this
	}
	
	def reversed() = {
		rev = true
		this
	}
	
	def max(maxColumnCount:Int) = {
		this.maxColumnCount = maxColumnCount
		this
	}
	
	def consistencyLevel(cl:ConsistencyLevel) = {
		this.cl = cl
		this
	}
	
	def toSlicePredicate = Selector.newColumnsPredicate(
		fromSuperColumnName map(Bytes.fromUTF8(_)) getOrElse Bytes.EMPTY, 
		toSuperColumnName map(Bytes.fromUTF8(_)) getOrElse Bytes.EMPTY, 
		rev, maxColumnCount)
	
}