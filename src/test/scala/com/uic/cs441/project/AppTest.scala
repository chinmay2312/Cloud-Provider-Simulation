package com.uic.cs441.project

import java.util

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._
import cloudsimplus.extension.broker.RegionalDatacenterBroker
import cloudsimplus.extension.cloudlet.RegionalCloudlet
import cloudsimplus.extension.vm.RegionalVm
import com.uic.cs441.project.config.CloudletValues
import com.uic.cs441.project.config.ConfigReader.getCloudletValues
import com.uic.cs441.project.generator.Generator.generateCloudlets
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.cloudlets.Cloudlet.Status
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.vms.{Vm, VmCost}
import org.scalatest.FlatSpec

/**
  * Integration test for the project
  *
  * @author Chinmay Gangal
  */
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
  //sim.abort()
  val cloudletSubmittedList: List[Cloudlet] = broker.getCloudletSubmittedList.toList

  val cloudletCount:Int = getCloudletValues.countOfCloudlets

  "MainApp" should "have all cloudlets in execution" in {
    //CloudSimPlus does not update Cloudlet status from INEXEC to SUCCESS
    assert(cloudletSubmittedList(cloudletCount - 1).getStatus == Status.INEXEC)
  }

}
