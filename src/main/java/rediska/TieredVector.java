package rediska;

import com.jusski.Vector;

import static rediska.Tools.roundUp;

public class TieredVector implements Vector {
    private int mask;
    private int[] arr;
    private int size;
    public int empty;
    private int blockSize;
    private int count;

    public TieredVector() {
        this(0, 2048);
    }

    public TieredVector(int size) {
        this(size, 2048);
    }

    public TieredVector(int size, int blockSize) {
        if (blockSize < 2 || (blockSize & blockSize - 1) != 0) {
            throw new IndexOutOfBoundsException(toSmallBlockSizeMsg(size));
        }
        this.blockSize = blockSize + 1;
        this.mask = blockSize;
        this.count = 0;
        int blocks = roundUp(size, mask);
        this.size = blocks * this.blockSize;
        arr = new int[this.size];
        empty = blocks * mask;
    }

    public int get(int index) {
        rangeCheck(index);
        return arr[getRealIdx(index)];
    }

    public int set(int index, int element) {
        rangeCheck(index);
        int real = getRealIdx(index);
        int oldValue = arr[real];
        arr[real] = element;
        return oldValue;
    }

    public void add(int element) {  // TODO скорее всего не правильно добавляет. только в конец списка, без оглядки на шифт?
        if (empty == 0) {
            addBlock(true);
        }
        int index = count;
        int block = index / mask;
        int mod = index & (mask - 1);
        int shiftIdx = block * blockSize;
        int shift = arr[shiftIdx];
        arr[shiftIdx + 1 + ((mod + shift) & (mask - 1))] = element;
        --empty;
        ++count;
    }

    public void add(int index, int element) {
        rangeCheckForAdd(index);
        if (empty == 0) {
            addBlock(true);
        }
        // shift between blocks
        int block = index / mask;
        int shiftIdx;
        int b = count / mask;
        int lastIdx0 = getLastIdxInBlock(b);
        for (; b > block; --b) {
            int lastIdx1 = lastIdx0;
            lastIdx0 = getLastIdxInBlock(b - 1);
            arr[lastIdx1] = arr[lastIdx0];
            shiftIdx = b * blockSize;
            arr[shiftIdx] = arr[shiftIdx] == 0 ? blockSize - 2 : arr[shiftIdx] - 1;
        }
        // shift inside block
        shiftIdx = block * blockSize;
        int repeat = mask - 1 - (index & (mask - 1));
        for (int i = 0; i < repeat; ++i) {
            int idx = lastIdx0 - 1 == shiftIdx ? shiftIdx + mask : lastIdx0 - 1;
            arr[lastIdx0] = arr[idx];
            lastIdx0 = idx;
        }
        // insert new element
        arr[lastIdx0] = element;
        --empty;
        ++count;
    }

    public void remove(int index) {
        rangeCheck(index);
        int block = index / mask;
        int shiftIdx = block * blockSize;
        // shift inside block
        int shift = arr[shiftIdx];
        int idx1 = shiftIdx + 1 + ((index + shift) & (mask - 1));
        int repeat = mask - 1 - (index & (mask - 1));
        for (int i = 0; i < repeat; ++i) {
            int idx0 = idx1;
            idx1 = idx1 + 1 == shiftIdx + blockSize ? shiftIdx + 1 : idx1 + 1;
            arr[idx0] = arr[idx1];
        }
        // shift between blocks
        int lastBlock = (count - 1) / mask;
        for (int b = block; b < lastBlock; ++b) {
            int idx0 = getFirstIdxInBlock(b + 1);
            arr[idx1] = arr[idx0];
            idx1 = idx0;
            shiftIdx = (b + 1) * blockSize;
            arr[shiftIdx] = ((arr[shiftIdx] + 1) & (mask - 1));
        }
        ++empty;
        --count;
    }

    public String toString() {
        StringBuilder str = new StringBuilder("[");
        String separator;
        for (int i = 0; i < count; ++i) {
            separator = i == count - 1 ? "]" : ", ";
            str.append(this.get(i)).append(separator);
        }
        return str.toString();
    }

    private void addBlock(boolean last) {
        int[] temp = new int[size + blockSize];
        System.arraycopy(arr, 0, temp, last ? 0 : mask, size);
        size += blockSize;
        empty = mask;
        arr = temp;
    }

    private int getFirstIdxInBlock(int block) {
        int shiftIdx = block * blockSize;
        int shift = arr[shiftIdx];
        return shiftIdx + (shift == 0 ? 1 : shift + 1);
    }

    private int getLastIdxInBlock(int block) {
        int shiftIdx = block * blockSize;
        int shift = arr[shiftIdx];
        return shiftIdx + (shift == 0 ? mask : shift);
    }

    private int getRealIdx(int index) {
        int block = index / mask;
        int shiftIdx = block * blockSize;
        int shift = arr[shiftIdx];
        return shiftIdx + 1 + (index + shift) % mask;
    }

    public int size() {
        return count;
    }

    private void rangeCheck(int index) {
        if (index < 0 || index >= this.count)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private void rangeCheckForAdd(int index) {
        if (index < 0 || index > this.count)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + this.count;
    }

    private String toSmallBlockSizeMsg(int size) {
        return "Block size to small: " + size;
    }
}
