package com.uic.cs441.project

import java.util

import cloudsimplus.extension.broker.RegionalDatacenterBroker
import cloudsimplus.extension.cloudlet.RegionalCloudlet
import cloudsimplus.extension.vm.RegionalVm
import com.uic.cs441.project.config.{CloudletValues, ConfigReader, TaskValues}
import com.uic.cs441.project.config.ConfigReader.{getCloudletValues, getTaskValues}
import com.uic.cs441.project.generator.Generator.generateCloudlets
import com.uic.cs441.project.regions.Region.Region
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.cloudlets.Cloudlet.Status
import org.cloudbus.cloudsim.core.CloudSim
import org.scalatest.FlatSpec

import scala.collection.JavaConverters._

class AppTest extends FlatSpec  {

  val sim:CloudSim = new CloudSim()

  val broker:RegionalDatacenterBroker = new RegionalDatacenterBroker(sim)

  MainApp.createDataCenters(sim)

  val vmList: util.List[RegionalVm] = MainApp.createVms()

  broker.submitVmList(vmList)


  val cloudletValues: CloudletValues = getCloudletValues

  val cloudlets : util.List[RegionalCloudlet] = generateCloudlets(cloudletValues.countOfCloudlets, cloudletValues.pes, cloudletValues.ram,
    cloudletValues.fileSize, cloudletValues.length, cloudletValues.outputFileSize).asJava

  broker.submitCloudletList(cloudlets)

  sim.start()
  sim.abort()

  val cloudletSubmittedList: List[Cloudlet] = broker.getCloudletSubmittedList.asScala.toList

  assert(cloudletSubmittedList(0).getStatus == Status.INEXEC)


}
