package org.cloudsim.extensions;

import org.cloudbus.cloudsim.*;
import java.util.List;

public class CloudletWithCost extends Cloudlet {

    public CloudletWithCost(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw)
    {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw);
    }

    public CloudletWithCost(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw, boolean record, List<String> fileList){
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw, record, fileList);
    }

    public CloudletWithCost(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw, List<String> fileList) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw, fileList);
    }

    public CloudletWithCost(int cloudletId, long cloudletLength, int pesNumber, long cloudletFileSize, long cloudletOutputSize, UtilizationModel utilizationModelCpu, UtilizationModel utilizationModelRam, UtilizationModel utilizationModelBw, boolean record) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw, record);
    }

    public double getProcessingCost()  {
        //Get resource usage by data centers used to host this cloudlet
        double totalCost = 0.0D;
        int[] resourceIds = this.getAllResourceId();
        for (int i = 0; i < resourceIds.length; i++) {
            //Get resource per second cost from data center and the cpu time taken by cloudlet on the datacenter hosts
            //This cost per second from the data center includes bandwidth cost, memory and storage costs.
            totalCost += this.getCostPerSec(resourceIds[i]) * this.getActualCPUTime(resourceIds[i]);
        }
        //Add bandwidth cost of processing the cloudlet
        totalCost += this.accumulatedBwCost + super.getProcessingCost();

        return totalCost;
    }
}
