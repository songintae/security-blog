package security.blog.account;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import java.util.List;

@Getter
public class AccountDetails extends User {
    private Account account;

    public AccountDetails(Account account) {
        super(account.getEmail(), account.getPassword(), authorities(account));
        this.account = account;
    }

    private static List<GrantedAuthority> authorities(Account account) {
        return AuthorityUtils.createAuthorityList(account.getRole().getCode());
    }
}
