package rootheart.codes.sudoku.solver.binaryoptimized;

import java.util.function.IntConsumer;

class IntStack {
    int[] stack = new int[81];
    int currentIndex = -1;

    void clear() {
        currentIndex = -1;
    }

    void push(int number) {
        stack[++currentIndex] = number;
    }

    int pop() {
        return stack[currentIndex--];
    }

    int size() {
        return currentIndex + 1;
    }

    void forEach(IntConsumer consumer) {
        for (int i = 0; i <= currentIndex; i++) {
            consumer.accept(stack[i]);
        }
    }
}
