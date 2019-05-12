package cloudsimplus.extension.broker

import cloudsimplus.extension.cloudlet.RegionalCloudlet
import cloudsimplus.extension.vm.RegionalVm

import scala.collection.JavaConversions._


/**
  * Policy for mapping cloudlet to Vm by prioritizing region
  * @author Chinmay Gangal
  */
object CloudletToVmMappingRegionFit {

  /**
    * Finds Vm in same region as cloudlet, and that occupies just enough PEs reuired by cloudlet
    * @param cloudlet Cloudlet of a specific Region
    * @return Vm
    */
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
