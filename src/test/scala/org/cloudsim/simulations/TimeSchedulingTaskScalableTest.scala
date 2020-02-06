package org.cloudsim.simulations

import java.util.Calendar

import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.{Vm, _}
import org.cloudbus.cloudsim.core.CloudSim
import org.junit.{After, Before}
import org.scalatest.{BeforeAndAfterEach, FunSuite}

import scala.collection.mutable.ListBuffer

class TimeSchedulingTaskScalableTest extends FunSuite with BeforeAndAfterEach with LazyLogging {
  @Before
  override def beforeEach(): Unit = {
    val num_user = 2
    val calendar = Calendar.getInstance
    val trace_flag = false
    CloudSim.init(num_user, calendar, trace_flag)
  }

  test("TimeSchedulingTaskScalableTest.createVMReturnsValidList\n") {
    logger.info("Verifying VM Creation returns list of specified size\n")
    val actualVmList: ListBuffer[Vm] = TimeSchedulingTaskScalable.createVM(4, 23)
    assert(actualVmList.size === 23)
  }

  test("TimeSchedulingTaskScalableTest.createCloudletReturnsValidList\n") {
    logger.info("Verifying Cloudlet Creation returns list of specified size\n")
    val actualCloudletList: ListBuffer[Cloudlet] = TimeSchedulingTaskScalable.createCloudlet(5, 42)
    assert(actualCloudletList.size === 42)
  }

  test("TimeSchedulingTaskScalableTest.createDataCenter\n") {
    logger.info("Verifying Cloudlet Creation returns a Datacenter Object\b")
    val dataCenter = TimeSchedulingTaskScalable.createDatacenter("Datacenter_0")
    assert(dataCenter.isInstanceOf[Some[Datacenter]])
  }

  test("TimeSchedulingTaskScalableTest.createBroker") {
    logger.info("Verifying Broker Creation returns a DatacenterBroker Object")
    val broker = TimeSchedulingTaskScalable.createBroker
    assert(broker.isInstanceOf[Some[DatacenterBroker]])
  }


  @After
  override def afterEach(): Unit = {
    // Do Nothing
  }
}
