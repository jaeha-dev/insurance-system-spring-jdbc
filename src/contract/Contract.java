package contract;

import client.Client;
import insurance.Insurance;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static global.Constants.ContractQueryOption.APPROVE_CONTRACT;
import static global.Constants.ContractQueryOption.REJECT_CONTRACT;
import static global.Util.*;
import static main.Main.contractDao;

public class Contract implements Serializable {
    private static final long serialVersionUID = 1L;
    private int contractNum; // 계약 번호(-> PK)
    private boolean status; // 계약 승인 여부
    private String contractDate; // 계약 일자
    private String clientId; // 계약 고객
    private String insuranceId; // 계약 보험상품
    private double contractRate; // 계약 보험 요율
    private int contractPremium; // 계약 월 보험료
    private Map<String, Object> contractDataMap; // 계약 데이터 맵 (INNER JOIN 을 통해 contract, client, insurance 테이블 데이터가 할당됨)

    public Contract() {
        this.status = false;
        this.contractDataMap = new LinkedHashMap<>();
    }

    public static Contract getInstance() {
        return new Contract();
    }

    /**
     * 보험상품을 계약한다.
     * @param client dataMap, insurance
     */
    public int contract(Client client, Map<String, Object> dataMap, Insurance insurance) { // 자산 데이터 맵과 보험상품 객체
        return contractDao.insertContract(client, dataMap, insurance); // 계약 등록
    }

    /**
     * DB에 저장된 특정 계약 데이터를 가져온다.
     * @param option, client
     */
    public List<Contract> getContract(String option, Client client) {
        return contractDao.selectContract(option, client);
    }

    /**
     * DB에 저장된 특정 계약 데이터를 가져온다. (By Contract Number)
     * @param option, contractNum
     */
    public Contract getContract(String option, String contractNum) {
        return contractDao.selectContract(option, contractNum);
    }

    /**
     * DB에 저장된 계약 리스트를 가져온다.
     * @param option
     */
    public List<Contract> getContractList(String option) {
        return contractDao.selectContractList(option);
    }

    /**
     * 계약 심사
     * @param evaluationNum, contractNum
     */
    public int evaluateContract(String evaluationNum, String contractNum) {
        if (evaluationNum.equals("1")) {
            return contractDao.updateContractByNum(APPROVE_CONTRACT.getOption(), contractNum);
        } else if (evaluationNum.equals("2")) {
            return contractDao.updateContractByNum(REJECT_CONTRACT.getOption(), contractNum);
        } else if (evaluationNum.equals("3")) { // 삭제
            return contractDao.deleteContractByNum(contractNum);
        }
        return 0;
    }

    /**
     * 계약 정보 조회 (기본)
     */
    public void showInfo() {
        System.out.println("\n*-------------* [계약 내역] *--------------*");
        System.out.println("- 계약 번호: " + this.getContractDataMap().get("contract_num"));
        System.out.println("- 계약 일자: " + printTime(this.getContractDataMap().get("contract_date")));
        System.out.println("- 계약 승인 여부: " + printStatus((Boolean) this.getContractDataMap().get("contract_status")));
        System.out.println("- 계약 고객 ID: " + this.getContractDataMap().get("contract_client_id"));
        System.out.println("- 계약 보험상품 ID: " + this.getContractDataMap().get("contract_insurance_id"));
        System.out.println("- 계약 보험 요율: " + this.getContractDataMap().get("contract_rate"));
        System.out.println("- 계약 월 보험료: " + printComma((Integer) this.getContractDataMap().get("contract_premium")));
        System.out.println("*-------------------------------------------*");
    }

    /**
     * 계약 정보 조회 (상세)
     */
    public void showSpecificInfo() {
        System.out.println("\n*-----------* [계약 상세내역] *-------------*");
        System.out.println("+ 계약 번호: " + this.getContractDataMap().get("contract_num"));
        System.out.println("- 계약 일자: " + printTime(this.getContractDataMap().get("contract_date")));
        System.out.println("- 계약 승인 여부: " + printStatus((Boolean) this.getContractDataMap().get("contract_status")));
        System.out.println("* - - - - - - - - - - - - - - - - - - - - - *");
        System.out.println("+ 보험상품 종류: " + printInsurance(this.getContractDataMap().get("insurance_type")));
        System.out.println("- 보험상품 ID: " + this.getContractDataMap().get("insurance_id"));
        System.out.println("- 보험상품 이름: " + this.getContractDataMap().get("insurance_name"));
        System.out.println("- 보험상품 설명: " + this.getContractDataMap().get("insurance_description"));
        System.out.println("- 기본 보험 요율: " + this.getContractDataMap().get("insurance_rate"));
        System.out.println("- 기본 월 보험료: " + printComma((Integer) this.getContractDataMap().get("insurance_premium")));
        System.out.println("- 계약 보험 요율: " + this.getContractDataMap().get("contract_rate"));
        System.out.println("- 계약 월 보험료: " + printComma((Integer) this.getContractDataMap().get("contract_premium")));
        System.out.println("* - - - - - - - - - - - - - - - - - - - - - *");
        System.out.println("+ 고객 번호: " + this.getContractDataMap().get("client_num"));
        System.out.println("- 고객 ID: " + this.getContractDataMap().get("client_id"));
        System.out.println("- 고객 비밀번호: " + this.getContractDataMap().get("client_pw"));
        System.out.println("- 고객 이름: " + this.getContractDataMap().get("client_name"));
        System.out.println("- 고객 주민등록번호: " + this.getContractDataMap().get("client_rrn"));
        System.out.println("- 고객 성별: " + printClient(this.getContractDataMap().get("client_gender")));
        System.out.println("- 고객 연령: " + printClient(this.getContractDataMap().get("client_age")));
        System.out.println("- 고객 직업: " + printClient(this.getContractDataMap().get("client_occupation")));
        System.out.println("- 고객 전화번호: " + this.getContractDataMap().get("client_phone"));
        System.out.println("* - - - - - - - - - - - - - - - - - - - - - *");
        if (this.getContractDataMap().get("property_num") != null) {
            System.out.println("+ 자산 번호: " + this.getContractDataMap().get("property_num"));
            System.out.println("- 자산 소유주 ID: " + this.getContractDataMap().get("property_owner"));
            System.out.println("- 자산 종류: " + printProperty(this.getContractDataMap().get("property_type")));
            System.out.println("- 자산 ID: " + this.getContractDataMap().get("property_id"));
        }
        System.out.println("*-------------------------------------------*");
    }

    // getters & setters
    public Map<String, Object> getContractDataMap() { return contractDataMap; }
    public void setContractDataMap(Map<String, Object> contractDataMap) { this.contractDataMap = contractDataMap; }
}