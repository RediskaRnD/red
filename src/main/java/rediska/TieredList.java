package rediska;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

public class TieredList<T> extends AbstractList<T>
        implements List<T>, RandomAccess, Cloneable, Serializable {

    private int blocksCountMask;
    private int blockSizeBit;
    private int blockSize;
    private final int blockSizeMask;
    public int count;
    public int shift;

    public int createdBlocks;
    public int fullBlocks;

    public Block[] blocks;


    public TieredList() {
        this(1024);
    }

    public TieredList(int expectedSize) {
        double size = Math.sqrt(expectedSize);
        blockSizeBit = 2;
        blockSize = 1 << blockSizeBit;
        while (blockSize < size) {
            blockSize <<= 1;
            ++blockSizeBit;
        }
        blockSizeMask = blockSize - 1;
        createdBlocks = 0;
        fullBlocks = 0;
        shift = 0;
        count = 0;
        blocks = new Block[blockSize];
        blocksCountMask = blocks.length - 1;
    }

    @Contract(pure = true)
    @Override
    public T get(int index) {
        rangeCheck(index);
        Block b = blocks[index >> blockSizeBit];
        return (T) b.arr[(index + b.shift) & blockSizeMask];
    }

    @Override
    public T set(int index, T item) {
        rangeCheck(index);
        Block b = blocks[index >> blockSizeBit];
        int i = index & blockSizeMask; /// TODO почему тут нет shifta?
        T temp = (T) b.arr[i];
        b.arr[i] = item;
        return temp;
    }

    @Override
    public boolean add(T item) {
        add(count, item);
        return true;
    }

    @Override
    public void add(int index, T item) {
        rangeCheckForAdd(index);
        // хватает ли блоков?
        if (fullBlocks >= createdBlocks) {
            // увеличиваем массив блоков
            if (createdBlocks >= blocks.length) {
                resizeBlocksArray();
            }
            addBlock();
        }
        // where to add? to the begin or to the end
        int bIdx = ((index >> blockSizeBit) + shift) & blocksCountMask;
        Block b = blocks[bIdx];
        int mod = index & blockSizeMask;
        if (b.count == blockSize) {
            int returnIdx = mod < blockSize >> 1 ? 0 : blockSizeMask;
            T element = shiftInBlock(b, mod, returnIdx, item);
            boolean shiftForward = index >= blocks.length >> 1;
            shiftBetweenBlocks(shiftForward, bIdx, element);
        } else {
            addToBlock(b, mod, item);
        }

        ++count;

//        if (dirOut) {   // = (b < blocks.length / 2)
//            // to the begin
//
//
//        } else {
//
//
//            // shift inside block
//            // TODO можно двигать не только в конец, но и начало, так сэкономим кучу времени! (при внутреннем шифте)
//
//
//            // to the end
//            // shift between blocks to the right
//            int lastBlock = ((count >> blockSizeBit) + shift) & blocksCountMask;
//            int lastIdx0 = getLastIdxInBlock(lastBlock);
//            Block b = blocks[lastBlock];
//            ++b.count; // TODO это надо сделать в конце, иначе repeat сработает
//            if (b.count == blockSize) ++fullBlocks;
//            for (; lastBlock > bIdx; --lastBlock) {
//                b = blocks[lastBlock];
//                int lastIdx1 = lastIdx0;
//                lastIdx0 = getLastIdxInBlock(lastBlock - 1);
//                b.arr[lastIdx1] = blocks[lastBlock - 1].arr[lastIdx0];
//                b.shift = (b.shift - 1) & blockSizeMask;
//            }
//
//
//            int mod = index & blockSizeMask;
//            blocks[lastBlock].arr[(blocks[lastBlock].shift + mod) & blockSizeMask] = item;
//            ++count;
////            if ((count & blockSizeMask) == 0) ++fullBlocks;
//        }
    }

    private void addToBlock(Block b, int index, T element) {
        do {
            if (index == 0) {
                b.shift = (b.shift - 1) & blockSizeMask;
                index = (index + b.shift) & blockSizeMask;
                break;
            }
            int dif1 = (index - b.count) & blockSizeMask;
            int dif2 = (b.count - index) & blockSizeMask;
            int repeat, inc;
            if (dif1 == dif2) {
                index = (index + b.shift) & blockSizeMask;
                break;
            }
            if (dif1 < dif2) {
                repeat = dif1;
                inc = 1;
                b.shift = (b.shift - 1) & blockSizeMask;
            } else {
                repeat = dif2;
                inc = -1;
            }
            int idx = b.count;
            for (int i = 0; i < repeat; ++i) {
                int nextIdx = (idx + inc) & blockSizeMask;
                b.arr[idx] = b.arr[nextIdx];
                idx = nextIdx;
            }
            break;
        } while (true);
        b.arr[index] = element;
        ++b.count;
        if (b.count == blockSize) ++fullBlocks;
    }

    private T shiftInBlock(Block b, int index, int returnIdx, T element) {
        assert index >= 0 && index <= b.count;
        index = (index + b.shift) & blockSizeMask;
        returnIdx = (returnIdx + b.shift) & blockSizeMask;
        T result = (T) b.arr[returnIdx];
        shift(b, index, returnIdx);
        b.arr[index] = element;
        return result;
    }

    private void shift(Block b, int index, int limit) {
        int dif1 = (index - limit) & blockSizeMask;
        int dif2 = (limit - index) & blockSizeMask;
        int repeat, inc;
        if (dif1 == dif2) {
            return;
        }
        if (dif1 < dif2) {
            repeat = dif1;
            inc = 1;
            b.shift = (b.shift - 1) & blockSizeMask;
        } else {
            repeat = dif2;
            inc = -1;
        }
        int idx = limit;
        for (int i = 0; i < repeat; ++i) {
            int nextIdx = (idx + inc) & blockSizeMask;
            b.arr[idx] = b.arr[nextIdx];
            idx = nextIdx;
        }
    }

    private void shiftBetweenBlocks (boolean shiftForward, int block, T element) {
        int repeat, inc;
        if (shiftForward) {
            repeat = count / blocks.length - block;
            inc = 1;
        } else {
            repeat = block;
            inc = -1;
        }
        for (int i = 0; i < repeat; ++i) {
            int nextBlock = (block + inc) & blockSizeMask;
            int elId = getLastIdxInBlock(nextBlock);
            T temp = (T) this.blocks[nextBlock].arr[elId];
//            this.blocks[nextBlock] = this.blocks[nextIdx];
//            idx = nextIdx;
        }
    }

    private void resizeBlocksArray() {
        // увеличиваем массив блоков
        if (createdBlocks >= blocks.length) {
            // копируем в зависимости от шифта
            int part1 = blocks.length - shift;
            int part2 = shift;
            Block[] temp = new Block[blocks.length << 1];
            if (part1 > 0) System.arraycopy(blocks, part2, temp, 0, part1);
            if (part2 > 0) System.arraycopy(blocks, 0, temp, part1, part2);
            blocks = temp;
            shift = 0;
            blocksCountMask = blocks.length - 1;
        }
    }

    public T remove(int index) {

        return null;
    }

    private void addBlock() {
        assert (blocks.length & blocks.length - 1) == 0;
        blocks[(shift + createdBlocks) & blocksCountMask] = new Block(blockSize);
        ++createdBlocks;
    }

    @Contract()
    private int getFirstIdxInBlock(int block) {
        return blocks[block].shift;
    }

    @Contract(pure = true)
    private int getLastIdxInBlock(int block) {
        int shift = blocks[block].shift;
        return shift > 0 ? shift - 1 : blockSizeMask;
    }

    @Override
    public int size() {
        return count;
    }

    class Block<T> {
        int shift = 0;
        int count = 0;
        Object[] arr;

        @Contract(pure = true)
        Block(int size) {
            assert (size & (size - 1)) == 0;
            arr = new Object[size];
        }
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
