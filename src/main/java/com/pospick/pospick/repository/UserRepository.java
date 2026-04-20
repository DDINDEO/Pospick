package com.pospick.pospick.repository;

import com.pospick.pospick.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // loginId로 유저 조회 (로그인, 중복 확인에 사용)
    Optional<User> findByLoginId(String loginId);

    // loginId 중복 여부 확인 (회원가입 시 사용)
    boolean existsByLoginId(String loginId);
}
