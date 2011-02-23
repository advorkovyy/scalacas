package org.scalacas.reflection

import collection.mutable
import java.util.Arrays

import org.scalacas.{Mapper, HasId, HasIdSupport}
import org.scale7.cassandra.pelops._
import org.apache.cassandra.thrift._
import ScalaReflection._
import org.scalacas.serialization.Serializers._

/**
 * Mapper implementation based on Java reflection. Does not support polymorphism.
 * 
 * Domain object's var members of serializable types are mapped to columns. 
 * Serializers for custom types can be mixed-in. Domain object must implement {@link HasId}.
 * If column name starts with '-' it is ignored during columns to object mapping.
 * 
 * Add it in your ColumnFamily implementation:
 * <pre>
 * <code>
 * implicit val myClassMapper = new ReflectionMapper[MyClass]("A", new MyClass)
 * implicit val myOtherClassMapper = new ReflectionMapper[MyOtherClass]("B", new MyOtherClass) with MyEnumSerializer
 * </code>
 * </pre>
 * 
 * @author Alexander Dvorkovyy
 *
 */
class ReflectionMapper[A <: HasId](prefix:String, ctor: =>A) extends Mapper[A](prefix) with ReflectionSupport[A] with HasIdSupport[A] {
	protected def createObjectInstance(colums:Seq[Column]):A = ctor
}

trait ReflectionSupport[A <: AnyRef] { self:Mapper[A] =>
	
	def objectToColumns(mutator:Mutator, obj:A):List[Column] = {		
		for ( p <- getProperties(obj.getClass) ) 
		yield mutator.newColumn(p.nameBytes, p.serializer.get.serialize(p.get(obj)))
	}
	
	/**
	 * Creates new object instance and maps columns to properties. 
	 * Column values are deserialized to object property types directly, 
	 * no type conversions are supported. This is by design, since 
	 * no type information is stored with column. 
	 */
	def columnsToObject(subColumns:Seq[Column]):A = {
		//val values = subColumns.map(col => (new Bytes(col.getName).toUTF8, new Bytes(col.getValue)))
		
		val obj = createObjectInstance(subColumns)
		val properties = getProperties(obj.getClass)
		for (col <- subColumns;
			 colName = col.getName;
			 if colName.size > 0 && colName(0) != '-' 
		) properties.find( p => Arrays.equals(p.nameBytes.getBytes.array, colName) ) match {
				case Some(p) =>
					try {
						p.set(obj, p.serializer.get.deserialize(col.getValue))
					} catch {
						case t => println("Warning: Couldn't set property '%s' in %s: %s".format(fromBytes[String](colName), obj.getClass, t.getMessage))
						t.printStackTrace()
					}
				case None => 
					println("Warning: property '%s' not serialized in %s".format(fromBytes[String](colName), obj.getClass))
		}
		
		obj.asInstanceOf[A]
	}
	
	protected def createObjectInstance(colums:Seq[Column]):A
	
	private val properties = new mutable.HashMap[Class[_], List[ScalaProperty]] with mutable.SynchronizedMap[Class[_], List[ScalaProperty]]
	private def getProperties(c:Class[_]) = properties.getOrElseUpdate(c, c.properties.filter { p => 
		(!p.isReadOnly) && 
		(if (p.serializer.isEmpty) { println("Skipping property %s of class %s: no serializer found"); false} else true) 
	})
}


