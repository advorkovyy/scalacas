package org.scalacas

import org.scale7.cassandra.pelops._
import org.apache.cassandra.thrift._
import scala.collection.JavaConversions._

class Database(val cluster: Cluster, val keyspaceName: String) {
  Pelops.addPool("pool", cluster, keyspaceDef.name)

  def keyspaceDef = {
    val keyspaceManager = new KeyspaceManager(cluster)

    var result: KsDef = null
    try {
      result = keyspaceManager.getKeyspaceSchema(keyspaceName)
    } catch {
      case e: NotFoundException =>
        result = new KsDef(keyspaceName, "org.apache.cassandra.locator.SimpleStrategy", 1, List[CfDef]())
        keyspaceManager.addKeyspace(result)
    }
    result
  }
}