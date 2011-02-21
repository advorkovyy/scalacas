package org.scalacas

import org.scale7.cassandra.pelops._
import org.apache.cassandra.thrift._

class Query(val keys:Seq[String]) {
	var fromSuperColumnName:Option[String] = None
	var toSuperColumnName:Option[String] = None
	var rev = false
	var maxColumnCount = Integer.MAX_VALUE
	var cl = ConsistencyLevel.ONE
	
	def startWith[O <: AnyRef](obj:O)(implicit mapperO:Mapper[O]):Query = {
		fromSuperColumnName = Some(mapperO.name(obj))
		this
	}
	
	def startWith[O <: AnyRef, P <: AnyRef](obj:O, parent:P)(implicit mapperO:Mapper[O], mapperP:Mapper[P]):Query = {
		fromSuperColumnName = Some(mapperO.name(obj, parent))
		this
	}
	
	def startWithClass[O <: AnyRef]()(implicit mapperO:Mapper[O]):Query = {
		fromSuperColumnName = Some(mapperO.fullPrefix)
		this
	}
	
	def endWith[O <: AnyRef](obj:O)(implicit mapperO:Mapper[O]):Query = {
		toSuperColumnName = Some(mapperO.name(obj))
		this
	}
	
	def endWith[O <: AnyRef, P <: AnyRef](obj:O, parent:P)(implicit mapperO:Mapper[O], mapperP:Mapper[P]):Query = {
		toSuperColumnName = Some(mapperO.name(obj, parent))
		this
	}

	def endWithClass[O <: AnyRef]()(implicit mapperO:Mapper[O]):Query = {
		toSuperColumnName = Some(mapperO.fullPrefix + "~")
		this
	}
	
	def objectsOfClass[O <: AnyRef]()(implicit mapperO:Mapper[O]):Query = {
		fromSuperColumnName = Some(mapperO.fullPrefix)
		toSuperColumnName = Some(mapperO.fullPrefix + "~")
		this
	}

	def reversed() = {
		rev = true
		this
	}
	
	def limit(maxColumnCount:Int) = {
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