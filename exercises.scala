
import scala.io.StdIn._


///////////////////   RECURSION  //////////////////////////////

def sum(n:Int):Int = {
  if ( n < 1 ) {
    0
  } else {
    readInt + sum(n-1)
  }
}

//
//println(sum(5))
//

def sumPositive():Int = {
  val input = readInt
  if (input >= 0) {
    input + sumPositive()
  } else {
    0
  }
}

//
//println(sumPositive())
//

def sumUntilQuit():Int = {
  val input = readLine.toLowerCase.trim
  if (input == "quit") {
    0
  } else {
    input.toInt + sumUntilQuit()
  }
}

//
//println(sumUntilQuit())
//

def sumAndCount():(Int,Int) = {
  val input = readLine.toLowerCase.trim
  if (input == "quit") {
    (0,0)
  } else {
    val (sum, count) = sumAndCount()
    (input.toInt+sum, count+1)
  }
}

//
//println(sumAndCount())
//



def multAndCount():(Int,Int) = {
  val input = readLine.toLowerCase.trim
  if (input == "quit") {
    (0,0)
  } else {
    val (prod, count) = multAndCount()
    (input.toInt*prod, count+1)
  }
}

//
//println(multAndCount())
//


def inputAndCount(base:Int, op:(Int,Int) => Int):(Int,Int) = {
  print("Enter a value (or quit): ")
  val input = readLine.toLowerCase.trim
  if (input == "quit") {
    (base,0)
  } else {
    val (value, count) = inputAndCount(base, op)
    (op(input.toInt,value), count+1)
  }
}

/*
val (s,c) = inputAndCount(1,_*_)
println(s+" "+c)
*/

///////////////////   MATCH  //////////////////////////////

/*
 
expr match {
  case pattern1 => ...
  case pattern2 => ...
  case pattern3 => ...
  ...
}

*/


def factMatch(n:Int):Int = n match {
  case 0 => 1
  case _ => n*factMatch(n-1)
}

//
//println(factMatch(5))
//

def squareMatch(n:Int):Int = n match {
  case 0 => 1
  case _ => n*n + squareMatch(n-1)
}

//
//println(squareMatch(6))
//

/*
readInt match {
  case 1 => println("It's a 1")
  case 2 => println("It's a 2")
  case 3 => println("It's a 3")
  case n => println("It's a " + n)
}
*/

def fizzBuzz(i:Int):Unit = {
  if (i <= 100) {
    (i%3, i%5) match {
      case (0,0) => println("FizzBuzz")
      case (0,_) => println("Fizz")
      case (_,0) => println("Buzz")
      case _ => println(i)
    }
    fizzBuzz(i+1)
  }
}

//
//fizzBuzz(1)
//

def safeReadInt:Int = {
  try {
    readInt
  } catch {
    case e:NumberFormatException => println("Not valid. Try Again")
    safeReadInt
  }
}

//
//safeReadInt
//


///////////////////   ARRAYS   //////////////////////////////


val myArray = Array[Int](2,3,4,5,6)


def fillArray(n:Array[Int],v:Int,i:Int):Unit = {
  if (i < n.length) {
    n(i) = v
    fillArray(n,v,i+1)
  }
}

//
//fillArray(myArray, 99, 0)
//

def operateOnArray(n:Array[Int], i:Int, f:(Int,Int) => Int):Int = {
  if (i < n.length -1) {
    f(n(i), operateOnArray(n,i+1,f))
  } else {
    n(i) 
  }
}

//
//println(operateOnArray(myArray,0,_+_))
//

///////////////////   LISTS   //////////////////////////////


def inputList(n:Int):List[Int] = {
  if (n < 1) {
    Nil
  } else {
    print("Enter value[" + n + "]: ")
    readInt :: inputList(n-1)
  }
}

def fillList(n:Int, v:Double):List[Double] = {
  if (n<1) {
    Nil
  } else {
    v :: fillList(n-1,v)
  }
}

//
//val lst = inputList(5)
//println(lst)
//

def operateOnList(n:List[Int],i:Int,f:(Int,Int) => Int):Int = {
  if (n == Nil) {
    i
  } else {
    f(n.head,operateOnList(n.tail,i,f))
  }
}

/*
val lst = inputList(5)
println(operateOnList(lst,0,_+_))
println(lst)
*/


val lst1 = List.tabulate(10)(i => i*i)
val lst2 = List.fill(10)(0)

//
//println(lst1)
//println(lst2)
//

val a = List(5,3,4,6,7,8,7,9)
val b = Array(4,6,8,1,6,8,9)

println(a)
println(b)
println("Drop Methond: " + a.drop(2))
println("Head Method: " + a.head)
println("Tail Method: " + a.tail)
println("Last Method: " + a.last)
println("Slice Method: " + a.slice(2,4))
println("SplitAt Method: " + a.splitAt(3))
println("Take Method: " + a.take(3))
println("DropRight Method: " + a.dropRight(3))
println("TakeRight Method: " + a.takeRight(3))
println("Contains MEthod: " + a.contains(99))
println("EndsWith Method: " + a.endsWith(List(8,9)))
println("IsEmpty Method: " + a.isEmpty)
println("NonEmpty Method: " + a.nonEmpty)
println("StartssWith Method: " + a.startsWith(List(5,3)))
println("IndexOf Method: " + a.indexOf(4))
println("LastIndexOf Method: " + a.lastIndexOf(7))
println("Diff Method: " + a.diff(List(3,6,7,5)))
println("Distinct Method: " + a.distinct)
println("MkString Method: " + b.mkString(","))
println("Patch Method: " + a.patch(2, Nil, 3))
println("Reverse Method: " + a.reverse)
println("ToArray: " + a.toArray)
println("Zip Method: " + a.zip(b))
println("ZipWithIndex: " + a.zipWithIndex)
println("ZipWithIndex: " + b.zipWithIndex.mkString(","))
