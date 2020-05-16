package rootheart.codes.sudoku.game;

import rootheart.codes.sudoku.user.SudokuUser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;

    public Game create(SudokuUser player) {
        Game game = new Game();
        game.setPlayer(player);
        return gameRepository.save(game);
    }

    public Game get(SudokuUser player, long id) {
        return gameRepository.findByPlayerAndId(player, id);
    }

    public Game makeMove(SudokuUser player, long id, int x, int y, int number) {
        Game game = get(player, id);
        game.set(x, y, number);
        gameRepository.save(game); // TODO why is this necessary?
        return game;
    }

    public GameStatus evaluate(@NonNull String board) {
        return GameStatus.OK;
    }
}
