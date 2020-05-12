package codes.rootheart.sudoku.game;

import codes.rootheart.sudoku.user.SudokuUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Game findByPlayerAndId(SudokuUser player, long id);
}
