package org.scalacas

import org.scale7.cassandra.pelops._
import org.apache.cassandra.thrift._
import scala.collection.JavaConversions._

class ColumnFamily(val db: Database, val columnFamilyName: String) {
  if (!db.keyspaceDef.getCf_defs.exists(_.getName == columnFamilyName)) {
    val columnFamilyManager = new ColumnFamilyManager(db.cluster, db.keyspaceName)
    val cfDef = new CfDef(db.keyspaceName, columnFamilyName)
    cfDef.setColumn_type("Super")
    cfDef.setComparator_type("UTF8Type")
    columnFamilyManager.addColumnFamily(cfDef)
  }

  def mutate(cl: ConsistencyLevel, mutators: ((Mutator, ColumnFamily) => _)*) = {
    val mutator = Pelops.createMutator("pool")
    for (m <- mutators)
      m(mutator, this)
    mutator.execute(cl)
  }

  def key(k: String) = new Query(List(k))
  def keys(ks: List[String]) = new Query(ks)
  def keys(ks: String*) = new Query(ks toList)

  def query(qry: Query) = {
    val selector = Pelops.createSelector("pool")
    new QueryResult(
      qry.keys match {
        case key :: Nil => Iterable(selector.getSuperColumnsFromRow(columnFamilyName, key, qry.toSlicePredicate, qry.cl))
        case _ => selector.getSuperColumnsFromRowsUtf8Keys(columnFamilyName, qry.keys, qry.toSlicePredicate, qry.cl) values () map (_ toIterable)
      }
    )    
  }
}