package com.uic.cs441.project.generator

import cloudsimplus.extension.cloudlet.RegionalCloudlet
import cloudsimplus.extension.datacenter.RegionalDatacenter
import cloudsimplus.extension.vm.RegionalVm
import com.uic.cs441.project.config.ConfigReader._
import com.uic.cs441.project.regions.Region
import com.uic.cs441.project.regions.Region.Region
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

import scala.collection.JavaConverters._

object Generator {
  var vmIdCount: Int = 0
  var cloudletCount: Int = 0
  var taskCountId=0

  def generateHostList(countOfHost: Int, ram: Int, bw: Long, storage: Long, pes: Int, mips: Int, vmScheduler: VmScheduler): List[Host] = {
    for (_ <- List.range(1, countOfHost + 1))
      yield createHost(ram, bw, storage, pes, mips, vmScheduler)
  }

  implicit def generateVmList(countOfVm: Int, ram: Int, bw: Long, storage: Long, pes: Int, mips: Int) = {
    for (_ <- List.range(1, countOfVm + 1))
      yield createVM(Region.getRandomRegion(), getVmIdCount(), ram, bw, storage, pes, mips, getCloudletSchedulerPolicy)

  }

  def getVmIdCount(): Int = {
    vmIdCount += 1
    vmIdCount
  }

  def getCloudletCount(): Int = {
    cloudletCount += 1
    cloudletCount
  }

  def getTaskCount(): Int = {
    taskCountId += 1
    taskCountId
  }
  def generateCloudlets(countOfCloudlets: Int, pes: Int, ram: Int, fileSize: Int, length: Int, outputFileSize: Int) = {
    for (_ <- List.range(1, countOfCloudlets + 1))
      yield createCloudlet(Region.getRandomRegion(), pes, ram, fileSize, length, outputFileSize)
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
    for (_ <- List.range(1, pes + 1))
      yield new PeSimple(mips, new PeProvisionerSimple)
  }

  implicit def createDataCenter(region: Region, simulation: Simulation, vmAllocationPolicy: VmAllocationPolicy, hostCount: Int,
                                ram: Int,
                                bw: Long,
                                storage: Long,
                                pes: Int, mips: Int,
                                vmScheduler: VmScheduler): Datacenter = {
    val dc: RegionalDatacenter = new RegionalDatacenter(region, simulation,
      (for (_ <- List.range(1, hostCount + 1)) yield createHost(ram, bw, storage, pes, mips, vmScheduler)).asJava,
      vmAllocationPolicy)
//     dc.setSchedulingInterval(50)
    createDataCenterNetwork(simulation.asInstanceOf[CloudSim], dc)
    dc
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

  implicit def createVM(region: Region, id: Int, ram: Int, bw: Long, storage: Long, pes: Int, mips: Int, cloudletScheduler: CloudletScheduler) = {
    val vm: RegionalVm = new RegionalVm(id, mips, pes, region)
    vm.setRam(ram)
    vm.setBw(bw)
    vm.setSize(storage)
    vm.setCloudletScheduler(cloudletScheduler)
    vm
  }

  implicit def createCloudlet(region: Region, pes: Int, ram: Int, fileSize: Int, length: Int, outputFileSize: Int): RegionalCloudlet = {
    val cloudlet = new RegionalCloudlet(getCloudletCount(), length, pes, region)
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
    val task: CloudletSendTask = new CloudletSendTask(getTaskCount())
    task.setMemory(taskRam)
    src.addTask(task)
    for (_ <- List.range(1, numOfPackets+1))
      yield task.addPacket(dest, packetDataLengthInBytes)
  }

  def addReceiveTasks(dest: NetworkCloudlet, cloudlet: NetworkCloudlet, taskRam: Int, numOfPackets: Int) = {
    val task: CloudletReceiveTask = new CloudletReceiveTask(getTaskCount(), cloudlet.getVm())
    task.setMemory(taskRam)
    task.setExpectedPacketsToReceive(numOfPackets)
    dest.addTask(task)
  }

  //  def createNetworkTopology() = {
  //
  //  }
}
