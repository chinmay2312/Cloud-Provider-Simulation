package datacenter

import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicy, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.core.{CloudSim, Simulation}
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.hosts.Host
import java.util

class RegionalDatacenter(
                          region:String,
                          simulation:Simulation,
                          hostList:util.ArrayList[Host],
                          vmAllocationPolicy:VmAllocationPolicy)
  extends NetworkDatacenter(simulation, hostList, vmAllocationPolicy){


}

object RegionalDatacenter {
  def main(args: Array[String]): Unit =  {

    val simulation:CloudSim = new CloudSim()
    val hostList = new util.ArrayList[Host]()
    val vmAllocationPolicy:VmAllocationPolicy = new VmAllocationPolicySimple()

    new RegionalDatacenter("Chicago", simulation, hostList, vmAllocationPolicy)
  }
}
