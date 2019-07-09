package dao;

import client.Client;
import contract.Contract;
import insurance.Insurance;

import java.util.List;
import java.util.Map;

public interface ContractDao {

    // Insert 계약 (고객 객체, 보험상품 객체)
    public int insertContract(Client client, Map<String, Object> dataMap, Insurance insurance);

    // Select 계약 Row 개수
    public int selectContractRowCount();

    // Select 계약
    public List<Contract> selectContractList(String option);

    // Select 계약
    public List<Contract> selectContract(String option, Client client);

    // Select 계약
    public Contract selectContract(String option, String contractNum);

    // Update 계약 (계약 ID)
    public int updateContractByNum(String option, String contractNum);

    // Delete 계약 (계약 ID)
    public int deleteContractByNum(String contractNum);
}