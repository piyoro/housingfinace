package com.kakaopay.hf.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Embeddable
public class BankYearMonth implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column
	private String bankCd;

	@Column
	private String year;

	@Column
	private String month;

}
