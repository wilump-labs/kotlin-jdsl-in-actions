package labs.wilump.kotlinjdsl.repository

import labs.wilump.kotlinjdsl.domain.Book
import labs.wilump.kotlinjdsl.dto.FindBookQuery
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDate

@DataJpaTest
class BookJpaRepositoryTest {
    @Autowired
    lateinit var repository: BookJpaRepository

    @Test
    fun findByTitle() {
        // given
        repository.save(Book("title1", "author1", LocalDate.of(2024, 1, 1)))
        repository.save(Book("title2", "author2", LocalDate.of(2024, 1, 1)))
        repository.save(Book("title3", "author3", LocalDate.of(2024, 1, 1)))

        // when
        val books = repository.findByTitle("title1")

        // then
        assertThat(books).hasSize(1)
        assertThat(books[0].author).isEqualTo("author1")
    }

    @Test
    fun findByTitleWithSpecification() {
        // given
        repository.save(Book("title1", "author1", LocalDate.of(2024, 1, 1)))
        repository.save(Book("title2", "author2", LocalDate.of(2024, 1, 1)))
        repository.save(Book("title3", "author3", LocalDate.of(2024, 1, 1)))

        val query = FindBookQuery(title = "title1")

        // when
        val books = repository.findAll(query.toSpecification())

        // then
        assertThat(books).hasSize(1)
        assertThat(books[0].author).isEqualTo("author1")
    }

    @Test
    fun findByTitleAndAuthor() {
        // given
        repository.save(Book("title1", "author1", LocalDate.of(2023, 1, 1)))
        repository.save(Book("title1", "author1", LocalDate.of(2024, 1, 1)))
        repository.save(Book("title1", "author2", LocalDate.of(2024, 1, 1)))
        repository.save(Book("title2", "author2", LocalDate.of(2024, 1, 1)))
        repository.save(Book("title3", "author3", LocalDate.of(2024, 1, 1)))

        val query = FindBookQuery(title = "title1", author = "author1")

        // when
        val books = repository.findAll(query.toSpecification())

        // then
        assertThat(books).hasSize(2)
        for (b in books) {
            assertThat(b.title).isEqualTo("title1")
            assertThat(b.author).isEqualTo("author1")
        }
    }

    @AfterEach
    fun tearDown() {
        repository.deleteAll()
    }
}