package com.application.questionservice.service;

import com.application.questionservice.dto.QuestionAnswerAdminResponse;
import com.application.questionservice.dto.QuestionAnswerResponse;
import com.application.questionservice.dto.QuestionRequest;
import com.application.questionservice.dto.QuestionResponse;
import com.application.questionservice.entity.Question;
import com.application.questionservice.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuestionServiceImpl implements QuestionService {


    private final QuestionRepository questionRepository;

    @Override
    public QuestionResponse createQuestion(QuestionRequest request) {

        Question question = Question.builder()
                .quizId(request.getQuizId())
                .questionText(request.getQuestionText())
                .optionA(request.getOptionA())
                .optionB(request.getOptionB())
                .optionC(request.getOptionC())
                .optionD(request.getOptionD())
                .correctOption(request.getCorrectOption())
                .build();

        Question saved = questionRepository.save(question);

        return mapToResponse(saved);
    }

    @Override
    public QuestionResponse updateQuestion(
            UUID questionId,
            QuestionRequest request
    ) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        question.setQuestionText(request.getQuestionText());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());

        // 🔥 CRITICAL FIX
        if (request.getCorrectOption() != null &&
                !request.getCorrectOption().isBlank()) {
            question.setCorrectOption(request.getCorrectOption());
        }

        Question updated = questionRepository.save(question);
        return mapToResponse(updated);
    }



    @Override
    public void deleteQuestion(UUID questionId) {
        questionRepository.deleteById(questionId);
    }

    @Override
    public List<QuestionResponse> getQuestionsByQuiz(UUID quizId) {

        List<Question> questions = questionRepository.findByQuizId(quizId);

        log.info("Questions found for quiz {} = {}", quizId, questions.size());

        return questions.stream()
                .map(q -> QuestionResponse.builder()
                        .id(q.getId())
                        .questionText(q.getQuestionText())
                        .options(List.of(
                                q.getOptionA(),
                                q.getOptionB(),
                                q.getOptionC(),
                                q.getOptionD()
                        ))
                        .build())
                .toList();
    }

    @Override
    public List<QuestionAnswerAdminResponse> getQuestionsAnswerByQuiz(UUID quizId) {
        List<Question> questions = questionRepository.findByQuizId(quizId);

        log.info("Questions found for quiz {} = {}", quizId, questions.size());

        return questions.stream()
                .map(q -> QuestionAnswerAdminResponse.builder()
                        .id(q.getId())
                        .questionText(q.getQuestionText())
                        .options(List.of(
                                q.getOptionA(),
                                q.getOptionB(),
                                q.getOptionC(),
                                q.getOptionD()
                        ))
                        .correctOption(q.getCorrectOption())
                        .build())
                .toList();
    }


    private QuestionResponse mapToResponse(Question q) {
        return QuestionResponse.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .options(List.of(
                        q.getOptionA(),
                        q.getOptionB(),
                        q.getOptionC(),
                        q.getOptionD()
                ))
                .build();
    }

}



