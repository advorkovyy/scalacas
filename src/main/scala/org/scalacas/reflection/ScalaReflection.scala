package org.scalacas.reflection

import collection.mutable
import java.util.{ Date, UUID }
import java.lang.reflect.{ Method, ParameterizedType, Constructor }

/**
 * Provides reflective property access to scala class.
 *
 * Usage: <code>import org.scalacas.reflection.ScalaReflection._</code>
 *
 * @author Ruud Diterwich
 */
object ScalaReflection {
  private val infoMap = new mutable.HashMap[Class[_], RichClass[_]] with mutable.SynchronizedMap[Class[_], RichClass[_]]
  implicit def richClass[T](c: Class[T]): RichClass[T] = infoMap.getOrElseUpdate(c, new RichClass[T](c)).asInstanceOf[RichClass[T]]
}

class RichClass[T](c: Class[T]) {

  /**
   * Top-level class in super-class hierarchy that is not
   * java.lang.Object
   */
  val topLevelClass: Class[_] = c.getSuperclass match {
    case null => c
    case superClass if superClass == classOf[java.lang.Object] => c
    case superClass => ScalaReflection.richClass(superClass).topLevelClass
  }

  /**
   * Properties of the class.
   */
  val properties = (for {
    getter <- c.getMethods
    if (!getter.getName.contains('$'))
    if (getter.getParameterTypes.length == 0)
    if (getter.getReturnType != Void.TYPE)
    setter = c.getMethods.find(_.getName == getter.getName + "_$eq")
  } yield new ScalaProperty(getter, setter)) toList

  /**
   * TODO Rewrite with more efficient implementation.
   * @param name
   * @return
   */
  def properties(name: String): Option[ScalaProperty] = properties.find(_.name == name)

  /**
   * Creates a new instance. Only classes that have no constructor
   * parameters or with a constructor that has default values for
   * all its parameters are supported. Inner classes are not supported.
   * Same default parameter values are used for each constructor invocation.
   */
  def create: T = constructor.newInstance(constructorPars: _*)

  /**
   * Makes a copy of the given object. All properties are copied by reference.
   */
  def copy[A <: AnyRef with T](orig: A, exclude: String*): T = {
    val result = create
    for (prop <- properties if !prop.isReadOnly && !exclude.contains(prop.name)) {
      prop.set(result.asInstanceOf[AnyRef], prop.get(orig))
    }

    result
  }

  /**
   * Constructor. Secondary constructors are not supported.
   */
  private lazy val constructor: Constructor[T] = {
    require(c.getConstructors().size > 0, c.getName + " has no constructors")
    c.getConstructors()(0).asInstanceOf[Constructor[T]]
  }

  private lazy val constructorPars = Array((for {
    index <- 1 to constructor.getParameterTypes.size
    method = c.getMethod("init$default$" + index)
  } yield method.invoke(null)): _*)

  def name = c.getName
}

class ScalaProperty(val getter: Method, val setter: Option[Method]) {
  val hasCollectionType = classOf[scala.collection.Seq[_]].isAssignableFrom(propertyType)
  val hasMapType = classOf[scala.collection.Map[_, _]].isAssignableFrom(propertyType)
  val hasOptionType = classOf[scala.Option[_]].isAssignableFrom(propertyType)

  val underlyingType: Class[_] =
    if (hasCollectionType || hasOptionType) parameterType
    else if (hasArrayType) propertyType.getComponentType
    else propertyType

  def name = getter.getName
  def propertyType: Class[_] = getter.getReturnType
  def hasArrayType = propertyType.isArray
  def isReadOnly = setter == None
  def get(obj: AnyRef): AnyRef = getter.invoke(obj)
  def set[A <: AnyRef](obj: AnyRef, value: A) = setter.get.invoke(obj, value)
  private def parameterType = getter.getGenericReturnType match {
    case t: ParameterizedType => t.getActualTypeArguments()(0) match {
      case c: java.lang.Class[_] => c
      case _ => classOf[java.lang.Object]
    }
    case _ => getter.getReturnType
  }
}