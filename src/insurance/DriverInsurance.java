package insurance;

import java.util.Map;

import static global.Constants.InsuranceQueryOption.DRIVER;
import static global.Util.*;

public class DriverInsurance extends Insurance {

    public DriverInsurance() {
        this.setInsuranceType(DRIVER);
    }

    @Override
    public double computeRate(Map<String, Object> dataMap) {
        double totalRate = 0;
        String clientGender = (String) dataMap.get("client_gender");
        String clientAge = (String) dataMap.get("client_age");
        String clientOccupation = (String) dataMap.get("client_occupation");

        totalRate += (double) this.getBasicDataMap().get("insurance_rate");

        if (clientGender.equals("man")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("gender_rate_man"));
        } else if (clientGender.equals("woman")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("gender_rate_woman"));
        }
        if (clientAge.equals("twenty")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("age_rate_twenty"));
        } else if (clientAge.equals("thirty")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("age_rate_thirty"));
        } else if (clientAge.equals("forty")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("age_rate_forty"));
        } else if (clientAge.equals("fifty")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("age_rate_fifty"));
        } else if (clientAge.equals("etc")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("age_rate_etc"));
        }
        if (clientOccupation.equals("risk")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("occupation_rate_risk"));
        } else if (clientOccupation.equals("non_risk")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("occupation_rate_non_risk"));
        }
        this.setInsuranceRate(totalRate);
        return totalRate;
    }

    @Override
    public void showInfo() {
        System.out.println("\n*-------------------------* [운전자 보험상품 정보] *-------------------------*");
        System.out.println("+ 승인 여부: " + printStatus((Boolean) this.getBasicDataMap().get("insurance_status")));
        System.out.println("- 보험상품 종류: " + printInsurance(this.getBasicDataMap().get("insurance_type")));
        System.out.println("- 보험상품 ID: " + this.getBasicDataMap().get("insurance_id"));
        System.out.println("- 보험상품 이름: " + this.getBasicDataMap().get("insurance_name"));
        System.out.println("- 보험상품 설명: " + this.getBasicDataMap().get("insurance_description"));
        System.out.println("- 기본 보험 요율: " + this.getBasicDataMap().get("insurance_rate"));
        System.out.println("- 기본 월 보험료: " + printComma((Integer) this.getBasicDataMap().get("insurance_premium")));
        System.out.println("* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - *");
        System.out.println("+ 운전자 성별 요율(남성, 여성): " +
                this.getSpecificDataMap().get("gender_rate_man") + ", " +
                this.getSpecificDataMap().get("gender_rate_woman"));
        System.out.println("- 운전자 연령별 요율(20대, 30대, 40대, 50대, 기타): " +
                this.getSpecificDataMap().get("age_rate_twenty") + ", " +
                this.getSpecificDataMap().get("age_rate_thirty") + ", " +
                this.getSpecificDataMap().get("age_rate_forty") + ", " +
                this.getSpecificDataMap().get("age_rate_fifty") + ", " +
                this.getSpecificDataMap().get("age_rate_etc"));
        System.out.println("- 운전자 직업별 요율(위험군, 비위험군): " +
                this.getSpecificDataMap().get("occupation_rate_risk") + ", " +
                this.getSpecificDataMap().get("occupation_rate_non_risk"));
        System.out.println("* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - *");
        System.out.println("+ 교통사고 최대 보장금액: " +
                printComma(Integer.parseInt((String) this.getSpecificDataMap().get("guaranteed_traffic_accident"))));
        System.out.println("- 교통상해후유장해 최대 보장금액: " +
                printComma(Integer.parseInt((String) this.getSpecificDataMap().get("guaranteed_traffic_accident_injury"))));
        System.out.println("- 교통상해사망 최대 보장금액: " +
                printComma(Integer.parseInt((String) this.getSpecificDataMap().get("guaranteed_traffic_accident_death"))));
        System.out.println("*---------------------------------------------------------------------------*");
    }
}
