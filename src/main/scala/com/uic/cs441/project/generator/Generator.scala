package com.uic.cs441.project.generator

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicy
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.network._
import org.cloudbus.cloudsim.core.{CloudSim, Simulation}
import org.cloudbus.cloudsim.datacenters._
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.hosts.Host
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudbus.cloudsim.network.switches.EdgeSwitch
import org.cloudbus.cloudsim.provisioners.{PeProvisionerSimple, ResourceProvisionerSimple}
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull
import org.cloudbus.cloudsim.vms.network.NetworkVm
import com.uic.cs441.project.config.ConfigReader._

import scala.collection.JavaConverters._

object Generator {
  var vmIdCount: Int = 0

  def generateHostList(countOfHost: Int, ram: Int, bw: Long, storage: Long, pes: Int, mips: Int, vmScheduler: VmScheduler): List[Host] = {
    for (_ <- List.range(1, countOfHost))
      yield createHost(ram, bw, storage, pes, mips, vmScheduler)
  }

  implicit def generateVmList(countOfVm: Int, ram: Int, bw: Long, storage: Long, pes: Int, mips: Int) = {
    for (_ <- List.range(1, countOfVm))
      yield createVM(getVmIdCount(), ram, bw, storage, pes, mips, getCloudletSchedulerPolicy)

  }

  def getVmIdCount() : Int = {

    vmIdCount += 1
    vmIdCount
  }

  def generateCloudlets(countOfCloudlets: Int, pes: Int, ram: Int, fileSize: Int, length : Int, outputFileSize: Int) = {
    for (_ <- List.range(1, countOfCloudlets))
      yield createCloudlet(pes, ram, fileSize, length, outputFileSize)
  }

  def generateAndAddTasksToCloudlets(numOfTasksForEachCloudlet: Int) = {

  }

  def createHost(ram: Int, bw: Long, storage: Long, pes: Int, mips: Int, vmScheduler: VmScheduler): Host = {
    new NetworkHost(ram, bw, storage, createPes(pes, mips).asJava)
      .setRamProvisioner(new ResourceProvisionerSimple())
      .setBwProvisioner(new ResourceProvisionerSimple())
      .setVmScheduler(vmScheduler)
  }

  def createPes(pes: Int, mips: Int): List[Pe] = {
    for (_ <- List.range(1, pes))
      yield new PeSimple(mips, new PeProvisionerSimple)
  }

  implicit def createDataCenter(simulation: Simulation, vmAllocationPolicy: VmAllocationPolicy, hostCount: Int,
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

  implicit def createDataCenterNetwork(simulation: CloudSim, datacenter: NetworkDatacenter) = {
    val edgeSwitch: EdgeSwitch = new EdgeSwitch(simulation, datacenter)
    datacenter.addSwitch(edgeSwitch)
    datacenter.getHostList.asInstanceOf[java.util.List[NetworkHost]].asScala.foreach(netHost => {
      edgeSwitch.connectHost(netHost)
    })
  }

  implicit def createDataCenterBroker(simulation: CloudSim) = {
    new DatacenterBrokerSimple(simulation)
  }

  implicit def createVM(id: Int, ram: Int, bw: Long, storage: Long, pes: Int, mips: Int, cloudletScheduler: CloudletScheduler) = {
    val vm: NetworkVm = new NetworkVm(id, mips, pes)
    vm.setRam(ram)
      .setBw(bw)
      .setSize(storage)
      .setCloudletScheduler(cloudletScheduler)
  }

  implicit def createCloudlet(pes: Int, ram: Int, fileSize: Int, length : Int, outputFileSize: Int) : NetworkCloudlet = {
    val cloudlet = new NetworkCloudlet(length, pes)
    cloudlet.setMemory(ram)
    cloudlet.setFileSize(fileSize)
    cloudlet.setOutputSize(outputFileSize)
    cloudlet.setUtilizationModel(getUtilizationModel)
    cloudlet
    //TODO remember to set VM at the broker
  }

    //TODO Vms are already assigned to the cloudlets
  implicit def createTasksForCloudlets(networkCloudlets: List[NetworkCloudlet], noOfTasks: Int, numOfPackets: Int, packetDataLengthInBytes: Int, taskLength: Int, taskRam: Int) = {
    val cloudletsSize: Int = networkCloudlets.size
    networkCloudlets.zipWithIndex.foreach { case (cloudlet, i) => {
      if (i < (cloudletsSize / 2)) {
        addExecutionTasks(cloudlet, i, taskLength, taskRam)
        addSendTasks(cloudlet, networkCloudlets(cloudletsSize - i - 1), taskRam, numOfPackets, packetDataLengthInBytes)
        addReceiveTasks(networkCloudlets(cloudletsSize - i - 1), cloudlet, taskRam, numOfPackets)
        addExecutionTasks(networkCloudlets(cloudletsSize - i - 1), i, taskLength, taskRam)
      }
    }
    }
  }

  implicit def addExecutionTasks(cloudlet: NetworkCloudlet, id: Int, taskLength: Int, taskRam: Int) = {
    val task: CloudletTask = new CloudletExecutionTask(id, taskLength)
    task.setMemory(taskRam)
    cloudlet.addTask(task)
  }

  def addSendTasks(src: NetworkCloudlet, dest: NetworkCloudlet, taskRam: Int, numOfPackets: Int, packetDataLengthInBytes: Int) = {
    val task: CloudletSendTask = new CloudletSendTask(src.getTasks.size())
    task.setMemory(taskRam)
    src.addTask(task)
    for (_ <- List.range(1, numOfPackets))
      yield task.addPacket(dest, packetDataLengthInBytes)
  }

  def addReceiveTasks(dest: NetworkCloudlet, cloudlet: NetworkCloudlet, taskRam: Int, numOfPackets: Int) = {
    val task: CloudletReceiveTask = new CloudletReceiveTask(cloudlet.getTasks().size(), cloudlet.getVm())
    task.setMemory(taskRam)
    task.setExpectedPacketsToReceive(numOfPackets)
    dest.addTask(task)
  }

  //  def createNetworkTopology() = {
  //
  //  }
}
