package cloudsimplus.extension.cloudlet

import com.uic.cs441.project.regions.Region.Region
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet

/**
  * @author Amrish Jhaveri
  * @param id     the unique ID of this cloudlet
  * @param length the length or size (in MI) of this cloudlet to be executed in a VM
  * @param pesNumber the pes number
  * @param region The source Region of the cloudlet
  */
class RegionalCloudlet(id: Int, length: Long, pesNumber: Int, region: Region)
  extends NetworkCloudlet(id: Int, length: Long, pesNumber: Int) {
  val reg: Region = region

  def getRegion(): Region = {    reg  }
}
