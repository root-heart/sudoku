package rootheart.codes.sudoku.solver;

import lombok.NoArgsConstructor;
import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.impl.factory.primitive.IntIntMaps;

import java.util.Objects;
import java.util.function.IntConsumer;

@NoArgsConstructor
public final class NumberSet implements Cloneable {
    static int[] bitCount = new int[1 << 9];

    static {
        for (int i = 0; i < 1 << 9; i++) {
            int count = 0;
            int n = i;
            while (n > 0) {
                count += n & 1;
                n >>>= 1;
            }
            bitCount[i] = count;
        }
    }

    public int binaryEncodedNumbers;

    public NumberSet(NumberSet other) {
        binaryEncodedNumbers = other.binaryEncodedNumbers;
    }

    public NumberSet(int number) {
        add(number);
    }

    public NumberSet(int... numbers) {
        for (int number : numbers) {
            add(number);
        }
    }

    public void add(int number) {
        binaryEncodedNumbers |= 1 << (number -1 );
    }

    public void addAll(NumberSet otherSet) {
        binaryEncodedNumbers |= otherSet.binaryEncodedNumbers;
    }

    public boolean hasOneNumber() {
        return bitCount[binaryEncodedNumbers] == 1;
    }

    public int getFirst() {
        if (binaryEncodedNumbers == 0) {
            return 0;
        }

        for (int n = binaryEncodedNumbers, number = 0; n != 0; n >>>= 1) {
            if ((n & 1) == 1) {
                return number + 1;
            }
            number++;
        }
        return 0;
    }

    public void remove(int number) {
        binaryEncodedNumbers &= ~(1 << ( number - 1));
    }

    public boolean contains(int number) {
        return (binaryEncodedNumbers & 1 << (number - 1)) > 0;
    }

    public boolean containsAll(int... numbers) {
        for (int number : numbers) {
            if (!contains(number)) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmpty() {
        return binaryEncodedNumbers == 0;
    }

    public void removeAllAndAdd(int number) {
        binaryEncodedNumbers = 1 << (number - 1);
    }

    public void removeAll(NumberSet other) {
        binaryEncodedNumbers &= ~other.binaryEncodedNumbers;
    }

    public void retainAll(NumberSet other) {
        binaryEncodedNumbers &= other.binaryEncodedNumbers;
    }

    public void set(NumberSet other) {
        binaryEncodedNumbers = other.binaryEncodedNumbers;
    }

    public int getCount() {
        return bitCount[binaryEncodedNumbers];
//        int count = 0;
//        int n = binaryEncodedNumbers;
//        while (n > 0) {
//            count += n & 1;
//            n >>>= 1;
//        }
//        return count;
    }

    public void forEach(IntConsumer consumer) {
        if (binaryEncodedNumbers == 0) {
            return;
        }

        for (int n = binaryEncodedNumbers, number = 0; n != 0; n >>>= 1) {
            if ((n & 1) == 1) {
                consumer.accept(number + 1);
            }
            number++;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumberSet numberSet = (NumberSet) o;
        return binaryEncodedNumbers == numberSet.binaryEncodedNumbers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(binaryEncodedNumbers);
    }

    public NumberSet clone() {
        try {
            return (NumberSet) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clear() {
        binaryEncodedNumbers = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int number = 0; number < 32; number++) {
            if (contains(number)) {
                sb.append(number).append(" ");
            }
        }
        return sb.toString();
    }
}
