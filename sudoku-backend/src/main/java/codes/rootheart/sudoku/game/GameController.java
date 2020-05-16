package codes.rootheart.sudoku.game;

import codes.rootheart.sudoku.user.SudokuUser;
import codes.rootheart.sudoku.user.SudokuUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    private final SudokuUserService userService;

    private final GameDtoConverter gameDtoConverter;

    @PostMapping
    public GameDto create(Authentication authentication) {
        SudokuUser player = userService.loadUserByUsername((String) authentication.getPrincipal());
        return gameDtoConverter.toDto(gameService.create(player));
    }

    @GetMapping("/{gameId}")
    public GameDto get(Authentication authentication, @PathVariable long gameId) {
        SudokuUser player = userService.loadUserByUsername((String) authentication.getPrincipal());
        Game game = gameService.get(player, gameId);
        return gameDtoConverter.toDto(game);
    }

    @PutMapping("/{gameId}/{column}/{row}/{number}")
    public GameDto makeMove(Authentication authentication,
                            @PathVariable long gameId,
                            @PathVariable int column,
                            @PathVariable int row,
                            @PathVariable int number) {
        SudokuUser player = userService.loadUserByUsername((String) authentication.getPrincipal());
        Game game = gameService.makeMove(player, gameId, column, row, number);
        return gameDtoConverter.toDto(game);
    }

    @GetMapping("/validate/{board}")
    public GameStatus validate(@PathVariable String board) {
        return gameService.evaluate(board);
    }
}
