package org.scalastuff.scalacas.beans

import scala.reflect.BeanProperty
import org.scalastuff.scalacas.{ Mapper, HasId, HasIdSupport }
import org.scale7.cassandra.pelops.{ Mutator, Bytes }
import org.apache.cassandra.thrift.Column
import org.specs.SpecificationWithJUnit
import org.junit.runner.RunWith
import org.specs.runner.JUnitSuiteRunner

class ReflectionTest extends HasId {
  var i: Int = 1
  var i2: Integer = 2
  var s: String = "whatever"
  var l: Long = 3
  var bd: BigDecimal = 2.33
  def id = i.toString
}

class ReflectionTestBean extends HasId {
  @BeanProperty
  var i: Int = 1
  @BeanProperty
  var i2: Integer = 2
  @BeanProperty
  var s: String = "whatever"
  @BeanProperty
  var l: Long = 3
  @BeanProperty
  var bd: java.math.BigDecimal = new java.math.BigDecimal("2.33")
  def id = i.toString
}

@RunWith(classOf[JUnitSuiteRunner])
class ReflectionPerformanceTest extends SpecificationWithJUnit {

  "test mapping performance" in {
    
    val mapper = new Mapper[ReflectionTest]("") with HasIdSupport[ReflectionTest] {
      def objectToColumns(mutator: Mutator, obj: ReflectionTest): Seq[Column] = throw new RuntimeException
      def columnsToObject(subColumns: Seq[Column]): ReflectionTest = {
        val result = new ReflectionTest
        for (col <- subColumns) new Bytes(col.getName).toUTF8 match {
          case "i" => result.i = new Bytes(col.getValue).toInt
          case "i2" => result.i2 = new Bytes(col.getValue).toInt
          case "s" => result.s = new Bytes(col.getValue).toUTF8
          case "l" => result.l = new Bytes(col.getValue).toLong
          case "bd" => result.bd = BigDecimal(new Bytes(col.getValue).toUTF8)
          case _ => // ignore
        }
        result
      }
    }
    val protoMapper = new ProtobufMapper[ReflectionTest]("")

    val rt = new ReflectionTest
    rt.i = 10
    rt.i2 = 20
    rt.s = "xxx aaa7"
    rt.l = 30
    rt.bd = 45.78

//    val cmutator = new Mutator(null, 0, false)
//    val cscl = mapper.objectToColumns(cmutator, rt)
//    cscl.size must be(5)

    val pmutator = new Mutator(null, 0, false)
    val pscl = protoMapper.objectToColumns(pmutator, rt)

    val obj = protoMapper.columnsToObject(pscl)
    obj.i must be equalTo (rt.i)
    obj.i2 must be equalTo (rt.i2)
    obj.s must be equalTo (rt.s)
    obj.l must be equalTo (rt.l)
    obj.bd must be equalTo (rt.bd)

    for (i <- 1 to 5) {
//	    testMapperPerformance("custom mapper", mapper, cscl)
	    testMapperPerformance("proto mapper", protoMapper, pscl)
    }
  }

  private def testMapperPerformance[A <: AnyRef](mapperName: String, mapper: Mapper[A], scl: Seq[Column]) {
    System.gc()
    val start = System.nanoTime
    for (i <- 1 to 1000000) {
      //    val scl = mapper.toSubColumnsList(mutator, rt)
      val obj = mapper.columnsToObject(scl)
    }

    val end = System.nanoTime
    println("Mapping columns to object with %s 1000000 times: %d ms".format(mapperName, (end - start) / 1000000))
  }
}