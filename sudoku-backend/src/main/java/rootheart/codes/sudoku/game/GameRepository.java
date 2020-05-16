package rootheart.codes.sudoku.game;

import rootheart.codes.sudoku.user.SudokuUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Game findByPlayerAndId(SudokuUser player, long id);
}
