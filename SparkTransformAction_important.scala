1.map&mapValues
map原RDD中的元素经map处理后只能生成一个元素
val a = sc.parallelize(List("dog", "tiger", "lion", "cat", "panther", " eagle"), 2)//2为设置的task数量,可以加速
val b = a.map(x => (x.length, x))//ab映射(2,ab)  一个元素映射为一个元组元素
b.mapValues("x" + _ + "x").collect//(2,ab)=>(2,xabx) 仅仅对value做映射 collect为action操作 得到返回值

2.flatMap
原RDD中的元素经flatmap处理后可生成多个元素来构建新RDD。 
scala> val a = sc.parallelize(1 to 4, 2)
scala> val b = a.flatMap(x => 1 to x)//比如将3会映射为1,2,3 一个元素变成多个元素
scala> b.collect
res12: Array[Int] = Array(1, 1, 2, 1, 2, 3, 1, 2, 3, 4)//(1) (1,2) (1,2,3) (1,2,3,4)

3.mappartion
val mappartionaa = sc.parallelize(1 to 9, 3)//分了三个区
def myfunc[T](iter: Iterator[T]) : Iterator[(T, T)] = {
      var res = List[(T, T)]()
      var pre = iter.next()//初始化 得到第一个
      while (iter.hasNext) {
        val cur = iter.next()
        res .::= (pre, cur)// res .::为拼接元素  (pre, cur)
        pre = cur
      }
      res.iterator//转换成迭代类型
    }
println("mappartion")
mappartionaa.mapPartitions(myfunc).collect().foreach(println)//以每个分区为一个map
上述例子中的函数myfunc是把分区中一个元素和它的下一个元素组成一个Tuple。因为分区中最后一个元素没有下一个元素了，所以(3,4)和(6,7)不在结果中。
mapPartitions还有些变种，比如mapPartitionsWithContext，它能把处理过程中的一些状态信息传递给用户指定的输入函数。还有mapPartitionsWithIndex，它能把分区的index传递给用户指定的输入函数。

4.reduce
reduce将RDD中元素两两传递给输入函数，同时产生一个新的值，新产生的值与RDD中下一个元素再被传递给输入函数直到最后只有一个值为止。
scala> val c = sc.parallelize(1 to 10)
scala> c.reduce((x, y) => x + y)
res4: Int = 55

5reduceByKey
顾名思义，reduceByKey就是对元素为KV对的RDD中Key相同的元素的Value进行reduce，因此，Key相同的多个元素的值被reduce为一个值，然后与原RDD中的Key组成一个新的KV对。

scala> val a = sc.parallelize(List((1,2),(3,4),(3,6)))
scala> a.reduceByKey((x,y) => x + y).collect
res7: Array[(Int, Int)] = Array((1,2), (3,10))

5
num.reduce (_ + _)#num Array[Int] = Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
num.take(5)#选择前5个数字
num.first#选择第一个元素
num.count#数据集元素个数
num.take(5).foreach(println)#前面5元素打印出来

6K-V演示  groupByKey  sortByKey  reduceByKey
#先创立KV类型的RDD
val kv1=sc.parallelize(List(("A",1),("B",2),("C",3),("A",4),("B",5)))
(1)kv1.sortByKey().collect //注意sortByKey的小括号不能省
#结果 Array((A,1), (A,4), (B,2), (B,5), (C,3))
(2)kv1.groupByKey().collect #不作合并  类似于hadoop中reduce的输入端
#Array((A,CompactBuffer(1, 4)), (B,CompactBuffer(2, 5)), (C,CompactBuffer(3)))
(3)kv1.reduceByKey(_+_).collect#做合并
Array[(String, Int)] = Array((A,5), (B,7), (C,3))

7去重
val kv2=sc.parallelize(List(("A",4),("A",4),("C",3),("A",4),("B",5)))
kv2.distinct.collect#去重
#Array[(String, Int)] = Array((A,4), (B,5), (C,3))
#如果val kv2=sc.parallelize(List(("A",4),("A",3),("C",3),("A",4),("B",5)))
#则： Array((A,4), (B,5), (A,3), (C,3))

8union   是a与b纵向拼接
kv1.union(kv2).collect  #联合两个集合
#kv1   Array[(String, Int)] = Array((A,1), (B,2), (C,3), (A,4), (B,5))
#kv2   Array[(String, Int)] = Array((A,4), (A,4), (C,3), (A,4), (B,5))
#结果不会去重,完全拼接  Array[(String, Int)] = Array((A,1), (B,2), (C,3), (A,4), (B,5), (A,4), (A,4), (C,3), (A,4), (B,5))

9join    是默认按照key横向拼接,类似数据库中多表联结查询
//join演示  类似结果的拼接
val format = new java.text.SimpleDateFormat("yyyy-MM-dd")
case class Register (d: java.util.Date, uuid: String, cust_id: String, lat: Float,lng: Float)
case class Click (d: java.util.Date, uuid: String, landing_page: Int)
val reg = sc.textFile("F:/HDFSinputfile//reg.tsv").map(_.split("\t")).map(r => (r(1), Register(format.parse(r(0)), r(1), r(2), r(3).toFloat, r(4).toFloat)))
reg.foreach(println)
val clk = sc.textFile("F:/HDFSinputfile/clk.tsv").map(_.split("\t")).map(c => (c(1), Click(format.parse(c(0)), c(1), c(2).trim.toInt)))
clk.foreach(println)
println("test join")
reg.join(clk).take(2).foreach(println)

#结果
reg
(15dfb8e6cc4111e3a5bb600308919594,Register(Sun Mar 02 00:00:00 CST 2014,15dfb8e6cc4111e3a5bb600308919594,1,33.659943,-117.95812))
(81da510acc4111e387f3600308919594,Register(Tue Mar 04 00:00:00 CST 2014,81da510acc4111e387f3600308919594,2,33.85701,-117.85574))
clk
(15dfb8e6cc4111e3a5bb600308919594,Click(Tue Mar 04 00:00:00 CST 2014,15dfb8e6cc4111e3a5bb600308919594,11))
(81da510acc4111e387f3600308919594,Click(Thu Mar 06 00:00:00 CST 2014,81da510acc4111e387f3600308919594,61))
test join
(81da510acc4111e387f3600308919594,(Register(Tue Mar 04 00:00:00 CST 2014,81da510acc4111e387f3600308919594,2,33.85701,-117.85574),Click(Thu Mar 06 00:00:00 CST 2014,81da510acc4111e387f3600308919594,61)))
(15dfb8e6cc4111e3a5bb600308919594,(Register(Sun Mar 02 00:00:00 CST 2014,15dfb8e6cc4111e3a5bb600308919594,1,33.659943,-117.95812),Click(Tue Mar 04 00:00:00 CST 2014,15dfb8e6cc4111e3a5bb600308919594,11)))