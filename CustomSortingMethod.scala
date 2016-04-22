package LoadTest

import org.apache.log4j.{Logger, Level}
import org.apache.spark.{SparkConf, SparkContext}

object CustomSortBy {

  def main (args: Array[String]){

    //屏蔽不必要的日志显示在终端上
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    // 设置运行环境
    val conf = new SparkConf().setAppName("CustomSortBy").setMaster("local")
    val sc = new SparkContext(conf)

    //装载数据集
    //val data = sc.textFile("F:/HDFSinputfile/customSortBy")
    val data = List((1,4),(4,8),(0,4),(12,8))
    val rdd = sc.parallelize(data)

    implicit val st = new Ordering[Int]{
      override def compare(a:Int,b:Int): Int ={
        a.toString.compare(b.toString)
      }
    }
    println("stage1")
    rdd.sortBy(x=>x._1).collect().foreach(println)

    //stage2
    //val data2 = List("a 1","a 3","a 11","b 2","c 5")//切分字符串方式  自定义排序
    val data2=List(("a",1),("a",3),("a",11),("b",2),("c",5))
    val rdd2 = sc.parallelize(data2)
//    字符串切分后自定义排序
//    implicit val st2 = new Ordering[String]{
//      override def compare(a:String,b:String): Int ={
//        val a1=a.split(" ")(0)
//        val a2=a.split(" ")(1).toInt
//        val b1=b.split(" ")(0)
//        val b2=b.split(" ")(1).toInt
//        if(a1==b1) a2.compare(b2)
//        else a1.compare(b1)
//      }
//    }
    //元组自定义排序
    implicit val st3 = new Ordering[(String,Int)]{
      override def compare(a:(String,Int),b:(String,Int)): Int ={
             if(a._1==b._1) a._2.compare(b._2)
             else a._1.compare(b._1)
      }
    }

    println("stage2")
    rdd2.sortBy(x=>x).collect().foreach(println)

    sc.stop()

  }

}

