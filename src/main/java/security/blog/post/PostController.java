package security.blog.post;


import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import security.blog.account.Account;
import security.blog.account.CurrentUser;

import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/posts", produces = "application/json;charset=UTF-8" )
public class PostController {

    private ModelMapper modelMapper;
    private PostRepository postRepository;

    @GetMapping("/{id}")
    public ResponseEntity read(@PathVariable("id") Optional<Post> post) {
        return ResponseEntity.ok(post.orElseThrow(IllegalArgumentException::new));
    }

    @PostMapping
    public ResponseEntity create(@CurrentUser Account currentUser, @RequestBody PostDto postDto) {
        Post post = modelMapper.map(postDto, Post.class);
        post.setAuthor(currentUser);
        Post savedPost = postRepository.save(post);

        URI location = linkTo(PostController.class).slash(savedPost.getId()).toUri();
        return ResponseEntity.created(location).body(savedPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@CurrentUser Account currentUser, @PathVariable("id") Optional<Post> post, @RequestBody PostDto postDto) {
        Post savedPost = post.orElseThrow(IllegalArgumentException::new);

        if(!savedPost.isAuthor(currentUser))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        modelMapper.map(postDto, savedPost);
        return ResponseEntity.ok(post);
    }

    @Secured(value = "ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        postRepository.deleteById(id);
    }
}
