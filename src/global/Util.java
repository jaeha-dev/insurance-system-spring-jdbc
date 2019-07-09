package global;

import client.Automobile;
import client.Building;
import client.Client;
import client.Property;
import contract.Contract;
import insurance.Insurance;
import main.Main;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;
import static client.Automobile.AutomobileType.*;
import static client.Building.BuildingType.*;
import static global.Constants.CHANGED_ROW_COUNT;
import static global.Constants.CheckNum.*;
import static global.Constants.ClaimQueryOption.*;
import static global.Constants.ClientQueryOption.*;
import static global.Constants.InsuranceQueryOption.*;
import static global.Constants.PaymentQueryOption.*;
import static global.Constants.ShowInfoOption.*;
import static main.Main.printClientMainMenu;

/**
 * 보험사 시스템에서 공통으로 사용되는 메소드
 */
public class Util {

    /**
     * 현재 시각 출력
     * @return
     */
    public static String printTimeStamp() {
        // 2018년 12월 08일(토) 01:16:52 (with Joda Time) (https://jojoldu.tistory.com/26)
        return DateTimeFormat.forPattern("yyyy년 MM월 dd일(E) HH:mm:ss").withLocale(new Locale("ko")).print(new DateTime());
    }

    /**
     * 각종 일자 변환 출력
     * @param inputData
     * @return
     */
    public static String printTime(Object inputData) {
        String outputStr = "";
        DateTime dateTime = DateTime.parse(String.valueOf(inputData), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")); // Mysql -> Java (Nano Sec)
        outputStr = DateTimeFormat.forPattern("yyyy년 MM월 dd일(E) HH:mm:ss").print(dateTime);
        return outputStr;
    }

    /**
     * 보험상품 데이터 변환 출력
     * @param inputData
     * @return
     */
    public static String printInsurance(Object inputData) {
        String outputStr = "";
        if (inputData.equals(AUTOMOBILE.getOption())) { outputStr = "자동차 보험상품";
        } else if (inputData.equals(FIRE.getOption())) { outputStr = "화재 보험상품";
        } else if (inputData.equals(DRIVER.getOption())) { outputStr = "운전자 보험상품";
        } else if (inputData.equals(ACCIDENT.getOption())) { outputStr = "상해 보험상품"; }
        return outputStr;
    }

    /**
     * 고객 데이터 변환 출력
     * @param inputData
     * @return
     */
    public static String printClient(Object inputData) {
        String outputStr = "";
        if (inputData.equals(MAN.getOption())) { outputStr = "남성";
        } else if (inputData.equals(WOMAN.getOption())) { outputStr = "여성";
        } else if (inputData.equals(RISK.getOption())) { outputStr = "위험군";
        } else if (inputData.equals(NON_RISK.getOption())) { outputStr = "비위험군";
        } else if (inputData.equals(TWENTY.getOption())) { outputStr = "20대";
        } else if (inputData.equals(THIRTY.getOption())) { outputStr = "30대";
        } else if (inputData.equals(FORTY.getOption())) { outputStr = "40대";
        } else if (inputData.equals(FIFTY.getOption())) { outputStr = "50대";
        } else if (inputData.equals(ETC.getOption())) { outputStr = "기타"; }
        return outputStr;
    }

    /**
     * 자산 데이터 변환 출력
     * @param inputData
     * @return
     */
    public static String printProperty(Object inputData) {
        String outputStr = "";
        if (inputData.equals(AUTOMOBILE.getOption())) { outputStr = "차량";
        } else if (inputData.equals(BUILDING.getOption())) { outputStr = "건물";
        } else if (inputData.equals("a")) { outputStr = "A등급";
        } else if (inputData.equals("b")) { outputStr = "B등급";
        } else if (inputData.equals("c")) { outputStr = "C등급";
        } else if (inputData.equals(COMPACT.getType())) { outputStr = "소형";
        } else if (inputData.equals(MID_SIZE.getType())) { outputStr = "중형";
        } else if (inputData.equals(FULL_SIZE.getType())) { outputStr = "대형";
        } else if (inputData.equals(HOUSE.getType())) { outputStr = "가정";
        } else if (inputData.equals(STORE.getType())) { outputStr = "상가";
        } else if (inputData.equals(OFFICE.getType())) { outputStr = "사무";
        } else if (inputData.equals(FACTORY.getType())) { outputStr = "공장"; }
        return outputStr;
    }

    /**
     * 보험료 납부 데이터 변환 출력
     * @param inputData
     * @return
     */
    public static String printPayment(Object inputData) {
        String outputStr = "";
        if (inputData.equals(UNPAID.getOption())) { outputStr = "미납";
        } else if (inputData.equals(COMPLETED_PAYMENT.getOption())) { outputStr = "납부완료"; }
        return outputStr;
    }

    /**
     * 보험금 청구 데이터 변환 출력
     * @param inputData
     * @return
     */
    public static String printClaim(Object inputData) {
        String outputStr = "";
        if (inputData.equals(UNCLAIMED.getOption())) { outputStr = "청구없음";
        } else if (inputData.equals(REQUESTED_CLAIM.getOption())) { outputStr = "청구완료";
        } else if (inputData.equals(UNDER_EXAMINATION.getOption())) { outputStr = "심사중";
        } else if (inputData.equals(APPROVED_CLAIM.getOption())) { outputStr = "지급완료";
        } else if (inputData.equals(REJECTED_CLAIM.getOption())) { outputStr = "지급거절"; }
        return outputStr;
    }

    /**
     * 천 단위로 콤마 표시
     * @param inputData
     * @return
     */
    public static String printComma(int inputData) {
        String outputStr = String.format("%,d", inputData);
        outputStr += "원";
        return outputStr;
    }

    /**
     * 요율 데이터 포맷 지정
     * @param rate
     * @return
     */
    public static double printDecimalFormat(double rate) {
        double outputValue = 0;
        DecimalFormat decimalFormat = new DecimalFormat("0.###");
        String decimal =  decimalFormat.format(rate);
        outputValue = Double.parseDouble(decimal);
        return outputValue;
    }

    /**
     * 승인 여부 변환 출력
     * @param status
     * @return
     */
    public static String printStatus(boolean status) {
        String outputStr = "";
        if (status == true) { outputStr = "승인";
        } else { outputStr = "미승인"; }
        return outputStr;
    }

    /**
     * 계약, 보험료 납부, 보험금 청구 변환 출력(+ 맵 요소 널 값 확인)
     * @param option, dataMap, key
     * @return
     */
    public static String checkNullAndPrint(String option, Map<String, Object> dataMap, String key) {
        if (option.equals("일자")) {
            if (dataMap.get(key) != null) {
                return printTime(dataMap.get(key));
            } else {
                return "없음";
            }
        } else if (option.equals("금액")) {
            if (dataMap.get(key) != null) {
                return printComma((Integer) dataMap.get(key));
            } else {
                return "없음";
            }
        }
        return "";
    }

    /**
     * 직업군 출력
     */
    public static void showOccupationGroup() {
        String risk     = "+ 위험군   : 건설 / 기계 / 재료 / 화학 / 전기 / 전자 / 정보통신 / 농업 / 어업 / 환경 / 군인 / 소방 / 경찰 / 교도 / 운송";
        String non_risk = "+ 비위험군 : 경영 / 회계 / 사무 / 관리 / 금융 / 보험 / 교육 / 연구 / 법률 / 의료 / 예술 / 방송 / 영업 / 미용 / 숙박 / 음식";
        System.out.println("* 직업군 분류 *" + "\n" + risk + "\n" + non_risk);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////// [중복 코드]

    /**
     * 각종 보험상품 설계에서 공통된 부분을 추출한 메소드(LinkedHashMap 에 사용자가 입력한 데이터 추가)
     * @param dataMap, keyArr, valueArr
     * @return dataMap
     */
    public static Map<String, Object> addEntry(LinkedHashMap<String, Object> dataMap, String[] keyArr, String[] valueArr) {
        if (keyArr.length == valueArr.length) {
            for (int i = 0; i < (keyArr.length); i++) {
                dataMap.put(keyArr[i], valueArr[i]);
            }
            return dataMap;
        } else {
            return null;
        }
    }

    /**
     * 각종 보험상품 설계에서 공통된 부분을 추출한 메소드
     * @param insurance, basicDataMap, specificDataMap, inputData
     */
    public static void addInsurance(Insurance insurance, Map<String, Object> basicDataMap, Map<String, Object> specificDataMap, String inputData) {
        String insuranceId = String.valueOf(basicDataMap.get("insurance_id"));
        String insuranceName = String.valueOf(basicDataMap.get("insurance_name"));
        if (insurance.designInsurance(basicDataMap, specificDataMap) != CHANGED_ROW_COUNT) {
            System.out.println("\n* " + inputData + " 보험상품(ID: " + insuranceId + " / 이름: " + insuranceName +  ")이 생성되었습니다. *");
        } else {
            System.out.println("\n* " + inputData + " 보험상품 생성에 실패하였습니다. *");
        }
    }

    /**
     * 각종 보험상품 계약에서 공통된 부분을 추출한 메소드 (자산 중복 확인)
     * @param option, list, propertyId
     * @return
     */
    public static Property checkPropertyList(String option, List<Property> list, String propertyId) {
        for (Property property : list) {
            if (option.equals(AUTOMOBILE.getOption())) {
                if (property.getPropertyDataMap().get("automobile_id").equals(propertyId)) {
                    return property;
                }
            } else if (option.equals(BUILDING.getOption())) {
                if (property.getPropertyDataMap().get("building_id").equals(propertyId)) {
                    return property;
                }
            }
        }
        return null;
    }

    /**
     * 각종 보험상품 계약에서 공통된 부분을 추출한 메소드 (요율 및 보험료 계산, 계약 신청)
     * @param insuranceType, insurance, client, dataMap
     */
    public static void computePremiumAndContract(String insuranceType, Scanner scanner, Insurance insurance, Client client, Map<String, Object> dataMap) {
        double rate = printDecimalFormat(insurance.computeRate(dataMap));
        int premium = insurance.computePremium(rate);
        System.out.println("\n: 요율 합계: " + rate);
        System.out.println(": 월 보험료: " + printComma(premium));
        System.out.println("* 계약 신청하시겠습니까? (Y/N) *");
        String answer = scanner.next();
        if (answer.equals("Y")) {
            String insuranceId = String.valueOf(insurance.getBasicDataMap().get("insurance_id"));
            String insuranceName = String.valueOf(insurance.getBasicDataMap().get("insurance_name"));
            insurance.setInsuranceRate(rate);
            insurance.setInsurancePremium(premium);
            registerProperty(client, dataMap);
            if (Contract.getInstance().contract(client, dataMap, insurance) != CHANGED_ROW_COUNT) {
                System.out.println("\n* " + insuranceType + " 보험상품(ID: " + insuranceId + " / 이름: " + insuranceName + ") 계약 신청이 완료되었습니다. *");
            } else {
                System.out.println("\n* " + insuranceType + " 보험상품 계약 신청에 실패하였습니다. *");
            }
        } else if (answer.equals("N")) {
            System.out.println("\n* 계약 신청이 취소되었습니다. *");
        }
    }

    /**
     * 중복 자산 조회. 해당 메소드 대신 checkProperty() 메소드를 사용함
     * @param option, list, propertyId
     * @return
     */
//    public static Property findProperty(String option, List<Property> list, String propertyId) {
//        for (Property property : list) {
//            if (property.getPropertyDataMap().get(option + "_id").equals(propertyId)) {
//                return property;
//            }
//        }
//        return null;
//    }

    /**
     * 각종 보험상품 계약에서 공통된 부분을 추출한 메소드(자산 등록 정보 및 본인 소유 확인)
     * @param client, property, propertyType, propertyId
     */
    public static void checkProperty(Client client, Property property, String propertyType, String propertyId) {
        String selectedOwnerId = String.valueOf(property.getPropertyDataMap().get(propertyType + "_owner"));
        String selectedPropertyId = String.valueOf(property.getPropertyDataMap().get(propertyType + "_id"));
        if (! selectedOwnerId.equals(client.getClientId()) && selectedPropertyId.equals(propertyId)) {
            System.out.println("\n+ 이미 등록된 " + printProperty(propertyType) + " 번호입니다. 본인 소유 정보를 확인하세요. +"); // 타인의 차량 번호를 입력하였을 때, 중복 자산 데이터 삽입을 방지하기 위한 코드
            printClientMainMenu(client);
            return;
        } else if (selectedOwnerId.equals(client.getClientId()) && selectedPropertyId.equals(propertyId)) {
            System.out.println("\n+ 이미 등록된 " + printProperty(propertyType) + " 번호입니다. 해당 " + printProperty(propertyType) + " 정보로 계약 신청을 진행합니다. +"); // 자신이 이미 등록한 차량 번호를 입력하였을 때, 중복 자산 데이터 삽입을 방지하기 위한 코드
        }
        property.showInfo();
    }

    /**
     * 각종 보험상품 계약에서 공통된 부분을 추출한 메소드(자산 등록)
     * @param client, dataMap
     */
    public static void registerProperty(Client client, Map<String, Object> dataMap) {
        if (dataMap.containsKey("property_type") && ! dataMap.containsKey("is_registered")) { // 이미 등록된 자산은 is_registered 을 key 로 갖는다.
            if (dataMap.get("property_type").equals(AUTOMOBILE.getOption())) {
                Property.getInstance(Automobile.class).registerProperty(AUTOMOBILE.getOption(), client, dataMap);
            } else if (dataMap.get("property_type").equals(BUILDING.getOption())) {
                Property.getInstance(Building.class).registerProperty(BUILDING.getOption(), client, dataMap);
            }
        }
    }

    /**
     * 각종 리스트 조회에서 공통된 부분을 추출한 메소드
     * @param option, list, inputData
     * @return
     */
    public static <T> boolean isEmptyList(String option, List<T> list, String inputData) {
        if (list.isEmpty()) {
            System.out.println("\n* " + inputData + " 목록이 존재하지 않습니다. *"); // 리스트가 비었을 시, 출력
            return true;
        } else {
            for (T t : list) {
                try {
                    // Method method = t.getClass().getDeclaredMethod("showInfo"); and ((Method) method).invoke(t); // Java Reflection (선언된 메소드 이름을 찾고, 호출한다.)
                    if (option.equals(SHOW_INFO.getOption())) {
                        t.getClass().getDeclaredMethod("showInfo").invoke(t);
                    } else if (option.equals(SHOW_SPECIFIC_INFO.getOption())) {
                        t.getClass().getDeclaredMethod("showSpecificInfo").invoke(t);
                    }
                } catch (NoSuchMethodException e) { e.printStackTrace();
                } catch (IllegalAccessException e) { e.printStackTrace();
                } catch (InvocationTargetException e) { e.printStackTrace(); }
            }
            return false;
        }
    }

    /**
     * manageInsurance, manageContract, manageClaim, payPremium, claimClaims 메소드 호출 시, 입력 받은 ID 또는 번호가 유효한지 판단한다.
     * 예를 들어, auto-001 에 대한 승인 심사를 진행하는데 잘못 입력 받은 auto-002 를 심사하게 되는 경우 문제가 발생할 수 있기 때문에 이를 방지하기 위함.
     * @param list, menuNum, num
     * @return
     */
    public static <T> void checkInputValueAndInvokeMethod(String option, List<T> list, String param1, String param2, String inputData) {
        boolean result = false;
        String getMapMethodName = "";
        String keyName = "";
        String invokeMethodName = "";

        if (option.equals(MANAGE_INSURANCE.getOption())) {
            getMapMethodName = "getBasicDataMap"; // 보험상품 ID 를 Key 로 갖는 맵
            keyName = "insurance_id";
            invokeMethodName = "evaluateInsurance";
        } else if (option.equals(MANAGE_CONTRACT.getOption())) {
            getMapMethodName = "getContractDataMap"; // 계약번호를 Key 로 갖는 맵
            keyName = "contract_num";
            invokeMethodName = "evaluateContract";
        } else if (option.equals(CREATE_DOCUMENT.getOption())) {
            getMapMethodName = "getContractDataMap";
            keyName = "contract_num";
            invokeMethodName = "createRequestDocument";
        } else if (option.equals(MANAGE_CLAIM.getOption())) {
            getMapMethodName = "getClaimDataMap"; // 청구번호를 Key 로 갖는 맵
            keyName = "claim_num";
            invokeMethodName = "evaluateClaim";
        } else if (option.equals(PAY_PREMIUM.getOption())) {
            getMapMethodName = "getPaymentDataMap"; // 납부번호를 Key 로 갖는 맵
            keyName = "payment_num";
            invokeMethodName = "payPremium";
        } else if (option.equals(CLAIM_CLAIMS.getOption())) {
            getMapMethodName = "getClaimDataMap";
            keyName = "claim_num";
            invokeMethodName = "claimClaims";
        }

        for (T t: list) {
            Method method = null;
            try {
                if (option.equals(MANAGE_INSURANCE.getOption())) { // 보험상품의 경우, 맵에 대한 get 메소드가 상위 클래스에 존재하므로
                    method = t.getClass().getSuperclass().getDeclaredMethod(getMapMethodName);
                } else {
                    method = t.getClass().getDeclaredMethod(getMapMethodName);
                }

                Map<String, Object> returnMap = (Map<String, Object>) method.invoke(t);

                if (option.equals(PAY_PREMIUM.getOption())) { // 보험료 납부의 경우, 인자가 납부번호 1개이다.
                    if (param2.equals(String.valueOf(returnMap.get(keyName)))) {
                        Main.class.getMethod(invokeMethodName, String.class).invoke(invokeMethodName, param2);
                        result = true;
                    }
                } else {
                    if (param2.equals(String.valueOf(returnMap.get(keyName)))) {
                        Main.class.getMethod(invokeMethodName, String.class, String.class).invoke(invokeMethodName, param1, param2);
                        result = true;
                    }
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (result == false) {
            System.err.println("* " + inputData + "(" + param2 + ")가 잘못 입력되었습니다. *\n");
        }
    }
}