object magic {

  def main (args: Array[String]){

    //列表添加元素
    val b=("1","2")
    val c=("3","4")
    val a = List[(String,String)]()
    val he=b :: c :: a
    val he2= a :+ b :+c
<p style="margin-top: 0px; margin-bottom: 1.1em; padding-top: 0px; padding-bottom: 0px; box-sizing: border-box; font-family: 'microsoft yahei'; font-size: 14.44444465637207px; line-height: 25.98958396911621px;">      // +:(elem: A): List[A] 在列表的头部添加一个元素   <span style="font-size: 14.44444465637207px; line-height: 25.98958396911621px;">:+(elem: A): List[A] 在列表的尾部添加一个元素</span></p>
    println("he:  "+he)
    println("he2:  "+he2)

    //list map
    val aa = List(("a1", "a2","a2"),("a3", "a4","a2"),("a9", "a10","a1"),("a9", "a10","a3"),("a9", "a10","a2"))
    val bb = List.empty[(String,String,Int)]//对应base  初始值
    var count=0
    //scala list尽量不用可变类型，随着list增长，耗时较长。
    //用foldleft重构出list
    val cc = aa.foldLeft(bb)((base, plus) => {
      var hebing=plus._1+plus._2//也可以作转换
      count+=1//可以传计数器
      (hebing,plus._3,count) :: base
    })
    //未排序
    println("cc:  "+cc)
    println("排序输出")
    //有sortby  对元组有sortbykey，sortbyvalue
    //自定义多值排序 先按第一值排序，第一个值相同再按第二个值排序
    cc.sortWith {
      case (user1, user2) =>
        if(user1._1!=user2._2) user1._1>user2._1
        else user1._2>user2._2
    }.foreach(println)//排序输出元素

  }
}
