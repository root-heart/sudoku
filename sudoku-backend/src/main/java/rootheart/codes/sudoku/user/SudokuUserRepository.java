package rootheart.codes.sudoku.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SudokuUserRepository extends JpaRepository<SudokuUser, Long> {
    SudokuUser findByUsername(String username);
}
