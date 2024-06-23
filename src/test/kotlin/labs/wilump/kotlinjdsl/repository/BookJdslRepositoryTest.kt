package labs.wilump.kotlinjdsl.repository

import labs.wilump.kotlinjdsl.domain.Book
import labs.wilump.kotlinjdsl.dto.FindBookQuery
import labs.wilump.kotlinjdsl.support.JsdlRepositoryBaseTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDate

@DataJpaTest
class BookJdslRepositoryTest : JsdlRepositoryBaseTest() {

    @Autowired
    lateinit var repository: BookJdslRepository

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
        val books = repository.findAll {
            select(entity(Book::class))
            .from(entity(Book::class))
            .whereAnd(
                query.author?.let { path(Book::author).eq(it) },
                query.title?.let { path(Book::title).eq(it) },
                query.publishedYear?.let {
                    path(Book::publishedAt).between(
                        LocalDate.of(it, 1, 1),
                        LocalDate.of(it, 12, 31),
                    )
                },
            )
        }.filterNotNull()

        // then
        Assertions.assertThat(books).hasSize(2)
        for (b in books) {
            Assertions.assertThat(b.title).isEqualTo("title1")
            Assertions.assertThat(b.author).isEqualTo("author1")
        }
    }

    @Test
    fun findByTitleAndAuthorAndPublishedYear() {
        // given
        repository.save(Book("title1", "author1", LocalDate.of(2023, 1, 1)))
        repository.save(Book("title1", "author1", LocalDate.of(2024, 1, 1)))
        repository.save(Book("title1", "author2", LocalDate.of(2024, 1, 1)))
        repository.save(Book("title2", "author2", LocalDate.of(2024, 1, 1)))
        repository.save(Book("title3", "author3", LocalDate.of(2024, 1, 1)))

        val query = FindBookQuery(title = "title1", author = "author1", publishedYear = 2024)

        // when
        val books = repository.findAll {
            select(entity(Book::class))
                .from(entity(Book::class))
                .whereAnd(
                    query.author?.let { path(Book::author).eq(it) },
                    query.title?.let { path(Book::title).eq(it) },
                    query.publishedYear?.let {
                        path(Book::publishedAt).between(
                            LocalDate.of(it, 1, 1),
                            LocalDate.of(it, 12, 31),
                        )
                    },
                )
        }.filterNotNull()

        // then
        Assertions.assertThat(books).hasSize(1)
        for (b in books) {
            Assertions.assertThat(b.title).isEqualTo("title1")
            Assertions.assertThat(b.author).isEqualTo("author1")
        }
    }
}