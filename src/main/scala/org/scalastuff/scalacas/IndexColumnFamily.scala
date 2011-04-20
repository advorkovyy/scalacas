package org.scalastuff.scalacas

import org.apache.cassandra.thrift.ConsistencyLevel
import org.scale7.cassandra.pelops.Mutator
import org.scale7.cassandra.pelops.Bytes

class IndexColumnFamily(_db: Database, _columnFamilyName: String) extends ColumnFamily(_db, _columnFamilyName) {
  import Mutators._

  implicit val mapper = StringMapper

  def findByKey(keyValue: String): Set[String] = {
    for {
      result <- query(key(keyValue))
      ref <- result.filter[String]
    } yield ref
  } toSet
  
  def saveRef(keyValue: String, ref: String) {
    mutate(ConsistencyLevel.QUORUM, write(keyValue, ref))
  }
  
  def deleteRef(keyValue: String, ref: String) {
    mutate(ConsistencyLevel.QUORUM, delete(keyValue, ref))
  }
  
  def deleteKey(keyValue: String) {
    deleteRow(keyValue, ConsistencyLevel.QUORUM)
  }
}