package security.blog.post;

import lombok.*;
import security.blog.account.Account;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class Post {
    @Id @GeneratedValue
    private Long id;
    private String title;
    private String contents;

    @ManyToOne
    private Account account;



}
