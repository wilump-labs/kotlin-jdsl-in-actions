package labs.wilump.kotlinjdsl.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import labs.wilump.kotlinjdsl.domain.Book
import labs.wilump.kotlinjdsl.domain.QBook
import labs.wilump.kotlinjdsl.dto.FindBookQuery
import org.springframework.stereotype.Repository

@Repository
class BookQueryDslRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    fun findAll(query: FindBookQuery): List<Book> {
        return jpaQueryFactory.selectFrom(QBook.book)
            .where(
                titleEq(query.title),
                authorEq(query.author),
                publishedYearBetween(query.publishedYear),
            )
            .fetch()
    }

    private fun titleEq(title: String?): BooleanExpression? {
        return title?.let { QBook.book.title.eq(it) }
    }

    private fun authorEq(author: String?): BooleanExpression? {
        return author?.let { QBook.book.author.eq(it) }
    }

    private fun publishedYearBetween(year: Int?): BooleanExpression? {
        return year?.let { QBook.book.publishedAt.year().eq(year) }
    }
}