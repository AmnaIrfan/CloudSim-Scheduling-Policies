package org.cloudsim.mapreduce;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;

import java.util.*;

public class Mapper extends Cloudlet {
    private List<MapperInput> input;
    private Map<Integer, List<Integer>> output;

    public Mapper(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw, List<MapperInput> input) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw);
        this.input = input;
    }

    public Mapper(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw, boolean record, List<String> fileList, List<MapperInput> input) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw, record, fileList);
        this.input = input;
    }

    public Mapper(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw, List<String> fileList, List<MapperInput> input) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw, fileList);
        this.input = input;
    }

    public Mapper(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw, boolean record, List <MapperInput> input) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw, record);
        this.input = input;
    }


    public Map<Integer, List<Integer>> getOutput() {
        return output;
    }

    public boolean respondToHeartBeatRequest() {
        return true;
    }

    public void mapInput(){
        Iterator iterator = this.input.iterator();
        this.output = new HashMap<>();
        while(iterator.hasNext()) {
            MapperInput i = (MapperInput) iterator.next();
            if (this.output.containsKey(i.getKey())) {
                this.output.get(i.getKey()).add(i.getValue());
            } else {
                List<Integer> newList = new ArrayList<Integer>();
                newList.add(i.getValue());
                this.output.put(i.getKey(), newList);
            }

        }
    }
}
