package com.kakaopay.hf.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankInfo {
	String year;
	@JsonIgnore
	String bankCd;
	String bankNm;
	Integer amt;

	public BankInfo(String bankCd, String year, Integer amt) {
		this.bankCd = bankCd;
		this.year = year;
		this.amt = amt;
	}
}
