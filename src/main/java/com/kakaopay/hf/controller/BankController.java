package com.kakaopay.hf.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kakaopay.hf.domain.Bank;
import com.kakaopay.hf.service.BankService;

@RestController()
@RequestMapping("/bank")
public class BankController {

	@Resource
	BankService bankService;

	@PostMapping(value = "/saveInfo")
	public ResponseEntity<?> saveInfo(HttpServletRequest req, HttpServletResponse res) {
		return bankService.saveBankSuppAmt();
	}

	@GetMapping(value = "/bankInfo")
	public ResponseEntity<?> bankInfo(HttpServletRequest req, HttpServletResponse res) {
		List<Bank> rtnList = bankService.findAllBank();
		if (rtnList.size() == 0) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(rtnList, HttpStatus.OK);
	}

	@GetMapping(value = "/sumInfo")
	public ResponseEntity<?> sumInfo(HttpServletRequest req, HttpServletResponse res) {
		return bankService.findBankSumSuppAmt();
	}

	@GetMapping(value = "/maxInfo")
	public ResponseEntity<?> maxInfo(HttpServletRequest req, HttpServletResponse res) {
		return bankService.finBankMaxSumInfo();
	}

	@GetMapping(value = "/avgInfo")
	public ResponseEntity<?> avgInfo(HttpServletRequest req, HttpServletResponse res) {
		String bankCd = "bank008";
		return bankService.findBankAvgInfo(bankCd);
	}
}
