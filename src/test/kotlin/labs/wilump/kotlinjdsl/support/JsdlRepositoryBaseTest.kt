package labs.wilump.kotlinjdsl.support

import com.linecorp.kotlinjdsl.support.spring.data.jpa.autoconfigure.KotlinJdslAutoConfiguration
import org.springframework.context.annotation.Import

@Import(KotlinJdslAutoConfiguration::class)
abstract class JsdlRepositoryBaseTest