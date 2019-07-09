package dao;

import client.Client;
import contract.Contract;
import insurance.Insurance;
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

import static global.Constants.ContractQueryOption.*;

@Repository
public class ContractDaoImpl implements ContractDao {
    private String tableName = "contract";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int insertContract(Client client, Map<String, Object> dataMap, Insurance insurance) {
        String contractQuery = "";
        if (! dataMap.containsKey("property_type")) {
            contractQuery = "insert into contract (contract_date, contract_client_id, contract_insurance_id, contract_rate, contract_premium) values (?, ?, ?, ?, ?)"; // 운전자/상해 보험
            return this.jdbcTemplate.update(contractQuery, new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    // Joda time: (http://jsonobject.tistory.com/121) (https://www.lesstif.com/display/JAVA/Joda-Time)
                    ps.setObject(1, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(new DateTime()));
                    ps.setObject(2, client.getClientDataMap().get("client_id"));
                    ps.setObject(3, insurance.getBasicDataMap().get("insurance_id"));
                    ps.setObject(4, insurance.getInsuranceRate());
                    ps.setObject(5, insurance.getInsurancePremium());
                }
            });
        } else if (dataMap.containsKey("property_type")) {
            String propertyId = "";
            contractQuery = "insert into contract (contract_date, contract_client_id, contract_insurance_id, contract_property_id, contract_rate, contract_premium) values (?, ?, ?, ?, ?, ?)"; // 자동차/화재 보험
            if (dataMap.containsKey("automobile_id")) {
                propertyId = String.valueOf(dataMap.get("automobile_id"));
            } else if (dataMap.containsKey("building_id")) {
                propertyId = String.valueOf(dataMap.get("building_id"));
            }
            String finalPropertyId = propertyId;
            return this.jdbcTemplate.update(contractQuery, new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    // Joda time: (http://jsonobject.tistory.com/121) (https://www.lesstif.com/display/JAVA/Joda-Time)
                    ps.setObject(1, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(new DateTime()));
                    ps.setObject(2, client.getClientDataMap().get("client_id"));
                    ps.setObject(3, insurance.getBasicDataMap().get("insurance_id"));
                    ps.setObject(4, finalPropertyId);
                    ps.setObject(5, insurance.getInsuranceRate());
                    ps.setObject(6, insurance.getInsurancePremium());
                }
            });
        }
        return 0;
    }

    @Override
    public int selectContractRowCount() {
        String query = "select count(*) from contract";
        return jdbcTemplate.queryForObject(query, Integer.class);
    }

    @Override
    public List<Contract> selectContractList(String option) {
        String query = "";
        if (option.equals(UNAPPROVED_CONTRACT.getOption())) {
            query = "select * from contract left join client on contract.contract_client_id = client.client_id left join insurance on contract.contract_insurance_id = insurance.insurance_id left join property on contract.contract_property_id = property.property_id where contract.contract_status = 0";
//            query = "select * from contract inner join client on contract.contract_client_id = client.client_id inner join insurance on contract.contract_insurance_id = insurance.insurance_id where contract.contract_status = 0";
        } else if (option.equals(APPROVED_CONTRACT.getOption())) {
            query = "select * from contract left join client on contract.contract_client_id = client.client_id left join insurance on contract.contract_insurance_id = insurance.insurance_id left join property on contract.contract_property_id = property.property_id where contract.contract_status = 1";
//            query = "select * from contract inner join client on contract.contract_client_id = client.client_id inner join insurance on contract.contract_insurance_id = insurance.insurance_id where contract.contract_status = 1";
        }
        List<Contract> contractList = new ArrayList<Contract>();
        List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(query);
        for (Map row : rows) {
            Contract tempContract = Contract.getInstance();
            tempContract.setContractDataMap(row);
            contractList.add(tempContract);
        }
        return contractList;
    }

    @Override
    public List<Contract> selectContract(String option, Client client) {
        String query = "";
        if (option.equals(ALL_CONTRACT.getOption())) {
            query = "select * from contract left join client on contract.contract_client_id = client.client_id left join insurance on contract.contract_insurance_id = insurance.insurance_id left join property on contract.contract_property_id = property.property_id where contract.contract_client_id = ?";
        } else if (option.equals(APPROVED_CONTRACT.getOption())) {
            query = "select * from contract left join client on contract.contract_client_id = client.client_id left join insurance on contract.contract_insurance_id = insurance.insurance_id left join property on contract.contract_property_id = property.property_id where contract.contract_client_id = ? && contract.contract_status = 1;";
        }
        List<Contract> contractList = new ArrayList<Contract>();
        List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(query, new Object[] {client.getClientId()});
        for (Map row : rows) {
            Contract tempProperty = Contract.getInstance();
            tempProperty.setContractDataMap(row);
            contractList.add(tempProperty);
        }
        return contractList;
    }

    @Override
    public Contract selectContract(String option, String contractNum) {
        String query = "";
        if (option.equals(ALL_CONTRACT.getOption())) {
            query = "select * from contract inner join client on contract.contract_client_id = client.client_id inner join insurance on contract.contract_insurance_id = insurance.insurance_id inner join property on contract.contract_property_id = property.property_id where contract.contract_num = ?";
        }
        Contract tempContract = Contract.getInstance();
        Map<String, Object> row = this.jdbcTemplate.queryForMap(query, new Object[] {contractNum});
        tempContract.setContractDataMap(row);
        return tempContract;
    }

    @Override
    public int updateContractByNum(String option, String contractNum) {
        String query = "";
        if (option.equals(APPROVE_CONTRACT.getOption())) {
            query = "update contract set contract_status = 1 where contract_num = ?";
        } else if (option.equals(REJECT_CONTRACT.getOption())) {
            query = "update contract set contract_status = 0 where contract_num = ?";
        }
        return this.jdbcTemplate.update(query, contractNum);
    }

    @Override
    public int deleteContractByNum(String contractNum) {
        String query = "delete from contract where contract_num = ?";
        return jdbcTemplate.update(query, new Object[] {contractNum});
    }
}