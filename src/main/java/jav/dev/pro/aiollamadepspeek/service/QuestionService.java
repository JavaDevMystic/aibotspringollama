package jav.dev.pro.aiollamadepspeek.service;



import jav.dev.pro.aiollamadepspeek.entity.QuestionHistory;
import jav.dev.pro.aiollamadepspeek.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {


    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }



    public void save(QuestionHistory questionHistory) {

        questionRepository.save(questionHistory);
    }
}