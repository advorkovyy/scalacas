package org.scalastuff.scalacas

import org.scale7.cassandra.pelops.Mutator
import org.apache.cassandra.thrift.Column
import org.scale7.cassandra.pelops.Bytes

/**
 * This mapper is useful for building indexes, where each row
 * contains only references in the form of string keys
 */
object StringMapper extends Mapper[String]("") {
  val almostEmpty = Bytes.fromByteArray(Array[Byte](0))
  override val fullPrefix = ""
  def id(ref: String) = ref
  def objectToColumns(mutator: Mutator, ref: String): Seq[Column] = Seq[Column](mutator.newColumn(almostEmpty,  Bytes.EMPTY))
  def columnsToObject(superColumnName: Array[Byte], subColumns: Seq[Column]): String = Bytes.toUTF8(superColumnName)
}