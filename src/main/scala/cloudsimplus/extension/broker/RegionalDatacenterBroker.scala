package cloudsimplus.extension.broker

import java.util

import cloudsimplus.extension.datacenter.RegionalDatacenter
import com.uic.cs441.project.regions.Region
import com.uic.cs441.project.regions.Region.Region
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicy, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
import org.cloudbus.cloudsim.hosts.Host

import scala.collection.JavaConversions._

class RegionalDatacenterBroker(simulation: CloudSim)
  extends DatacenterBrokerSimple(simulation:CloudSim) {

  def getAllDCbyRegion(region: Region): List[Datacenter] = {
    getDatacenterList.toList.filter(dc => dc.asInstanceOf[RegionalDatacenter].reg==region)
  }

}

object RegionalDatacenterBroker {
  def main(args: Array[String]): Unit =  {

    val sim:CloudSim = new CloudSim()
    val broker = new RegionalDatacenterBroker(sim)
    /*val hostList1 = new util.ArrayList[Host]()
    val hostList2 = new util.ArrayList[Host]()
    val vmAllocationPolicy1:VmAllocationPolicy = new VmAllocationPolicySimple()
    val vmAllocationPolicy2:VmAllocationPolicy = new VmAllocationPolicySimple()
    */
    val rdc0:RegionalDatacenter = new RegionalDatacenter(Region.withName("Region1"), sim, new util.ArrayList[Host](), new VmAllocationPolicySimple())
    val rdc1:RegionalDatacenter = new RegionalDatacenter(Region.withName("Region1"), sim, new util.ArrayList[Host](), new VmAllocationPolicySimple())
    val rdc2:RegionalDatacenter = new RegionalDatacenter(Region.withName("Region1"), sim, new util.ArrayList[Host](), new VmAllocationPolicySimple())
    val rdc3:RegionalDatacenter = new RegionalDatacenter(Region.withName("Region2"), sim, new util.ArrayList[Host](), new VmAllocationPolicySimple())
    val rdc4:RegionalDatacenter = new RegionalDatacenter(Region.withName("Region1"), sim, new util.ArrayList[Host](), new VmAllocationPolicySimple())
    sim.start()
    println("Datacenters in Region1: "+broker.getAllDCbyRegion(Region.REGION1))

  }
}
