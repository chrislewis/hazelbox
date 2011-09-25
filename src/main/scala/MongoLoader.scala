package net.godcode.hazelbox

import com.hazelcast.core.{MapLoader, MapStore}
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._
import java.{util => ju}

class MongoLoader extends MapLoader[ObjectId, Person] with MapStore[ObjectId, Person] {
  
  val collection = MongoConnection("localhost")("hazelcast_mongo")("people")
  
  /* MapLoader */
  
  def loadAllKeys = new ju.HashSet[ObjectId]()
  
  def loadAll(keys: ju.Collection[ObjectId]) = {
    val m = new ju.HashMap[ObjectId, Person]()
    val it = keys.iterator
    while(it.hasNext) {
      val key = it.next
      m.put(key, load(key))
    }
    m
  }
  
  def load(key: ObjectId) = {
    val one = collection.findOne(MongoDBObject("_id" -> key))
    one.map(grater[Person].asObject(_)).getOrElse(null) // java, ftl
  }
  
  /* MapStore */
  
  def deleteAll(keys: ju.Collection[ObjectId]) {
    val it = keys.iterator
    while(it.hasNext)
      delete(it.next)
  }
  
  def delete(key: ObjectId) {
    collection.remove(MongoDBObject("_id" -> key))
  }
  
  def storeAll(map: ju.Map[ObjectId, Person]) {
    val it = map.entrySet.iterator
    while(it.hasNext) {
      val es = it.next
      store(es.getKey, es.getValue)
    }
  }
  
  def store(key: ObjectId, value: Person) {
    val dbo = grater[Person].asDBObject(value)
    collection.insert(dbo)
  }
}
