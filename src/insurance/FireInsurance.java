package insurance;

import java.util.Map;

import static global.Constants.InsuranceQueryOption.FIRE;
import static global.Util.*;

public class FireInsurance extends Insurance {

    public FireInsurance() {
        this.setInsuranceType(FIRE);
    }

    @Override
    public double computeRate(Map<String, Object> dataMap) {
        double totalRate = 0;
        String fireGrade = (String) dataMap.get("fire_grade");
        String buildingType = (String) dataMap.get("building_type");
        int buildingFloor = Integer.parseInt(String.valueOf(dataMap.get("building_floor")));
        int buildingPyeong = Integer.parseInt(String.valueOf(dataMap.get("building_pyeong")));
        int buildingValue = Integer.parseInt(String.valueOf(dataMap.get("building_value")));

        totalRate += (double) this.getBasicDataMap().get("insurance_rate");

        if (fireGrade.equals("a")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("fire_rate_a"));
        } else if (fireGrade.equals("b")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("fire_rate_b"));
        } else if (fireGrade.equals("c")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("fire_rate_c"));
        }
        if (buildingType.equals("house")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("type_rate_house"));
        } else if (buildingType.equals("store")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("type_rate_store"));
        } else if (buildingType.equals("office")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("type_rate_office"));
        } else if (buildingType.equals("factory")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("type_rate_factory"));
        }
        if (0 <= buildingFloor && buildingFloor <= 30) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("floor_rate_lt30"));
        } else if (30 < buildingFloor && buildingFloor <= 60) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("floor_rate_lt60"));
        } else if (60 < buildingFloor) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("floor_rate_mt60"));
        }
        if (0 <= buildingPyeong && buildingPyeong <= 150) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("pyeong_rate_lt150"));
        } else if (150 < buildingPyeong && buildingPyeong <= 500) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("pyeong_rate_lt500"));
        } else if (500 < buildingPyeong) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("pyeong_rate_mt500"));
        }
        if (0 <= buildingValue && buildingValue <= 500000000) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("value_rate_lt500m"));
        } else if (500000000 < buildingValue && buildingValue <= 1000000000) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("value_rate_lt1000m"));
        } else if (1000000000 < buildingValue) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("value_rate_mt1000m"));
        }
        this.setInsuranceRate(totalRate);
        return totalRate;
    }

    @Override
    public void showInfo() {
        System.out.println("\n*--------------------------* [화재 보험상품 정보] *--------------------------*");
        System.out.println("+ 승인 여부: " + printStatus((Boolean) this.getBasicDataMap().get("insurance_status")));
        System.out.println("- 보험상품 종류: " + printInsurance(this.getBasicDataMap().get("insurance_type")));
        System.out.println("- 보험상품 ID: " + this.getBasicDataMap().get("insurance_id"));
        System.out.println("- 보험상품 이름: " + this.getBasicDataMap().get("insurance_name"));
        System.out.println("- 보험상품 설명: " + this.getBasicDataMap().get("insurance_description"));
        System.out.println("- 기본 보험 요율: " + this.getBasicDataMap().get("insurance_rate"));
        System.out.println("- 기본 월 보험료: " + printComma((Integer) this.getBasicDataMap().get("insurance_premium")));
        System.out.println("* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - *");
        System.out.println("+ 건물 화재등급별 요율(A등급, B등급, C등급): " +
                this.getSpecificDataMap().get("fire_rate_a") + ", " +
                this.getSpecificDataMap().get("fire_rate_b") + ", " +
                this.getSpecificDataMap().get("fire_rate_c"));
        System.out.println("- 건물 업종별(가정, 상가, 사무, 공장): " +
                this.getSpecificDataMap().get("type_rate_house") + ", " +
                this.getSpecificDataMap().get("type_rate_store") + ", " +
                this.getSpecificDataMap().get("type_rate_office") + ", " +
                this.getSpecificDataMap().get("type_rate_factory"));
        System.out.println("- 건물 층수별(0~30층, 31~60층, 61층~): " +
                this.getSpecificDataMap().get("floor_rate_lt30") + ", " +
                this.getSpecificDataMap().get("floor_rate_lt60") + ", " +
                this.getSpecificDataMap().get("floor_rate_mt60"));
        System.out.println("- 건물 평수별(0~150평, 151~500평, 501평~): " +
                this.getSpecificDataMap().get("pyeong_rate_lt150") + ", " +
                this.getSpecificDataMap().get("pyeong_rate_lt500") + ", " +
                this.getSpecificDataMap().get("pyeong_rate_mt500"));
        System.out.println("- 건물 가액별(0~5억원, 5억원~10억원, 10억원~): " +
                this.getSpecificDataMap().get("value_rate_lt500m") + ", " +
                this.getSpecificDataMap().get("value_rate_lt1000m") + ", " +
                this.getSpecificDataMap().get("value_rate_mt1000m"));
        System.out.println("* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - *");
        System.out.println("+ 화재손해 최대 보장금액: " +
                printComma(Integer.parseInt((String) this.getSpecificDataMap().get("guaranteed_fire_damage"))));
        System.out.println("- 붕괴손해 최대 보장금액: " +
                printComma(Integer.parseInt((String) this.getSpecificDataMap().get("guaranteed_collapse_damage"))));
        System.out.println("- 화재배상책임 최대 보장금액: " +
                printComma(Integer.parseInt((String) this.getSpecificDataMap().get("guaranteed_fire_compensation"))));
        System.out.println("*---------------------------------------------------------------------------*");
    }
}
