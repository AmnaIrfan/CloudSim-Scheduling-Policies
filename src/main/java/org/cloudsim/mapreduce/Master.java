package org.cloudsim.mapreduce;


import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.util.*;

public class Master {
    private List<Mapper> mappers;
    private List<Reducer> reducers;
    private int taskLength;
    private int fileSize;
    private String inputDirectory;
    private String outputDirectory;
    private String mapperScript;
    private String reducerScript;
    private List<MapperInput> mapperInput;
    private Map<Integer, List<Integer>> mapperOutput;
    private Map<Integer, Integer> reducerOutput;
    private String state;
    private DatacenterBroker broker;
    private Datacenter datacenter;

    public Master(List<MapperInput> input, int taskLength, int fileSize, String mapperScript, String reducerScript, String inputDirectory, String outputDirectory ){
            //Initializing the job
            this.mapperInput = input;
            this.taskLength = taskLength;
            this.fileSize = fileSize;
            this.mapperScript = mapperScript;
            this.reducerScript = reducerScript;
            this.inputDirectory = inputDirectory;
            this.outputDirectory = outputDirectory;
    }

    public void submitJob() {
        TimerTask timerTask = new CheckpointTask();
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 10*1000);
        System.out.println("********** Map/Reduce Input **********");
        Iterator mapperIterator = this.mapperInput.iterator();
        while(mapperIterator.hasNext()) {
            MapperInput m = (MapperInput) mapperIterator.next();
            System.out.println("("+m.getKey()+", "+m.getValue()+")");
        }

