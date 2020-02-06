package org.cloudsim.extensions;

import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.ResCloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import java.util.*;

public class CloudletSchedulerFIFO extends CloudletSchedulerSpaceShared {
    @Override
    public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
        List<ResCloudlet> fifoQueue = new ArrayList<>();
        Iterator var13 = this.getCloudletWaitingList().iterator();
        while(var13.hasNext()) {
            ResCloudlet rcl = (ResCloudlet) var13.next();
            fifoQueue.add(rcl);
        }
        //Sort by Cloud id in ascending order -  First In First Out
        Collections.sort(fifoQueue, new Comparator<ResCloudlet>() {
            @Override
            public int compare(ResCloudlet r1, ResCloudlet r2) {
                return   r1.getCloudletId() - r2.getCloudletId() ;
            }
        });

        this.setCloudletWaitingList(fifoQueue);
        this.setCurrentMipsShare(mipsShare);
        double timeSpam = currentTime - this.getPreviousTime();
        double capacity = 0.0D;
        int cpus = 0;
        Iterator var9 = mipsShare.iterator();

        while(var9.hasNext()) {
            Double mips = (Double)var9.next();
            capacity += mips;
            if (mips > 0.0D) {
                ++cpus;
            }
        }

        this.currentCpus = cpus;
        capacity /= (double)cpus;
        var9 = this.getCloudletExecList().iterator();

        while(var9.hasNext()) {
            ResCloudlet rcl = (ResCloudlet)var9.next();
            rcl.updateCloudletFinishedSoFar((long)(capacity * timeSpam * (double)rcl.getNumberOfPes() * 1000000.0D));
        }

        if (this.getCloudletExecList().size() == 0 && this.getCloudletWaitingList().size() == 0) {
            this.setPreviousTime(currentTime);
            return 0.0D;
        } else {
            int finished = 0;
            List<ResCloudlet> toRemove = new ArrayList();
            Iterator var11 = this.getCloudletExecList().iterator();

            while(var11.hasNext()) {
                ResCloudlet rcl = (ResCloudlet)var11.next();
                if (rcl.getRemainingCloudletLength() == 0L) {
                    toRemove.add(rcl);
                    this.cloudletFinish(rcl);
                    ++finished;
                }
            }

            this.getCloudletExecList().removeAll(toRemove);
            if (!this.getCloudletWaitingList().isEmpty()) {
                for(int i = 0; i < finished; ++i) {
                    toRemove.clear();
                    Iterator var24 = this.getCloudletWaitingList().iterator();

                    while(var24.hasNext()) {
                        ResCloudlet rcl = (ResCloudlet)var24.next();
                        if (this.currentCpus - this.usedPes >= rcl.getNumberOfPes()) {
                            rcl.setCloudletStatus(3);

                            for(int k = 0; k < rcl.getNumberOfPes(); ++k) {
                                rcl.setMachineAndPeId(0, i);
                            }

                            this.getCloudletExecList().add(rcl);
                            this.usedPes += rcl.getNumberOfPes();
                            toRemove.add(rcl);
                            break;
                        }
                    }

                    this.getCloudletWaitingList().removeAll(toRemove);
                }
            }

            double nextEvent = 1.7976931348623157E308D;
            Iterator var25 = this.getCloudletExecList().iterator();

            while(var25.hasNext()) {
                ResCloudlet rcl = (ResCloudlet)var25.next();
                double remainingLength = (double)rcl.getRemainingCloudletLength();
                double estimatedFinishTime = currentTime + remainingLength / (capacity * (double)rcl.getNumberOfPes());
                if (estimatedFinishTime - currentTime < CloudSim.getMinTimeBetweenEvents()) {
                    estimatedFinishTime = currentTime + CloudSim.getMinTimeBetweenEvents();
                }

                if (estimatedFinishTime < nextEvent) {
                    nextEvent = estimatedFinishTime;
                }
            }

            this.setPreviousTime(currentTime);
            return nextEvent;
        }
    }
}
