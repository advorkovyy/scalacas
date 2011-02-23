package org.scalacas.reflection

import org.scalacas.{ Mapper, HasId, HasIdSupport }
import org.scale7.cassandra.pelops._
import org.apache.cassandra.thrift._
import ScalaReflection._
import org.scalacas.serialization.Serializers._
import java.util.Arrays

/**
 * Mapper implementation based on Java reflection.
 *
 * Supports polymorphism by adding extra column with class name. While property access
 * using reflection seems to be very fast, constructor calls are very slow. Columns to
 * object conversion can be 2-3 times slower compared to {@link ReflectionMapper}.
 *
 * If performance is an issue, implement your own polymorphic mapper. General idea is the same:
 * add the column with name starting with '-' (ignored by ReflectionMapper) and use it's
 * value to decide which subclass instance you want to create.
 *
 * @author Alexander Dvorkovyy
 *
 */
class PolymorphicReflectionMapper[A <: HasId](prefix: String) extends Mapper[A](prefix) with PolymorphicReflectionSupport[A] with HasIdSupport[A]

trait PolymorphicReflectionSupport[A <: AnyRef] extends ReflectionSupport[A] { self: Mapper[A] =>
  import PolymorphicReflectionSupport._

  override def objectToColumns(mutator: Mutator, obj: A): List[Column] = {
    mutator.newColumn(CONCRETE_CLASS_BYTES, toBytes(obj.getClass.getName)) ::
      super.objectToColumns(mutator, obj)
  }

  protected def createObjectInstance(colums: Seq[Column]): A = {
    colums.find( c => Arrays.equals(c.getName, CONCRETE_CLASS_BYTES_ARRAY) ) match {
      case Some(col) =>
        val c = Class.forName(fromBytes[String](col.getValue))
        c.create.asInstanceOf[A]
      case None => throw new RuntimeException("Cannot deserialize object: column with name '%s' not found".format(CONCRETE_CLASS))
    }

  }
}

object PolymorphicReflectionSupport {
  val CONCRETE_CLASS = "-concrete class-"
  val CONCRETE_CLASS_BYTES = toBytes(CONCRETE_CLASS)
  val CONCRETE_CLASS_BYTES_ARRAY = CONCRETE_CLASS_BYTES.getBytes.array
}