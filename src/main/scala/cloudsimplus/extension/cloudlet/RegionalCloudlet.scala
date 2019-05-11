package cloudsimplus.extension.cloudlet

import com.uic.cs441.project.regions.Region.Region
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet

class RegionalCloudlet(id: Int, length: Long, pesNumber: Int, region: Region)
  extends NetworkCloudlet(id: Int, length: Long, pesNumber: Int) {
  val reg: Region = region

  def getRegion(): Region = {
    reg
  }
}
