package com.kakaopay.hf.service;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.kakaopay.hf.domain.Bank;
import com.kakaopay.hf.domain.BankSuppAmt;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BankServiceTest {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(BankServiceTest.class);

	@Resource
	BankService bankService;

	@BeforeClass
	public static void init() {
		logger.debug("Starting testt...");
	}

	@Test
	public void saveBankSuppAmtTest() {

		bankService.saveBankSuppAmt();

		List<Bank> list = bankService.findAllBank();

		List<Bank> testList = new ArrayList<>();
		int index = 0;

		try (InputStream stream = new ClassPathResource("resource.csv").getInputStream()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));

			// 첫번째 줄일 경우 헤더 데이터
			String line = br.readLine();

			String[] cols = line.split(",");
			for (String item : Arrays.copyOfRange(cols, 2, cols.length)) {
				testList.add(Bank.builder().bankCd("bank00" + (++index)).bankNm(item).build());
			}
		} catch (IOException e) {
			assertTrue(false);
		}

		assertEquals(9, list.size());
		assertTrue(list.size() == testList.size());

		List<Bank> noneMatchList = list.stream().filter(target -> testList.stream().anyMatch(comp -> target.equals(comp)))
				.collect(Collectors.toList());

		assertTrue(noneMatchList.size() == 0);

		List<BankSuppAmt> amtList = bankService.findAllBankSuppAmt();
		assertTrue(amtList.size() == 154 * 9);
	}

	@Test
	public void findAllBankTest() {
		List<Bank> list = bankService.findAllBank();
		assertEquals(9, list.size());
	}

	@Test
	/**
	 * <pre>
	 * 년도별 각 금융기관의 지원금액 합계 조회
	 * </pre>
	 * 
	 * @return
	 */
	public void findBankSumSuppAmtTest() {
		ResponseEntity<?> t = bankService.findBankSumSuppAmt();
		Map<String, Object> map = (Map<String, Object>) t.getBody();
		assertNotNull(map);
		List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("list");
		assertNotNull(list);
		assertEquals(13, list.size());

		Map<String, Object> compMap1 = new HashMap<>();
		List<Map<String, Integer>> subList1 = new ArrayList<>();
		Map<String, Integer> subMap11 = new HashMap<>();
		subMap11.put("주택도시기금1)", 89823);
		Map<String, Integer> subMap12 = new HashMap<>();
		subMap12.put("국민은행", 33063);
		Map<String, Integer> subMap13 = new HashMap<>();
		subMap13.put("우리은행", 37661);
		Map<String, Integer> subMap14 = new HashMap<>();
		subMap14.put("신한은행", 21330);
		Map<String, Integer> subMap15 = new HashMap<>();
		subMap15.put("한국시티은행", 50);
		Map<String, Integer> subMap16 = new HashMap<>();
		subMap16.put("하나은행", 15167);
		Map<String, Integer> subMap17 = new HashMap<>();
		subMap17.put("농협은행/수협은행", 17908);
		Map<String, Integer> subMap18 = new HashMap<>();
		subMap18.put("외환은행", 10619);
		Map<String, Integer> subMap19 = new HashMap<>();
		subMap19.put("기타은행", 40184);

		subList1.add(subMap11);
		subList1.add(subMap12);
		subList1.add(subMap13);
		subList1.add(subMap14);
		subList1.add(subMap15);
		subList1.add(subMap16);
		subList1.add(subMap17);
		subList1.add(subMap18);
		subList1.add(subMap19);
		compMap1.put("detail_amount", subList1);
		compMap1.put("year", "2013");
		compMap1.put("total_amount", 265805);
		assertThat(list, hasItem(compMap1));

		/*
		assertThat(list.stream().map(Map::entrySet).collect(Collectors.toList()),
				hasItem(hasItems(compMap1.entrySet().toArray(new Map.Entry[compMap1.size()]))));
		*/
		Map<String, Object> compMap2 = new HashMap<>();
		List<Map<String, Integer>> subList2 = new ArrayList<>();
		Map<String, Integer> subMap21 = new HashMap<>();
		subMap21.put("주택도시기금1)", 22247);
		Map<String, Integer> subMap22 = new HashMap<>();
		subMap22.put("국민은행", 13231);
		Map<String, Integer> subMap23 = new HashMap<>();
		subMap23.put("우리은행", 2303);
		Map<String, Integer> subMap24 = new HashMap<>();
		subMap24.put("신한은행", 1815);
		Map<String, Integer> subMap25 = new HashMap<>();
		subMap25.put("한국시티은행", 704);
		Map<String, Integer> subMap26 = new HashMap<>();
		subMap26.put("하나은행", 3122);
		Map<String, Integer> subMap27 = new HashMap<>();
		subMap27.put("농협은행/수협은행", 1486);
		Map<String, Integer> subMap28 = new HashMap<>();
		subMap28.put("외환은행", 1732);
		Map<String, Integer> subMap29 = new HashMap<>();
		subMap29.put("기타은행", 1376);

		subList2.add(subMap21);
		subList2.add(subMap22);
		subList2.add(subMap23);
		subList2.add(subMap24);
		subList2.add(subMap25);
		subList2.add(subMap26);
		subList2.add(subMap27);
		subList2.add(subMap28);
		subList2.add(subMap29);
		compMap2.put("detail_amount", subList2);
		compMap2.put("year", "2005");
		compMap2.put("total_amount", 48016);
		assertThat(list, hasItem(compMap2));

	}

	@Test
	/**
	 * <pre>
	 * 각 년도별 각 기관의 전체 지원금액 중에서 가장 큰 금액의 기관명 조회
	 * </pre>
	 * 
	 * @return
	 */
	public void finBankMaxSumInfoTest() {
		ResponseEntity<?> t = bankService.finBankMaxSumInfo();
		Map<String, Object> map = (Map<String, Object>) t.getBody();
		Map<String, Object> compMap = new HashMap<>();
		compMap.put("bank", "주택도시기금1)");
		compMap.put("year", "2014");
		assertThat(map, is(compMap));
	}

	@Test
	/**
	 * <pre>
	 * 전체 년도(2005~2016)에서 외환은행의 지원금액 평균 중에서 가장 작은 금액과 큰 금액 조회
	 * </pre>
	 * 
	 * @return
	 */
	public void findBankAvgInfoTest() {
		// 외환은행 기관코드
		String bankCd = "bank008";
		ResponseEntity<?> t = bankService.findBankAvgInfo(bankCd);
		Map<String, Object> map = (Map<String, Object>) t.getBody();
		Map<String, Object> compMap = new HashMap<>();
		compMap.put("bank", "외환은행");
		Map<String, String> t1 = new HashMap<>();
		t1.put("amount", "0");
		t1.put("year", "2017");

		Map<String, String> t2 = new HashMap<>();
		t2.put("amount", "1702");
		t2.put("year", "2015");
		compMap.put("support_amount", Arrays.asList(t1, t2));
		assertThat(map, is(compMap));

	}

	@AfterClass
	public static void complete() {
		logger.debug("Shut down test.");
	}
}
