package labs.wilump.kotlinjdsl.repository

import labs.wilump.kotlinjdsl.domain.Book
import labs.wilump.kotlinjdsl.dto.FindBookQuery
import labs.wilump.kotlinjdsl.support.TestQueryDslConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import java.time.LocalDate

@Import(TestQueryDslConfig::class)
@DataJpaTest
class BookQueryDslRepositoryTest {

    @Autowired
    lateinit var jpaRepository: BookJpaRepository

    @Autowired
    lateinit var repository: BookQueryDslRepository

    @Test
    fun findByTitleAndAuthor() {
        // given
        jpaRepository.save(Book("title1", "author1", LocalDate.of(2023, 1, 1)))
        jpaRepository.save(Book("title1", "author1", LocalDate.of(2024, 1, 1)))
        jpaRepository.save(Book("title1", "author2", LocalDate.of(2024, 1, 1)))
        jpaRepository.save(Book("title2", "author2", LocalDate.of(2024, 1, 1)))
        jpaRepository.save(Book("title3", "author3", LocalDate.of(2024, 1, 1)))

        val query = FindBookQuery(title = "title1", author = "author1")

        // when
        val books = repository.findAll(query)

        // then
        assertThat(books).hasSize(2)
        for (b in books) {
            assertThat(b.title).isEqualTo("title1")
            assertThat(b.author).isEqualTo("author1")
        }
    }

    @Test
    fun findByTitleAndAuthorAndPublishedYear() {
        // given
        jpaRepository.save(Book("title1", "author1", LocalDate.of(2023, 1, 1)))
        jpaRepository.save(Book("title1", "author1", LocalDate.of(2024, 1, 1)))
        jpaRepository.save(Book("title1", "author2", LocalDate.of(2024, 1, 1)))
        jpaRepository.save(Book("title2", "author2", LocalDate.of(2024, 1, 1)))
        jpaRepository.save(Book("title3", "author3", LocalDate.of(2024, 1, 1)))

        val query = FindBookQuery(title = "title1", author = "author1", publishedYear = 2024)

        // when
        val books = repository.findAll(query)

        // then
        assertThat(books).hasSize(1)
        for (b in books) {
            assertThat(b.title).isEqualTo("title1")
            assertThat(b.author).isEqualTo("author1")
            assertThat(b.publishedAt.year).isEqualTo(2024)
        }
    }

    @AfterEach
    fun tearDown() {
        jpaRepository.deleteAll()
    }
}