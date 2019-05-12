package cloudsimplus.extension.vm

import com.uic.cs441.project.regions.Region.Region
import org.cloudbus.cloudsim.vms.network.NetworkVm

/**
  * Extends NetworkVm by adding Region property
  *
  * @param id unique ID of the VM
  * @param mips the mips capacity of each Vm { @link Pe}
  * @param pes amount of PEs (CPU cores)
  * @param region Region to which VM belongs
  */
class RegionalVm(id: Int, mips: Long, pes: Int, region: Region)
  extends NetworkVm(id: Int, mips: Long, pes: Int) {

  val reg: Region = region

  def getRegion(): Region = {
    reg
  }
}
