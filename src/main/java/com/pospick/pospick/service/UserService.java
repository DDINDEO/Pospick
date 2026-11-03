package com.pospick.pospick.service;

import com.pospick.pospick.domain.User;
import com.pospick.pospick.exception.CustomException;
import com.pospick.pospick.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String getAccountNumber(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));
        return user.getAccountNumber();
    }

    @Transactional
    public void updateAccountNumber(String loginId, String accountNumber) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));
        user.setAccountNumber(accountNumber);
    }
}
