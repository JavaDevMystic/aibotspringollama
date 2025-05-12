package jav.dev.pro.aiollamadepspeek.service;


import jav.dev.pro.aiollamadepspeek.entity.User;
import jav.dev.pro.aiollamadepspeek.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Boolean isStart(Long chatId) {
        Optional<User> byChatId = userRepository.findByChatId(chatId);
        if (byChatId.isPresent()) {
            return true;
        }
        return false;
    }

    public Boolean checkUser(Long chatId) {

        Optional<User> byChatId = userRepository.findByChatId(chatId);

        return byChatId.isPresent();
    }

    public boolean deleteUser(Long chatId) {
        int deletedCount = userRepository.deleteByChatId(chatId);
        return deletedCount > 0; // Agar 1 yoki undan ko‘p bo‘lsa true, aks holda false
    }

}
