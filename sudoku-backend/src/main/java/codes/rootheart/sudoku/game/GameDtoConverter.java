package codes.rootheart.sudoku.game;

import org.springframework.stereotype.Component;

@Component
public class GameDtoConverter {
    public GameDto toDto(Game game) {
        return new GameDto(game.getId(), game.getBoard());
    }
}
