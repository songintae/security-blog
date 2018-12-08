package security.blog.account;


import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class Account {

    @Id @GeneratedValue
    private Long id;

    private String email;
    private String password;
    @Builder.Default
    @Enumerated(value = EnumType.STRING)
    private ROLE role = ROLE.USER;

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    @Getter
    public enum ROLE {
        ADMIN("ROLE_ADMIN"), USER("ROLE_USER");
        private String code;

        ROLE(String code) {
            this.code = code;
        }
    }
}
