# Kotlin JDSL in actions

- Spring Data Jpa(+Specification), QueryDSL을 이용한 동적 쿼리 생성
- Kotlin JDSL을 이용한 동적 쿼리 생성

## 1. Spring Data Jpa, QueryDSL
### 1.1. Spring Data Jpa(+Specification)
JPA Specification을 이용하면 조건에 따라 동적 쿼리를 생성할 수 있음

```kotlin
// DTO
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

// Repository
interface BookJpaRepository : JpaRepository<Book, Long>, JpaSpecificationExecutor<Book>

// 조회
val books = repository.findAll(query.toSpecification(), query.toPageable())
```

Repository에 `JpaSpecificationExecutor`를 추가로 상속받으면, `Specification`을 이용하여 동적 쿼리를 생성할 수 있음

조회할 방법에 따라 `Specification`을 정의하고, 정의된 spec에 따라 조회할 수 있음
- Pageable을 이용하여 페이징 처리도 가능함

### 1.2. QueryDSL
#### 의존성 추가
```groovy
// build.gradle.kts
...
plugins {
  kotlin("kapt") version "1.9.24"
}
...
dependencies {
  implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
  implementation("com.querydsl:querydsl-apt:5.0.0:jakarta")
  implementation("jakarta.persistence:jakarta.persistence-api")
  implementation("jakarta.annotation:jakarta.annotation-api")
  kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
  kapt("org.springframework.boot:spring-boot-configuration-processor")
}
```

#### QueryDSL 설정
```kotlin
// QueryDslConfig.kt
@Configuration
class QueryDslConfig {
  lateinit var entityManager: EntityManager

  @Bean
  fun jpaQueryFactory(): JPAQueryFactory {
    return JPAQueryFactory(entityManager)
  }
}
```

#### QueryDSL 사용
```kotlin
// Repository
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
```

사용 전에는 반드시 Build 를 통해 `QClass`를 선행해야 함
- 빌드 시 `build/generated/source/kapt/main`에 `QClass`가 생성됨

생성된 `QClass`를 이용하여 동적 쿼리를 생성할 수 있음

<br>

## 2. Kotlin JDSL?
> Kotlin JDSL is a Kotlin library that makes query building and execution easy.
> 
> You can create queries using your own classes and Kotlin's built-in functions without an annotation processor, and easily execute them in your library.

라인에서 공개한 Kotlin 기반 쿼리 빌더 라이브러리로, 별도의 메타 모델 생성 없이 쿼리를 쉽게 작성할 수 있음
- Annotation Processing Tool(APT)를 사용하면 엔티티나 테이블 클래스의 필드 이름이나 유형이 변경될 때마다 다시 컴파일해야 하는 어려움이 있음
- Kotlin JDSL은 KClass와 KProperty 기반의 DSL(Domain-Specific Language)을 제공하므로 APT의 불편함 없이 쉽게 쿼리를 작성할 수 있음


### 관련 링크
- [Github: Kotlin JDSL](https://github.com/line/kotlin-jdsl)
- [Kotlin JDSL Docs](https://kotlin-jdsl.gitbook.io/docs/v/ko-1)
- [Line Engineering Blog: Kotlin JDSL: Kotlin을 이용해 좀 더 쉽게 JPA Criteria API를 작성해 봅시다](https://engineering.linecorp.com/ko/blog/kotlinjdsl-jpa-criteria-api-with-kotlin)

<br>

## 3. Kotlin JDSL Quick Start
### 의존성 추가
```groovy
dependencies {
  implementation("com.linecorp.kotlin-jdsl:jpql-dsl:3.5.0")
  implementation("com.linecorp.kotlin-jdsl:jpql-render:3.5.0")
  implementation("com.linecorp.kotlin-jdsl:spring-data-jpa-support:3.5.0") // Optional
}
```
- `jpql-dsl`: JPQL 쿼리를 만들 수 있게 도와주는 DSL
- `jpql-render`: DSL로 만든 쿼리를 String으로 변환시켜주는 라이브러리
- `spring-data-jpa-support`: Spring Data Jpa와 함께 쿼리를 실행하도록 도움을 주는 라이브러리

추가로 제공되는 패키지는 [여기](https://kotlin-jdsl.gitbook.io/docs/v/ko-1/jpql-with-kotlin-jdsl#support-dependencies)에서 확인할 수 있습니다.

### 테스트 환경 설정
```kotlin
@Import(KotlinJdslAutoConfiguration::class)
abstract class JsdlRepositoryBaseTest
```

`KotlinJdslJpqlExecutor`를 `@DataJpaTest`에서 사용할 경우 `KotlinJdslAutoConfiguration` 직접 Import 해야함

- `@DataJpaTest`는 slice test 이기 때문에 최소한의 Bean만 생성하고, `KotlinJdslAutoConfiguration`은 생성 대상에 포함되어 있지 않음
- [참고: Spring Data Repository Support](https://kotlin-jdsl.gitbook.io/docs/v/ko-1/jpql-with-kotlin-jdsl/spring-supports#spring-data-repository)

### 기본 사용 방법
```kotlin
// select statement 생성
val query = jpql {
  select(
    path(Author::authorId),
  ).from(
    entity(Author::class)
  )
}

// 쿼리 렌더링
val context = JpqlRenderContext()
val renderer = JpqlRenderer()
val rendered = renderer.render(query, context)

// 쿼리 실행
val jpaQuery: Query = entityManager.createQuery(rendered.query).apply {
    rendered.params.forEach { (name, value) ->
        setParameter(name, value)
    }
}
val result = jpaQuery.resultList
```
`jpql()`을 이용하여 `statement`를 생성할 수 있음
- [참고: Build a query](https://kotlin-jdsl.gitbook.io/docs/v/ko-1/jpql-with-kotlin-jdsl#build-a-query)

쿼리를 만든 뒤에는 RenderContext를 이용해 쿼리를 실행할 수 있음
- [참고: Execute the query](https://kotlin-jdsl.gitbook.io/docs/v/ko-1/jpql-with-kotlin-jdsl#execute-the-query)

만약 Spring Data Jpa를 사용하고 있다면 아래 방법과 같이 편하게 사용할 수도 있음

### Repository 생성 및 쿼리 실행
```kotlin
// JDSL Repository
interface BookJdslRepository : JpaRepository<Book, Long>, KotlinJdslJpqlExecutor

// 조회 테스트
@DataJpaTest
class BookJdslRepositoryTest : JsdlRepositoryBaseTest() {
    ...

  @Test
  fun findByTitleAndAuthor() {
    ...
    
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
    
    ...
  }
}
```

사용하고 있는 `JpaRepository`에 `KotlinJdslJpqlExecutor`를 상속하면, Kotlin JDSL이 제공하는 확장 기능을 사용할 수 있음
- [참고: Spring Data Repository](https://kotlin-jdsl.gitbook.io/docs/v/ko-1/jpql-with-kotlin-jdsl/spring-supports#spring-data-repository)

이후에 메소드를 사용할 때, Kotlin JDSL을 이용하여 쿼리를 작성할 수 있음

