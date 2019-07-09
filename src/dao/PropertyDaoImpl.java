package dao;

import client.Client;
import client.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static global.Constants.InsuranceQueryOption.AUTOMOBILE;
import static global.Constants.InsuranceQueryOption.BUILDING;

@Repository
public class PropertyDaoImpl implements PropertyDao {
    private String tableName = "property";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int insertProperty(String propertyType, String propertyId, Client client, Map<String, Object> dataMap) {
        String basicQuery = "insert into property (property_owner, property_type, property_id) values (?, ?, ?)"; // 자산 테이블
        this.jdbcTemplate.update(basicQuery, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setObject(1, client.getClientDataMap().get("client_id"));
                ps.setObject(2, propertyType);
                ps.setObject(3, propertyId);
            }
        });

        String specificQuery = "";
        if (propertyType.equals(AUTOMOBILE.getOption())) {
            specificQuery = "insert into " + propertyType + " (automobile_owner, automobile_id, accident_grade, automobile_type, automobile_value) values (?, ?, ?, ?, ?)"; // 자산 하위 차량 테이블
            return this.jdbcTemplate.update(specificQuery, new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setObject(1, client.getClientDataMap().get("client_id"));
                    ps.setObject(2, dataMap.get("automobile_id"));
                    ps.setObject(3, dataMap.get("accident_grade"));
                    ps.setObject(4, dataMap.get("automobile_type"));
                    ps.setObject(5, dataMap.get("automobile_value"));
                }
            });
        } else if (propertyType.equals(BUILDING.getOption())) {
            specificQuery = "insert into " + propertyType + " (building_owner, building_id, fire_grade, building_type, building_floor, building_pyeong, building_value) values (?, ?, ?, ?, ?, ?, ?)"; // 자산 하위 건물 테이블
            return this.jdbcTemplate.update(specificQuery, new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setObject(1, client.getClientDataMap().get("client_id"));
                    ps.setObject(2, dataMap.get("building_id"));
                    ps.setObject(3, dataMap.get("fire_grade"));
                    ps.setObject(4, dataMap.get("building_type"));
                    ps.setObject(5, dataMap.get("building_floor"));
                    ps.setObject(6, dataMap.get("building_pyeong"));
                    ps.setObject(7, dataMap.get("building_value"));
                }
            });
        }
        return 0;
    }

    @Override
    public int selectPropertyRowCount() {
        String query = "select count(*) from property";
        return jdbcTemplate.queryForObject(query, Integer.class);
    }

    @Override
    public List<Property> selectPropertyList(String option, Client client, Property property) {
        String query = "";
        String propertyType = option;
        List<Property> propertyList = new ArrayList<Property>();
        List<Map<String, Object>> rows = null;

        Optional<Client> optClient = Optional.ofNullable(client);
        if (optClient.isPresent()) { // Not Null
            // 예시) select * from automobile inner join client on automobile.automobile_owner = client.client_id where client_id = 'client-001';
            query = "select * from " + propertyType + " inner join client on " + propertyType + "." + propertyType + "_owner = client.client_id where client.client_id = ?";
            rows = this.jdbcTemplate.queryForList(query, new Object[] {client.getClientId()});
        } else { // Null
            // 예시) select * from automobile inner join client on automobile.automobile_owner = client.client_id;
            query = "select * from " + propertyType + " inner join client on " + propertyType + "." + propertyType + "_owner = client.client_id";
            rows = this.jdbcTemplate.queryForList(query);
        }

        for (Map row : rows) {
            Property tempProperty = Property.getInstance(property.getClass());
            tempProperty.setPropertyDataMap(row);
            propertyList.add(tempProperty);
        }
        return propertyList;
    }

    @Override
    public Property selectProperty(String option, String propertyId, Property property) {
        String query = "select * from " + option + " where " + option + "_id = ?";

        try {
            Property tempProperty = Property.getInstance(property.getClass());
            Map<String, Object> row = this.jdbcTemplate.queryForMap(query, new Object[] {propertyId});
            tempProperty.setPropertyDataMap(row);
            return tempProperty;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public int updatePropertyByNum(String propertyNum) {
        return 0;
    }

    @Override
    public int deletePropertyByNum(String propertyNum) {
        String query = "delete from property where property_num = ?";
        return jdbcTemplate.update(query, new Object[] {propertyNum});
    }
}
