package net.godcode.hazelbox.wwpeek

import net.godcode.hazelbox.config._

object Grid {
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
    maps = MapConfig() :: Nil
  )
}