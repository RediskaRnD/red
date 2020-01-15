package rediska;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.jetbrains.annotations.Contract;

public class RingBuff<T> {
    public int head = 1;    // из за особенности функции add добавляя самый первый элемент он декрементит head
    public int count = 0;
    public int size;
    public T[] buff;

    private int sizeMask;

    RingBuff(int size) {
        assert (size & (size - 1)) == 0;
        sizeMask = size - 1;
        buff = (T[]) new Object[size];
        this.size = size;
    }

    @Contract(pure = true)
    public T add(T item) {
        return add(count, item, false);
    }

    @Contract(pure = true)
    public T add(int index, T item) {
        return add(index, item, false);
    }

    @Contract(pure = true)
    public T add(int index, T item, boolean toLeft) {
        rangeCheckForAdd(index);
        T result;
        if (count == size) {
            if (toLeft) {
                result = buff[getRealIndex(0)];
                incHead();
            } else {
                result = buff[getRealIndex(sizeMask)];
            }
            --count;
        } else {
            result = null;
        }
        if (index <= count >> 1) {
            decHead();
            shiftToLeft(0, index);
        } else {
            shiftToRight(index, count);
        }
        buff[getRealIndex(index)] = item;
        ++count;
        return result;
    }

    @Contract(pure = true)
    protected void shiftToLeft(int from, int to) {
        int i0 = getRealIndex(from);
        for (int i = from + 1; i <= to; ++i) {
            int i1 = getRealIndex(i);
            buff[i0] = buff[i1];
            i0 = i1;
        }
    }

    @Contract(pure = true)
    protected void shiftToRight(int from, int to) {
        int i0 = getRealIndex(to);
        for (int i = to - 1; i >= from; --i) {
            int i1 = getRealIndex(i);
            buff[i0] = buff[i1];
            i0 = i1;
        }
    }

    @Contract(pure = true)
    public T remove(int index) {
        rangeCheck(index);
        T result = buff[getRealIndex(index)];
        if (index < count >> 1) {
            shiftToRight(0, index);
            buff[head] = null;
            incHead();
        } else {
            shiftToLeft(index, count - 1);
            buff[getRealIndex(count - 1)] = null;
        }
        --count;
        return result;
    }

    @Contract(pure = true)
    private int getRealIndex(int index) {
        return (index + head) & sizeMask;
    }

    @Contract(pure = true)
    void incHead() {
        head = head == sizeMask ? 0 : head + 1;
    }

    @Contract(pure = true)
    void decHead() {
        head = head > 0 ? head - 1 : sizeMask;
    }

    @Contract(pure = true)
    public T get(int index) {
        rangeCheck(index);
        return buff[getRealIndex(index)];
    }

    @Contract(pure = true)
    public T set(int index, T item) {
        rangeCheck(index);
        int i = getRealIndex(index);
        T result = buff[i];
        buff[i] = item;
        return result;
    }

    void doubleSize() {
        // копируем в зависимости от шифта
        int part1 = size - head;
        int part2 = head;
        T[] temp = (T[]) new Object[size << 1];
        if (part1 > 0) System.arraycopy(buff, part2, temp, 0, part1);
        if (part2 > 0) System.arraycopy(buff, 0, temp, part1, part2);
        buff = temp;
        head = 0;
        size = temp.length;
        sizeMask = size - 1;
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

    private void rangeCheck(int index) {
        if (index < 0 || index >= this.count)
            throw new OutOfRangeException(index, 0, count - 1);
    }

    private void rangeCheckForAdd(int index) {
        int max = Math.min(count, size - 1);
        if (index < 0 || index > max)
            throw new OutOfRangeException(index, 0, max);
    }
}
