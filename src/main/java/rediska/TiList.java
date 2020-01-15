package rediska;

import org.apache.commons.math3.exception.OutOfRangeException;

public class TiList<T> {

    //    private int blocksCountMask;
//    private int blockSizeBits;
    private final int blockSize;
    private int blockSizeBits;
    private final int blockSizeMask;

    private int size;   // total size
    private int count;  // total count of items

    private RingBuff<RingBuff<T>> blocks;

    public TiList(int expectedSize) {
        expectedSize = (int) Math.sqrt(expectedSize);
        blockSizeBits = 2;
        int blockSize = 1 << blockSizeBits;
        while (blockSize < expectedSize) {
            blockSize <<= 1;
            ++blockSizeBits;
        }
        blocks = new RingBuff<>(blockSize);
        blocks.add(new RingBuff<>(blockSize));
        this.blockSize = blockSize;
        this.blockSizeMask = blockSize - 1;
        this.size = blocks.size * blockSize;
    }

    public void add(T item) {
        add(count, item);
    }

    public void add(int index, T item) {
        rangeCheckForAdd(index);
        int count0 = blocks.get(0).count;
        boolean leftSide = index < count >> 1;
        int block;
        int idx;

        if (leftSide) {
            // add a new block if necessary
            if (count0 == blockSize) {
                if (blocks.count == blocks.size) {
                    blocks.doubleSize();
                    size = blocks.size * blockSize;
                }
                blocks.add(0, new RingBuff<T>(blockSize), true);
                count0 = 0;
            }
            int temp = index - count0 + blockSizeMask;
            block = temp >> blockSizeBits;
            idx = index <= (count0 & blockSizeMask) ? index : temp & blockSizeMask;
        } else {
            // add a new block if necessary
            if (blocks.get(blocks.count - 1).count == blockSize) {
                if (blocks.count == blocks.size) {
                    blocks.doubleSize();
                    size = blocks.size * blockSize;
                }
                blocks.add(blocks.count, new RingBuff<T>(blockSize), false);
            }
            if (index <= (count0 & blockSizeMask)) {
                block = 0;
                idx = index;
            } else {
                int temp = index - count0 + blockSize;
                block = temp >> blockSizeBits;
                idx = temp & blockSizeMask;
            }
        }
        // insert new item
        RingBuff<T> b = blocks.get(block);
        item = b.add(idx, item, leftSide);
        // shift between blocks
        while (item != null) {
            if (leftSide) {
                b = blocks.get(--block);
                idx = Math.min(b.count, blockSizeMask);
            } else {
                b = blocks.get(++block);
                idx = 0;
            }
            item = b.add(idx, item, leftSide);
        }
        ++count;
        return; // true
    }

    public void remove(int index) {
        int count0 = blocks.get(0).count;
        int temp = index - count0;
        int block = (temp + blockSize) >> blockSizeBits;
        int idx;
        boolean leftSide = index < count >> 1;
        RingBuff<T> block0 = blocks.get(block);
        if (leftSide) {
            if (block == 0) {
                block0.remove(index);
            } else {
                idx = temp & blockSizeMask;
                block0.shiftToRight(0, idx);
                while (--block >= 0) {
                    RingBuff<T> block1 = blocks.get(block);
                    block0.set(0, block1.get(block1.count - 1));
                    if (block != 0) {
                        block1.decHead();
                    }
                    block0 = block1;
                }
                block0.remove(count0 - 1);
            }
        } else {
            idx = block == 0 ? index : temp & blockSizeMask;
            if (block == blocks.count - 1) {
                blocks.get(block).remove(idx);
            } else {
                block0.shiftToLeft(idx, block0.count - 1);  // todo тут не нравится мне
                while (++block < blocks.count) {
                    RingBuff<T> block1 = blocks.get(block);
                    block0.set(block0.count - 1, block1.get(0));
                    if (block != blocks.count - 1) {
                        block1.incHead();
                        block0 = block1;
                    }
                }
                blocks.get(block - 1).remove(0);
            }
        }
        --count;
        if (blocks.count > 1) {
            if (blocks.get(0).count == 0) {
                blocks.remove(0);
                return;
            }
            if (blocks.get(blocks.count - 1).count == 0) {
                blocks.remove(blocks.count - 1);
            }
            return;
        }
    }

    public T get(int index) {
        int count0 = blocks.get(0).count;
        int temp = index - count0;
        int b = (temp + blockSize) >> blockSizeBits;
        int idx = b == 0 ? index : temp & blockSizeMask;
        return blocks.get(b).get(idx);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < count - 1; ++i) {
            str.append(get(i).toString() + ',');
        }
        if (count > 0) {
            str.append(get(count - 1).toString());
        }
        return str.toString();
    }

    public int size() {
        return count;
    }

    private void rangeCheck(int index) {
        if (index < 0 || index >= this.count)
            throw new OutOfRangeException(index, 0, count - 1);
    }

    private void rangeCheckForAdd(int index) {
        if (index < 0 || index > count)
            throw new OutOfRangeException(index, 0, count);
    }
}
