package labs.wilump.kotlinjdsl

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinjdslApplication

fun main(args: Array<String>) {
	runApplication<KotlinjdslApplication>(*args)
}
