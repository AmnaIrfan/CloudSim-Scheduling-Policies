package org.cloudsim.extensions;

import org.cloudbus.cloudsim.UtilizationModel;
import java.util.List;

public class CloudletWithPriority extends CloudletWithCost {

    private CloudletPriorityLevel priorityLevel;

    public CloudletWithPriority(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw, CloudletPriorityLevel priorityLevel) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw);
        this.priorityLevel = priorityLevel;
    }

    public CloudletWithPriority(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw, boolean record, List<String> fileList, CloudletPriorityLevel priorityLevel){
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw, record, fileList);
        this.priorityLevel = priorityLevel;
    }

    public CloudletWithPriority(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw, List<String> fileList, CloudletPriorityLevel priorityLevel) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw, fileList);
        this.priorityLevel = priorityLevel;
    }

    CloudletWithPriority(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw, boolean record, CloudletPriorityLevel priorityLevel) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw, record);
        this.priorityLevel = priorityLevel;
    }

    public CloudletPriorityLevel getPriorityLevel() {
        return this.priorityLevel;
    }

    void setPriorityLevel(CloudletPriorityLevel priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

}
