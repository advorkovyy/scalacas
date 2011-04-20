package org.scalastuff.scalacas
package integration

object TestIndex extends IndexColumnFamily(TestDatabase.keyspace, "TestIndex") 