package security.blog.account;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AccountService implements UserDetailsService {

    private AccountRepository accountRepository;
    private PasswordEncoder passwordEncoder;

    public Account saveAccount(Account account) {
        account.encodePassword(passwordEncoder);
        return accountRepository.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 사용자가 없습니다."));

        return new AccountDetails(account);
    }
}
