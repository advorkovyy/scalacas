package org.scalacas
import org.scale7.cassandra.pelops.Mutator
import org.apache.cassandra.thrift.Column

trait SuperColumnMapper[A <: AnyRef] {
	def name[P <: AnyRef](obj:A, parent:Option[P] = None)(implicit mP:SuperColumnMapper[P]):String
	def toSubColumnsList(mutator:Mutator, obj:A):List[Column]
}