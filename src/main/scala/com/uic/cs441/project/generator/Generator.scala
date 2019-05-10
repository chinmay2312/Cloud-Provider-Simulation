package com.uic.cs441.project.generator

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.network.{CloudletExecutionTask, CloudletTask, NetworkCloudlet}
import org.cloudbus.cloudsim.core.{CloudSim, Simulation}
import org.cloudbus.cloudsim.datacenters._
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudbus.cloudsim.network.switches.EdgeSwitch
import org.cloudbus.cloudsim.provisioners.{PeProvisionerSimple, ResourceProvisionerSimple}
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull
import org.cloudbus.cloudsim.vms.network.NetworkVm

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

  def createVM(id: Int, ram: Int, bw: Long, storage: Long, pes: Int, mips: Int, cloudletScheduler: CloudletScheduler) = {
    val vm: NetworkVm = new NetworkVm(id, mips, pes)
    vm.setRam(ram)
      .setBw(bw)
      .setSize(storage)
      .setCloudletScheduler(cloudletScheduler)
  }

  def createCloudlet(pes: Int, ram: Int, fileSize: Int, outputFileSize: Int) = {
    new NetworkCloudlet(1, pes)
      .setMemory(ram)
      .setFileSize(fileSize)
      .setOutputSize(outputFileSize)
      .setUtilizationModel(new UtilizationModelFull)
    //TODO remember to set VM at the broker
  }

  def createTasksForCloudlets(networkCloudlets: List[NetworkCloudlet], noOfTasks: Int, percentageOfSendTasks: Int, taskLength: Int, taskRam: Int) = {
    val cloudletsSize: Int = networkCloudlets.size
    networkCloudlets.zipWithIndex.foreach { case (cloudlet, i) => {
      addExecutionTasks(cloudlet, i, taskLength, taskRam)
      if (i < (cloudletsSize / 2)) {
        addSendTasks(cloudlet, networkCloudlets(cloudletsSize - i))
        addReceiveTasks(networkCloudlets(cloudletsSize - i), cloudlet)
      }
    }
    }
  }

  def addExecutionTasks(cloudlet: NetworkCloudlet, id: Int, taskLength: Int, taskRam: Int) = {
    val task: CloudletTask = new CloudletExecutionTask(id, taskLength)
    task.setMemory(taskRam)
    cloudlet.addTask(task)
  }

  def addSendTasks(src: NetworkCloudlet, dest: NetworkCloudlet) = {

  }

  def addReceiveTasks(dest: NetworkCloudlet, cloudlet: NetworkCloudlet) = {

  }

  def createNetworkTopology() = {

  }
}
