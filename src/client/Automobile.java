package client;

import java.io.Serializable;

import static global.Util.printComma;
import static global.Util.printProperty;

public class Automobile extends Property implements Serializable {
    private static final long serialVersionUID = 1L;
    private AccidentGrade accidentGrade; // 차량 사고등급
    private AutomobileType automobileType; // 차량 종류
    private String automobileNum; // 차량 번호
    private int automobileValue; // 차량 가액

    public enum AccidentGrade {
        A("a"), B("b"), C("c");
        private String type;
        private AccidentGrade(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }
    }

    public enum AutomobileType {
        COMPACT("compact"), MID_SIZE("mid_size"), FULL_SIZE("full_size");
        private String type;
        private AutomobileType(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }
    }

    public Automobile() {
        super();
    }

    @Override
    public void showInfo() {
        System.out.println("\n*------------* [차량 정보] *--------------*");
        System.out.println("+ 차량 자산 번호: " + this.getPropertyDataMap().get("property_num"));
        System.out.println("- 차량 소유주 ID: " + this.getPropertyDataMap().get("automobile_owner"));
        System.out.println("- 차량 번호: " + this.getPropertyDataMap().get("automobile_id"));
        System.out.println("- 차량 사고등급: " + printProperty(this.getPropertyDataMap().get("accident_grade")));
        System.out.println("- 차량 종류: " + printProperty(this.getPropertyDataMap().get("automobile_type")));
        System.out.println("- 차량 가액: " + printComma((Integer) this.getPropertyDataMap().get("automobile_value")));
        System.out.println("*-----------------------------------------*");
    }
}