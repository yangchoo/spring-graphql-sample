package com.example.demo

import com.example.demo.gql.types.Post
import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.graphql.dgs.reactive.DgsReactiveQueryExecutor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import reactor.test.StepVerifier
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@SpringBootTest
class MutationTests {

    @Autowired
    lateinit var dgsQueryExecutor: DgsReactiveQueryExecutor

    @MockBean
    lateinit var postService: PostService

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `create new post`() = runTest {
        `when`(postService.createPost(any()))
            .thenReturn(
                Post(
                    id = UUID.randomUUID().toString(),
                    title = "test title",
                    content = "test content"
                )
            )

        val requestData = mapOf<String, Any>(
            "query" to "mutation createPost(\$input: CreatePostInput!){ createPost(createPostInput:\$input) {id, title} }",
            "variables" to mapOf(
                "input" to mapOf(
                    "title" to "test title",
                    "content" to "test content"
                )
            )
        )

        val titles = dgsQueryExecutor.executeAndExtractJsonPath<String>(
            objectMapper.writeValueAsString(requestData),
            "data.createPost.title"
        )

        StepVerifier.create(titles)
            .consumeNextWith { assertThat(it).isEqualTo("test title") }
            .verifyComplete()

        verify(postService, times(1)).createPost(any())
        verifyNoMoreInteractions(postService)
    }
}


