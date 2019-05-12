package com.uic.cs441.project.generator

import cloudsimplus.extension.vm.RegionalVm
import com.uic.cs441.project.MainApp.createVms
import com.uic.cs441.project.config.ConfigReader.{getHostValues, getVMAllocationPolicy, getVmScheduler}
import com.uic.cs441.project.generator.Generator.{createCloudlet, createDataCenter, createPes, createVM}
import com.uic.cs441.project.regions.Region
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
import org.cloudbus.cloudsim.resources.Pe
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared
import org.cloudbus.cloudsim.schedulers.vm.VmScheduler
import org.cloudbus.cloudsim.vms.Vm
import org.scalatest.FlatSpec

/**
  * Unit tests for Generator
  *
  * @author Karan Kadakia
  */
class GeneratorTest extends FlatSpec{

  "createPes" should "return a list of size 3" in {
    val peCount =3
    val peList: List[Pe] = createPes(peCount, 1000)
    assert(peList.length == peCount)
  }

  "createDataCenter" should "return a Datacenter tyoe" in {
    val cloudsim = new CloudSim()
    val broker  = new DatacenterBrokerSimple(cloudsim)
    val vmList : java.util.List[RegionalVm] = createVms()
    broker.submitVmList(vmList)
    val returntype =  createDataCenter(Region.REGION1, cloudsim, getVMAllocationPolicy, 5, getHostValues.ram,
      getHostValues.bw, getHostValues.storage, getHostValues.pes, getHostValues.mips, getVmScheduler)
    assert(returntype.isInstanceOf[Datacenter])
  }

  "createVM" should "return a Vm type" in {
    val vm = createVM(Region.REGION1, 1, 4, 1000, 3, 4, 1000, new CloudletSchedulerTimeShared)
    assert(vm.isInstanceOf[Vm])
  }

  "createCloudlet" should "return Cloudlet type" in {
    val cloudlet = createCloudlet(Region.REGION1, 4, 4, 200, 200,200)
    assert(cloudlet.isInstanceOf[Cloudlet])
  }

}
