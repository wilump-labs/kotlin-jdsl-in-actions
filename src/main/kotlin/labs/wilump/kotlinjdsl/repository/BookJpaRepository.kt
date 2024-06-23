package labs.wilump.kotlinjdsl.repository

import labs.wilump.kotlinjdsl.domain.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface BookJpaRepository : JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    fun findByTitle(title: String): List<Book>
}