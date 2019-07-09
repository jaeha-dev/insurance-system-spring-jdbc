package contract;

import client.Client;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static global.Constants.ClaimQueryOption.*;
import static global.Util.checkNullAndPrint;
import static global.Util.printClaim;
import static main.Main.claimDao;

public class Claim implements Serializable {
    private static final long serialVersionUID = 1L;
    private int claimNum; // 보험금 청구 번호
    private ClaimType status; // 보험금 지급 여부
    private String paymentDate; // 보험금 지급 일자
    private int claimAmount; // 보험금 청구 금액
    private Map<String, Object> claimDataMap; // 보험료 납부 데이터 맵

    public enum ClaimType {
        // 보험금 청구 및 심사결과 (청구없음, 청구완료, 심사중, 지급완료, 지급거절)
        UNCLAIMED("unclaimed"), COMPLETED_CLAIM("completed_claim"), UNDER_EXAMINATION("under_examination"), CLAIM_PAYMENT_APPROVAL("claim_payment_approval"), CLAIM_PAYMENT_REFUSAL("claim_payment_refusal");
        private String type;
        private ClaimType(String type) { this.type = type; }
        public String getType() { return type; }
    }

    public Claim() {
        this.status = ClaimType.UNCLAIMED;
        this.claimDataMap = new LinkedHashMap<String, Object>();
    }

    public static Claim getInstance() {
        return new Claim();
    }

    /**
     * 보험금 청구 생성
     * @param contractNum
     */
    public int createClaim(String contractNum) {
        return claimDao.insertClaim(contractNum);
    }

    /**
     * 보험금 청구 신청
     * @param option, contractNum, claimAmount
     */
    public int claimClaims(String option, String contractNum, String claimAmount) {
        return claimDao.updateClaimByNum(option, contractNum, claimAmount);
    }

    /**
     * 보험금 청구내역 조회
     * @param option, client
     */
    public List<Claim> getClaimList(String option, Client client) {
        return claimDao.selectClaimList(option, client);
    }

    /**
     * 보험금 지급 심사
     * @param evaluationNum, claimNum
     */
    public int evaluateClaim(String evaluationNum, String claimNum) {
        if (evaluationNum.equals("1")) { // 계약번호
            return claimDao.updateClaimByNum(APPROVED_CLAIM.getOption(), claimNum, null);
        } else if (evaluationNum.equals("2")) { // 계약번호
            return claimDao.updateClaimByNum(REJECTED_CLAIM.getOption(), claimNum, null);
        } else if (evaluationNum.equals("3")) { // 계약번호
            return claimDao.updateClaimByNum(UNDER_EXAMINATION.getOption(), claimNum, null);
        } else if (evaluationNum.equals("4")) { // 삭제 (청구번호)
            return claimDao.deleteClaimByNum(claimNum);
        }
        return 0;
    }

    public void showInfo() {
        System.out.println("\n*-----------* [보험금 청구내역] *------------*");
        System.out.println("+ 계약 번호: " + this.getClaimDataMap().get("contract_num"));
        System.out.println("- 계약 고객 ID: " + this.getClaimDataMap().get("contract_client_id"));
        System.out.println("- 계약 보험상품 ID: " + this.getClaimDataMap().get("contract_insurance_id"));
        System.out.println("* - - - - - - - - - - - - - - - - - - - - - *");
        System.out.println("+ 청구 번호: " + this.getClaimDataMap().get("claim_num"));
        System.out.println("- 청구 상태: " + printClaim(this.getClaimDataMap().get("claim_status")));
        System.out.println("- 청구 일자: " + checkNullAndPrint("일자", this.getClaimDataMap(), "claim_date"));
        System.out.println("- 청구 금액: " + checkNullAndPrint("금액", this.getClaimDataMap(), "claim_amount"));
        System.out.println("* - - - - - - - - - - - - - - - - - - - - - *");
        if (this.getClaimDataMap().get("payment_amount") == null) { // 지급보류, 지급거절 시 출력
            System.out.println("+ 심사 일자: " + checkNullAndPrint("일자", this.getClaimDataMap(), "payment_date"));
        } else { // 지급승인 시 출력
            System.out.println("+ 지급 일자: " + checkNullAndPrint("일자", this.getClaimDataMap(), "payment_date"));
        }
        System.out.println("- 지급 금액: " + checkNullAndPrint("금액", this.getClaimDataMap(), "payment_amount"));
        System.out.println("*-------------------------------------------*");
    }

    // getters & setters
    public Map<String, Object> getClaimDataMap() { return claimDataMap; }
    public void setClaimDataMap(Map<String, Object> claimDataMap) { this.claimDataMap = claimDataMap; }
}
