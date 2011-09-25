package net.godcode.hazelbox

import com.hazelcast.core._
import com.hazelcast.client.HazelcastClient
import com.mongodb.casbah.Imports.ObjectId

object App {
  
  val (erin, chris) = (
    Person(firstName = "Erin"),
    Person(firstName = "Chris")
  )
  
  def main(args: Array[String]) {
    val config = Hazelcast.getConfig()
    val (c1, c2) = (
      Hazelcast.newHazelcastInstance(config),
      Hazelcast.newHazelcastInstance(config)
    )
    
    val (map1, map2) = (
      c1.getMap[ObjectId, Person]("default"),
      c2.getMap[ObjectId, Person]("default")
    )
    
    assert(map1.get(erin._id) == null)
    assert(map2.get(erin._id) == null)
    
    map1.put(erin._id, erin)
    
    assert(map1.get(erin._id) == erin)
    assert(map2.get(erin._id) == erin)
    
    assert(map1.get(chris._id) == null)
    assert(map2.get(chris._id) == null)
    
    map2.put(chris._id, chris)
    
    assert(map1.get(chris._id) == chris)
    assert(map2.get(chris._id) == chris)
    
    println("Goodbye.")
    Hazelcast.shutdownAll()
  }
}
