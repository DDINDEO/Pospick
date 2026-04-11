package com.pospick.pospick.repository;

import com.pospick.pospick.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
