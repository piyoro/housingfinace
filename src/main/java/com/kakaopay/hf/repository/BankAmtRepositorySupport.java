package com.kakaopay.hf.repository;

import static com.kakaopay.hf.domain.QBankSuppAmt.bankSuppAmt;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.kakaopay.hf.domain.BankSuppAmt;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class BankAmtRepositorySupport extends QuerydslRepositorySupport {
	private final JPAQueryFactory queryFactory;

	public BankAmtRepositorySupport(JPAQueryFactory queryFactory) {
		super(BankSuppAmt.class);
		this.queryFactory = queryFactory;
	}

	public List<Tuple> findBankAmtInfo(String bankCd) {
		NumberPath<Integer> max_amt = Expressions.numberPath(Integer.class, "amt");
		List<Tuple> list = queryFactory
				.select(bankSuppAmt.id.year, bankSuppAmt.amt.sum().castToNum(Double.class).divide(12).round().castToNum(Integer.class).as("amt"),
						bankSuppAmt.id.bankCd)
				.from(bankSuppAmt).where(bankSuppAmt.id.bankCd.eq(bankCd)).groupBy(bankSuppAmt.id.year, bankSuppAmt.id.bankCd).orderBy(max_amt.asc())
				.fetch();
		return list;
	}
}
