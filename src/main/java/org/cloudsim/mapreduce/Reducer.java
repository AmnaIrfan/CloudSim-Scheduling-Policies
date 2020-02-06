package org.cloudsim.mapreduce;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;

import java.util.*;

public class Reducer extends Cloudlet {
    private Integer key;
    private List<Integer> input;
    private Integer output;

    public Reducer(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw, Integer key, List<Integer> values) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw);
        this.key = key;
        this.input = values;
    }

    public Reducer(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw, boolean record, List<String> fileList, Integer key, List<Integer> values){
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw, record, fileList);
        this.key = key;
        this.input = values;
    }

    public Reducer(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw, List<String> fileList, Integer key, List<Integer> values) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw, fileList);
        this.key = key;
        this.input = values;
    }

    public Reducer(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw, boolean record, Integer key, List<Integer> values) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw, record);
        this.key = key;
        this.input = values;
    }

    public boolean respondToHeartBeatRequest() {
        return true;
    }

    public void reduceInput(){
        this.output = 0;
        for(Integer k : this.input ) {
            this.output += k;
        }
    }

    public Integer getKey() {
        return key;
    }

    public Integer getOutput() {
        return output;
    }
}
