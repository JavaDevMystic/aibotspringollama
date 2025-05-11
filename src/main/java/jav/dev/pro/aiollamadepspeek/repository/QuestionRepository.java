package jav.dev.pro.aiollamadepspeek.repository;

import jav.dev.pro.aiollamadepspeek.entity.QuestionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionHistory, Long> {

}
