package com.uic.cs441.project.config
import org.cloudbus.cloudsim.cloudlets.CloudletSimple
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.scalatest.FlatSpec
import com.uic.cs441.project.config.ConfigReader._

class ConfigReaderTest extends FlatSpec {

  private val vm = new VmSimple(1000, 4)
  private val utilizationModel = new UtilizationModelDynamic(0.5)
  private val cloudlet = new CloudletSimple(10000, 2, utilizationModel)

  "getExpectedCloudletCompletionTime" should "return a double value which is the Expected Cloudlet Completion Time" in{
    val x = getExpectedCloudletCompletionTime(cloudlet,vm)
    assert(x.isInstanceOf[Double])
  }

  "getExpectedNumberOfFreeVmPes" should "return the numberOfVmFreePes = no of free PEs in VM" in{
    val x = getExpectedNumberOfFreeVmPes(vm)
    assert(x == 4)
  }

  "cloudletToVmMappingTimeMinimized" should "return a Vm" in{
    val x = cloudletToVmMappingTimeMinimized(cloudlet)
    assert(x.isInstanceOf[Vm])
  }

  "getHostValues" should "return type HostValues" in{
    val x = getHostValues
    assert(x.isInstanceOf[HostValues])
  }

  "getVmValues" should "return type VmValues" in{
    val x = getVmValues
    assert(x.isInstanceOf[VmValues])
  }


}
