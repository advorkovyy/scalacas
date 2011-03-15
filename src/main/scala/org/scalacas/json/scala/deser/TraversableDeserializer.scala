package org.scalacas.json.scala.deser
import org.codehaus.jackson.JsonToken

import org.codehaus.jackson.map.DeserializationContext
import org.codehaus.jackson.JsonParser
import scala.collection.generic.GenericCompanion
import org.codehaus.jackson.map.JsonDeserializer

class TraversableDeserializer(
    elementDeserializer: JsonDeserializer[_],
    elementClass: Class[_],
    companion: GenericCompanion[Traversable]) extends JsonDeserializer[Traversable[Any]] {
	
  def deserialize(parser: JsonParser, ctx: DeserializationContext): Traversable[_] = {
    val builder = companion.newBuilder[Any]
    
    if (parser.getCurrentToken != JsonToken.START_ARRAY)
    	throw ctx.mappingException(elementClass)
    	
    while (parser.nextToken != JsonToken.END_ARRAY)
    	builder += elementDeserializer.deserialize(parser, ctx)
    	
    builder.result
  }
}