package insurance;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static global.Constants.InsuranceQueryOption.APPROVE_INSURANCE;
import static global.Constants.InsuranceQueryOption.REJECT_INSURANCE;
import static main.Main.insuranceDao;

public abstract class Insurance implements Serializable {
    private static final long serialVersionUID = 1L;
    private int insuranceNum; // 보험상품 번호(-> PK)
    private String insuranceId; // 보험상품 ID(-> UNIQUE)
    private boolean status; // 보험상품 승인 여부
    private Object insuranceType; // 보험상품 종류
    private String insuranceName; // 보험상품 이름
    private String insuranceDescription; // 보험상품 설명
    private double insuranceRate; // 보험상품 최종 요율
    private int insurancePremium;  // 보험상품 최종 보험료
    private Map<String, Object> basicDataMap; // 보험상품 ID, 보험상품 이름 등
    private Map<String, Object> specificDataMap; // 요율, 보장금액

    public Insurance() {
        this.basicDataMap = new LinkedHashMap<String, Object>();
        this.specificDataMap = new LinkedHashMap<String, Object>();
        this.status = false;
    }

    public static Insurance getInstance(Class<? extends Insurance> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance(); // Java 9: newInstance() is deprecated. Can be replaced by getDeclaredConstructor().newInstance()
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract void showInfo();
    public abstract double computeRate(Map<String, Object> dataMap);

    /**
     * DB에 보험상품 추가
     * @param basicDataMap, specificDataMap
     * @return
     */
    public int designInsurance(Map<String, Object> basicDataMap, Map<String, Object> specificDataMap) {
        this.setBasicDataMap(basicDataMap);
        this.setSpecificDataMap(specificDataMap);
        return insuranceDao.insertInsurance(this.getBasicDataMap(), this.getSpecificDataMap());
    }

    /**
     * DB에 저장된 특정 보험상품 추출
     * @param option, insuranceId
     */
    public Insurance getInsurance(String option, String insuranceId) {
        this.setInsuranceId(insuranceId);
        return insuranceDao.selectInsurance(option, this);
    }

    /**
     * DB에 저장된 보험상품 리스트 추출
     * @param option
     */
    public List<Insurance> getInsuranceList(String option) {
        return insuranceDao.selectInsuranceList(option, this);
    }

    /**
     * 보험상품 심사
     * @param evaluationNum, insuranceId
     */
    public static int evaluateInsurance(String evaluationNum, String insuranceId) {
        if (evaluationNum.equals("1")) {
            return insuranceDao.updateInsuranceById(APPROVE_INSURANCE.getOption(), insuranceId);
        } else if (evaluationNum.equals("2")) {
            return insuranceDao.updateInsuranceById(REJECT_INSURANCE.getOption(), insuranceId);
        } else if (evaluationNum.equals("3")) { // 삭제
            return insuranceDao.deleteInsuranceById(insuranceId);
        }
        return 0;
    }

    /**
     * 보험료 산정
     * @param rate
     */
    public int computePremium(double rate) {
        int premium = 0;
        premium = (int) ((int) this.getBasicDataMap().get("insurance_premium") * rate);
        this.setInsurancePremium(premium);
        return this.getInsurancePremium();
    }

    // getters & setters
    public Object getInsuranceType() { return insuranceType; }
    public void setInsuranceType(Object insuranceType) { this.insuranceType = insuranceType; }
    public String getInsuranceId() { return insuranceId; }
    public void setInsuranceId(String insuranceId) { this.insuranceId = insuranceId; }
    public double getInsuranceRate() { return insuranceRate; }
    public void setInsuranceRate(double insuranceRate) { this.insuranceRate = insuranceRate; }
    public int getInsurancePremium() { return insurancePremium; }
    public void setInsurancePremium(int insurancePremium) { this.insurancePremium = insurancePremium; }

    public Map<String, Object> getBasicDataMap() { return basicDataMap; }
    public void setBasicDataMap(Map<String, Object> basicDataMap) { this.basicDataMap = basicDataMap; }
    public Map<String, Object> getSpecificDataMap() { return specificDataMap; }
    public void setSpecificDataMap(Map<String, Object> specificDataMap) { this.specificDataMap = specificDataMap; }
}