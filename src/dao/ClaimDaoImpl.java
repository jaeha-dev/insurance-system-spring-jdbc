package dao;

import client.Client;
import contract.Claim;
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
import static global.Constants.ClaimQueryOption.*;

@Repository
public class ClaimDaoImpl implements ClaimDao {
    private String tableName = "claim";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int insertClaim(String contractNum) {
        String query = "insert into claim (contract_num, claim_status) values (?, ?)";
        return this.jdbcTemplate.update(query, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setObject(1, contractNum);
                ps.setObject(2, UNCLAIMED.getOption());
            }
        });
    }

    @Override
    public int selectClaimRowCount() {
        String query = "select count(*) from claim";
        return this.jdbcTemplate.queryForObject(query, Integer.class);
    }

    @Override
    public List<Claim> selectClaimList(String option, Client client) {
        String query = "";
        List<Claim> claimList = new ArrayList<Claim>();
        List<Map<String, Object>> rows = null;

        Optional<Client> optClient = Optional.ofNullable(client);
        if (optClient.isPresent()) { // Not Null
            if (option.equals(UNCLAIMED.getOption())) {
                // 예시) select * from claim inner join contract on claim.contract_num = contract.contract_num where claim.claim_status = 'unclaimed' && contract_client_id = 'client-001';
                query = "select * from claim inner join contract on claim.contract_num = contract.contract_num where claim.claim_status = ? && contract_client_id = ?";
                rows = this.jdbcTemplate.queryForList(query, new Object[] {option, client.getClientId()});
            } else if (option.equals(ALL_CLAIM.getOption())) {
                // 예시) select * from claim inner join contract on claim.contract_num = contract.contract_num where claim.claim_status < 6 && contract_client_id = 'client-001';
                query = "select * from claim inner join contract on claim.contract_num = contract.contract_num where claim.claim_status < 10 && contract_client_id = ?";
                rows = this.jdbcTemplate.queryForList(query, new Object[] {client.getClientId()});
            }
        } else {
            if (option.equals(REQUESTED_CLAIM.getOption()) || option.equals(UNDER_EXAMINATION.getOption())) {
                query = "select * from claim inner join contract on claim.contract_num = contract.contract_num where claim_status = ?";
                rows = this.jdbcTemplate.queryForList(query, option);
            } else if (option.equals(ALL_CLAIM.getOption())) {
                // MYSQL ENUM 타입 인덱스 (https://dev.mysql.com/doc/refman/5.6/en/enum.html), 인덱스를 특정 값 이하로 지정하면 모든 인덱스에 해당하는 ENUM 타입을 선택할 수 있다.
                query = "select * from claim inner join contract on claim.contract_num = contract.contract_num where claim.claim_status < 10";
                rows = this.jdbcTemplate.queryForList(query);
            }
        }

        for (Map row : rows) {
            Claim tempClaim = Claim.getInstance();
            tempClaim.setClaimDataMap(row);
            claimList.add(tempClaim);
        }
        return claimList;
    }

    @Override
    public Claim selectClaim(Client client) {
        return null;
    }

    @Override
    public int updateClaimByNum(String option, String claimNum, String claimAmount) {
        String query = "";

        Optional<String> optString = Optional.ofNullable(claimAmount);
        if (optString.isPresent()) { // Not Null (청구 신청)
            // 예시) update claim set claim_status = 'requested_claim', claim_date = now(), claim_amount = 300000 where contract_num = 11;
            query = "update claim set claim_status = ?, claim_date = ?, claim_amount = ? where claim_num = ?";
            return this.jdbcTemplate.update(query, new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setObject(1, option);
                    ps.setObject(2, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(new DateTime()));
                    ps.setObject(3, Integer.parseInt(claimAmount));
                    ps.setObject(4, claimNum);
                }
            });
        } else { // (청구 심사)
            if (option.equals(APPROVED_CLAIM.getOption())) {
                query = "update claim set claim_status = ?, payment_date = ?, payment_amount = claim_amount where claim_num = ?";
                return this.jdbcTemplate.update(query, new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps) throws SQLException {
                        ps.setObject(1, option);
                        ps.setObject(2, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(new DateTime()));
                        ps.setObject(3, claimNum);
                    }
                });
            } else if (option.equals(REJECTED_CLAIM.getOption()) || (option.equals(UNDER_EXAMINATION.getOption()))) {
                // payment_amount 을 0으로 변경하면 출력 시 '0원', NULL 로 두면 '없음'
                query = "update claim set claim_status = ?, payment_date = ? where claim_num = ?";
                return this.jdbcTemplate.update(query, new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps) throws SQLException {
                        ps.setObject(1, option);
                        ps.setObject(2, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(new DateTime()));
                        ps.setObject(3, claimNum);
                    }
                });
            }
        }
        return 0;
    }

    @Override
    public int deleteClaimByNum(String claimNum) {
        String query = "delete from claim where claim_num = ?";
        return jdbcTemplate.update(query, new Object[] {claimNum});
    }
}