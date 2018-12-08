package security.blog.post;


import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity create(@RequestBody PostDto postDto) {
        Post post = modelMapper.map(postDto, Post.class);
        Post savedPost = postRepository.save(post);
        URI location = linkTo(PostController.class).slash(savedPost.getId()).toUri();
        return ResponseEntity.created(location).body(savedPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") Optional<Post> post, @RequestBody PostDto postDto) {
        Post savedPost = post.orElseThrow(IllegalArgumentException::new);
        modelMapper.map(postDto, savedPost);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        postRepository.deleteById(id);
    }
}
