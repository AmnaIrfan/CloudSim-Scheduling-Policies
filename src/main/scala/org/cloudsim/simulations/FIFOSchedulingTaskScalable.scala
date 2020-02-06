package org.cloudsim.simulations

import java.text.DecimalFormat
import java.util
import java.util.Calendar

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim._
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.provisioners.{BwProvisionerSimple, PeProvisionerSimple, RamProvisionerSimple}
import org.cloudsim.extensions.{CloudletSchedulerFIFO, CloudletWithCost}

import scala.collection.JavaConverters._
import scala.collection.{immutable, mutable}

/*
 * A CloudSim Simulation showing how to create scalable cloud simulations.
 * In this simulation, we prioritize tasks based on first come first serve basis
 *
 * @author Amna Irfan
 */

// Main class
object FIFOSchedulingTaskScalable extends App with LazyLogging {

  /*
   * Load Typesafe Lightbend Configuration Files
   */
  val defaultConfig: Config = ConfigFactory.parseResources("simulation_fifo.conf")
  val fallbackConfig: Config = ConfigFactory.parseResources("simulation_fifo.conf")
    .withFallback(FIFOSchedulingTaskScalable.defaultConfig).resolve
  logger.info("Configuration files loaded")


  /*
  * Creates main() to run this example
  */
  Log.printLine("Starting FIFO Scheduling Task...")

  try {

    // number of cloud users
    val num_user = fallbackConfig.getInt("broker.num_user")
    // Calendar set with today's date
    val calendar = Calendar.getInstance
    // Flag to turn on/off logging of cloud events
    val trace_flag = false

    // STEP 1 -> Initialize CloudSim
    logger.info("Initializing cloudsim library")
    CloudSim.init(num_user, calendar, trace_flag)
    logger.info("Cloudsim library initialized")


    // STEP 2 -> Create Datacenters
    /*
       Datacenters are housing units for commodity computers.
       They contain physical computers (hosts) that the databroker's virtual machines will run on.
       We need atleast one datacenter to start cloudsim simulation.
    */
    logger.info("Creating Datacenters")
    // TODO - DataCenters with similar cost of resources are created
    @SuppressWarnings(Array.apply("unused")) val datacenter0 = FIFOSchedulingTaskScalable.createDatacenter("Datacenter_0")
    @SuppressWarnings(Array.apply("unused")) val datacenter1 = FIFOSchedulingTaskScalable.createDatacenter("Datacenter_1")
    logger.info("CIS created")
    logger.info("Datacenter creation completed")



    //STEP 3 -> Create Datacenter broker
    /*
        Datacenter Brokers can be considered the middle man between customers and the datacenter.
        They purchase virtual machines that are later ran on physical hosts of datacenters.
        We need atleast one datacenter broker to start cloudsim simulation.
    */
    logger.info("Creating datacenter broker")
    val Some(broker: DatacenterBroker) = FIFOSchedulingTaskScalable.createBroker
    logger.info("Dataset broker created")
    val brokerId = broker.getId



    // STEP 4 -> Add virtual machines.
    /*
        These virtual machines are later assigned to datacenter brokers.
     */
    logger.info("Creating Virtual Machines (VM)")
    val total_vms = fallbackConfig.getInt("vm.num_vms")
    val vmlist = FIFOSchedulingTaskScalable.createVM(brokerId, total_vms) //creating 20 vms
    logger.info("Virtual Machines (VM) created successfully")



    // STEP 5 -> Add Cloudlets.
    /*
        Cloudlets are tasks that are assigned to datacenter brokers.
        During initialization, a cloudlet can manually be assigned to one of the datacenter broker's vm.
        If no virtual machine is manually assigned, the VM uses a Cloudlet Allocation and Scheduling Policy to execute cloudlets.
     */
    logger.info("Spawning cloudlets")
    val total_cloudlets = fallbackConfig.getInt("cloudlet.num_cloudlets")
    val cloudletList = FIFOSchedulingTaskScalable.createCloudlet(brokerId, total_cloudlets) // creating 40 cloudlets
    logger.info("Cloudlets spawned successfully")



    // STEP 6 --> Submit previously created virtual machines to broker.
    broker.submitVmList(vmlist.toList.asJava)
    logger.info("Virtual machines submitted to Broker")



    // STEP 7 --> Submit previously created cloudlets to broker.
    broker.submitCloudletList(cloudletList.toList.asJava)
    logger.info("Cloudlets submitted to broker")



    // STEP 8 --> Start simulation
    CloudSim.startSimulation
    logger.info("Simulation started")



    // STEP 8 --> End simulation
    CloudSim.stopSimulation()
    logger.info("Simulation ended")


    // STEP 9 --> Print Cloudlet Results
    val newList = broker.getCloudletReceivedList

    FIFOSchedulingTaskScalable.printCloudletList(newList)
    logger.info("Cloudlet results printed")

    Log.printLine("FIFOSchedulingTaskScalable completed!")
  } catch {
    case e: Exception =>
      e.printStackTrace()
      Log.printLine("The simulation has been terminated due to an unexpected error")
  }


