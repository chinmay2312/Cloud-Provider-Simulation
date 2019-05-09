package com.uic.cs441.project.generator

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.core.{CloudSim, Simulation}
import org.cloudbus.cloudsim.datacenters._
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudbus.cloudsim.network.switches.EdgeSwitch
import org.cloudbus.cloudsim.provisioners.{PeProvisionerSimple, ResourceProvisionerSimple}
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler

import scala.collection.JavaConverters._

object Generator {

  def createHost(ram: Int, bw: Long, storage: Long, pes: Int, mips: Int, vmScheduler: VmScheduler) = {
    new NetworkHost(ram, bw, storage, createPes(pes, mips).asJava)
      .setRamProvisioner(new ResourceProvisionerSimple())
      .setBwProvisioner(new ResourceProvisionerSimple())
      .setVmScheduler(vmScheduler)
  }

  def createPes(pes: Int, mips: Int): List[Pe] = {
    for (_ <- List.range(1, pes))
      yield new PeSimple(mips, PeProvisionerSimple)
  }

  def createDataCenter(simulation: Simulation, vmAllocationPolicy: VmAllocationPolicy, hostCount: Int,
                       ram: Int,
                       bw: Long,
                       storage: Long,
                       pes: Int, mips: Int,
                       vmScheduler: VmScheduler): Datacenter = {
    new NetworkDatacenter(simulation,
      (for (_ <- List.range(1, hostCount)) yield createHost(ram, bw, storage, pes, mips, vmScheduler)).asJava,
      vmAllocationPolicy)
    //TODO new Datacenter.setSchedulingInterval(2)
  }

  def createDataCenterNetwork(simulation: CloudSim, datacenter: NetworkDatacenter) = {
    val edgeSwitch: EdgeSwitch = new EdgeSwitch(simulation, datacenter)
    datacenter.addSwitch(edgeSwitch)
    datacenter.getHostList.asScala.foreach(netHost => {
      edgeSwitch.connectHost(netHost)
    })
  }

  def createDataCenterBroker(simulation: CloudSim) = {
    new DatacenterBrokerSimple(simulation)
  }

  def createVM() = {

  }

  def createCloudlet() = {

  }

  def createNetwork() = {

  }
}
