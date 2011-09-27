package net.godcode.hazelbox.config

case class MapStore[A](
  implementation: Option[A] = None
)

/* Eviction policies. */
sealed abstract class EvictionPolicy(val name: String)
case object LRU extends EvictionPolicy("LRU")
case object LFU extends EvictionPolicy("LFU")

/* Merge policies. */
sealed abstract class MergePolicy(val name: String)
case object ADD_NEW_ENTRY extends MergePolicy("ADD_NEW_ENTRY")
case object HIGHER_HITS extends MergePolicy("HIGHER_HITS")
case object LATEST_UPDATE extends MergePolicy("LATEST_UPDATE")

//http://www.hazelcast.com/docs/1.9.4/manual/multi_html/ch02s03.html#MapNearCache
case class NearCache(
  maxSize: Int = 0,
  ttl: Int = 0,
  maxIdle: Int = 60,
  evictionPolicy: Option[EvictionPolicy] = None,
  invalidateOnChange: Boolean = true)

case class MapConfig[A](
  store: Option[MapStore[A]] = None,
  name: String = "default",
  backups: Int = 1,
  ttl: Int = 0,
  maxIdle: Int = 0,
  evictionPolicy: Option[EvictionPolicy] = None,
  maxSize: Int = 0,
  evictionPercentage: Int = 25,
  mergePolicy: Option[MergePolicy] = None,
  nearCache: Option[NearCache] = None)

sealed trait Network
case class TcpIp(hosts: List[String], interfaces: List[String]) extends Network

case class NetworkConfig(port: Int, auto: Boolean = true, network: Network)

/* Simple builder object. Not quite a DSL, but close enough. */
object Hazel {
  import com.hazelcast.config.{Config, MapConfig => HMapConfig, MapStoreConfig, NearCacheConfig}
  
  def apply[A](
    group: (String, String),
    network: NetworkConfig,
    maps: Seq[MapConfig[A]]
  ) = {
    val cfg = new Config
    cfg.setPort(network.port)
    cfg.setPortAutoIncrement(network.auto)
    
    val netCfg = cfg.getNetworkConfig()
    val join = netCfg.getJoin()
    network.network match {
      case TcpIp(hosts, interfaces) =>
        join.getMulticastConfig().setEnabled(false)
        val tcpIp = join.getTcpIpConfig().setEnabled(true)
        hosts.foreach(tcpIp.addMember)
        val ifaces = netCfg.getInterfaces().setEnabled(true)
        interfaces.foreach(ifaces.addInterface)
    }
    
    maps.map(mm).foreach(cfg.addMapConfig)
    
    cfg
  }
  
  def mm[A](map: MapConfig[A]) = {
    val mapCfg = new HMapConfig()
    mapCfg.setName(map.name)
      .setBackupCount(map.backups)
      .setTimeToLiveSeconds(map.ttl)
      .setMaxIdleSeconds(map.maxIdle)
      .setEvictionPolicy(map.evictionPolicy.map(_.name).getOrElse("NONE"))
      .setMaxSize(map.maxSize) // TODO policy
      .setEvictionPercentage(map.evictionPercentage)
      .setMergePolicy(map.mergePolicy.map(_.name).getOrElse("NONE"))
      
    map.store.foreach { s =>
      val storeCfg = new MapStoreConfig().setEnabled(true)
      s.implementation.foreach(storeCfg.setImplementation)
      mapCfg.setMapStoreConfig(storeCfg)
    }
    
    map.store.map {
      new MapStoreConfig().setEnabled(true) -> _.implementation
    } map {
      case (cfg, impl) =>
        impl.foreach(cfg.setImplementation)
        cfg
    } foreach(mapCfg.setMapStoreConfig)
    
    map.nearCache.map { nc =>
      new NearCacheConfig()
        .setEvictionPolicy(nc.evictionPolicy.map(_.name).getOrElse("NONE"))
        .setInvalidateOnChange(nc.invalidateOnChange)
        .setMaxIdleSeconds(nc.maxIdle)
        .setMaxSize(nc.maxSize)
        .setTimeToLiveSeconds(nc.ttl)
    } foreach(mapCfg.setNearCacheConfig)
    
    mapCfg
  }
}
