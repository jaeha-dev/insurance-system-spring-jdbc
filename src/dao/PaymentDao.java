package dao;

import client.Client;
import contract.Payment;

import java.util.List;

public interface PaymentDao {

    // Insert 보험료 납부내역
    public int insertPayment(String contractNum);

    // Select 보험료 납부내역 Row 개수
    public int selectPaymentRowCount();

    // Select 보험료 납부내역
    public List<Payment> selectPaymentList(String option, Client client);

    // Select 보험료 납부내역
    public Payment selectPayment(Client client);

    // Update 보험료 납부내역 (선택 옵션, 계약 번호) -> 보험료 납부 여부(미납, 납부완료)
    public int updatePaymentByNum(String option, String paymentNum);

    // Delete 보험료 납부내역 (계약 번호)
    public int deletePaymentByNum(String paymentNum);
}