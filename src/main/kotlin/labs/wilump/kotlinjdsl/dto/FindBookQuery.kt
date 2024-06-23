package labs.wilump.kotlinjdsl.dto

import jakarta.persistence.criteria.Predicate
import labs.wilump.kotlinjdsl.domain.Book
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate

data class FindBookQuery(
    val title: String? = null,
    val author: String? = null,
    val publishedYear: Int? = null,
    val pageNumber: Int = 0,
    val pageSize: Int = 20,
) {
    fun toPageable(): Pageable {
        return PageRequest.of(pageNumber, pageSize)
    }

    fun toSpecification(): Specification<Book> {
        return Specification { root, _, criteriaBuilder ->
            val predicates = mutableListOf<Predicate>()
            title?.let { predicates.add(criteriaBuilder.equal(root.get<String>("title"), it)) }
            author?.let { predicates.add(criteriaBuilder.equal(root.get<String>("author"), it)) }
            publishedYear?.let {
                val start = LocalDate.of(it, 1, 1)
                val end = LocalDate.of(it, 12, 31)
                predicates.add(criteriaBuilder.between(root.get("publishedAt"), start, end))
            }
            criteriaBuilder.and(*predicates.toTypedArray())
        }
    }
}