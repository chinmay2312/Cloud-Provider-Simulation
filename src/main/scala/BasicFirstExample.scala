import org.cloudbus.cloudsim.brokers.{DatacenterBroker, DatacenterBrokerSimple}
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.DatacenterSimple
import org.cloudbus.cloudsim.hosts.{Host, HostSimple}
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import java.util

import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic

/**
  * A minimal but organized, structured and re-usable CloudSim Plus example
  * which shows good coding practices for creating simulation scenarios.
  *
  * <p>It defines a set of constants that enables a
  * to change the number of Hosts, VMs and Cloudlets developer to create
  * and the number of {@link Pe}s for Hosts, VMs and Cloudlets.</p>
  *
  * @author Manoel Campos da Silva Filho
  * @since CloudSim Plus 1.0
  */
object BasicFirstExample {
  private val HOSTS = 1
  private val HOST_PES = 8
  private val VMS = 2
  private val VM_PES = 4
  private val CLOUDLETS = 4
  private val CLOUDLET_PES = 2
  private val CLOUDLET_LENGTH = 10000

  def main(args: Array[String]): Unit = {
    new BasicFirstExample
  }
}

class BasicFirstExample() {
  /*Enables just some level of log messages.
           Make sure to import org.cloudsimplus.util.Log;*/
  //Log.setLevel(ch.qos.logback.classic.Level.WARN);

  val simulation: CloudSim = new CloudSim
  val datacenter0 = createDatacenter

  //Creates a broker that is a software acting on behalf a cloud customer to manage his/her VMs and Cloudlets
  val broker0: DatacenterBroker = new DatacenterBrokerSimple(simulation)
  val vmList = createVms
  val cloudletList = createCloudlets
  broker0.submitVmList(vmList)
  broker0.submitCloudletList(cloudletList)
  simulation.start

  val finishedCloudlets: java.util.List[Cloudlet] = broker0.getCloudletFinishedList()
  new CloudletsTableBuilder(finishedCloudlets).build()

  /**
    * Creates a Datacenter and its Hosts.
    */
  private def createDatacenter = {
    val hostList = new util.ArrayList[Host](BasicFirstExample.HOSTS)

    for( i <- 0 until BasicFirstExample.HOSTS) {
      val host = createHost
      hostList.add(host)
    }

    //Uses a VmAllocationPolicySimple by default to allocate VMs
    new DatacenterSimple(simulation, hostList)
  }

  private def createHost = {
    val peList = new util.ArrayList[Pe](BasicFirstExample.HOST_PES)

    //List of Host's CPUs (Processing Elements, PEs)
    for(i<- 0 until BasicFirstExample.HOST_PES) {
      peList.add(new PeSimple(1000))
    }

    val ram = 2048 //in Megabytes
    val bw = 10000 //in Megabits/s
    val storage = 1000000
    /*Uses ResourceProvisionerSimple by default for RAM and BW provisioning
      and VmSchedulerSpaceShared for VM scheduling.*/

    new HostSimple(ram, bw, storage, peList)
  }

  /**
    * Creates a list of VMs.
    */
  private def createVms = {
    val list = new util.ArrayList[Vm](BasicFirstExample.VMS)
    var i = 0
    while ({i < BasicFirstExample.VMS}) {
      //Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
      val vm = new VmSimple(1000, BasicFirstExample.VM_PES)
      vm.setRam(512).setBw(1000).setSize(10000)
      list.add(vm)

      {i += 1; i - 1}
    }
    list
  }

  /**
    * Creates a list of Cloudlets.
    */
  private def createCloudlets = {
    val list = new util.ArrayList[Cloudlet](BasicFirstExample.CLOUDLETS)

    //UtilizationModel defining the Cloudlets use only 50% of any resource all the time
    val utilizationModel = new UtilizationModelDynamic(0.5)

    var i = 0
    while ( {
      i < BasicFirstExample.CLOUDLETS
    }) {
      val cloudlet = new CloudletSimple(BasicFirstExample.CLOUDLET_LENGTH, BasicFirstExample.CLOUDLET_PES, utilizationModel)
      cloudlet.setSizes(1024)
      list.add(cloudlet)

      {
        i += 1; i - 1
      }
    }
    list
  }
}