package ch.kbw.sl;

import ch.kbw.sl.controller.PostController;
import ch.kbw.sl.entity.Post;
import ch.kbw.sl.entity.User;
import ch.kbw.sl.service.PostService;
import ch.kbw.sl.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class PostControllerTest {

    private final PostService postService = Mockito.mock(PostService.class);
    private final UserService userService = Mockito.mock(UserService.class);

    private final PostController postController = new PostController(postService, userService);

    @Test
    void findPostByIdExists() {
        Post post = new Post();
        post.setId(1L);
        Mockito.when(postService.findById(1L))
                .thenReturn(Optional.of(post));

        ResponseEntity<Post> response = postController.getPostById(1L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(post);
    }

    @Test
    void createPostValidUser() {
        Post post = new Post();
        post.setId(1L);
        User user = new User();
        user.setId(1L);

        Mockito.when(userService.findById(1L))
                .thenReturn(Optional.of(user));

        Mockito.when(postService.createPost(any(Post.class)))
                .thenAnswer(invocation -> {
                    Post savedPost = invocation.getArgument(0);
                    savedPost.setId(2L);
                    return savedPost;
                });

        ResponseEntity<Post> response = postController.createPost(post);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(2L);
        assertThat(response.getBody().getUser()).isEqualTo(user);
    }

    @Test
    void deletePostNotExists() {
        Mockito.doThrow(new IllegalArgumentException("not found"))
                .when(postService).deletePost(99L);

        ResponseEntity<Void> response = postController.deletePost(99L);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }
}
