package net.godcode.hazelbox

import net.godcode.hazelbox.config._
import com.hazelcast.core.Hazelcast
import com.mongodb.casbah.Imports.ObjectId

import com.hazelcast.query.{Predicate, PredicateBuilder, EntryObject}

object App {
  
  val (erin, chris) = (
    Person(firstName = "Erin"),
    Person(firstName = "Chris")
  )
  
  val config = Hazel(
    group = "dev" -> "dev-pass",
    network = NetworkConfig(
      port = 5000,
      auto = true,
      network = TcpIp(
        hosts = "localhost" :: Nil,
        interfaces = "127.0.0.1" :: Nil
      )
    ),
    maps = MapConfig(
      store = Some(MapStore(
        implementation = Some(new MongoLoader()))),
      nearCache = Some(NearCache())
    ) :: Nil
  )
  
  def main(args: Array[String]) {
    //Hazelcast.init(config)
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
    
    // raw criteria query example
    val e = new PredicateBuilder().getEntryObject
    val predicate = e.get("firstName").equal("Chris")
    assert(map1.values(predicate).isEmpty == false)
    
    println("Goodbye.")
    Hazelcast.shutdownAll()
  }
}
