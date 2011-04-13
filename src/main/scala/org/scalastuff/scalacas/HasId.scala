package org.scalastuff.scalacas

/**
 * Implement this trait in your domain object if you want SuperColumn name be generated automatically.
 * Do not forget to mixin {@link HasIdSupport} into your {@link Mapper}.
 * 
 * @author Alexander Dvorkovyy
 *
 */
trait HasId {
	def id : String
}

/**
 * Mixin trait to provide automatic SuperColumn name generation for objects implementing {@link HasId} trait.
 * 
 * @author Alexander Dvorkovyy
 *
 */
trait HasIdSupport[A <: HasId] extends Mapper[A] {
	def id(obj:A) = obj.id
}