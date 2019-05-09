package com.uic.cs441.project.config

import java.util.function.Function

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicy, VmAllocationPolicyBestFit, VmAllocationPolicyFirstFit, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletNull, CloudletSimple}
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletScheduler, CloudletSchedulerCompletelyFair, CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
import org.cloudbus.cloudsim.schedulers.vm.{VmScheduler, VmSchedulerSpaceShared, VmSchedulerTimeShared, VmSchedulerTimeSharedOverSubscription}
import org.cloudbus.cloudsim.utilizationmodels._
import org.cloudbus.cloudsim.vms.Vm

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

 /* implicit def getVmToCloudletMappingPolicy : Function[Cloudlet, Vm] = {

    config.getString("policies.vmToCloudletMapping") match {
      case "Hungarian" => () => new CloudletNull()
    }
  }*/

  implicit def getCloudletSchedulerPolicy : CloudletScheduler = {

    logger.info("Picked up cloudlet scheduler policy "
      + config.getString("policies.cloudletSchedulerPolicy"))

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
      case "UtilizationModelPlanetLab" => UtilizationModelPlanetLab
      case "UtilizationModelStochastic" => new UtilizationModelStochastic
      case _ => new UtilizationModelFull
    }
  }


}

