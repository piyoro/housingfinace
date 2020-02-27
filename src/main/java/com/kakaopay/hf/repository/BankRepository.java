package com.kakaopay.hf.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kakaopay.hf.domain.Bank;

public interface BankRepository extends JpaRepository<Bank, String> {

}
