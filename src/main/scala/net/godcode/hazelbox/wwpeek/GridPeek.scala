package net.godcode.hazelbox.wwpeek

import net.godcode.hazelbox.config._

import com.hazelcast.core.Hazelcast

object GridPeek {
  def main(args: Array[String]) {
    val hazel = Hazelcast.newHazelcastInstance(Grid.config)
    unfiltered.jetty.Http(9090)
      .resources(this.getClass.getResource("/"))
      .filter(new GridPeekPlan(hazel))
      .run({ _ => () }, { _ => Hazelcast.shutdownAll() })
  }
}