package SparkSQL

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions._

case class Person(name: String, age: Int)
case class Record(key: Int, value: String)

object SparkSQLTest {
  
  def DFoperation(): Unit ={

    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    val sparkConf = new SparkConf().setAppName("SparkExample").setMaster("local")
    val sc = new SparkContext(sparkConf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    
    val df = sqlContext.read.json("F:/HDFSinputfile/people.json")
    println("df.show")
    df.show()

    println("df.printSchema()")
    df.printSchema()

    println("df.select(\"name\").show()")
    df.select("name").show()

    println("df.select(df(\"name\"), df(\"age\") + 1).show()")
    df.select(df("name"), df("age") + 1).show()

    println("df.filter(df(\"age\") > 21).show()")
    df.filter(df("age") > 21).show()

    println("df.groupBy(\"age\").count().show()")
    df.groupBy("age").count().show()

    sc.stop()
  }

  def main (args: Array[String]){

    //example1()
    DFoperation()
    
  }

}
