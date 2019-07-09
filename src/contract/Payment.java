package contract;

import client.Client;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static global.Util.checkNullAndPrint;
import static global.Util.printPayment;
import static main.Main.paymentDao;

public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;
    private int paymentNum; // 보험료 납부 번호
    private PaymentType status; // 보험료 납부 여부
    private String paymentDate; // 보험료 납부 일자
    private int paymentAmount; // 보험료 납부 금액
    private Map<String, Object> paymentDataMap; // 보험료 납부 데이터 맵

    public enum PaymentType {
        // 보험료 납부 여부(미납, 납부완료)
        UNPAID("unpaid"), COMPLETED_PAYMENT("completed_payment");
        private String type;
        private PaymentType(String type) { this.type = type; }
        public String getType() { return type; }
    }

    public Payment() {
        this.status = PaymentType.UNPAID;
        this.paymentDataMap = new LinkedHashMap<String, Object>();
    }

    public static Payment getInstance() {
        return new Payment();
    }

    /**
     * 보험료 납부 생성
     * @param contractNum
     */
    public int createPayment(String contractNum) {
        return paymentDao.insertPayment(contractNum);
    }

    /**
     * 보험료 납부
     * @param option, contractNum
     */
    public int payPremium(String option, String contractNum) {
        return paymentDao.updatePaymentByNum(option, contractNum);
    }

    /**
     * 보험료 납부내역 조회
     * @param option, client
     */
    public List<Payment> getPaymentList(String option, Client client) {
        return paymentDao.selectPaymentList(option, client);
    }

    public void showInfo() {
        System.out.println("\n*-----------* [보험료 납부내역] *-----------*");
        System.out.println("+ 계약 번호: " + this.getPaymentDataMap().get("contract_num"));
        System.out.println("- 계약 고객 ID: " + this.getPaymentDataMap().get("contract_client_id"));
        System.out.println("- 계약 보험상품 ID: " + this.getPaymentDataMap().get("contract_insurance_id"));
        System.out.println("* - - - - - - - - - - - - - - - - - - - - - *");
        System.out.println("+ 납부 번호: " + this.getPaymentDataMap().get("payment_num"));
        System.out.println("- 납부 상태: " + printPayment(this.getPaymentDataMap().get("payment_status")));
        System.out.println("- 납부 일자: " + checkNullAndPrint("일자", this.getPaymentDataMap(), "payment_date"));
        System.out.println("- 납부 금액: " + checkNullAndPrint("금액", this.getPaymentDataMap(), "payment_amount"));
        System.out.println("*-------------------------------------------*");
    }

    public Map<String, Object> getPaymentDataMap() { return paymentDataMap; }
    public void setPaymentDataMap(Map<String, Object> paymentDataMap) { this.paymentDataMap = paymentDataMap; }
}
