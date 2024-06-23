package labs.wilump.kotlinjdsl.support

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import labs.wilump.kotlinjdsl.repository.BookQueryDslRepository
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@TestConfiguration
class TestQueryDslConfig {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    @Bean
    fun jpaQueryFactory(): JPAQueryFactory {
        return JPAQueryFactory(entityManager);
    }

    @Bean
    fun bookQueryDslRepository(): BookQueryDslRepository {
        return BookQueryDslRepository(jpaQueryFactory())
    }
}