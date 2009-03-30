import java.util.Random
import java.util.ArrayList
import compat.Platform
import io.Source
import org.apache.commons.httpclient._
import org.apache.commons.httpclient.methods._

object HTTP {
    def get(url: String) : Array[Byte] = {
        var client : HttpClient = new HttpClient()
        var method : GetMethod = new GetMethod(url)
        method.addRequestHeader("Accept-Encoding", "gzip")
        var statusCode = client.executeMethod(method)
        if (statusCode != HttpStatus.SC_OK) {
            println("Method failed: " + method.getStatusLine())
        }
        
//        println(method.getResponseContentLength)
        var byte : Array[Byte] = method.getResponseBody()
        return byte
    }
}

object Time {

    def apply[T](action : => T ) : (T,Long) = { 
        val startTime = Platform.currentTime
        val res = action
        val stopTime = Platform.currentTime
        (res,stopTime - startTime)
    }

    def elapsed(action : => AnyRef) : Long = {
        val startTime = Platform.currentTime
        action
        val stopTime = Platform.currentTime
        stopTime - startTime
    }
}

object Repeat {
    def repeat[T](n:Int)(what: => T): List[T] = {
        var items : List[T] = List()
        var i = 1  
        while(i <= n) {
            what
            items =  what ::items 
            i = i + 1
        }
        items
    }

    def parallel[T](n: Int)(what: => T): List[T] =
        repeat(n)(scala.actors.Futures.future(what)).map(_.apply)

}

object sb {

    def main(args: Array[String]) {
        var urls : ArrayList[String] = new ArrayList()
        for {
            (line) <- Source.fromFile("urls.txt").getLines
        } urls.add(line)

        val rand = new Random(System.currentTimeMillis())

        def r = { 
            var url = urls.get(rand.nextInt(urls.size))
            Time.elapsed(HTTP.get(url))
        }
        for(i <- 1 to 3) {
            val (latencies, ms) = Time(Repeat.parallel(10)(r))
            println(latencies,ms)
        } 
        println("end")
    }
}
