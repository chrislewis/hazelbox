package net.godcode.hazelbox.wwpeek

import net.godcode.hazelbox._
import unfiltered.filter.Plan
import unfiltered.request._
import unfiltered.response._

import com.mongodb.casbah.Imports.ObjectId
import com.hazelcast.core.HazelcastInstance

class GridPeekPlan(hazel: HazelcastInstance) extends Plan {
  
  def scalaMap[A, B](jm: java.util.Map[A, B]) =
    scala.collection.JavaConversions.asScalaMap(jm)
  
  val people = scalaMap(hazel.getMap[ObjectId, Person]("default"))
  
  val me = Person(firstName = "Chris")
  people += (me._id -> me)
  
  def intent = {
    case GET(Path("/person")) => Html(
      <ul id="people">{ (List.empty[xml.NodeSeq] /: people) { case (ns, (k, v)) => <li>{v}</li> :: ns } }</ul>
    )
    case POST(Path("/person") & Params(p)) =>
      p("firstName").headOption.map { n =>
        val person = Person(firstName = n)
        people += person._id -> person
        Ok
      }.getOrElse(BadRequest)
  }
}