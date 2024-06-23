package labs.wilump.kotlinjdsl.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "book")
class Book(
    @Column(name = "title")
    var title: String,
    @Column(name = "author")
    var author: String,
    @Column
    var publishedAt: LocalDate,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    var id: Long? = null
)

