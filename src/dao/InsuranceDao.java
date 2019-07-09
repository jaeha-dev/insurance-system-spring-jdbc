package dao;

import insurance.Insurance;

import java.util.List;
import java.util.Map;

public interface InsuranceDao {
    // basicDataMap에는 "insurance_id", "insurance_type", "insurance_name", "insurance_description" 등이 Key 값으로 존재한다.
    // specificDataMap에는 "accident_rate_a", "accident_rate_b", "accident_rate_c" 등이 Key 값으로 존재한다.

    // Insert 보험상품 (보험상품 코드: automobile, fire, driver, accident, ...)
    public int insertInsurance(Map<String, Object> basicDataMap, Map<String, Object> specificDataMap);

    // Select 고객 Row 개수
    public int selectInsuranceRowCount();

    // Select 보험상품 (선택 옵션, 보험상품 코드)
    public List<Insurance> selectInsuranceList(String option, Insurance insurance);

    // Select 보험상품 (보험상품 코드)
    public Insurance selectInsurance(String option, Insurance insurance);

    // Update 보험상품 (선택 옵션, 보험상품 ID)
    public int updateInsuranceById(String option, String insuranceId);

    // Delete 보험상품 (보험상품 ID)
    public int deleteInsuranceById(String insuranceId);
}