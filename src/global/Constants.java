package global;

/**
 * 비즈니스 로직, DAO 에서 공통으로 사용하는 데이터
 */
public final class Constants {

    // Menu Number
    public static final String CANCELLATION = "0";
    public static final String INSURANCE = "1";
    public static final String CLIENT = "2";
    public static final String PAYMENT = "1";
    public static final String CLAIM = "2";
    // Insurance Type Number
    public static final String AUTOMOBILE_NUM = "1";
    public static final String FIRE_NUM = "2";
    public static final String DRIVER_NUM = "3";
    public static final String ACCIDENT_NUM = "4";
    // Property Type Number
    public static final String PROPERTY_NUM = "3";
    // Result Query Value
    // 쿼리 결과로 CRUD 된 행의 수가 0 일 경우, 비정상 작업 결과를 의미한다. 따라서 해당 상수와 비교하여 값이 같을 경우 결과가 반영되지 못하였음을 뜻한다.
    public static final int CHANGED_ROW_COUNT = 0;

    public enum CheckNum {
        MANAGE_INSURANCE("manage_insurance"), MANAGE_CONTRACT("manage_contract"), CREATE_DOCUMENT("create_document"), MANAGE_CLAIM("manage_claim"),
        PAY_PREMIUM("pay_premium"), CLAIM_CLAIMS("claim_claims");

        private String type;
        private CheckNum(String type) { this.type = type; }
        public String getOption() { return type; }
    }

    public enum ClientQueryOption {
        CHECK_CLIENT_ID("check_client_id"), CHECK_CLIENT_RRN("check_client_rrn"), CHECK_LOGIN_VALIDATION("check_login_validation"),
        INQUIRE_CLIENT("inquire_client"),
        MAN("man"), WOMAN("woman"),
        TWENTY("twenty"), THIRTY("thirty"), FORTY("forty"), FIFTY("fifty"), ETC("etc"),
        RISK("risk"), NON_RISK("non_risk");

        private String type;
        private ClientQueryOption(String type) { this.type = type; }
        public String getOption() { return type; }
    }

    public enum InsuranceQueryOption {
        CHECK_INSURANCE_ID("check_insurance_id"),
        UNAPPROVED_INSURANCE("unapproved_insurance"), APPROVED_INSURANCE("approved_insurance"), ALL_INSURANCE("all_insurance"),
        APPROVE_INSURANCE("approve_insurance"), REJECT_INSURANCE("reject_insurance"),
        AUTOMOBILE("automobile"), FIRE("fire"), DRIVER("driver"), ACCIDENT("accident"), BUILDING("building");

        private String type;
        private InsuranceQueryOption(String type) { this.type = type; }
        public String getOption() { return type; }
    }

    public enum ContractQueryOption {
        UNAPPROVED_CONTRACT("unapproved_contract"), APPROVED_CONTRACT("approved_contract"), ALL_CONTRACT("all_contract"),
        APPROVE_CONTRACT("approve_contract"), REJECT_CONTRACT("reject_contract");

        private String type;
        private ContractQueryOption(String type) { this.type = type; }
        public String getOption() { return type; }
    }

    public enum PaymentQueryOption {
        UNPAID("unpaid"), COMPLETED_PAYMENT("completed_payment");

        private String type;
        private PaymentQueryOption(String type) { this.type = type; }
        public String getOption() { return type; }
    }

    public enum ClaimQueryOption {
        UNCLAIMED("unclaimed"), REQUESTED_CLAIM("requested_claim"), UNDER_EXAMINATION("under_examination"), APPROVED_CLAIM("approved_claim"), REJECTED_CLAIM("rejected_claim"), ALL_CLAIM("all_claim");
//        APPROVE_CLAIM("approve_claim"), REJECT_CLAIM("reject_claim");

        private String type;
        private ClaimQueryOption(String type) { this.type = type; }
        public String getOption() { return type; }
    }

    public enum ShowInfoOption {
        SHOW_INFO("showInfo"), SHOW_SPECIFIC_INFO("showSpecificInfo");

        private String type;
        private ShowInfoOption(String type) { this.type = type; }
        public String getOption() { return type; }
    }
}