  /**
   * Creates multiple virtual machine with same specs. These machines are later submitted to the data broker.
   *
   * @return a list of virtual machines
   */
  def createVM(userId: Int, vms: Int) = {

    val defaultConfig: Config = ConfigFactory.parseResources("simulation_fifo.conf")
    val fallbackConfig: Config = ConfigFactory.parseResources("simulation_fifo.conf")
      .withFallback(defaultConfig).resolve
    logger.info("Configuration files loaded")

    val list = new mutable.ListBuffer[Vm]()

    //Get vm size
    val size = fallbackConfig.getInt("vm.size")

    //Get vm ram
    val ram = fallbackConfig.getInt("vm.ram")

    //Get Vm mips
    val mips = fallbackConfig.getInt("vm.mips")

    //Get bandwidth of vm's network
    val bw = fallbackConfig.getInt("vm.bw")

    //Get number of CPUs per vm
    val pesNumber = fallbackConfig.getInt("vm.pesNumber")

    //VMM name
    val vmm = fallbackConfig.getString("vm.vmm")

    //create VMs
    val vm = new Array[Vm](vms)

    val range_vms = immutable.List.range(0, vms)(Numeric.IntIsIntegral)
    range_vms.foreach((vm_no: Int) => {
      //assign virtual machine the FIFO scheduler.
      vm(vm_no) = new Vm(vm_no, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerFIFO)
      list += vm.apply(vm_no)
    })
    list
  }


  /**
   * Creates cloudlets
   *
   * @return a list of cloudlets
   */
  def createCloudlet(userId: Int, cloudlets: Int) = {

    val defaultConfig: Config = ConfigFactory.parseResources("simulation_fifo.conf")
    val fallbackConfig: Config = ConfigFactory.parseResources("simulation_fifo.conf")
      .withFallback(defaultConfig).resolve

    val list = new mutable.ListBuffer[Cloudlet]()

    //Get cloudlet specs
    val length = fallbackConfig.getInt("cloudlet.length")
    val fileSize = fallbackConfig.getInt("cloudlet.fileSize")
    val outputSize = fallbackConfig.getInt("cloudlet.outputSize")
    val pesNumber = fallbackConfig.getInt("cloudlet.pesNumber")

    val utilizationModelToObject = Map(
      1 -> new UtilizationModelFull,
      2 -> new UtilizationModelNull,
      3 -> new UtilizationModelStochastic
    )

    val cloudletUtilizationModel = utilizationModelToObject(fallbackConfig.getInt("cloudlet.cloudletUtilizationModel"))

    val cloudlet = new Array[Cloudlet](cloudlets)
    val range_cloudlets = immutable.List.range(0, cloudlets)(Numeric.IntIsIntegral)
    range_cloudlets.foreach((cloudlet_no: Int) => {
      cloudlet(cloudlet_no) = new CloudletWithCost(cloudlet_no, length, pesNumber, fileSize, outputSize,
        cloudletUtilizationModel, cloudletUtilizationModel, cloudletUtilizationModel)
      // setting the owner of these Cloudlets
      cloudlet.apply(cloudlet_no).setUserId(userId)
      list += cloudlet.apply(cloudlet_no)
    })
    list
  }


