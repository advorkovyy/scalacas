package org.scalastuff.scalacas
package integration

import org.scale7.cassandra.pelops.Cluster

object TestDatabase {
  val cluster = new Cluster("localhost", 9160);

  val keyspace = new Database(cluster, "Test")
}