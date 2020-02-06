## Description: Create cloud simulators for evaluating executions of applications/cloudlets in cloud datacenters with different characteristics and scheduling policies.
### Name: Amna Irfan

### Instructions

Install [IntelliJ](https://www.jetbrains.com/student/), JDK, Scala runtime, IntelliJ Scala plugin and the [Simple Build Toolkit (SBT)](https://www.scala-sbt.org/1.x/docs/index.html) and make sure that you can run Java monitoring tools.

Open the project in IntelliJ and build the project. This may take some time as the Library Dependencies mentioned in the build.sbt file will be downloaded and added to the classpath of the project.

Alternatively, you can run the project using command line. Open the terminal and `cd` into the project directory. 

Enter the command `sbt clean compile run` to run the cloud simulations. This will both run and compile the project. You will be prompted to select a simulation to run. Each simulation is a separate Scala class with a main function.

1. org.cloudsim.mapreduce.MapReduce
2. org.cloudsim.simulations.FIFOSchedulingTaskScalable
3. org.cloudsim.simulations.LIFOSchedulingTaskScalable
4. org.cloudsim.simulations.PrioritySchedulingTaskScalable
5. org.cloudsim.simulations.SJFSchedulingTaskScalable
6. org.cloudsim.simulations.SpaceSchedulingTaskScalable
7. org.cloudsim.simulations.TimeSchedulingTaskScalable

Options 2 - 7 are simulations of different instances of cloudlet scheduling policies.


This project has a total of 24 tests in Scala. In order to run them using the terminal, `cd` into the project directory and run the command `sbt clean compile test`


The results of the simulation and additional logging will be printed in the terminal.

The configuration files used by the simulations are located under src/main/resources. You can change the configuration files directly to play around with the simulations.
