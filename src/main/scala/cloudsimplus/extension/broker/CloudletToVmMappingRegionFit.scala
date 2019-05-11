package cloudsimplus.extension.broker

import cloudsimplus.extension.cloudlet.RegionalCloudlet
import cloudsimplus.extension.vm.RegionalVm

import scala.collection.JavaConversions._

object CloudletToVmMappingRegionFit {
  def regionFitCloudletToVmMapper(cloudlet: RegionalCloudlet): RegionalVm = {

    val vmList:List[RegionalVm] = cloudlet.getBroker.getVmCreatedList.toList

    vmList
      .toStream
      .filter(vm => vm.reg == cloudlet.reg)
      .filter(vm => vm.getNumberOfPes>=cloudlet.getNumberOfPes)
      .minBy(vm => vm.getNumberOfPes)

    //TODO: handle exception if no eligible VM available
  }

}
