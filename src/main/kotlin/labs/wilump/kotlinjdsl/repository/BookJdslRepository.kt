package labs.wilump.kotlinjdsl.repository

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import labs.wilump.kotlinjdsl.domain.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BookJdslRepository : JpaRepository<Book, Long>, KotlinJdslJpqlExecutor