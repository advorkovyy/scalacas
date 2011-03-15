package org.scalacas.json.serialization

import collection.mutable
import org.codehaus.jackson.{JsonParser, JsonGenerator, JsonToken}
import org.codehaus.jackson.`type`.JavaType
import org.codehaus.jackson.map.`type`.TypeFactory

trait JsonSerializer[T] {
  def serialize(obj: T, generator: JsonGenerator): Unit
  def deserialize(parser:JsonParser):T
}

trait JsonSerializerFactory[T] {
  def createSerializerFor(t: JavaType):JsonSerializer[T]
}

trait NullableValueJsonSerializer[T >: Null] extends JsonSerializer[T] {
	abstract override def serialize(obj: T, generator: JsonGenerator) {
		if (obj == null) generator.writeNull()
		else super.serialize(obj, generator)
	}
	
	abstract override def deserialize(parser:JsonParser) = {
		if (parser.getCurrentToken == JsonToken.VALUE_NULL) null
		else super.deserialize(parser)
	}
}

object JsonSerializers {  

  def findJsonSerializerFor[T](implicit mf:Manifest[T]):JsonSerializer[T] = findJsonSerializerFor(TypeFactory.`type`(mf.erasure))
  def findJsonSerializerFor[T](t:JavaType):JsonSerializer[T] = {
    val jsf = (serializers.get(t.getRawClass) match {
    	case Some(sjsf) => sjsf
    	case None => register(new ScalaBeanSerializer(t.getRawClass) with NullableValueJsonSerializer[AnyRef], t.getRawClass)  
    }).asInstanceOf[JsonSerializerFactory[T]]
    jsf.createSerializerFor(t)    
  }
  
  def register[T](js:JsonSerializer[T])(implicit mf:Manifest[T]):JsonSerializerFactory[T] = register(js, mf.erasure)
  
  def register[T](js:JsonSerializer[T], erasure:Class[_]):JsonSerializerFactory[T] = {
	  val jsf = new StaticJsonSerializerFactory(js)
	  serializers(erasure) = jsf
	  jsf
  }
  
  def register[T](jsf:JsonSerializerFactory[T])(implicit mf:Manifest[T]):JsonSerializerFactory[T] = {
	  serializers(mf.erasure) = jsf
	  jsf
  }
  
  private val serializers = new mutable.HashMap[Class[_], JsonSerializerFactory[_]] //with mutable.SynchronizedMap[Class[_], JsonSerializerFactory[_]]
  
  //
  //
  //
  register(new StringJsonSerializer with NullableValueJsonSerializer[String])
  register(new CharJsonSerializer with NullableValueJsonSerializer[java.lang.Character])
  register(new CharJsonSerializer, classOf[Char])
  register(new BooleanJsonSerializer with NullableValueJsonSerializer[java.lang.Boolean])
  register(new BooleanJsonSerializer, classOf[Boolean])
  
  register(OptionJsonSerializerFactory)
  
  //
  // Numeric types
  //
  register(new ByteJsonSerializer with NullableValueJsonSerializer[java.lang.Byte])
  register(new ByteJsonSerializer, classOf[Byte])
  
  register(new ShortJsonSerializer with NullableValueJsonSerializer[java.lang.Short])
  register(new ShortJsonSerializer, classOf[Short])
  
  register(new IntegerJsonSerializer with NullableValueJsonSerializer[java.lang.Integer])
  register(new IntegerJsonSerializer, classOf[Int])
  
  register(new LongJsonSerializer with NullableValueJsonSerializer[java.lang.Long])
  register(new LongJsonSerializer, classOf[Long])
  
  register(new FloatJsonSerializer with NullableValueJsonSerializer[java.lang.Float])
  register(new FloatJsonSerializer, classOf[Float])
  
  register(new DoubleJsonSerializer with NullableValueJsonSerializer[java.lang.Double])
  register(new DoubleJsonSerializer, classOf[Double])
  
  register(new ScalaBigDecimalJsonSerializer with NullableValueJsonSerializer[BigDecimal])
  register(new JavaBigDecimalJsonSerializer with NullableValueJsonSerializer[java.math.BigDecimal])
  register(new ScalaBigIntJsonSerializer with NullableValueJsonSerializer[BigInt])
  register(new JavaBigIntegerJsonSerializer with NullableValueJsonSerializer[java.math.BigInteger])
  
  private class StaticJsonSerializerFactory[T](s: JsonSerializer[T]) extends JsonSerializerFactory[T] {
    def  createSerializerFor(t: JavaType) = s
  }
}

