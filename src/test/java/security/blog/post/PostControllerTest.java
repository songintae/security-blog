package security.blog.post;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import security.blog.account.Account;
import security.blog.account.AccountDetails;
import security.blog.account.AccountRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails(userDetailsServiceBeanName = "accountService", value = "kookooku@woowahan.com")
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    private Account author;
    private Account anotherAuthor;

    @Before
    public void setUp() {
        author = accountRepository.findByEmail("kookooku@woowahan.com").get();
        anotherAuthor = accountRepository.findByEmail("kookooku@naver.com").get();
    }

    @After
    public void tearDown() throws Exception {
        postRepository.deleteAll();
    }

    @Test
    public void getTest() throws Exception {
        // given
        Post savedPost = createPost();

        // when & then
        mockMvc.perform(get("/api/posts/{id}", savedPost.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("title").value(savedPost.getTitle()))
                .andExpect(jsonPath("contents").value(savedPost.getContents()));
    }

    @Test
    public void createTest() throws Exception {
        // given
        PostDto postDto = new PostDto();
        postDto.setTitle("Spring Security");
        postDto.setContents("Spring Security Test");

        // when & then
        mockMvc.perform(post("/api/posts")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(postDto))
                    .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("title").value("Spring Security"))
                .andExpect(jsonPath("contents").value("Spring Security Test"));
    }

    @Test
    public void updateTest() throws Exception {
        // given
        Post savedPost = createPost();
        PostDto postDto = new PostDto();
        postDto.setTitle("Update Title");
        postDto.setContents("Update Contents");

        // when & then
        mockMvc.perform(put("/api/posts/{id}", savedPost.getId())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(postDto))
                    .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("title").value(postDto.getTitle()))
                .andExpect(jsonPath("contents").value(postDto.getContents()));
    }

    @Test
    public void updateTest_Invalid_Author() throws Exception{
        // given
        Post savedPost = createPost();
        PostDto postDto = new PostDto();
        postDto.setTitle("Update Title");
        postDto.setContents("Update Contents");

        // when & then
        mockMvc.perform(put("/api/posts/{id}", savedPost.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(postDto))
                .with(csrf())
                .with(user(new AccountDetails(anotherAuthor))))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteTest() throws Exception {
        // given
        Post savedPost = createPost();

        // when & then
        mockMvc.perform(delete("/api/posts/{id}", savedPost.getId())
                    .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private Post createPost() {
        Post post = new Post();
        post.setTitle("Spring Security");
        post.setContents("Spring Security Test");
        post.setAuthor(author);
        return postRepository.save(post);
    }
}