package LoadTest

import org.apache.log4j.{Logger, Level}
import org.apache.spark.{SparkConf, SparkContext}

object GroupUserInfo {

  def main (args: Array[String]){

    //屏蔽不必要的日志显示在终端上
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    // 设置运行环境
    val conf = new SparkConf().setAppName("GroupUserInfo").setMaster("local")
    val sc = new SparkContext(conf)

    //装载数据集
    val data = sc.textFile("F:/HDFSinputfile/GroupUserInfoData")
    val parsedData = data.map {
      line =>
        val parts = line.toString.split(" ")
        //(parts(27),parts(0),parts(1),parts(2),parts(4))
        (parts(3),(parts(0),parts(1),parts(2),parts(4)))//(key,(value))
    }

    val data2=parsedData.groupByKey()//将数据按用户聚合 结果类似Hadoop的reduce输入端值

    val data3=data2.map {
      lineline =>
      var count=lineline._2.toList.length
      var mlist = List[(String,String,String,Int)]()
      // lineline._2.map(aa=>{(count+=1,mlist :+ aa)})
      val result=lineline._2.foldRight(mlist)((base, plus) => {
        //base为mlist,plus为lineline._逐个遍历出来
        var time=base._1+" "+base._2//将日期与小时两个字段 拼凑成时间一个字段
        (time,base._3,base._4,count)::plus
      })
      result.sortBy(x=>x._1)
      (lineline._1,result)
    }

    val data4=data3.flatMap{
      line=>
        line._2.map{
          line2=>
            line._1+"\t"+line2._1+"\t"+line2._2+"\t"+line2._3+"\t"+line2._4
        }//此处map会迭代返回多个结果
    }.sortBy(x=>x.split("\t")(4).toInt,ascending = false)//按照用户记录数,倒序输出

    println("targetdata")
    data4.saveAsTextFile("F:/HDFSoutputfile/spark/groupuser")//输出数据
    data4.foreach(println)

    println("Oh Yeah successful")

    sc.stop()

  }

}
