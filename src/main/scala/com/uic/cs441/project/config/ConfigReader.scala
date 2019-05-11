package com.uic.cs441.project.config

import java.util
import java.util.Comparator
import java.util.Comparator.{comparingDouble, comparingLong}
import java.util.function.Function

import cloudsimplus.extension.policies.VMAllocationPolicyPercentagePes
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger
import com.uic.cs441.project.regions.Region
import com.uic.cs441.project.regions.Region.Region
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicy, VmAllocationPolicyBestFit, VmAllocationPolicyFirstFit, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletScheduler, CloudletSchedulerCompletelyFair, CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
import org.cloudbus.cloudsim.schedulers.vm.{VmScheduler, VmSchedulerSpaceShared, VmSchedulerTimeShared, VmSchedulerTimeSharedOverSubscription}
import org.cloudbus.cloudsim.utilizationmodels._
import org.cloudbus.cloudsim.vms.Vm

import scala.collection.JavaConverters._


object ConfigReader {

  val logger = Logger("ConfigReader")

  //Type safe config object
  val config: Config = ConfigFactory.load()

  implicit def getVMAllocationPolicy: VmAllocationPolicy = {

    logger.info("Picked up VM Allocation policy "
      + config.getString("policies.vmAllocationPolicy"))


    config.getString("policies.vmAllocationPolicy") match {

      case "VmAllocationPolicyBestFit" => new VmAllocationPolicyBestFit
      case "VmAllocationPolicyFirstFit" => new VmAllocationPolicyFirstFit
      case "VmAllocationPolicySimple" => new VmAllocationPolicySimple
      case "VMAllocationPolicyPercentagePes" => new VMAllocationPolicyPercentagePes
      case _ => new VmAllocationPolicySimple

    }
  }

  implicit def getVmToCloudletMappingPolicy(cloudlet: Cloudlet): Function[Cloudlet, Vm] = {

    config.getString("policies.vmToCloudletMapping") match {

      case "CloudletToVmMappingTimeMinimizedMapping" => cloudletToVmMappingTimeMinimized
    }
  }

  implicit def getExpectedCloudletCompletionTime(cloudlet: Cloudlet, vm: Vm): Double
  = cloudlet.getLength / vm.getMips

  implicit def getExpectedNumberOfFreeVmPes(vm: Vm): Long = {
    val totalPesForCloudletsOfVm = vm.getBroker.getCloudletCreatedList
      .stream.filter(c => c.getVm == vm).mapToLong(c => c.getNumberOfPes).sum

    val numberOfVmFreePes = vm.getNumberOfPes - totalPesForCloudletsOfVm

    numberOfVmFreePes
  }

  implicit def cloudletToVmMappingTimeMinimized(cloudlet: Cloudlet): Vm = {

    val execVms: util.List[Vm] = cloudlet.getBroker.getVmExecList.asInstanceOf[util.List[Vm]]

    val sortByFreePesNumber: Comparator[Vm] = comparingLong(getExpectedNumberOfFreeVmPes)

    val sortByExpectedCloudletCompletionTime: Comparator[Vm] =
      comparingDouble((vm: Vm) => getExpectedCloudletCompletionTime(cloudlet, vm))

    execVms.sort(sortByExpectedCloudletCompletionTime.thenComparing(
      sortByFreePesNumber.reversed))

    val mostFreePesVm: Vm = execVms.stream.findFirst.orElse(Vm.NULL)

    execVms.stream.filter((vm: Vm) => getExpectedNumberOfFreeVmPes(vm) >= cloudlet.getNumberOfPes)
      .findFirst.orElse(mostFreePesVm)

  }

  implicit def getCloudletSchedulerPolicyString = config.getString("policies.cloudletSchedulerPolicy")

  implicit def getCloudletSchedulerPolicy: CloudletScheduler = {

    config.getString("policies.cloudletSchedulerPolicy") match {

      case "CloudletSchedulerTimeShared" => new CloudletSchedulerTimeShared
      case "CloudletSchedulerSpaceShared" => new CloudletSchedulerSpaceShared
      case "CloudletSchedulerCompletelyFair" => new CloudletSchedulerCompletelyFair
      case _ => new CloudletSchedulerTimeShared
    }
  }

