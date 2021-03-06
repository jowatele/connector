package connector.core

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import connector.api.{TweetReaderActor, ApplicationActor}
import spray.can.Http
import scala.concurrent.duration._


object Boot extends App {

  implicit val system = ActorSystem(Config.actorSystemName)
  implicit val timeout = Timeout(15.seconds)

  val application = system.actorOf(Props[ApplicationActor], "connector-service")
  val readTweets = system.actorOf(Props(new TweetReaderActor(CassandraCluster.cluster)))

  IO(Http) ? Http.Bind (
    listener = application,
    interface = Config.interface,
    port = Config.port
  )
}

object Config {

  private val config = ConfigFactory.load()

  lazy val interface = config.getString("app.server.host")
  lazy val port = config.getInt("app.server.port")
  lazy val actorSystemName = config.getString("app.actor-system.name")

  lazy val cassandraPort = config.getInt("app.database.cassandra.port")
  lazy val cassandraHost = config.getString("app.database.cassandra.host")

}


