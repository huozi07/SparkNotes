package scalastudy

import scala.io.Source
import java.io.PrintWriter
import java.io._//File对象

object ReadFile {

  class ReadIO{
    def ReadFromLocal(Dir:String): Unit ={
      var file=Source.fromFile(Dir)
      file.foreach(print)//函数可以作为参数传入
    }

    def OutputFile(OutDir:String): Unit ={
      val file=Source.fromURL("http://spark.apache.org/")
      val writer=new PrintWriter(new File(OutDir))
      for(line<-file.getLines()){//每行读取
           writer.println(line)
      }
      writer.close()
    }

  }

  def main (args: Array[String]){
    val file=Source.fromFile("F:\\HDFSinputfile\\words.txt")
    file.foreach(print)//函数可以作为参数传入
    println("———test for class——")
    val Dir="F:\\HDFSinputfile\\words.txt"
    var readio=new ReadIO
    readio.ReadFromLocal(Dir)
    readio.OutputFile("F:\\HDFSinputfile\\readfromurl")

  }


}