  /**
   * Creates datacenters.
   *
   * @param name the name
   * @return the datacenter
   */
  def createDatacenter(name: String) : Option[Datacenter] = { // Here are the steps needed to create a PowerDatacenter:

    val defaultConfig: Config = ConfigFactory.parseResources("simulation_fifo.conf")
    val fallbackConfig: Config = ConfigFactory.parseResources("simulation_fifo.conf")
      .withFallback(defaultConfig).resolve

    //1. Create hosts
    val hostList = new util.ArrayList[Host]
    val mips = fallbackConfig.getInt("pe.mips")

    //2. Create and assign CPUs/Cores for first quad-core host
    val peList1 = new util.ArrayList[Pe]
    peList1.add(new Pe(0, new PeProvisionerSimple(mips)))
    peList1.add(new Pe(1, new PeProvisionerSimple(mips)))
    peList1.add(new Pe(2, new PeProvisionerSimple(mips)))
    peList1.add(new Pe(3, new PeProvisionerSimple(mips)))

    //3. Create and assign CPUs/Cores for second dual-core host
    val peList2 = new util.ArrayList[Pe]
    peList2.add(new Pe(0, new PeProvisionerSimple(mips)))
    peList2.add(new Pe(1, new PeProvisionerSimple(mips)))

    logger.info(name + " - All Processing Elements added to respective Pe lists")

    //4. Create Hosts and add PEs
    val ram = fallbackConfig.getInt("host.ram")
    val storage = fallbackConfig.getInt("host.storage")
    val bw = fallbackConfig.getInt("host.bw")
    val vmScheduling1 = fallbackConfig.getInt("host.vmScheduling1")
    val hostId1 = fallbackConfig.getInt("host.hostId1")

    //5. Create first host
    val host1 = vmScheduling1 match {
      case 1 => new Host(hostId1, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList1, new VmSchedulerSpaceShared(peList1))
      case 2 => new Host(hostId1, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList1, new VmSchedulerTimeShared(peList1))
      case 3 => new Host(hostId1, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList1, new VmSchedulerTimeSharedOverSubscription(peList1))
    }
    hostList.add(host1)

    val vmScheduling2 = fallbackConfig.getInt("host.vmScheduling2")
    val hostId2 = fallbackConfig.getInt("host.hostId2")

    //6. Create second host
    val host2 = vmScheduling2 match {
      case 1 => new Host(hostId2, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList2, new VmSchedulerSpaceShared(peList2))
      case 2 => new Host(hostId2, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList2, new VmSchedulerTimeShared(peList2))
      case 3 => new Host(hostId2, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, peList2, new VmSchedulerTimeSharedOverSubscription(peList2))
    }
    hostList.add(host2)

    logger.info(name + " - PEs added to Hosts")

    val arch = fallbackConfig.getString("datacenter.arch")
    // system architecture
    val os = fallbackConfig.getString("datacenter.os")
    // operating system
    val vmm = fallbackConfig.getString("datacenter.vmm")
    // virtual machine manager
    val time_zone = fallbackConfig.getInt("datacenter.time_zone")
    // time zone this resource located
    val costPerSec = fallbackConfig.getInt("datacenter.costPerSec")
    // the cost of using processing in this resource
    val costPerMem = fallbackConfig.getInt("datacenter.costPerMem")
    // the cost of using memory in this resource
    val costPerStorage = fallbackConfig.getInt("datacenter.costPerStorage")
    // the cost of using storage in this resource
    val costPerBw = fallbackConfig.getInt("datacenter.costPerBw")
    // the cost of using bandwidth in this resource
    val storageList = new util.LinkedList[Storage]
    //we are not adding SAN devices by now

    val characteristics = new DatacenterCharacteristics(arch, os, vmm, hostList, time_zone, costPerSec, costPerMem, costPerStorage, costPerBw)

    logger.info(name + " - Datacenter Characteristics finalized")

    // 7. Create final datacenter with hosts and characterstics
    try {
      val datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0)
      Some.apply(datacenter)
    }
    catch {
      case e: Exception => e.printStackTrace()
        scala.None
    }
  }


  /**
   * Creates the broker.
   *
   * @return the datacenter broker
   */
  def createBroker : Option[DatacenterBroker] = {
    try {
      val broker = new DatacenterBroker("Broker")
      Some.apply(broker)
    }
    catch {
      case e: Exception => e.printStackTrace()
        scala.None
    }
  }


  /**
   * Prints the Cloudlet objects
   *
   * @param list list of Cloudlets
   */
  def printCloudletList(list: util.List[_ <: Cloudlet]): Unit = {
    val indent = "    "
    Log.printLine()
    Log.printLine("========== OUTPUT ==========")
    Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent + indent +
      "Time" + indent + "Start Time" + indent + "Finish Time" + indent + "Processing Cost")
    val dft = new DecimalFormat("###.##")

    var totalCost: Double = 0.0D
    list.forEach((cloudlet: Cloudlet) => {
      Log.print(indent + cloudlet.getCloudletId + indent + indent)
      if (cloudlet.getCloudletStatus == Cloudlet.SUCCESS) {
        Log.print("SUCCESS")
        Log.printLine(indent + indent + cloudlet.getResourceId + indent + indent + indent + cloudlet.getVmId + indent +
          indent + indent + dft.format(cloudlet.getActualCPUTime) + indent + indent + dft.format(cloudlet.getExecStartTime) +
          indent + indent + indent + dft.format(cloudlet.getFinishTime) + indent + indent + indent +
          (Math.round(cloudlet.getProcessingCost * 100D)/100D))
        totalCost += cloudlet.getProcessingCost
      }
    })
    Log.printLine()
    Log.printLine("Total cost of processing " + list.size + " Cloudlets = $" + Math.round(totalCost * 100D)/100D)
  }
}