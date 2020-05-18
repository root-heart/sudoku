package rootheart.codes.sudoku.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rootheart.codes.sudoku.user.SudokuUser;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Game findByPlayerAndId(SudokuUser player, long id);
}
