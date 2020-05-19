package rootheart.codes.sudoku.solver;

import java.util.Objects;
import java.util.function.IntConsumer;

public class NumberSet implements Cloneable {
    private int binaryEncodedNumbers;

    public void add(int number) {
        binaryEncodedNumbers |= 1 << number;
    }

    public void addAll(NumberSet otherSet) {
        binaryEncodedNumbers = otherSet.binaryEncodedNumbers;
    }

    public boolean hasOneNumber() {
        return (binaryEncodedNumbers & (binaryEncodedNumbers - 1)) == 0;
    }

    public int getFirst() {
        if (binaryEncodedNumbers == 0) {
            return 0;
        }

        for (int numberToCheck = 1; numberToCheck < 32; numberToCheck++) {
            if (contains(numberToCheck)) {
                return numberToCheck;
            }
        }
        return 0;
    }

    public void remove(int number) {
        binaryEncodedNumbers &= ~(1 << number);
    }

    public boolean contains(int number) {
        return (binaryEncodedNumbers & 1 << number) > 0;
    }

    public void removeAllAndAdd(int number) {
        binaryEncodedNumbers = 1 << number;
    }

    public void removeAll(NumberSet other) {
        binaryEncodedNumbers &= ~other.binaryEncodedNumbers;
    }

    public int getCount() {
        int count = 0;
        int n = binaryEncodedNumbers;
        while (n > 0) {
            count += n & 1;
            n >>= 1;
        }
        return count;
    }

    public void forEach(IntConsumer consumer) {
        for (int number = 0; number < 32; number++) {
            if (contains(number)) {
                consumer.accept(number);
            }
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
}
