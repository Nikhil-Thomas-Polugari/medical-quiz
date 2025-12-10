package medical_quiz.quiz;

import org.springframework.boot.SpringApplication;

public class TestQuizApplication {

	public static void main(String[] args) {
		SpringApplication.from(QuizApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
