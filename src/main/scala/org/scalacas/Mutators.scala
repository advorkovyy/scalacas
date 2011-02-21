package org.scalacas

import org.scale7.cassandra.pelops._
import scala.collection.JavaConversions._

object Mutators {
	def write[O <: AnyRef](key : String, obj : O)(implicit mapperO:Mapper[O]) = (mutator : Mutator, cf:ColumnFamily) => {
		mutator.writeSubColumns(cf.columnFamilyName, Bytes.fromUTF8(key), Bytes.fromUTF8(mapperO.name(obj)), mapperO.objectToColumns(mutator, obj), true)
	}
	
	def write[O <: AnyRef, P <: AnyRef](key : String, obj : O, parent : P)(implicit mapperO:Mapper[O], mapperP:Mapper[P]) = (mutator : Mutator, cf:ColumnFamily) => {
		mutator.writeSubColumns(cf.columnFamilyName, Bytes.fromUTF8(key), Bytes.fromUTF8(mapperO.name(obj, parent)), mapperO.objectToColumns(mutator, obj), true)
	}
	
	def writeAll[O <: AnyRef](key : String, objs : Iterable[O])(implicit mapperO:Mapper[O]) = (mutator : Mutator, cf:ColumnFamily) => {
		objs foreach { obj =>
			mutator.writeSubColumns(cf.columnFamilyName, Bytes.fromUTF8(key), Bytes.fromUTF8(mapperO.name(obj)), mapperO.objectToColumns(mutator, obj), true)
		}
	}
	
	def writeAll[O <: AnyRef, P <: AnyRef](key : String, objs : Iterable[O], parent : P)(implicit mapperO:Mapper[O], mapperP:Mapper[P]) = (mutator : Mutator, cf:ColumnFamily) => {
		objs foreach { obj =>
			mutator.writeSubColumns(cf.columnFamilyName, Bytes.fromUTF8(key), Bytes.fromUTF8(mapperO.name(obj, parent)), mapperO.objectToColumns(mutator, obj), true)
		}
	}
}