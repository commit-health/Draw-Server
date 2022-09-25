package com.project.draw.domain.user.facade;

import com.corundumstudio.socketio.SocketIOClient;
import com.project.draw.domain.user.domain.User;
import com.project.draw.domain.user.domain.repository.UserRepository;
import com.project.draw.domain.user.exception.PasswordMismatchException;
import com.project.draw.domain.user.exception.UserNotFoundException;
import com.project.draw.global.socket.util.SocketUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return getUserByEmail(email);
    }

    public User getCurrentUser(SocketIOClient socketIOClient) {
        return getUserByEmail(SocketUtil.getEmail(socketIOClient));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
    }

    public boolean emailIsExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void checkPassword(User user, String passowrd) {
        if (!passwordEncoder.matches(passowrd, user.getPassword())) {
            throw PasswordMismatchException.EXCEPTION;
        }
    }
}