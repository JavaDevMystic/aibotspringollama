package jav.dev.pro.aiollamadepspeek.repository;

import jakarta.transaction.Transactional;
import jav.dev.pro.aiollamadepspeek.entity.QuestionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionHistory, Long> {

    @Query("SELECT q FROM QuestionHistory q WHERE q.userChatId = :userChatId")
    List<QuestionHistory> findByUserChatId(@Param("userChatId") Long userChatId);

    @Modifying
    @Transactional
    @Query("DELETE FROM QuestionHistory q WHERE q.userChatId = :userChatId")
    void deleteByUserChatId(@Param("userChatId") Long userChatId);

}
