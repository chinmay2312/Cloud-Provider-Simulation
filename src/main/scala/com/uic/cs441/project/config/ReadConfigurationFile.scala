/*
package com.uic.cs441.project.config

import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple

import scala.collection.JavaConverters._
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim._
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterCharacteristics}
import org.cloudbus.cloudsim.hosts.Host
import org.cloudbus.cloudsim.provisioners.{BwProvisionerSimple, PeProvisionerSimple, RamProvisionerSimple}
import org.cloudbus.cloudsim.resources.{Pe, SanStorage, Storage}
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletScheduler, CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared
import org.cloudbus.cloudsim.utilizationmodels.{UtilizationModel, UtilizationModelFull}
import org.cloudbus.cloudsim.vms.Vm

import scala.collection.JavaConverters._

class ReadConfigurationFile(file: String) extends LazyLogging {
  //Type safe config object
  val config: Config = ConfigFactory.load(file)

  /**
    * Read the VM data from the configuration file
    *
    * @param brokerId Broker who submits the VMs
    * @return
    */
  def readVms(brokerId: Int, schedulerOpt: String): List[Vm] = {
    logger.debug("readVms(brokerId:{})", brokerId)
    config.getConfigList("vms").asScala.toList
      .map(vm => {
        new Vm(vm.getInt("id"),
          brokerId,
          vm.getDouble("mips"),
          vm.getInt("no_of_pes"),
          vm.getInt("ram"),
          vm.getLong("bw"),
          vm.getLong("size"),
          vm.getString("vmm"),
          provideScheduler(schedulerOpt))
      })
  }

  def provideScheduler(op: String): CloudletScheduler = {
    op match {
      case "1" => new CloudletSchedulerTimeShared
      case "2" => new CloudletSchedulerSpaceShared
    }
  }

  /**
    * Read the cloudlets from the configuration file and provide the id of the broker.
    *
    * @param brokerId Broker who submits the Cloudlets
    * @return
    */
  def readCloudlets(brokerId: Int): List[Cloudlet] = {
    logger.debug("readCloudlets(brokerId:{})", brokerId)
    config.getConfigList("cloudlets").asScala.toList
      .map(cloudlet => {
        val um: UtilizationModel = new UtilizationModelFull
        val temp: Cloudlet = new Cloudlet(cloudlet.getInt("id"),
          cloudlet.getLong("length"),
          cloudlet.getInt("pes"),
          cloudlet.getLong("file_size"),
          cloudlet.getLong("output_size"),
          um,
          um,
          um
        )
        temp.setUserId(brokerId)
        //        temp.setVmId(cloudlet.getInt("vm_id"))
        temp
      })
  }

  /**
    * Read the broker from the configuration file.
    *
    * @return
    */
  def readBroker: DatacenterBroker = {
    logger.debug("readBroker():{}", config.getConfig("broker"))
    new DatacenterBroker(config.getString("broker.name"))
  }

  /**
    * Read the values required to init the CloudSim
    *
    * @param fileName Config file name located in the src/main/resources folder
    * @return
    */
  def readInitData: List[Any] = {
    logger.debug("readInitData():{},{}", config.getInt("num_user"), config.getBoolean("trace_flag"))
    List(config.getInt("num_user"), config.getBoolean("trace_flag"))
  }

  /**
    * Read the Data centers and initialization the entities like the host, DatacenterCharacteristics etc.
    *
    * @param fileName
    * @return
    */
  def readDataCenters: List[Datacenter] = {
    logger.debug("readDataCenters():{}", config.getConfigList("data_centers"))
    config.getConfigList("data_centers")
      .asScala
      .toList
      .map(config => {
        val hostList: List[Host] = config.getConfigList("hosts").asScala.toList.map(host => {
          val pesList: List[_ <: Pe] = host.getConfigList("pes").asScala.toList.map(pe => {
            new Pe(pe.getInt("id"),
              new PeProvisionerSimple(pe.getInt("mips")))
          })
          new Host(host.getInt("id"),
            new RamProvisionerSimple(host.getInt("ram")),
            new BwProvisionerSimple(host.getLong("bw")),
            host.getLong("storage"),
            pesList.asJava,
            new VmSchedulerTimeShared(pesList.asJava)
          )
        })

        val dcCharString: String = "datacenter_characteristics"

        val characteristics = new DatacenterCharacteristics(
          config.getString(s"$dcCharString.arch"),
          config.getString(s"$dcCharString.os"),
          config.getString(s"$dcCharString.vmm"),
          hostList.asJava,
          config.getDouble(s"$dcCharString.time_zone"),
          config.getDouble(s"$dcCharString.cost"),
          config.getDouble(s"$dcCharString.costPerMem"),
          config.getDouble(s"$dcCharString.costPerStorage"),
          config.getDouble(s"$dcCharString.costPerBw"))
        //storage

        val storageList: List[Storage] = config.getConfigList("storage")
          .asScala
          .toList
          .map(str => new SanStorage(str.getDouble("capacity"),
            str.getDouble("bw"),
            str.getDouble("netLatency")))

        new Datacenter(config.getString("datacenter_name"),
          characteristics,
          new VmAllocationPolicySimple(hostList.asJava),
          storageList.asJava,
          config.getDouble("scheduling_interval")
        )
      }
      )
  }
}*/
