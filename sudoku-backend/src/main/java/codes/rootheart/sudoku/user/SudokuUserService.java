package codes.rootheart.sudoku.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SudokuUserService implements UserDetailsService {
    private final SudokuUserRepository userRepository;

    @Override
    public SudokuUser loadUserByUsername(String username) throws UsernameNotFoundException {
        SudokuUser user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }
}
