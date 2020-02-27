package com.kakaopay.hf.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakaopay.hf.domain.User;

public interface UserRepository extends JpaRepository<User, String> {

}
