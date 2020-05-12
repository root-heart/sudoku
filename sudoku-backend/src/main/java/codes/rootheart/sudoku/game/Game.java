package codes.rootheart.sudoku.game;

import codes.rootheart.sudoku.user.SudokuUser;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class Game {
    @Id
    @GeneratedValue
    private long id;
    private String board;
    @ManyToOne
    private SudokuUser player;

    public Game() {
        board = String.format("%081d", 0);
    }

    public void set(int column, int row, int number) {
        Validate.inclusiveBetween(1, 9, column, "Invalid column argument");
        Validate.inclusiveBetween(1, 9, row, "Invalid row argument");
        Validate.inclusiveBetween(1, 9, number, "Invalid number argument");
        int index = 9 * (row - 1) + column -1;
        char[] chars = board.toCharArray();
        chars[index] = Character.forDigit(number, 10);
        board = new String(chars);
    }
}
