package com.kakaopay.hf.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "tb_bank")
public class Bank {

	@Id
	private String bankCd;

	@Column
	@NotNull
	private String bankNm;

	@Override
	public boolean equals(Object obj) {

		if (obj == null)
			return false;

		if (this.getClass() != obj.getClass())
			return false;

		if (this == obj)
			return true;

		Bank that = (Bank) obj;

		if (this.bankCd == null && that.bankCd != null)
			return false;
		if (this.bankNm == null && that.bankNm != null)
			return false;

		if (this.bankCd.equals(that.bankCd) && this.bankNm.equals(that.bankNm))
			return true;

		return false;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int hashCode = 1;

		hashCode = prime * hashCode + ((bankCd == null) ? 0 : bankCd.hashCode());
		hashCode = prime * hashCode + ((bankNm == null) ? 0 : bankNm.hashCode());

		return hashCode;
	}

}
