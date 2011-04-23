package org.scalastuff.scalacas

import org.scale7.cassandra.pelops._
import org.apache.cassandra.thrift._
import scala.collection.JavaConversions._
import org.scale7.cassandra.pelops.pool.CommonsBackedPool

class Database(val cluster: Cluster, val keyspaceName: String, 
    policy: CommonsBackedPool.Policy = null,
    operandPolicy: OperandPolicy = new OperandPolicy()) {
  
  Pelops.addPool("pool", cluster, keyspaceDef.name, 
      policy, 
      operandPolicy)

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