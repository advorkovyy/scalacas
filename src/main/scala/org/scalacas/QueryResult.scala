package org.scalacas

import scala.collection.JavaConversions._
import org.scale7.cassandra.pelops.Bytes
import org.apache.cassandra.thrift.SuperColumn

class QueryResult(row: Iterable[SuperColumn]) {
  def filter[O <: AnyRef](implicit mapper: SuperColumnMapper[O]): Iterable[O] = {
    filterPrefix(mapper.fullPrefix, mapper)
  }
  
  def filter[O <: AnyRef, P <: AnyRef](parent:P)(implicit mapper: SuperColumnMapper[O], mapperP:SuperColumnMapper[P]): Iterable[O] = {
    filterPrefix(mapper.fullPrefix(parent), mapper)
  }
  
  def find[O <: AnyRef](implicit mapper: SuperColumnMapper[O]): Option[O] = {
    findPrefix(mapper.fullPrefix, mapper)
  }
  
  def find[O <: AnyRef, P <: AnyRef](parent:P)(implicit mapper: SuperColumnMapper[O], mapperP:SuperColumnMapper[P]): Option[O] = {
    findPrefix(mapper.fullPrefix(parent), mapper)
  }
  
  private def filterPrefix[O <: AnyRef](fullPrefix:String, mapper: SuperColumnMapper[O]) = {
	for (sc <- row if Bytes.fromBytes(sc.getName).toString.startsWith(fullPrefix))
      yield mapper.fromSubColumnsList(sc.getColumns toList)  
  }
  
  private def findPrefix[O <: AnyRef](fullPrefix:String, mapper: SuperColumnMapper[O]) = {
	for (sc <- row find { sc => Bytes.fromBytes(sc.getName).toString.startsWith(fullPrefix) } )
      yield mapper.fromSubColumnsList(sc.getColumns toList)
  }
}