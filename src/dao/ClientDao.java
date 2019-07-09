package dao;

import client.Client;

import java.util.List;
import java.util.Map;

// CRUD: Create, Retrieve(Select), Update, Delete

public interface ClientDao {

    // Insert 고객 (고객 데이터 맵)
    public int insertClient(Map<String, Object> clientDataMap);

    // Select 고객 Row 개수
    public int selectClientRowCount();

    // Select 고객
    public List<Client> selectClientList();

    // Select 고객 (선택 옵션, 고객 객체)
    public Client selectClient(String option, Client client);

    // Update 고객 (고객 ID, 고객 비밀번호)
    public int updateClientByIdAndPassword(String clientId, String clientPw);

    // Delete 고객 (고객 ID)
    public int deleteClientById(String clientId);
}