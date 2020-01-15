package rediska;

public class Main {

    public static void main(String[] args) {

//        int expectedSize = 10;
//        TiList<String> tl = new TiList<>(expectedSize);
//
//        int blockSize = 512;
//        TieredVector vec = new TieredVector(expectedSize, blockSize);
////        TieredVectorArray vec2 = new TieredVectorArray(expectedSize, blockSize);
////        com.jusski.TieredVector vec3 = new com.jusski.TieredVector(expectedSize, blockSize);
////
//
//        for (int i = 0; i < 20; ++i) {
//            tl.add("Liaw " + i);
//        }
//        for (int i = 0; i < tl.size(); ++i) {
//            System.out.println(i + " : " + tl.get(i));
//        }
//        for (int i = 0; i < 10; i += 2) {
//            tl.add(0, "Miaw " + -i);
//        }
//        for (int i = 0; i < tl.size(); ++i) {
//            System.out.println(i + " : " + tl.get(i));
//        }
//        for (int i = 0; i < 10; ++i) {
//            tl.remove(tl.size() - 5);
//        }
//        for (int i = 0; i < tl.size(); ++i) {
//            System.out.println(i + " : " + tl.get(i));
//        }
//        Bench<TieredList> bench = new Bench<>();
//
//        bench.tlTest(tl, 100000, 0, 10000);
//        juskiTest(vec);
//        compareTest(vec, vec2, 100, 1, 20);


//        for (int i = 0; i < 16; ++i) {
//            int size = 4;
//            int b = (int) ((i + size - 2) / size);
//            int idx = b == 0 ? i : (i - 2) % size;
//            System.out.println(i + " : " + b + ", " + idx);
//        }
//        =============================================
//        int expectedSize = 4;
//        int blockSizeBits = 2;
//        int blockSize = 1 << blockSizeBits;
//        while (blockSize < expectedSize) {
//            blockSize <<= 1;
//            ++blockSizeBits;
//        }
//        int blockSizeMask = blockSize - 1;
//        boolean toLeft = false;
//        System.out.println("    \t" + "01234, 01234");
//        for (int index = 0; index < 12; ++index) {
//            int block[] = new int[blockSize + 1];
//            int idx[] = new int[blockSize + 1];
//            for (int count0 = 0; count0 <= blockSize; count0++) {
//                int temp = index - count0 - 1;
//                if (toLeft) {
//                    block[count0] = (temp + blockSize) >> blockSizeBits;
//                    idx[count0] = index <= (count0 & blockSizeMask) ? index : temp & blockSizeMask;
////                              idx = index <= (count0 & blockSizeMask) ? index : temp & blockSizeMask;
////                        idx = (index - count0 - (count0 < blockSize ? 1 : 0)) & blockSizeMask;
//                    idx[count0] = count0 == blockSize ? index & blockSizeMask : (index <= (count0 & blockSizeMask) ? index : temp & blockSizeMask);
//                } else {
//                    if (count0 < blockSize) {
//                        block[count0] = (temp + blockSize) >> blockSizeBits;
//                        idx[count0] = index <= count0 ? index : temp & blockSizeMask;
//                    } else {
//                        block[count0] = (index - count0 + blockSize) >> blockSizeBits;
//                        idx[count0] = index & blockSizeMask;
//                    }
//                }
//            }
//            System.out.println(index + " : \t" + block[0] + "" + block[1] + "" + block[2] + "" + block[3] + "" + block[4]
//                    + ", " + idx[0] + "" + idx[1] + "" + idx[2] + "" + idx[3] + "" + idx[4]);
//        }
//        =============================================
//

//
        TiList<String> tl = new TiList<>(16);
        for (int i = 0; i < 10; ++i) {
            tl.add("La " + i);
            System.out.println(tl.toString());
        }
        System.out.println("REMOVE");
        for (int i = 0; i < tl.size();) {
            tl.remove(0);
            System.out.println(tl.toString());
        }
        System.out.println("Add");
        for (int i = 0; i < 10; ++i) {
            tl.add(i, "Ma " + i);
            System.out.println(tl.toString());
        }
//        for (int j = 0; j < tl.size(); ++j) {
//                System.out.println(j + " : " + tl.get(j));
//        }
//        System.out.println("REMOVE % 2");
//        for (int i = 0; i < tl.size(); ++i) {
//            int r = Integer.parseInt(tl.get(i).substring(3));
//            if ((r & 1) == 1) {
//                tl.remove(i);
//                --i;
//                for (int j = 0; j < tl.size(); ++j) {
//                    System.out.println(j + " : " + tl.get(j));
//                }
//            }
//        }

//        RingBuff<String> rb = new RingBuff(4);
//        for (int i = 0; i < rb.size + 10; ++i) {
//            System.out.println(i + "---");
//            rb.add(0, "Liaw " + i, true);
//            System.out.println(rb.toString());
//            System.out.println(i + "---");
//            rb.add(1, "Miaw " + i, true);
//            System.out.println(rb.toString());
//        }
//        System.out.println("REMOVE");
//
//        while (rb.count > 0) {
//            rb.remove(0);
//            System.out.println(rb.toString());
//        }
//        int i = 0;
//        rb.add(i, "Miaw " + i, true);
//        for (int j = 0; j < rb.count; ++j) {
//            System.out.println(j + " : " + rb.get(j));
//        }
//
//
////        System.out.println("---------");
////        for (int i = 0; i < rb.size; ++i) {
////            rb.add(rb.count, "Miaw " + i, true);
////            for (int j = 0; j < rb.count; ++j) {
////                System.out.println(j + " : " + rb.get(j));
////            }
////        }
////        System.out.println("---------");
////        for (int i = 0; i < rb.size; ++i) {
////            rb.add(0, "Kriu " + i, false);
////            for (int j = 0; j < rb.count; ++j) {
////                System.out.println(j + " : " + rb.get(j));
////            }
////        }
//
//
////        for (int i = 0; i < 8; i += 2) {
////            rb.add(i, "Miaw " + -i);
////        }
////        for (int i = 0; i < rb.size(); ++i) {
////            System.out.println(i + " : " + rb.get(i));
////        }
////        for (int i = 0; i < 8; i += 1) {
////            rb.add(i, "Kriu " + i, true);
////
////            System.out.println(i + " : ---");
////            for (int j = 0; j < rb.size(); ++j) {
////                System.out.println(j + " : " + rb.get(j));
////            }
////        }
    }
}
