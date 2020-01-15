package benchmark;

import blogspot.software_and_algorithms.stern_library.data_structure.ThriftyList;
import com.jusski.Vector;
import rediska.TiList;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class TieredVectorBenchmark {
    static int initialSize = 200_000;
    static int blockSize = 512;
    static int loopCount = 10;
    static int[] index = new int[initialSize];
    static HashMap<String, Double> map = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random(1234);

        for (int i = 0; i < index.length; i++) {
            index[i] = random.nextInt(index.length - 1);
        }
        System.out.println("Benchmark start");
        vector();
        thrift();
//        array();
//        rediska();
        map.forEach((k, v) -> {System.out.format("%s: %2f%n", k, v);});
        System.out.println("Benchmark end");
    }

//    private static void rediska() throws InterruptedException {
//        rediska.TieredVector tieredVector = new rediska.TieredVector(initialSize, blockSize);
//        bench(tieredVector, "rediska");
//        System.out.println("rediska array end");
//    }

//    private static void array() {
//        com.jusski.optimize.array.TieredVectorArray tieredVector = new com.jusski.optimize.array.TieredVectorArray(initialSize, blockSize);
//        bench(tieredVector, "double array");
//        System.out.println("optimized array end");
//    }

    static void vector() {
        TiList<Integer> tieredVector = new TiList<Integer>(initialSize);
        bench(tieredVector, "structure");
        System.out.println("vector end");
    }

    static void thrift() {
        ThriftyList<Integer> tieredVector = new ThriftyList<Integer>();
        for (int i = 0; i < index.length; i++) {
            tieredVector.add(index[i]);
        }

        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < index.length; i++) {
                tieredVector.add(index[i], index[i]);
                tieredVector.remove(index[i] + 1);
            }
        }
        double sum = 0;
        for (int j = 0; j < loopCount; j++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < index.length; i++) {
                tieredVector.add(index[i] + i, index[i]);
            }
            long stop = System.currentTimeMillis();
            sum += stop - start;
            for (int i = 0; i < index.length; i++) {
                tieredVector.remove(index[i]);
            }
            System.out.println((stop - start) / 1000.0);
        }
        System.out.println("average: " + sum / loopCount);
        System.out.println("thrift end");
        map.put("thrift", sum / loopCount);
    }

    private static void bench(TiList<Integer> tieredVector, String name) {
        for (int i = 0; i < index.length; i++) {
            tieredVector.add(index[i]);
        }

        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < index.length; i++) {
                tieredVector.add(index[i], index[i]);
                tieredVector.remove(index[i] + 1);
            }
        }
        double sum = 0;

        for (int j = 0; j < loopCount; j++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < index.length; i++) {
                tieredVector.add(index[i] + i, index[i]);
            }
            long stop = System.currentTimeMillis();
            sum += stop - start;
            for (int i = 0; i < index.length; i++) {
                tieredVector.remove(index[i]);
            }
            System.out.println((stop - start) / 1000.0);
        }
        System.out.println("average: " + sum / loopCount);
        map.put(name, sum / loopCount);
    }


}
