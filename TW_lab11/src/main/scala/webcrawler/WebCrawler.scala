package webcrawler

import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object WebCrawler extends App {
  import scala.io.Source
  import org.htmlcleaner.HtmlCleaner
  import java.net.URL
  import java.io._

  println("Give web address")
  val url = scala.io.StdIn.readLine()
  //val url = "http://google.com"

  println("Give search deep lvl as int:")
  val searchDeepLvl = scala.io.StdIn.readInt()

  val cleaner = new HtmlCleaner


  val links = crawler(searchDeepLvl,Array(url).toList,1000)
  Await.result(links,1000.second)

  val end = scala.io.StdIn.readLine()

  def crawler(lvl:Int,urls:List[String],maxWait:Int):Future[Unit] = Future{

    if(lvl >= 0){
      urls foreach { url =>
        val result1 = asyncWebDownload(url)

        result1 onComplete {
          case Success(value) =>
            asyncWebSerializer("results\\"+lvl+"_"+randomAlpha(7)+".html",value)

          case Failure(e)     => e.printStackTrace()
        }

        val result2 = asyncWebClean(url)

        result2 onComplete {
          case Success(value) =>
            val _ = crawler(lvl-1,value,maxWait)
          case Failure(e)     => e.printStackTrace()
        }

      }
    }
  }

  def asyncWebDownload(url: String): Future[String] = Future {
    println("Downloading "+url+" ...")
    val htmlSource = Source.fromURL(url)
    htmlSource.mkString
  }

  def asyncWebSerializer(fName:String,htmlString: String):Unit = {
    println("Saving "+fName+" ...")
    val file = new File(fName)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(htmlString)
    bw.close()
  }

  def asyncWebClean(url: String): Future[List[String]] = Future {
    println("Extracting links from "+url+" ...")
    val rootNode = cleaner.clean(new URL(url))
    val elements = Option(rootNode.getElementsByName("a", true)).getOrElse(Array())

    val linkList = elements map { elem =>
      var urlFound = Option(elem.getAttributeByName("href")).getOrElse("")
      if(!urlFound.matches("http.*")){
        urlFound=url+urlFound
      }
      urlFound
    }
    linkList.toList
  }

  def randomAlpha(length: Int): String = {
    val chars = ('a' to 'z') ++ ('A' to 'Z')
    randomStringFromCharList(length, chars)
  }

  def randomStringFromCharList(length: Int, chars: Seq[Char]): String = {
    val sb = new StringBuilder
    for (i <- 1 to length) {
      val randomNum = util.Random.nextInt(chars.length)
      sb.append(chars(randomNum))
    }
    sb.toString
  }
}
