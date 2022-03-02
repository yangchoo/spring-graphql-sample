package com.example.demo;

import com.example.demo.model.Post;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {
  final PostRepository posts;
  final CommentRepository comments;

  @Override
  @Transactional
  public void run(ApplicationArguments args) throws Exception {
    log.info("start data initialization...");
    this.posts.deleteAll();
    List<Post> posts = Stream.concat(
            IntStream.range(1, 5)
                .mapToObj(
                    i -> Post.builder().title("Dgs post #" + i)
                        .content("test content of #" + i)
                        .build()

                ),
            Stream.of(
                Post.builder().title("Dgs post #6").content("test content of #1").build()
            )
        )
        .toList();


    this.posts.saveAll(posts);
    this.posts.findAll().forEach(p -> log.info("post: {}", p));
    log.info("done data initialization...");
  }
}
