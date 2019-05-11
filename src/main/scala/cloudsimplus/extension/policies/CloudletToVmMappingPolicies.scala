package cloudsimplus.extension.policies

import java.util.Comparator
import java.util.Comparator.{comparingDouble, comparingLong}

import cloudsimplus.extension.cloudlet.RegionalCloudlet
import cloudsimplus.extension.vm.RegionalVm
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.vms.Vm

import scala.collection.JavaConverters._

object CloudletToVmMappingPolicies {

  implicit def cloudletToVmMapperTimeMinimized(cloudlet: RegionalCloudlet, execVms : java.util.List[RegionalVm]): Vm = {

    val sortByFreePesNumber = (vmList : List[RegionalVm]) => vmList
      .sortWith((vm1, vm2) => getExpectedNumberOfFreeVmPes(vm1) > getExpectedNumberOfFreeVmPes(vm2))

    val sortByExpectedCloudletCompletionTime = (vmList : List[RegionalVm]) => vmList
        .sortWith((vm1, vm2) => getExpectedCloudletCompletionTime(cloudlet, vm1)
          < getExpectedCloudletCompletionTime(cloudlet, vm2))

    val sortedVms = sortByFreePesNumber(sortByExpectedCloudletCompletionTime(execVms.asScala.toList))

    val mostFreePesVm: Vm = sortedVms.headOption.getOrElse(Vm.NULL)

    val filteredVms = sortedVms
      .filter((vm: RegionalVm) => getExpectedNumberOfFreeVmPes(vm) >= cloudlet.getNumberOfPes)

    val bestFilteredVm = filteredVms.headOption.getOrElse(mostFreePesVm)

    val regionFilteredVms = filteredVms
      .filter(vm => vm.getRegion() == cloudlet.getRegion())

    val selectedVM : Option[Vm] = regionFilteredVms.headOption

    selectedVM match {

      case Some(vm) => vm
      case None => {
        cloudlet.setStatus(Cloudlet.Status.FAILED)
        bestFilteredVm
      }
    }

  }

  // TODO : Changes name of Policy
  implicit def cloudletToVmMapperRegionFit(cloudlet: RegionalCloudlet, execVms : java.util.List[RegionalVm]): Vm = {

    val sortedVms = execVms
      .asScala
      .filter(vm => vm.reg == cloudlet.reg)
      .filter(vm => vm.getNumberOfPes>=cloudlet.getNumberOfPes)

    val bestFilteredVm = sortedVms.headOption.getOrElse(Vm.NULL)

    val regionFilteredVms = sortedVms
      .filter(vm => vm.getRegion() == cloudlet.getRegion())

    val selectedVM : Option[Vm] = regionFilteredVms.headOption

    selectedVM match {

      case Some(vm) => vm
      case None => {
        cloudlet.setStatus(Cloudlet.Status.FAILED)
        bestFilteredVm
      }
    }


    //TODO: handle exception if no eligible VM available
  }


  implicit def getExpectedCloudletCompletionTime(cloudlet: Cloudlet, vm: Vm): Double
  = cloudlet.getLength / vm.getMips

  implicit def getExpectedNumberOfFreeVmPes(vm: Vm): Long = {
    val totalPesForCloudletsOfVm = vm.getBroker.getCloudletCreatedList
      .stream.filter(c => c.getVm == vm).mapToLong(c => c.getNumberOfPes).sum

    val numberOfVmFreePes = vm.getNumberOfPes - totalPesForCloudletsOfVm

    numberOfVmFreePes
  }



}
