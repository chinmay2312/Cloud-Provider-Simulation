package com.uic.cs441.project.generator
import com.uic.cs441.project.MainApp.createVms
import com.uic.cs441.project.config.ConfigReader.{getVMAllocationPolicy, getVmScheduler, _}
import com.uic.cs441.project.generator.Generator._
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
import org.cloudbus.cloudsim.resources.Pe
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared
import org.cloudbus.cloudsim.vms.Vm
import org.scalatest.FlatSpec


class GeneratorTest extends FlatSpec {

  "createPes" should "return a list of size 4" in {
    val x: List[Pe] = createPes(4, 1000)
    assert(x.size == 4)
  }

  "createDataCenter" should "return a Datacenter tyoe" in {
    val cloudsim = new CloudSim()
    val broker  = new DatacenterBrokerSimple(cloudsim)
    val vmList : java.util.List[Vm] = createVms()
    broker.submitVmList(vmList)
    val returntype =  createDataCenter(cloudsim, getVMAllocationPolicy, 5, getHostValues.ram,
      getHostValues.bw, getHostValues.storage, getHostValues.pes, getHostValues.mips,
      getVmScheduler)
    assert(returntype.isInstanceOf[Datacenter])
  }

  "createVM" should "return a Vm type" in {
    val vm = createVM(1, 4, 1000, 3, 4, 1000, new CloudletSchedulerTimeShared)
    assert(vm.isInstanceOf[Vm])
  }

  "createCloudlet" should "return Cloudlet type" in {
    val cloudlet = createCloudlet(4, 4, 200, 200,200)
    assert(cloudlet.isInstanceOf[Cloudlet])
  }

}
