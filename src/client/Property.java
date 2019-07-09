package client;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static global.Util.printProperty;
import static main.Main.propertyDao;

public abstract class Property implements Serializable {
    private static final long serialVersionUID = 1L;
    private int propertyNum; // 자산 번호 (기본키)
    private String propertyOwner; // 소유주 (고객 ID를 외래키로 참조)
    private Object propertyType; // 자산 종류
    private String propertyId; // 자산 ID
    private Map<String, Object> propertyDataMap; // 자산 데이터 맵

    public enum PropertyType {
        AUTOMOBILE("automobile"), BUILDING("building");
        private String type;
        private PropertyType(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }
    }

    public Property() {
        this.propertyDataMap = new LinkedHashMap<>();
    }

    public static Property getInstance(Class<? extends Property> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance(); // Java 9: newInstance() is deprecated. Can be replaced by getDeclaredConstructor().newInstance()
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 자산을 등록한다.
     * @param propertyType, client, dataMap
     */
    public int registerProperty(String propertyType, Client client, Map<String, Object> dataMap) { // 자산 데이터 맵
        String propertyId = "";
        if (dataMap.containsKey("automobile_id")) {
            propertyId = String.valueOf(dataMap.get("automobile_id"));
        } else if (dataMap.containsKey("building_id")) {
            propertyId = String.valueOf(dataMap.get("building_id"));
        }
        return propertyDao.insertProperty(propertyType, propertyId, client, dataMap); // 자산 등록
    }

    /**
     * DB에 저장된 특정 자산을 가져온다.
     * @param option, propertyId
     */
    public Property getProperty(String option, String propertyId) {
        return propertyDao.selectProperty(option, propertyId, this);
    }

    /**
     * DB에 저장된 특정 고객의 자산 리스트를 가져온다.
     * @param option, client
     */
    public List<Property> getPropertyList(String option, Client client) {
        return propertyDao.selectPropertyList(option, client, this);
    }

    // 보유 자산 조회
    public void showInfo() {
        System.out.println("\n*-------------* [자산 정보] *--------------*");
        System.out.println("- 자산 번호: " + this.getPropertyDataMap().get("property_num"));
        System.out.println("- 자산 소유주 ID: " + this.getPropertyDataMap().get("property_owner"));
        System.out.println("- 자산 종류: " + printProperty(this.getPropertyDataMap().get("property_type")));
        System.out.println("- 자산(차량/건물) 번호: " + this.getPropertyDataMap().get("property_id"));
        System.out.println("*------------------------------------------*");
    }

    // getters & setters
    public Object getPropertyType() { return propertyType; }
    public void setPropertyType(Object propertyType) { this.propertyType = propertyType; }
    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }

    public Map<String, Object> getPropertyDataMap() { return propertyDataMap; }
    public void setPropertyDataMap(Map<String, Object> propertyDataMap) { this.propertyDataMap = propertyDataMap; }
}