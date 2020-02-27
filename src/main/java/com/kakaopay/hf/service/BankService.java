package com.kakaopay.hf.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.kakaopay.hf.common.exception.HousingFinanceException;
import com.kakaopay.hf.common.util.StringUtil;
import com.kakaopay.hf.component.BankComponent;
import com.kakaopay.hf.domain.Bank;
import com.kakaopay.hf.domain.BankSuppAmt;
import com.kakaopay.hf.domain.BankYearMonth;
import com.kakaopay.hf.repository.BankAmtRepository;
import com.kakaopay.hf.repository.BankAmtRepositorySupport;
import com.kakaopay.hf.repository.BankRepository;
import com.kakaopay.hf.vo.BankInfo;
import com.querydsl.core.Tuple;

@Service
public class BankService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(BankService.class);

	@Resource
	BankComponent bankComponent;

	@Resource
	BankRepository bankRepository;
	@Resource
	BankAmtRepository bankAmtRepository;
	@Resource
	BankAmtRepositorySupport bankAmtRepositorySupport;

	/**
	 * <pre>
	 * csv 파일 데이터 각 레코드를 저장
	 * </pre>
	 */
	public ResponseEntity<?> saveBankSuppAmt() {

		int cnt = 0;
		try (InputStream stream = new ClassPathResource("resource.csv").getInputStream()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));

			// 첫번째 줄일 경우 헤더 데이터
			String line = br.readLine();

			String[] cols = line.split(",");

			// 연도, 월 을 제외한 데이터 저장
			int bankCnt = saveBankData(Arrays.copyOfRange(cols, 2, cols.length));
			while ((line = br.readLine()) != null) {
				// 금액 데이터 저장
				cols = StringUtil.getStrToAllowNum(line).split(",");
				saveBankAmtData(cols[0], cols[1], Arrays.copyOfRange(cols, 2, cols.length), bankCnt);
				cnt++;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (HousingFinanceException hfe) {
			// 캐시 올 클리어
			bankComponent.clearBankData();
			throw hfe;
		}
		Map<String, String> response = new HashMap<>();
		response.put("msg", String.format("%s건이 처리되었습니다.", cnt));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * <pre>
	 * csv 헤더 데이터 저장
	 * </pre>
	 * 
	 * @param data
	 */
	private int saveBankData(String[] data) {
		String bankNm = null;
		int cnt = 0, idx = 0;
		String[] tmp = null;
		for (String item : data) {
			tmp = Arrays.copyOfRange(data, ++idx, data.length);
			if (!StringUtil.isEmpty(item)) {
				cnt++;
				// Bad Request
				if (ArrayUtils.contains(tmp, item)) {
					throw new HousingFinanceException(HttpStatus.BAD_REQUEST, "기관명 중복");
				}
				bankNm = StringUtil.trim(item).replace("(억원)", "");
				if (!bankComponent.containsName(bankNm)) {
					Bank bank = Bank.builder().bankCd(bankComponent.getNextBankCd()).bankNm(bankNm).build();
					bankRepository.save(bank);
					// 저장된 데이터를 캐싱
					bankComponent.bankData(bank);
				}
			}
		}
		return cnt;
	}

	/**
	 * <pre>
	 * csv 데이터 저장
	 * </pre>
	 * 
	 * @param data
	 */
	private void saveBankAmtData(String year, String month, String[] data, int bankCnt) {
		int cnt = 0, idx = 0;
		for (String item : data) {
			if (cnt < bankCnt) {
				bankAmtRepository.save(BankSuppAmt.builder().amt(StringUtil.getStrToNum(item))
						.id(BankYearMonth.builder().year(year).month(month).bankCd(bankComponent.getBankCd(++idx)).build()).build());
			} else {
				// Bad Request
				if (!StringUtil.isEmpty(item)) {
					logger.error("this point [{}]", new Object[] { item });
					throw new HousingFinanceException(HttpStatus.BAD_REQUEST, "파일 포맷 확인");
				}
			}
			cnt++;
		}
	}

	/**
	 * <pre>
	 * 주택금융 공급 금융기관(은행) 목록 조회
	 * </pre>
	 * 
	 * @return
	 */
	public List<Bank> findAllBank() {
		return bankRepository.findAll();
	}

	/**
	 * <pre>
	 * 년도별 각 금융기관의 지원금액 합계 조회
	 * </pre>
	 * 
	 * @return
	 */
	public ResponseEntity<?> findBankSumSuppAmt() {
		// List<BankSuppAmt> list = bankAmtRepository.findAll();
		List<BankInfo> list = bankAmtRepository.findBankSumSuppAmt();
		int size = list.size();
		Map<String, Object> rtnObj = new HashMap<>();
		rtnObj.put("name", "주택금융 공급현황");

		if (size == 0) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		logger.debug("{}", new Object[] { list });
		// 조회된 리스트를 year 항목으로 group by
		Map<String, List<BankInfo>> grouppingYearMap = list.stream().collect(Collectors.groupingBy(allData -> allData.getYear()));

		// groupping 된 데이터를 형식에 맞춰 정리
		List<Map<String, Object>> subList = grouppingYearMap.entrySet().stream().map(dataByYear -> {
			Map<String, Object> rtn = new HashMap<>();
			rtn.put("year", dataByYear.getKey());
			List<Map<String, Integer>> amount_list = dataByYear.getValue().stream().map(bank -> {
				Map<String, Integer> detail_amount = new HashMap<>();
				detail_amount.put(bankComponent.getBankNm(bank.getBankCd()), bank.getAmt());
				return detail_amount;
			}).collect(Collectors.toList());
			// 전체 금액(sum) reducing
			int total = dataByYear.getValue().stream().mapToInt(bank -> bank.getAmt()).reduce(0, Integer::sum);
			rtn.put("detail_amount", amount_list);
			rtn.put("total_amount", total);
			return rtn;
		}).collect(Collectors.toList());
		rtnObj.put("list", subList);
		Collections.sort(subList, (a, b) -> StringUtil.getStrToNum((String) a.get("year")) < StringUtil.getStrToNum((String) b.get("year")) ? -1
				: (StringUtil.getStrToNum((String) a.get("year")) > StringUtil.getStrToNum((String) b.get("year")) ? 1 : 0));
		return new ResponseEntity<>(rtnObj, HttpStatus.OK);
	}

	/**
	 * <pre>
	 * 각 년도별 각 기관의 전체 지원금액 중에서 가장 큰 금액의 기관명 조회
	 * </pre>
	 * 
	 * @return
	 */
	public ResponseEntity<?> finBankMaxSumInfo() {
		BankSuppAmt maxBankInfo = bankAmtRepository.finBankMaxSumInfo();
		if (maxBankInfo == null)
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		Map<String, Object> rtnObj = new HashMap<>();
		rtnObj.put("year", maxBankInfo.getId().getYear());
		rtnObj.put("bank", bankComponent.getBankNm(maxBankInfo.getId().getBankCd()));
		return new ResponseEntity<>(rtnObj, HttpStatus.OK);
	}

	/**
	 * <pre>
	 * 전체 년도(2005~2016)에서 외환은행의 지원금액 평균 중에서 가장 작은 금액과 큰 금액 조회
	 * </pre>
	 * 
	 * @return
	 */
	public ResponseEntity<?> findBankAvgInfo(String bankCd) {
		Map<String, Object> rtnObj = new HashMap<>();

		rtnObj.put("bank", bankComponent.getBankNm(bankCd));
		List<Tuple> list = bankAmtRepositorySupport.findBankAmtInfo(bankCd);
		List<Map<String, String>> bankInfoList = new ArrayList<>();
		int size = list.size();
		Map<String, String> tmp = null;

		if (size == 0) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		// 최소값
		tmp = new HashMap<>();
		tmp.put("year", list.get(0).get(0, String.class));
		tmp.put("amount", list.get(0).get(1, Integer.class).toString());
		bankInfoList.add(tmp);
		// 최대값
		tmp = new HashMap<>();
		tmp.put("year", list.get(Math.max(0, size - 1)).get(0, String.class));
		tmp.put("amount", list.get(Math.max(0, size - 1)).get(1, Integer.class).toString());
		bankInfoList.add(tmp);

		rtnObj.put("support_amount", bankInfoList);
		return new ResponseEntity<>(rtnObj, HttpStatus.OK);
	}

	// 년도별 금융기관의 지원금액 조회 (테스트용)
	public List<BankSuppAmt> findAllBankSuppAmt() {
		return Lists.newArrayList(bankAmtRepository.findAll());
	}
}
