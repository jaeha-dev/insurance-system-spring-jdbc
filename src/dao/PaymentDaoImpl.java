package dao;

import client.Client;
import contract.Payment;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static global.Constants.PaymentQueryOption.UNPAID;

@Repository
public class PaymentDaoImpl implements PaymentDao {
    private String tableName = "payment";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int insertPayment(String contractNum) {
        // https://stackoverflow.com/questions/25969/insert-into-values-select-from, http://www.gurubee.net/lecture/2052
        // 예시) insert into payment (contract_num, payment_status, payment_date, payment_amount) select contract.contract_num, 'completed_payment', now(), contract.contract_premium from contract where contract.contract_client_id = 'client-001';

        // String query = "insert into payment (contract_num, payment_status, payment_amount) select ?, ?, contract.contract_premium from contract where contract.contract_num = ?"
        String query = "insert into payment (contract_num, payment_status, payment_amount) select contract.contract_num, ?, contract.contract_premium from contract where contract.contract_num = ?";
        return this.jdbcTemplate.update(query, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setObject(1, UNPAID.getOption());
                ps.setObject(2, contractNum);
            }
        });
    }

    @Override
    public int selectPaymentRowCount() {
        String query = "select count(*) from payment";
        return this.jdbcTemplate.queryForObject(query, Integer.class);
    }

    @Override
    public List<Payment> selectPaymentList(String option, Client client) {
        String query = "";
        List<Payment> paymentList = new ArrayList<Payment>();
        List<Map<String, Object>> rows = null;

        Optional<Client> optClient = Optional.ofNullable(client);
        if (optClient.isPresent()) { // Not Null
            // 예시) select * from payment inner join contract on payment.contract_num = contract.contract_num where payment.payment_status = 'unpaid' && contract_client_id = 'client-001';
            query = "select * from payment inner join contract on payment.contract_num = contract.contract_num where payment.payment_status = ? && contract_client_id = ?";
            rows = this.jdbcTemplate.queryForList(query, new Object[] {option, client.getClientId()});
        } else { // Null
            query = "select * from payment inner join contract on payment.contract_num = contract.contract_num where payment.payment_status = ?";
            rows = this.jdbcTemplate.queryForList(query, option);
        }

        for (Map row : rows) {
            Payment tempPayment = Payment.getInstance();
            tempPayment.setPaymentDataMap(row);
            paymentList.add(tempPayment);
        }
        return paymentList;
    }

    @Override
    public Payment selectPayment(Client client) {
        return null;
    }

    @Override
    public int updatePaymentByNum(String option, String paymentNum) {
        // 예시) update payment set payment_status = 'unpaid', payment_date = now() where contract_num = 8;
        String query = "update payment set payment_status = ?, payment_date = ? where payment_num = ?";
        return this.jdbcTemplate.update(query, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setObject(1, option); // option: completed_payment
                ps.setObject(2, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(new DateTime()));
                ps.setObject(3, paymentNum);
            }
        });
    }

    @Override
    public int deletePaymentByNum(String paymentNum) {
        String query = "delete from payment where payment_num = ?";
        return jdbcTemplate.update(query, new Object[] {paymentNum});
    }
}
