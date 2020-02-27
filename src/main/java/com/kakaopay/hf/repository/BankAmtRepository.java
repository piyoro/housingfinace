package com.kakaopay.hf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kakaopay.hf.domain.BankSuppAmt;
import com.kakaopay.hf.vo.BankInfo;

public interface BankAmtRepository extends JpaRepository<BankSuppAmt, String> {

	// @Query(value = "SELECT new com.kakaopay.hf.domain.BankSuppAmt(v.year, '' AS month, v.bankCd, SUM(v.amt) AS amt) FROM BankSuppAmt v GROUP BY
	// v.year, v.bankCd ")
	@Query(value = "SELECT new com.kakaopay.hf.vo.BankInfo(v.id.bankCd, v.id.year, CAST(SUM(v.amt) as org.hibernate.type.IntegerType) AS amt) FROM BankSuppAmt v GROUP BY v.id.year, v.id.bankCd ORDER BY 2")
	List<BankInfo> findBankSumSuppAmt();

	@Query(value = "SELECT v.year, v.bank_cd, SUM(v.amt) AS amt, '' AS month "
			+ " FROM tb_bank_supp_amt v GROUP BY v.year, v.bank_cd ORDER BY 3 DESC LIMIT 1", nativeQuery = true)
	BankSuppAmt finBankMaxSumInfo();
}
