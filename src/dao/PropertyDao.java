package dao;

import client.Client;
import client.Property;

import java.util.List;
import java.util.Map;

public interface PropertyDao {

    // Insert 자산 (자산 데이터 맵)
    public int insertProperty(String propertyType, String propertyId, Client client, Map<String, Object> dataMap);

    // Select 자산 Row 개수
    public int selectPropertyRowCount();

    // Select 자산 (선택 옵션, 고객 객체)
    public List<Property> selectPropertyList(String option, Client client, Property property);

    // Select 자산 (선택 옵션)
    public Property selectProperty(String option, String propertyId, Property property);

    // Update 자산 (자산 ID)
    public int updatePropertyByNum(String propertyNum);

    // Delete 자산 (자산 ID)
    public int deletePropertyByNum(String propertyNum);
}
