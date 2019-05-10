package com.uic.cs441.project.config

import java.util.Comparator.{comparingDouble, comparingLong}
import java.util.{Comparator}
import java.util.function.Function
import java.util

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicy, VmAllocationPolicyBestFit, VmAllocationPolicyFirstFit, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletNull, CloudletSimple}
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletScheduler, CloudletSchedulerCompletelyFair, CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
import org.cloudbus.cloudsim.schedulers.vm.{VmScheduler, VmSchedulerSpaceShared, VmSchedulerTimeShared, VmSchedulerTimeSharedOverSubscription}
import org.cloudbus.cloudsim.utilizationmodels._
import org.cloudbus.cloudsim.vms.Vm
import scala.collection.JavaConverters._


object ConfigReader {

  val logger = Logger("ConfigReader")

  //Type safe config object
  val config: Config = ConfigFactory.load()

  implicit def getVMAllocationPolicy : VmAllocationPolicy = {

    logger.info("Picked up VM Allocation policy "
      + config.getString("policies.vmAllocationPolicy"))


    config.getString("policies.vmAllocationPolicy") match {

      case "VmAllocationPolicyBestFit" => new VmAllocationPolicyBestFit
      case "VmAllocationPolicyFirstFit" => new VmAllocationPolicyFirstFit
      case "VmAllocationPolicySimple" => new VmAllocationPolicySimple
      case _ => new VmAllocationPolicySimple

    }
  }

  implicit def getVmToCloudletMappingPolicy(cloudlet: Cloudlet) : Function[Cloudlet, Vm] = {

    config.getString("policies.vmToCloudletMapping") match {

      case "CloudletToVmMappingTimeMinimizedMapping" => cloudletToVmMappingTimeMinimized
    }
  }

  implicit def getExpectedCloudletCompletionTime(cloudlet: Cloudlet, vm: Vm) : Double
  = cloudlet.getLength / vm.getMips

  implicit def getExpectedNumberOfFreeVmPes(vm : Vm): Long = {
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

  implicit def getCloudletSchedulerPolicy : CloudletScheduler = {

    config.getString("policies.cloudletSchedulerPolicy") match {

      case "CloudletSchedulerTimeShared" => new CloudletSchedulerTimeShared
      case "CloudletSchedulerSpaceShared" => new CloudletSchedulerSpaceShared
      case "CloudletSchedulerCompletelyFair" => new CloudletSchedulerCompletelyFair
      case _ => new CloudletSchedulerTimeShared
    }
  }

  implicit def getVmScheduler : VmScheduler = {

    logger.info("Picked up cloudlet VM Scheduler "
     + config.getString("policies.vmSchedulerPolicy"))

    config.getString("policies.vmSchedulerPolicy") match {

      case "VmSchedulerSpaceShared" => new VmSchedulerSpaceShared
      case "VmSchedulerTimeShared" => new VmSchedulerTimeShared
      case "VmSchedulerTimeSharedOverSubscription" => new VmSchedulerTimeSharedOverSubscription
      case _ => new VmSchedulerTimeShared

    }
  }

  implicit def getUtilizationModel : UtilizationModel = {

    logger.info("Picked up utilization model "
      + config.getString("policies.utilizationModel"))

    config.getString("policies.utilizationModel") match {

      case "UtilizationModelDynamic" => new UtilizationModelDynamic
      case "UtilizationModelFull" => new UtilizationModelFull
      case "UtilizationModelStochastic" => new UtilizationModelStochastic
      case _ => new UtilizationModelFull
    }
  }

  implicit def getHostValues : HostValues = {

    logger.info("Picked up host values " + config.getConfig("hostValues").toString)
    val hostValues = config.getConfig("hostValues")

    HostValues(hostValues.getInt("ram"),hostValues.getLong("bw"), hostValues.getLong("storage"),
      hostValues.getInt("noOfPes"), hostValues.getInt("mips"))

  }

  implicit def getDataCenterList : List[DataCenter] = {

    config.getConfigList("regions").asScala.toList
      .flatMap(region => region.getConfigList("dcList").asScala.toList
        .map(dc => DataCenter(dc.getInt("noOfHosts"))))

  }

  implicit def getVmValues : VmValues = {

    logger.info("Picked up VM values " + config.getConfig("vmValues").toString)

    val vmValues = config.getConfig("vmValues")

    VmValues(vmValues.getInt("noOfVms"), vmValues.getInt("maxRam"), vmValues.getLong("maxBw"),
      vmValues.getLong("maxStorage"), vmValues.getInt("maxNoOfPes"), vmValues.getInt("maxMips"))


  }
}


case class HostValues(ram: Int, bw: Long, storage: Long, pes: Int, mips: Int)

case class DataCenter(noOfHosts : Int)

case class VmValues(countOfVm: Int, ram: Int, bw: Long, storage: Long, pes: Int, mips: Int)
