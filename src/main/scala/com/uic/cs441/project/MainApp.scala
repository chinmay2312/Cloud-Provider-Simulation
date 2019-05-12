package com.uic.cs441.project
import cloudsimplus.extension.broker.RegionalDatacenterBroker
import cloudsimplus.extension.cloudlet.RegionalCloudlet
import cloudsimplus.extension.vm.RegionalVm
import com.typesafe.scalalogging.Logger
import com.uic.cs441.project.config.ConfigDataCenter
import com.uic.cs441.project.config.ConfigReader._
import com.uic.cs441.project.generator.Generator._
import com.uic.cs441.project.regions.Region.Region
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
import org.cloudbus.cloudsim.vms.Vm
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import scala.collection.JavaConverters._
import scala.util.Random

/**
  * Main class of the project
  *
  */
object MainApp {

  val logger = Logger("MainApp")

  def main(args: Array[String]): Unit = {

    logger.info("Starting simulation")

    val cloudsim = new CloudSim()

    logger.info("Creating datacenter resources ")

    val datacenterList : List[Datacenter] = createDataCenters(cloudsim)

    logger.info("Creating broker")

    val broker:DatacenterBrokerSimple = new RegionalDatacenterBroker(cloudsim)

    logger.info("Creating virtual machines")

    val vmList : java.util.List[RegionalVm] = createVms()

    broker.submitVmList(vmList)

//    broker.getDatacenterList()

    logger.info("Creating cloudlets")

    val cloudletList : java.util.List[RegionalCloudlet] = createAndAssignVmToCloudlets(vmList.asScala.toList)

    broker.submitCloudletList(cloudletList)

    logger.info("Stopping simulation")

    cloudsim.start()

    printCloudletsResult(broker.getCloudletFinishedList())


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

  def createVms() : java.util.List[RegionalVm] = {

    val vmValues = getVmValues

    generateVmList(vmValues.countOfVm, vmValues.ram, vmValues.bw, vmValues.storage, vmValues.pes,
      vmValues.mips).asJava

  }

  def createAndAssignVmToCloudlets(vmList:List[RegionalVm]) : java.util.List[RegionalCloudlet] = {

    val cloudletValues = getCloudletValues

    val taskValues = getTaskValues

    val regionToVmMap:Map[Region,List[RegionalVm]] = vmList.groupBy(_.getRegion())

    val cloudlets : List[RegionalCloudlet] = generateCloudlets(cloudletValues.countOfCloudlets, cloudletValues.pes, cloudletValues.ram,
      cloudletValues.fileSize, cloudletValues.length, cloudletValues.outputFileSize)

    val regionToCloudletMap:Map[Region,List[RegionalCloudlet]]=cloudlets.groupBy(_.getRegion())

    var onlyAssignedCloudlets:List[RegionalCloudlet] = List()



    regionToCloudletMap.foreach {
      case (region: Region, cloudletList: List[RegionalCloudlet]) =>{

        cloudletList.foreach(cloudlet => {

          regionToVmMap.get(cloudlet.getRegion())
            .foreach(list => {

              cloudlet.setVm(getVmToCloudletMappingPolicy(cloudlet)(cloudlet,
              vmList.asJava))

              if(cloudlet.getStatus != Cloudlet.Status.FAILED){
                onlyAssignedCloudlets = onlyAssignedCloudlets :+ cloudlet
              }

            })
        })
      }
    }


    //TODO assign VM to each cloudlet then call the tasks

    if(onlyAssignedCloudlets.length % 2 != 0){
      // dropping one cloudlet to ensure even number of cloudlets
      onlyAssignedCloudlets = onlyAssignedCloudlets.dropRight(1)
    }

    if(onlyAssignedCloudlets.length == 0){
      logger.error("Cloudlets could not be assigned to any VM")
      System.exit(1)
    }

    createTasksForCloudlets(onlyAssignedCloudlets, taskValues.noOfTasks, taskValues.noOfTasks,
      taskValues.packetDataLengthInBytes, taskValues.taskLength, taskValues.taskRam)

    onlyAssignedCloudlets.asJava
  }

  def getRandomVm(maxCount:Int):Int = {
    Random.nextInt(maxCount)
  }

  def printCloudletsResult(cloudlets:java.util.List[RegionalCloudlet])={
    new CloudletsTableBuilder(cloudlets).build()
  }


}

class MainApp{}
