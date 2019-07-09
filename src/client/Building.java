package client;

import java.io.Serializable;

import static global.Util.printComma;
import static global.Util.printProperty;

public class Building extends Property implements Serializable {
    private static final long serialVersionUID = 1L;
    private FireGrade fireGrade; // 건물 사고등급
    private BuildingType buildingType; // 건물 업종
    private String buildingNum; // 건물 번호
    private int buildingFloor; // 건물 층수
    private int buildingPyeong; // 건물 평수
    private int buildingValue; // 건물 가액

    public enum FireGrade {
        A("a"), B("b"), C("c");
        private String type;
        private FireGrade(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }
    }

    public enum BuildingType {
        HOUSE("house"), STORE("store"), OFFICE("office"), FACTORY("factory");
        private String type;
        private BuildingType(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }
    }

    public Building() {
        super();
    }

    @Override
    public void showInfo() {
        System.out.println("\n*------------* [건물 정보] *--------------*");
        System.out.println("+ 건물 자산 번호: " + this.getPropertyDataMap().get("property_num"));
        System.out.println("- 건물 소유주 ID: " + this.getPropertyDataMap().get("building_owner"));
        System.out.println("- 건물 번호: " + this.getPropertyDataMap().get("building_id"));
        System.out.println("- 건물 화재등급: " + printProperty(this.getPropertyDataMap().get("fire_grade")));
        System.out.println("- 건물 업종: " + printProperty(this.getPropertyDataMap().get("building_type")));
        System.out.println("- 건물 층수: " + this.getPropertyDataMap().get("building_floor") + "층");
        System.out.println("- 건물 평수: " + this.getPropertyDataMap().get("building_pyeong") + "평");
        System.out.println("- 건물 가액: " + printComma((Integer) this.getPropertyDataMap().get("building_value")));
        System.out.println("*-----------------------------------------*");
    }
}