  implicit def getVmScheduler: VmScheduler = {

    logger.info("Picked up cloudlet VM Scheduler "
      + config.getString("policies.vmSchedulerPolicy"))

    config.getString("policies.vmSchedulerPolicy") match {

      case "VmSchedulerSpaceShared" => new VmSchedulerSpaceShared
      case "VmSchedulerTimeShared" => new VmSchedulerTimeShared
      case "VmSchedulerTimeSharedOverSubscription" => new VmSchedulerTimeSharedOverSubscription
      case _ => new VmSchedulerTimeShared

    }
  }

  implicit def getUtilizationModel: UtilizationModel = {

    config.getString("policies.utilizationModel") match {

      case "UtilizationModelDynamic" => new UtilizationModelDynamic
      case "UtilizationModelFull" => new UtilizationModelFull
      case "UtilizationModelStochastic" => new UtilizationModelStochastic
      case _ => new UtilizationModelFull
    }
  }

  implicit def getHostValues: HostValues = {

    logger.info("Picked up host values " + config.getConfig("hostValues").toString)
    val hostValues = config.getConfig("hostValues")

    HostValues(hostValues.getInt("ram"), hostValues.getLong("bw"), hostValues.getLong("storage"),
      hostValues.getInt("noOfPes"), hostValues.getInt("mips"))

  }

  implicit def getDataCenterList: List[ConfigDataCenter] = {

    config.getConfigList("regions").asScala.toList
      .flatMap(region => region.getConfigList("dcList").asScala.toList
        .map(dc => ConfigDataCenter(dc.getInt("noOfHosts"),
          mapToRegion(region.getString("name"))))


      )

  }

  def mapToRegion(region: String): Region = {
    region match {
      case "Region1" => Region.REGION1
      case "Region2" => Region.REGION2
      case "Region3" => Region.REGION3
      case "Region4" => Region.REGION4
      case "Region5" => Region.REGION5
      case "Region6" => Region.REGION6
      case "Region7" => Region.REGION7
    }
  }

  implicit def getVmValues: VmValues = {

    logger.info("Picked up VM values " + config.getConfig("vmValues").toString)

    val vmValues = config.getConfig("vmValues")

    VmValues(vmValues.getInt("noOfVms"), vmValues.getInt("maxRam"), vmValues.getLong("maxBw"),
      vmValues.getLong("maxStorage"), vmValues.getInt("maxNoOfPes"), vmValues.getInt("maxMips"))


  }

  implicit def getCloudletValues: CloudletValues = {

    logger.info("Picked up Cloudlet values " + config.getConfig("cloudletValues").toString)

    val cloudletValues = config.getConfig("cloudletValues")

    CloudletValues(cloudletValues.getInt("noOfCloudlets"), cloudletValues.getInt("maxNoOfPes"),
      cloudletValues.getInt("maxRam"), cloudletValues.getInt("maxFileSize"), cloudletValues.getInt("maxlength"),
      cloudletValues.getInt("maxOutputSize"))
  }

  implicit def getTaskValues: TaskValues = {

    logger.info("Picked up VM values " + config.getConfig("taskValues").toString)

    val taskValues = config.getConfig("taskValues")

    TaskValues(taskValues.getInt("noOfTasks"), taskValues.getInt("noOfPackets"),
      taskValues.getInt("packetDataLengthInBytes"), taskValues.getInt("taskLength"),
      taskValues.getInt("taskRam"))
  }
}


case class HostValues(ram: Int, bw: Long, storage: Long, pes: Int, mips: Int)

case class ConfigDataCenter(noOfHosts: Int, region: Region)

case class VmValues(countOfVm: Int, ram: Int, bw: Long, storage: Long, pes: Int, mips: Int)

case class CloudletValues(countOfCloudlets: Int, pes: Int, ram: Int, fileSize: Int, length: Int, outputFileSize: Int)

case class TaskValues(noOfTasks: Int, numOfPackets: Int, packetDataLengthInBytes: Int, taskLength: Int, taskRam: Int)
