package com.uic.cs441.project.generator

import org.cloudbus.cloudsim.hosts.Host
import org.cloudbus.cloudsim.hosts.network.NetworkHost
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

  def createDataCenter(hostCount: Int,
                       ram: Int,
                       bw: Long,
                       storage: Long,
                       pes: Int, mips: Int,
                       vmScheduler: VmScheduler): List[Host] = {
    for (_ <- List.range(1, hostCount)) yield createHost(ram, bw, storage, pes, mips, vmScheduler)
  }

  def createDataCenterBroker() = {

  }

  def createVM() = {

  }

  def createCloudlet() = {

  }

  def createNetwork() = {

  }
}
