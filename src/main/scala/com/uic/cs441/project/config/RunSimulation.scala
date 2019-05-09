/*
package com.uic.cs441.project.config


import java.text.DecimalFormat
import java.util.Calendar
import com.typesafe.scalalogging.Logger

import org.cloudbus.cloudsim.core.CloudSim

import scala.collection.JavaConverters._

object RunSimulation extends App {

  override def main(args: Array[String]): Unit = {
    //Read the configuration file
    val readConfigurations: ReadConfigurationFile = new ReadConfigurationFile("simulation1.conf")

    //read init configs
    val initConfigs: List[Any] = readConfigurations.readInitData

    //Call init of the Cloudsim
    //Without this you can't create instances of the Datacenters
    CloudSim.init(initConfigs.head.asInstanceOf[Int], Calendar.getInstance, initConfigs(1).asInstanceOf[Boolean])

    // Creation of Data center(s)
    readConfigurations.readDataCenters

    //creation of broker
    val broker: DatacenterBroker = readConfigurations.readBroker

    //submit the VM list to the broker
    broker.submitVmList(readConfigurations.readVms(broker.getId,args(0)).asJava)
    //submit the cloudlet list to the broker
    broker.submitCloudletList(readConfigurations.readCloudlets(broker.getId).asJava)

    //start the simulation
    CloudSim.startSimulation
    //stop the simulation
    CloudSim.stopSimulation()

    //Final step: Print results when simulation is over
    val newList = broker.getCloudletReceivedList[Cloudlet]

    //print the cloudlets
    printCloudlets(newList.asScala.toList)

    Log.printLine("CloudSimExample1 finished!")
  }

  /**
    * Printing the cloudlet's details with the cost for bandwidth and CPU utilization.
    *
    * @param list
    */
  def printCloudlets(list: List[Cloudlet]): Unit = {
    val logger = Logger("RunSimulation")
    val indent = "    "
    logger.debug("")
    logger.debug("========== OUTPUT ==========")
    logger.debug("Cloudlet_ID" + indent + "STATUS" + indent + "Datacenter_ID" + indent + "VM_ID" + indent + "Time" + indent + "Start_Time" + indent + "Finish_Time" + indent + "Total Cost")
    val dft = new DecimalFormat("###.##")
    list.foreach(cloudlet => {
      val totalCost: Double = cloudlet.getAllResourceId.map(id => {
        cloudlet.getCostPerSec(id) * cloudlet.getActualCPUTime(id)
      }).sum
      val bwCost: Double = cloudlet.getProcessingCost
      logger.debug(indent + indent + cloudlet.getCloudletId + indent + indent + cloudlet.getStatus + indent + indent + indent + cloudlet.getResourceId + indent + indent + indent + cloudlet.getVmId + indent + indent + dft.format(cloudlet.getActualCPUTime) + indent + indent + dft.format(cloudlet.getExecStartTime) + indent + indent + dft.format(cloudlet.getFinishTime) + indent + indent + indent + (totalCost + bwCost))
    })
  }
}*/
