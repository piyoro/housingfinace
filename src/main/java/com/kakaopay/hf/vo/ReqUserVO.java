package com.kakaopay.hf.vo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReqUserVO {
	@NotNull(message = "id 는 필수입력 항목입니다.")
	@NotEmpty(message = "id 는 필수입력 항목입니다.")
	String userId;
	@NotNull(message = "비밀번호 는 필수입력 항목입니다.")
	@NotEmpty(message = "비밀번호 는 필수입력 항목입니다.")
	String pwd;
}
