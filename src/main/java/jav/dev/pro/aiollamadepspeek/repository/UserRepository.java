package jav.dev.pro.aiollamadepspeek.repository;

import jav.dev.pro.aiollamadepspeek.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT b FROM User b WHERE b.chatId=(:chatId)")
    Optional<User> findByChatId(@Param("chatId") Long chatId);

}
