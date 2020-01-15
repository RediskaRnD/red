package rediska;

import com.jusski.Vector;

import java.util.AbstractList;
import java.util.Random;

public class Bench<T extends AbstractList> {

    public static void tvTest(Vector v, int repeat, int botLimit, int topLimit) {
        boolean actionAdd = false;
        Random random = new Random(1234);
        long start = System.currentTimeMillis();
        for (int i = 0; i < repeat; ++i) {
            if (v.size() < botLimit) {
                actionAdd = true;
            } else if (v.size() > topLimit) {
                actionAdd = false;
            }
            int id = v.size() == 0 ? 0 : random.nextInt(v.size());
            if (actionAdd) {
                v.add(id, i);
//                v.add(i);
            } else {
                v.remove(v.size() - 1);
            }
        }
        long stop = System.currentTimeMillis();
        System.out.println((stop - start) / 1000.0);
    }

    public void tlTest(T tl, int repeat, int botLimit, int topLimit) {
        boolean actionAdd = false;
        Random random = new Random(1234);
        long start = System.currentTimeMillis();
        for (Integer i = 0; i < repeat; ++i) {
            if (tl.size() < botLimit) {
                actionAdd = true;
            } else if (tl.size() > topLimit) {
                actionAdd = false;
            }
            int id = tl.size() == 0 ? 0 : random.nextInt(tl.size());
            if (actionAdd) {
                tl.add(id, i);
//                v.add(i);
            } else {
                tl.remove(tl.size() - 1);
            }
        }
        long stop = System.currentTimeMillis();
        System.out.println((stop - start) / 1000.0);
    }

    protected void tlCompare(T vec, T vec2) {
        for (int i = 0; i < vec.size(); ++i) {
            if (vec.get(i).equals(vec2.get(i))) {
                throw new IllegalStateException("Alarma: " + i);
            }
        }
    }

    public static void juskiTest(Vector v) {
        Random random = new Random(1234);
        int size = 300000;
        int[] index = new int[size];

        for (int i = 0; i < size; i++) {
            v.add(i, index[i]);
            index[i] = random.nextInt(size - 1);
        }
        System.out.println("step 1");
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < size; i++) {
                v.add(index[i], index[i]);
//                v.remove(index[i] + 1);
                v.remove(v.size() - 1);
            }
        }
        System.out.println("step 2");
        for (int j = 0; j < 4; j++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < size; i++) {
                v.add(index[i], index[i]);
            }
            long stop = System.currentTimeMillis();

            for (int i = 0; i < size; i++) {
//                v.remove(index[i]);
                v.remove(v.size() - 1);
            }
            System.out.println((stop - start) / 1000.0);
        }
    }

    public static void compareTest(Vector vec, Vector vec2, int repeat, int botLimit, int topLimit) {
        tvTest(vec, repeat, botLimit, topLimit);
        tvTest(vec2, repeat, botLimit, topLimit);
        if (vec.size() != vec2.size()) {
            throw new IllegalStateException("Different size: " + vec.size() + ", " + vec2.size() + ", count: ");
        }
        compare(vec, vec2);
        System.out.println("Finita la comedia");
    }

    protected static void compare(Vector vec, Vector vec2) {
        for (int i = 0; i < vec.size(); ++i) {
            if (vec.get(i) != vec2.get(i)) {
                throw new IllegalStateException("Alarma: " + i);
            }
        }
    }
}
