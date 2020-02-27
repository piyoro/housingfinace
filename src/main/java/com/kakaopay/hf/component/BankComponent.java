package com.kakaopay.hf.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.kakaopay.hf.common.util.StringUtil;
import com.kakaopay.hf.domain.Bank;
import com.kakaopay.hf.service.BankService;

@Component
public class BankComponent {

	@Resource
	BankService bankService;

	private static Map<String, Bank> Bank_Map = new HashMap<>();;

	@PostConstruct
	private void init() {
		List<Bank> bankList = bankService.findAllBank();
		bankList.stream().forEach(bank -> Bank_Map.put(bank.getBankCd(), bank));
	}

	public boolean containsName(String bankNm) {
		return Bank_Map.values().stream().anyMatch(bank -> bankNm.equals(bank.getBankNm()));
	}

	public boolean contains(String bankCd) {
		return Bank_Map.containsKey(bankCd);
	}

	public String getBankNm(String bankCd) {
		return Bank_Map.get(bankCd).getBankNm();
	}

	public int getBankSize() {
		return Bank_Map.size();
	}

	public String getBankCd(int index) {
		return "bank" + StringUtil.pad(index, 3, "0");
	}

	public String getNextBankCd() {
		return "bank" + StringUtil.pad(Bank_Map.size() + 1, 3, "0");
	}

	public void bankData(Bank bank) {
		Bank_Map.put(bank.getBankCd(), bank);
	}

	public void clearBankData() {
		Bank_Map.clear();
		;
	}

}
