package dao;

import com.google.gson.Gson;
import insurance.Insurance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static global.Constants.InsuranceQueryOption.*;

@Repository
public class InsuranceDaoImpl implements InsuranceDao {
    private String tableName = "insurance";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int insertInsurance(Map<String, Object> basicDataMap, Map<String, Object> specificDataMap) {

        // insurance 테이블에 수행할 기본 데이터 쿼리문
        String basicQuery = "insert into insurance (insurance_id, insurance_type, insurance_name, insurance_description, insurance_rate, insurance_premium) values (?, ?, ?, ?, ?, ?)"; // insurance 테이블에 수행할 쿼리문
        Object[] basicParams = basicDataMap.values().toArray();
        int[] dataTypes = new int[] {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.DOUBLE, Types.INTEGER}; // insurance 테이블에 저장될 데이터 타입
        this.jdbcTemplate.update(basicQuery, basicParams, dataTypes); // insurance 테이블에 Insert 수행

        // xxx_insurance 테이블에 수행할 상세 데이터 쿼리문
        String insuranceType = String.valueOf(basicDataMap.get("insurance_type")); // insuranceType = "automobile", "fire" , ...
        String specificQuery = "insert into " + insuranceType + "_insurance (insurance_id, insurance_data) values (?, ?)";
        return this.jdbcTemplate.update(specificQuery, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setObject(1, basicDataMap.get("insurance_id"));
                ps.setObject(2, new Gson().toJson(specificDataMap));
            }
        });
    }

    @Override
    public int selectInsuranceRowCount() {
        String query = "select count(*) from insurance";
        return this.jdbcTemplate.queryForObject(query, Integer.class);
    }

    @Override
    public List<Insurance> selectInsuranceList(String option, Insurance insurance) {
        String query = "";
        String insuranceType = String.valueOf(insurance.getInsuranceType());
        if (option.equals(UNAPPROVED_INSURANCE.getOption())) {
            query = "select * from insurance inner join " + insuranceType + "_insurance on (insurance.insurance_id = " + insuranceType + "_insurance.insurance_id) where insurance_status = 0";
        } else if (option.equals(APPROVED_INSURANCE.getOption())) {
            query = "select * from insurance inner join " + insuranceType + "_insurance on (insurance.insurance_id = " + insuranceType + "_insurance.insurance_id) where insurance_status = 1";
        }

        List<Insurance> insuranceList = new ArrayList<Insurance>();
        List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(query);
        for (Map row : rows) {
            insuranceList.add(this.getRow(insurance, row));
        }
        return insuranceList;
    }

    @Override
    public Insurance selectInsurance(String option, Insurance insurance) {
        String query = "";
        String insuranceType = String.valueOf(insurance.getInsuranceType());
        String insuranceId = String.valueOf(insurance.getInsuranceId());

        try {
            if (option.equals(UNAPPROVED_INSURANCE.getOption())) {
                query = "select * from insurance inner join " + insuranceType + "_insurance on (insurance.insurance_id = " + insuranceType + "_insurance.insurance_id) where insurance.insurance_id = ? && insurance.insurance_status = 0";
            } else if (option.equals(APPROVED_INSURANCE.getOption())) {
                query = "select * from insurance inner join " + insuranceType + "_insurance on (insurance.insurance_id = " + insuranceType + "_insurance.insurance_id) where insurance.insurance_id = ? && insurance.insurance_status = 1";
            } else if (option.equals(CHECK_INSURANCE_ID.getOption())) {
                query = "select * from insurance where insurance_id = ?";
            }

            Map<String, Object> row = this.jdbcTemplate.queryForMap(query, new Object[] {insuranceId});
            return this.getRow(insurance, row);
        } catch (
            EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public int updateInsuranceById(String option, String insuranceId) {
        String query = "";
        if (option.equals(APPROVE_INSURANCE.getOption())) {
            query = "update insurance set insurance_status = 1 where insurance_id = ?";
        } else if (option.equals(REJECT_INSURANCE.getOption())) {
            query = "update insurance set insurance_status = 0 where insurance_id = ?";
        }
        return this.jdbcTemplate.update(query, insuranceId);
    }

    @Override
    public int deleteInsuranceById(String insuranceId) {
        String query = "delete from insurance where insurance_id = ?";
        return this.jdbcTemplate.update(query, insuranceId);
    }

    /**
     * selectInsuranceList(), selectInsurance() 에서 공통된 부분을 추출한 메소드
     * @param insurance, row
     * @return
     */
    public Insurance getRow(Insurance insurance, Map<String, Object> row) {
        Insurance tempInsurance = Insurance.getInstance(insurance.getClass());
        tempInsurance.setBasicDataMap(row); // Map 타입으로 가져온 row 의 모든 데이터가 basicDataMap 에 할당된다. (즉, basicData 뿐만 아니라 해당 맵에서 불필요한 specificData 까지 포함된 데이터가 할당되지만 사용상 문제는 없음)
        tempInsurance.setSpecificDataMap((Map<String, Object>) new Gson().fromJson(String.valueOf(row.get("insurance_data")), Map.class));
        return tempInsurance;
    }
}