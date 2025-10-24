package booklet.Application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "booklet.Application")
@EntityScan(basePackages = "booklet.Application.Entities")
@EnableJpaRepositories(basePackages = "booklet.Application.Repositories")

public class BookletApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookletApplication.class, args);
	}

}
