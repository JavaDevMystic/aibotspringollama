package jav.dev.pro.aiollamadepspeek.service;



import jav.dev.pro.aiollamadepspeek.entity.QuestionHistory;
import jav.dev.pro.aiollamadepspeek.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {


    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }



    public void save(QuestionHistory questionHistory) {

        questionRepository.save(questionHistory);
    }

    public List<QuestionHistory> questionHistories(Long userChatId) {
        List<QuestionHistory> questions = questionRepository.findByUserChatId(userChatId);

        return questions.isEmpty() ? Collections.emptyList() : questions;
    }

    public void deleteUserQuestion(Long chatId) {

        questionRepository.deleteByUserChatId(chatId);

    }



}