        System.out.println("********** Submitting Mapper Jobs **********");
        startMappers();
        processMappers();
        getMapperOutput();
        requestMapperHeartBeat();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("********** Submitting Reducer Jobs **********");
        //based on keys
        startReducers(this.mapperOutput.keySet().size());
        requestReducerHeartBeat();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        processReducers();
        getReducerOutput();
        timer.cancel();
    }

    private boolean startMappers() {
        try {
            int num_user = 1;
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;
            CloudSim.init(num_user, calendar, trace_flag);
            Datacenter datacenter0 = createDatacenter("Datacenter_0");
            DatacenterBroker broker = createBroker();
            this.datacenter = datacenter0;
            this.broker = broker;
            int brokerId = broker.getId();
            List<Vm> vmList = new ArrayList();
            int vmid = 0;
            int mips = 1000;
            long size = 10000L;
            int ram = 512;
            long bw = 1000L;
            int pesNumber = 1;
            String vmm = "Xen";
            Vm vm = new Vm(vmid, brokerId, (double)mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            vmList.add(vm);
            broker.submitVmList(vmList);
            this.mappers = new ArrayList();
            for (int i = 0; i < this.taskLength/1000; i++) {
                int id = i;
                long length = this.taskLength/1000;
                long fileSize = this.fileSize/(this.taskLength/1000);
                long outputSize = 300;
                UtilizationModel utilizationModel = new UtilizationModelFull();
                Mapper mapperCloudlet = new Mapper(id, length, 1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel, mapperInput.subList(i * 10,(i + 1) * 10));
                mapperCloudlet.setUserId(this.broker.getId());
                this.mappers.add(mapperCloudlet);
            }
            broker.submitCloudletList(mappers);
            CloudSim.startSimulation();
            CloudSim.stopSimulation();
        } catch (Exception var27) {
            var27.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
        return true;
    }

    public boolean startReducers(int numberOfReducers) {
        try {
            int num_user = 1;
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;
            CloudSim.init(num_user, calendar, trace_flag);
            Datacenter datacenter1 = createDatacenter("Datacenter_1");
            DatacenterBroker broker = createBroker();
            List<Vm> vmList = new ArrayList();
            int vmid = 1;
            int mips = 1000;
            long size = 10000L;
            int ram = 512;
            long bw = 1000L;
            int pesNumber = 1;
            String vmm = "Xen";
            Vm vm = new Vm(vmid, broker.getId(), (double)mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            vmList.add(vm);
            broker.submitVmList(vmList);
            this.reducers = new ArrayList();
            int id = 0;
            for (Integer key: this.mapperOutput.keySet()) {
                long length = this.taskLength/1000;
                long fileSize = 300;
                long outputSize = 300;
                UtilizationModel utilizationModel = new UtilizationModelFull();
                Reducer reducerCloudlet = new Reducer(id, length, 1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel, key, this.mapperOutput.get(key) );
                reducerCloudlet.setUserId(this.broker.getId());
                this.reducers.add(reducerCloudlet);
                id++;
            }
            broker.submitCloudletList(reducers);
            CloudSim.startSimulation();
            CloudSim.stopSimulation();
        } catch (Exception var27) {
            var27.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
        return true;
    }

    public void requestMapperHeartBeat() {
        Iterator mapperIterator = this.mappers.iterator();
        while(mapperIterator.hasNext()) {
            Mapper m = (Mapper) mapperIterator.next();
            if (m.respondToHeartBeatRequest()) {
                System.out.println("*****Mapper ID: " + m.getCloudletId() + " running on VM " + m.getVmId()+ " responded to heartbeat!*****");
            } else {
                restartWorker();
            }
        }
    }

    public void requestReducerHeartBeat() {
        Iterator reducerIterator = this.reducers.iterator();
        while(reducerIterator.hasNext()) {
            Reducer r = (Reducer) reducerIterator.next();
            if (r.respondToHeartBeatRequest()) {
                System.out.println("*****Reducer ID: " + r.getCloudletId() + " running on VM " + r.getVmId()+ " responded to heartbeat!*****");
            } else {
                restartWorker();
            }
        }
    }

    public void processMappers() {
        Iterator mapperIterator = this.mappers.iterator();
        while(mapperIterator.hasNext()) {
            Mapper m = (Mapper) mapperIterator.next();
            m.mapInput();
        }
    }
    public void processReducers() {
        Iterator reducerIterator = this.reducers.iterator();
        while(reducerIterator.hasNext()) {
            Reducer r = (Reducer) reducerIterator.next();
            r.reduceInput();
        }
    }
    public void getMapperOutput() {
        this.mapperOutput = new HashMap<>();
        Iterator mapperIterator = this.mappers.iterator();
        while(mapperIterator.hasNext()) {
            Mapper m = (Mapper) mapperIterator.next();
            Map<Integer, List<Integer>> o = m.getOutput();
            System.out.println("********** Output from Mapper " + m.getCloudletId() + " **********");
            System.out.println(o);
            for (Integer key : o.keySet()) {
                if (this.mapperOutput.containsKey(key)) {
                    List<Integer> values = o.get(key);
                    for (Integer val: values) {
                        this.mapperOutput.get(key).add(val);
                    }
                } else {
                    this.mapperOutput.put(key, o.get(key));
                }
            }
        }
        System.out.println("********** Final Mapper Output **********");
        System.out.println(this.mapperOutput);
    }
    public void getReducerOutput() {
        this.reducerOutput = new HashMap<>();
        Iterator reducerIterator = this.reducers.iterator();
        while(reducerIterator.hasNext()) {
            Reducer r = (Reducer) reducerIterator.next();
            this.reducerOutput.put(r.getKey(), r.getOutput());
        }
        System.out.println("********** Final Reducer Output **********");
        System.out.println(this.reducerOutput);
    }

    public boolean restartWorker() {
        return true;
    }

    public static void checkpoint() {
        System.out.println("*****Saved Master's State!!*****");
    }

    private static Datacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList();
        List<Pe> peList = new ArrayList();
        int mips = 1000;
        peList.add(new Pe(0, new PeProvisionerSimple((double)mips)));
        int hostId = 0;
        int ram = 2048;
        long storage = 1000000L;
        int bw = 10000;
        hostList.add(new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple((long)bw), storage, peList, new VmSchedulerTimeShared(peList)));
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0D;
        double cost = 3.0D;
        double costPerMem = 0.05D;
        double costPerStorage = 0.001D;
        double costPerBw = 0.0D;
        LinkedList<Storage> storageList = new LinkedList();
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);
        Datacenter datacenter = null;

        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0.0D);
        } catch (Exception var26) {
            var26.printStackTrace();
        }

        return datacenter;
    }

    private static DatacenterBroker createBroker() {
        DatacenterBroker broker = null;

        try {
            broker = new DatacenterBroker("Broker");
            return broker;
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

}

class CheckpointTask extends TimerTask {
    @Override
    public void run() {
        Master.checkpoint();
    }
}

class MapperInput {
    private int key;
    private int value;
    MapperInput(int key, int value){
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }
}