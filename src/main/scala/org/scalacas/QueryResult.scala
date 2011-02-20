package org.scalacas

import scala.collection.JavaConversions._
import org.scale7.cassandra.pelops.Bytes
import org.apache.cassandra.thrift.SuperColumn

class QueryResult(row: Iterable[SuperColumn]) {
  def filter[O <: AnyRef](implicit mapper: Mapper[O]): Iterable[O] = {
    filterPrefix(mapper.fullPrefix, mapper)
  }
  
  def filter[O <: AnyRef, P <: AnyRef](parent:P)(implicit mapper: Mapper[O], mapperP:Mapper[P]): Iterable[O] = {
    filterPrefix(mapper.fullPrefix(parent), mapper)
  }
  
  def find[O <: AnyRef](implicit mapper: Mapper[O]): Option[O] = {
    findPrefix(mapper.fullPrefix, mapper)
  }
  
  def find[O <: AnyRef, P <: AnyRef](parent:P)(implicit mapper: Mapper[O], mapperP:Mapper[P]): Option[O] = {
    findPrefix(mapper.fullPrefix(parent), mapper)
  }
  
  private def filterPrefix[O <: AnyRef](fullPrefix:String, mapper: Mapper[O]) = {
	for (sc <- row if Bytes.toUTF8(sc.getName).startsWith(fullPrefix))
      yield mapper.columnsToObject(sc.getColumns toList)  
  }
  
  private def findPrefix[O <: AnyRef](fullPrefix:String, mapper: Mapper[O]) = {
	for (sc <- row find { sc => Bytes.toUTF8(sc.getName).startsWith(fullPrefix) } )
      yield mapper.columnsToObject(sc.getColumns toList)
  }
}