package cloudsimplus.extension.datacenter

import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicy, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.core.{CloudSim, Simulation}
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.hosts.Host
import java.util

import com.uic.cs441.project.regions.Region
import com.uic.cs441.project.regions.Region.Region

class RegionalDatacenter(
                          region:Region,
                          simulation:Simulation,
                          hostList:util.List[Host],
                          vmAllocationPolicy:VmAllocationPolicy)
  extends NetworkDatacenter(simulation, hostList, vmAllocationPolicy){

  val reg = region
  println("You selected "+region)
}

object RegionalDatacenter {
  def main(args: Array[String]): Unit =  {

    val simulation:CloudSim = new CloudSim()
    val hostList = new util.ArrayList[Host]()
    val vmAllocationPolicy:VmAllocationPolicy = new VmAllocationPolicySimple()
    val region:String = "Region2"
    //println(s"${Region.values}")
    new RegionalDatacenter(Region.withName(region), simulation, hostList, vmAllocationPolicy)
  }
}
