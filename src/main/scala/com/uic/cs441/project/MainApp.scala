package com.uic.cs441.project
import cloudsimplus.extension.broker.{CloudletToVmMappingRegionFit, RegionalDatacenterBroker}
import com.typesafe.scalalogging.Logger
import com.uic.cs441.project.config.ConfigDataCenter
import config.ConfigReader._
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
import generator.Generator._
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletScheduler
import org.cloudbus.cloudsim.vms.Vm

import scala.collection.JavaConverters._

object MainApp {

  val logger = Logger("MainApp")

  def main(args: Array[String]): Unit = {

    logger.info("Starting simulation")

    val cloudsim = new CloudSim()

    logger.info("Creating datacenter resources ")

    val datacenterList : List[Datacenter] = createDataCenters(cloudsim)

    logger.info("Creating broker")

    val broker:DatacenterBrokerSimple = new RegionalDatacenterBroker(cloudsim)

    logger.info("Setting policy for mapping cloudlets to VMs")

    broker.setVmMapper(Function[Cloudlet, Vm])

    logger.info("Creating virtual machines")

    val vmList : java.util.List[Vm] = createVms()

    broker.submitVmList(vmList)

//    broker.getDatacenterList()

    logger.info("Creating cloudlets")

    val cloudletList : java.util.List[NetworkCloudlet] = createCloudlets()

//    broker.submitCloudletList(cloudletList)

    logger.info("Stopping simulation")

    cloudsim.start()



  }

  def createDataCenters(cloudsim: CloudSim) : List[Datacenter] = {

    val dcList : List[ConfigDataCenter] = getDataCenterList

    val hostValues = getHostValues

    dcList.map(dc => {

      createDataCenter(dc.region,cloudsim, getVMAllocationPolicy, dc.noOfHosts, hostValues.ram,
        hostValues.bw, hostValues.storage, hostValues.pes, hostValues.mips,
        getVmScheduler)

    })
  }

  def createVms() : java.util.List[Vm] = {

    val vmValues = getVmValues

    generateVmList(vmValues.countOfVm, vmValues.ram, vmValues.bw, vmValues.storage, vmValues.pes,
      vmValues.mips).asJava

  }

  def createCloudlets() : java.util.List[NetworkCloudlet] = {

    val cloudletValues = getCloudletValues

    val taskValues = getTaskValues

    val cloudlets : List[NetworkCloudlet] = generateCloudlets(cloudletValues.countOfCloudlets, cloudletValues.pes, cloudletValues.ram,
      cloudletValues.fileSize, cloudletValues.length, cloudletValues.outputFileSize)

    createTasksForCloudlets(cloudlets, taskValues.noOfTasks, taskValues.noOfTasks,
      taskValues.packetDataLengthInBytes, taskValues.taskLength, taskValues.taskRam)

    cloudlets.asJava
  }


}
