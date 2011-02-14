package org.scalacas

import org.scale7.cassandra.pelops._
import scala.collection.JavaConversions._

object Mutators {
	def write[O <: AnyRef, P <: AnyRef](key : String, obj : O, parent : Option[P] = None)(implicit mapperO:SuperColumnMapper[O], mapperP:SuperColumnMapper[P]) = (mutator : Mutator, cf:ColumnFamily) => {
		mutator.writeSubColumns(cf.columnFamilyName, key, mapperO.name(obj, parent), mapperO.toSubColumnsList(mutator, obj))
	}
}