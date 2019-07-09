package insurance;

import java.util.Map;

import static global.Constants.InsuranceQueryOption.AUTOMOBILE;
import static global.Util.*;

public class AutomobileInsurance extends Insurance {

    public AutomobileInsurance() {
        this.setInsuranceType(AUTOMOBILE);
    }

    @Override
    public double computeRate(Map<String, Object> dataMap) {
        double totalRate = 0;
        String clientGender = (String) dataMap.get("client_gender");
        String clientAge = (String) dataMap.get("client_age");
        String accidentGrade = (String) dataMap.get("accident_grade");
        String automobileType = (String) dataMap.get("automobile_type");
        int automobileValue = Integer.parseInt(String.valueOf(dataMap.get("automobile_value")));
        String driverScope = (String) dataMap.get("driver_scope");

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
        if (accidentGrade.equals("a")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("accident_rate_a"));
        } else if (accidentGrade.equals("b")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("accident_rate_b"));
        } else if (accidentGrade.equals("c")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("accident_rate_c"));
        }
        if (automobileType.equals("compact")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("type_rate_compact"));
        } else if (automobileType.equals("mid_size")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("type_rate_mid_size"));
        } else if (automobileType.equals("full_size")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("type_rate_full_size"));
        }
        if (0 <= automobileValue && automobileValue <= 30000000) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("value_rate_lt30m"));
        } else if (30000000 < automobileValue && automobileValue <= 60000000) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("value_rate_lt60m"));
        } else if (60000000 < automobileValue) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("value_rate_mt60m"));
        }
        if (driverScope.equals("one_person")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("scope_rate_one_person"));
        } else if (driverScope.equals("two_person")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("scope_rate_two_person"));
        } else if (driverScope.equals("three_person")) {
            totalRate += Double.parseDouble((String) this.getSpecificDataMap().get("scope_rate_three_person"));
        }
        this.setInsuranceRate(totalRate);
        return totalRate;
    }

    @Override
    public void showInfo() {
        System.out.println("\n*------------------------* [자동차 보험상품 정보] *--------------------------*");
        System.out.println("+ 승인 여부: " + printStatus((Boolean) this.getBasicDataMap().get("insurance_status")));
        System.out.println("- 보험상품 종류: " + printInsurance(this.getBasicDataMap().get("insurance_type")));
        System.out.println("- 보험상품 ID: " + this.getBasicDataMap().get("insurance_id"));
        System.out.println("- 보험상품 이름: " + this.getBasicDataMap().get("insurance_name"));
        System.out.println("- 보험상품 설명: " + this.getBasicDataMap().get("insurance_description"));
        System.out.println("- 기본 보험 요율: " + this.getBasicDataMap().get("insurance_rate"));
        System.out.println("- 기본 월 보험료: " + printComma((Integer) this.getBasicDataMap().get("insurance_premium")));
        System.out.println("* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - *");
        System.out.println("+ 차량 사고등급별 요율(A등급, B등급, C등급): " +
                this.getSpecificDataMap().get("accident_rate_a") + ", " +
                this.getSpecificDataMap().get("accident_rate_b") + ", " +
                this.getSpecificDataMap().get("accident_rate_c"));
        System.out.println("- 차량 종류별 요율(소형, 중형, 대형): " +
                this.getSpecificDataMap().get("type_rate_compact") + ", " +
                this.getSpecificDataMap().get("type_rate_mid_size") + ", " +
                this.getSpecificDataMap().get("type_rate_full_size"));
        System.out.println("- 차량 가액별 요율(0~3000만원, 3001~6000만원, 6001만원~): " +
                this.getSpecificDataMap().get("value_rate_lt30m") + ", " +
                this.getSpecificDataMap().get("value_rate_lt60m") + ", " +
                this.getSpecificDataMap().get("value_rate_mt60m"));
        System.out.println("- 운전자 성별 요율(남성, 여성): " +
                this.getSpecificDataMap().get("gender_rate_man") + ", " +
                this.getSpecificDataMap().get("gender_rate_woman"));
        System.out.println("- 운전자 연령별 요율(20대, 30대, 40대, 50대, 기타): " +
                this.getSpecificDataMap().get("age_rate_twenty") + ", " +
                this.getSpecificDataMap().get("age_rate_thirty") + ", " +
                this.getSpecificDataMap().get("age_rate_forty") + ", " +
                this.getSpecificDataMap().get("age_rate_fifty") + ", " +
                this.getSpecificDataMap().get("age_rate_etc"));
        System.out.println("- 운전자 범위별 요율(1인, 2인, 3인): " +
                this.getSpecificDataMap().get("scope_rate_one_person") + ", " +
                this.getSpecificDataMap().get("scope_rate_two_person") + ", " +
                this.getSpecificDataMap().get("scope_rate_three_person"));
        System.out.println("* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - *");
        System.out.println("+ 대인배상 최대 보장금액: " +
                printComma(Integer.parseInt((String) this.getSpecificDataMap().get("guaranteed_other_person"))));
        System.out.println("- 대물배상 최대 보장금액: " +
                printComma(Integer.parseInt((String) this.getSpecificDataMap().get("guaranteed_other_property"))));
        System.out.println("- 신체사고 최대 보장금액: " +
                printComma(Integer.parseInt((String) this.getSpecificDataMap().get("guaranteed_person"))));
        System.out.println("- 차량손해 최대 보장금액: " +
                printComma(Integer.parseInt((String) this.getSpecificDataMap().get("guaranteed_property"))));
        System.out.println("*---------------------------------------------------------------------------*");
    }
}