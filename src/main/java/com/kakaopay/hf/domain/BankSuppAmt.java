package com.kakaopay.hf.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_bank_supp_amt")
public class BankSuppAmt {

	@EmbeddedId
	private BankYearMonth id;

	@Column
	private Integer amt;
}
