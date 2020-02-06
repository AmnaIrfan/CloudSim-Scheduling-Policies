package org.cloudsim.mapreduce;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapReduce {
    public MapReduce(){

    }
    public static void main(String[] args) {
        //randomly create input
        int taskLength = 4000;
        List<MapperInput> mapperInput = new ArrayList<MapperInput>();
        Random rn = new Random();
        for (int i = 0; i < 10 * (taskLength / 1000); i++) {
            mapperInput.add(new MapperInput(rn.nextInt(10 - 2) + 1, rn.nextInt(10 - 2) + 1));
        }
        Master m = new Master(mapperInput, taskLength, 1000, "", "", "", "");
        m.submitJob();

    }

}
