package org.scalacas.reflection

import scala.reflect.BeanProperty
import ScalaReflection._
import org.scalacas.{ Mapper, HasId, HasIdSupport }
import org.scalacas.json.JsonMapper
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

@RunWith(classOf[JUnitSuiteRunner])
class ReflectionPerformanceTest extends SpecificationWithJUnit {

  "test reflection performance" in {
    val rt = new ReflectionTest
    val properties = rt.getClass.properties filter (!_.isReadOnly)
    val start = System.nanoTime
    for (i <- 1 to 1000000; p <- properties) {
      val value = p.get[AnyRef](rt)
      //			p.set(rt, value)
      //			val buf = Bytes.fromUTF8("")
      //			buf.toUTF8
    }

    println("get via reflection 1M times: %d ms".format((System.nanoTime - start) / 1000000))
  }
  
  "test mapping performance" in {
    val rt = new ReflectionTest
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
    val reflectionMapper = new ReflectionMapper[ReflectionTest]("", new ReflectionTest)
    val polyMapper = new PolymorphicReflectionMapper[ReflectionTest]("")

    rt.i = 10
    rt.i2 = 20
    rt.s = "xxx aaa7"
    rt.l = 30
    rt.bd = 45.78

    val mutator = new Mutator(null, 0, false)
    val scl = polyMapper.objectToColumns(mutator, rt)
    scl.size must be(6)

    val obj = reflectionMapper.columnsToObject(scl)
    obj.i must be equalTo (rt.i)
    obj.i2 must be equalTo (rt.i2)
    obj.s must be equalTo (rt.s)
    obj.l must be equalTo (rt.l)
    obj.bd must be equalTo (rt.bd)
    
    val rtb = new ReflectionTest
    val jsonMapper = new JsonMapper[ReflectionTest]("")
	   
	  val jmutator = new Mutator(null, 0, false)
      val sclj = jsonMapper.objectToColumns(jmutator, rtb)

    testMapperPerformance("custom mapper", mapper, scl)
    testMapperPerformance("reflection mapper", reflectionMapper, scl)
    testMapperPerformance("polymorphic mapper", polyMapper, scl)
    testMapperPerformance("JSON mapper", jsonMapper, sclj)

    testMapperPerformance("reflection mapper", reflectionMapper, scl)
    testMapperPerformance("polymorphic mapper", polyMapper, scl)
    testMapperPerformance("custom mapper", mapper, scl)
    testMapperPerformance("JSON mapper", jsonMapper, sclj)

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