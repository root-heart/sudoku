package rootheart.codes.sudoku.solver;

import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.function.IntConsumer;

@NoArgsConstructor
public final class NumberSet implements Cloneable {
    private int binaryEncodedNumbers;

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
        binaryEncodedNumbers |= 1 << number;
    }

    public void addAll(NumberSet otherSet) {
        binaryEncodedNumbers |= otherSet.binaryEncodedNumbers;
    }

    public boolean hasOneNumber() {
        return binaryEncodedNumbers > 0 && (binaryEncodedNumbers & (binaryEncodedNumbers - 1)) == 0;
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
        binaryEncodedNumbers = 1 << number;
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
        int count = 0;
        int n = binaryEncodedNumbers;
        while (n > 0) {
            count += n & 1;
            n >>>= 1;
        }
        return count;
    }

    public void forEach(IntConsumer consumer) {
        if (binaryEncodedNumbers == 0) {
            return;
        }

        for (int n = binaryEncodedNumbers, number = 0; n != 0; n >>>= 1) {
            if ((n & 1) == 1) {
                consumer.accept(number);
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
