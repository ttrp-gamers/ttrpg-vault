package com.ttrp.manager.repository;

import com.ttrp.manager.entity.User;
import com.ttrp.manager.entity.type.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;



public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByUserRole(UserRole userRole);
}

