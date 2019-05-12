package cloudsimplus.extension.policies

import java.util.Optional

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyAbstract
import org.cloudbus.cloudsim.hosts.Host
import org.cloudbus.cloudsim.vms.Vm

import scala.collection.JavaConverters._

/**
  * Policy for allocating VM to Host
  */
class VMAllocationPolicyPercentagePes
  extends VmAllocationPolicyAbstract {

  override def defaultFindHostForVm(vm: Vm): Optional[Host] = {

    Optional.ofNullable(getHostList[Host].asScala.toList
      .filter(host => host.isSuitableForVm(vm))
      .sortBy(host => host.isActive)
      .reverse
      .sortWith((host1, host2) => (host1.getFreePesNumber / host1.getNumberOfPes)
        > (host2.getFreePesNumber / host2.getNumberOfPes)).headOption.orNull)


  }
}
