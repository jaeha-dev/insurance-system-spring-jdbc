package dao;

import client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static global.Constants.ClientQueryOption.*;

@Repository
public class ClientDaoImpl implements ClientDao {
    private String tableName = "client";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int insertClient(Map<String, Object> clientDataMap) {
        String query = "insert into client (client_id, client_pw, client_name, client_rrn, client_gender, client_age, client_occupation, client_phone) values (?, ?, ?, ?, ?, ?, ?, ?) ";
        Object[] params = clientDataMap.values().toArray();
        int[] types = new int[] {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR};
        return this.jdbcTemplate.update(query, params, types);
    }

    @Override
    public int selectClientRowCount() {
        String query = "select count(*) from client";
        return this.jdbcTemplate.queryForObject(query, Integer.class);
    }

    @Override
    public List<Client> selectClientList() {
        String query = "select * from client";
        List<Client> clientList = new ArrayList<Client>();
        List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(query);
        for (Map row : rows) {
            Client tempClient = Client.getInstance();
            tempClient.setClientDataMap(row);
            clientList.add(tempClient);
        }
        return clientList;
    }

    @Override
    public Client selectClient(String option, Client client) {
        String query = "";

        try {
            if (option.equals(CHECK_CLIENT_ID.getOption())) {
                query = "select * from client where client_id = ?";
                Client selectedClient = this.jdbcTemplate.queryForObject(query, new Object[] {client.getClientId()}, new RowMapper<Client>() {
                    @Override
                    public Client mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Client tempClient = Client.getInstance();
                        tempClient.setClientId(rs.getString("client_id"));
                        return tempClient;
                    }
                });
                return selectedClient;
            } else if (option.equals(CHECK_CLIENT_RRN.getOption())) {
                query = "select * from client where client_rrn = ?";
                Client selectedClient = this.jdbcTemplate.queryForObject(query, new Object[] {client.getClientRRN()}, new RowMapper<Client>() {
                    @Override
                    public Client mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Client tempClient = Client.getInstance();
                        tempClient.setClientRRN(rs.getString("client_rrn"));
                        return tempClient;
                    }
                });
                return selectedClient;
            } else if (option.equals(CHECK_LOGIN_VALIDATION.getOption())) {
                query = "select * from client where client_id = ? and client_pw = ?";
                Client selectedClient = this.jdbcTemplate.queryForObject(query, new Object[] {client.getClientId(), client.getClientPw()}, new RowMapper<Client>() {
                    @Override
                    public Client mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Client tempClient = Client.getInstance();
                        tempClient.setClientId(rs.getString("client_id"));
                        tempClient.setClientPw(rs.getString("client_pw"));
                        return tempClient;
                    }
                });
                return selectedClient;
            } else if (option.equals(INQUIRE_CLIENT.getOption())) { // 특정 고객 조회
                query = "select * from client where client_id = ? and client_pw = ?";
                Client tempClient = Client.getInstance();
                Map<String, Object> row = this.jdbcTemplate.queryForMap(query, new Object[] {client.getClientId(), client.getClientPw()});
                tempClient.setClientDataMap(row);
                return tempClient;
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return null;
    }

    @Override
    public int updateClientByIdAndPassword(String clientId, String clientPw) {
        return 0;
    }

    @Override
    public int deleteClientById(String clientId) {
        String query = "delete from client where client_id = ?";
        return this.jdbcTemplate.update(query, clientId);
    }
}