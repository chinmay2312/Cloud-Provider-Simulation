package cloudsimplus.extension.vm

import com.uic.cs441.project.regions.Region.Region
import org.cloudbus.cloudsim.vms.network.NetworkVm

class RegionalVm(id: Int, mips: Long, pes: Int, region: Region)
  extends NetworkVm(id: Int, mips: Long, pes: Int) {

  val reg: Region = region

  def getRegion(): Region = {
    reg
  }
}
