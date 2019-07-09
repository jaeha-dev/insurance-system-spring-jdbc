package main;

import client.Automobile;
import client.Building;
import client.Client;
import client.Property;
import contract.Claim;
import contract.Contract;
import contract.Payment;
import dao.*;
import insurance.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.util.*;
import static global.Constants.*;
import static global.Constants.CheckNum.*;
import static global.Constants.ClaimQueryOption.*;
import static global.Constants.ClientQueryOption.*;
import static global.Constants.ContractQueryOption.*;
import static global.Constants.InsuranceQueryOption.*;
import static global.Constants.PaymentQueryOption.*;
import static global.Constants.ShowInfoOption.*;
import static global.Util.*;

/**
 * BMProject1101 디렉토리: 비즈니스 모델링1 소스 코드
 *
 * 고객: 고객 등록, 고객 로그인, 로그인 검증
 * 자산 관리: 차량 정보 등록, 건물 정보 등록, 차량 정보 조회, 건물 정보 조회
 * 계약 관리: 보험상품 계약, 보험료 납부, 보험금 청구
 * 보험 상품: 자동차 보험상품 설계, 화재 보험상품 설계, 운전자 보험상품 설계, 상해 보험상품 설계
 * 보험 상품 관리: 보험상품 승인, 보험상품 기각
 * 계약 관리: 계약 승인, 계약 기각, 보험금 지급
 * 추가 기능: 고객 ID/RRN 중복 검사, 보험상품 ID 중복 검사, 자산 중복 검사, (예상 보험금 산출), (손해율 분석)
 */

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static List<Insurance> insuranceList;
    private static List<Client> clientList;
    private static List<Property> propertyList;
    private static List<Contract> contractList;
    private static List<Payment> paymentList;
    private static List<Claim> claimList;

    public static ClientDao clientDao;
    public static InsuranceDao insuranceDao;
    public static PropertyDao propertyDao;
    public static ContractDao contractDao;
    public static PaymentDao paymentDao;
    public static ClaimDao claimDao;

    public static void main(String[] args) {
        insuranceList = new ArrayList<Insurance>(); // List <- DB (DB에 저장된 모든 보험상품 데이터를 가져와 저장한다.)
        clientList = new ArrayList<Client>(); // List <- DB (DB에 저장된 모든 고객 데이터를 가져와 저장한다.)
        propertyList = new ArrayList<Property>(); // List <- DB (DB에 저장된 모든 자산(차량, 건물) 데이터를 가져와 저장한다.)
        contractList = new ArrayList<Contract>(); // List <- DB (DB에 저장된 모든 계약 데이터를 가져와 저장한다.)
        paymentList = new ArrayList<Payment>(); // List <- DB (DB에 저장된 모든 보험료 납부내역 데이터를 가져와 저장한다.)
        claimList = new ArrayList<Claim>(); // List <- DB (DB에 저장된 모든 보험금 청구내역 데이터를 가져와 저장한다.)
        boolean runFlag = true;

        @SuppressWarnings("resource")
        ApplicationContext context = new ClassPathXmlApplicationContext("Spring.xml");
        clientDao = context.getBean("clientDaoImpl", ClientDao.class);
        insuranceDao = context.getBean("insuranceDaoImpl", InsuranceDao.class);
        propertyDao = context.getBean("propertyDaoImpl", PropertyDao.class);
        contractDao = context.getBean("contractDaoImpl", ContractDao.class);
        paymentDao = context.getBean("paymentDaoImpl", PaymentDao.class);
        claimDao = context.getBean("claimDaoImpl", ClaimDao.class);

        while (runFlag == true) {
            printMainMenu();
            String loginNum = scanner.next();
            checkMainMenu(loginNum);
            if (loginNum.equals(CANCELLATION)) runFlag = false;
        }
    }

    /**
     * Main 메뉴 출력
     */
    public static void printMainMenu() {
        System.out.println("\n*------------------------------* [보험사 메뉴] *------------------------------*");
        System.out.println("1. 보험사 메뉴");
        System.out.println("2. 고객 메뉴");
        System.out.println("*-------------------------------* [종료 메뉴] *-------------------------------*");
        System.out.println("0. 프로그램 종료");
        System.out.print("   메뉴번호 입력: ");
        String menuNum = scanner.next();
        checkMainMenu(menuNum);
    }

    /**
     * Main 메뉴 선택 값에 따른 실행 분기
     * @param menuNum
     */
    public static void checkMainMenu(String menuNum) {
        if (menuNum.equals(CANCELLATION)) { System.exit(0);
        } else if (menuNum.equals(INSURANCE)) { printInsuranceMainMenu();
        } else if (menuNum.equals(CLIENT)) { printClientLoginMenu();
        } else {
            System.out.println("잘못된 코드(" + menuNum + ") 입력입니다. 다시 입력하세요.");
            return;
        }
    }

    /**
     * Client Login 메뉴 출력
     * Client 로그인만 구현, Insurance Manager Login(보험사 직원의 각 권한별(보험상품 설계자, 보험상품 승인자 등) 계정 로그인은 미구현
     */
    public static void printClientLoginMenu() {
        System.out.println("\n*------------------------------* [로그인 메뉴] *------------------------------*");
        System.out.println("1. 고객 등록");
        System.out.println("2. 고객 로그인");
        System.out.println("*-------------------------------* [종료 메뉴] *-------------------------------*");
        System.out.println("3. 보험사 메뉴");
        System.out.println("0. 프로그램 종료");
        System.out.print("   메뉴번호 입력: ");
        String menuNum = scanner.next();
        loginClient(menuNum);
    }

    /**
     * Client Login 메뉴 선택 값에 따른 실행 분기
     * @param menuNum
     */
    public static void loginClient(String menuNum) {
        switch (menuNum) {
            case "1":
                Client tempClient = Client.getInstance();
                Map<String, Object> dataMap = new LinkedHashMap<>(); // 데이터 저장 순서를 보장하기 위해 인덱스를 갖는 LinkedHashMap 을 사용한다.
                System.out.println("\n* 고객 등록(기본 정보 입력) *");
                System.out.println("* 다중 데이터 입력 시, 공백문자(Space Key)로 구분하여 입력하세요. *");
                System.out.println("+ ID(최대 10자)와 비밀번호(최대 20자)를 입력하세요. (Ex abc123 123)");
                String clientId = scanner.next();
                String clientPw = scanner.next();

                // ... ID 중복 확인
                tempClient.setClientId(clientId);
                tempClient.setClientPw(clientPw);
                if (Client.getInstance().validateClient(CHECK_CLIENT_ID.getOption(), tempClient) == true) {
                    System.out.println("* 이미 등록된 ID 입니다.");
                    printClientLoginMenu();
                    return;
                }
                System.out.println("+ 이름을 입력하세요. (Ex 명지대)");
                String clientName = scanner.next();
                System.out.println("+ 주민등록번호를 입력하세요. (Ex 000000-0000000)");
                String clientRRN = scanner.next();

                // ... 주민등록번호 중복 확인
                tempClient.setClientRRN(clientRRN);
                if (Client.getInstance().validateClient(CHECK_CLIENT_RRN.getOption(), tempClient) == true) {
                    System.out.println("* 이미 등록된 주민등록번호 입니다.");
                    printClientLoginMenu();
                    return;
                }
                System.out.println("+ 성별(남성, 여성)을 입력하세요. (Ex 남성)");
                String clientGender = scanner.next();
                System.out.println("+ 연령을 입력하세요. (Ex 25)");
                String clientAge = scanner.next();
                showOccupationGroup(); // 직업군 선택문 출력
                System.out.println("+ 직업군(위험군, 비위험군)을 입력하세요. (Ex 비위험군)");
                String clientOccupation = scanner.next();
                System.out.println("+ 전화번호를 입력하세요. (Ex 010-0000-0000)");
                String clientPhoneNum = scanner.next();

                if (clientGender.equals("남성")) { clientGender = "man"; }
                else if (clientGender.equals("여성")) { clientGender = "woman"; }
                if (clientOccupation.equals("위험군")) { clientOccupation = "risk"; }
                else if (clientOccupation.equals("비위험군")) { clientOccupation = "non_risk"; }

                String[] dataKeys = new String[]{"client_id", "client_pw", "client_name", "client_rrn", "client_gender", "client_age", "client_occupation", "client_phone"};
                String[] dataValues = new String[]{clientId, clientPw, clientName, clientRRN, clientGender, clientAge, clientOccupation, clientPhoneNum};
                dataMap = (LinkedHashMap<String, Object>) addEntry((LinkedHashMap<String, Object>) dataMap, dataKeys, dataValues);

                // 등록 검증
                if (Client.getInstance().registerClient(dataMap) != CHANGED_ROW_COUNT) {
                    System.out.println("\n* 고객 등록이 완료되었습니다. *");
                } else {
                    System.err.println("\n* 고객 등록에 실패하였습니다. *");
                }
                printClientLoginMenu();
                break;
            case "2":
                System.out.println("\n* 고객 로그인 *");
                System.out.println("* 다중 데이터 입력 시, 공백문자(Space Key)로 구분하여 입력하세요. *");
                System.out.println("+ ID와 비밀번호를 입력하세요. (Ex client-001 12345)");
                String id = scanner.next();
                String pw = scanner.next();
                Client client = Client.getInstance();
                client.setClientId(id);
                client.setClientPw(pw);
                // 로그인 검증
                if (Client.getInstance().validateClient(CHECK_LOGIN_VALIDATION.getOption(), client) == true) {
                    System.out.println("\n* (" + client.getClientId() + ") 계정 로그인에 성공하였습니다. *");
                    printClientMainMenu(client);
                } else {
                    System.err.println("\n* 로그인에 실패하였습니다. *");
                    printClientLoginMenu();
                    return;
                }
                break;
            case "3":
                printMainMenu();
                break;
            case "0":
                System.exit(0);
                break;
            default:
                break;
        }
    }

    /**
     * Insurance Main Menu 출력
     */
    public static void printInsuranceMainMenu() {
        System.out.println("\n*------------------------------* [보험 메뉴] *-------------------------------*");
        System.out.println("+. 환영합니다. (" + printTimeStamp() + ")");
        System.out.println("*----------------------------------------------------------------------------*");
        System.out.println("1. 보험상품 설계"); // 보험상품 설계자(보험상품 설계)
        System.out.println("2. 보험상품 관리"); // 보험상품 승인자(보험상품 승인 및 기각)
        System.out.println("3. 계약 관리"); // UW(고객 및 계약 인수심사)
        System.out.println("4. 보상 관리"); // 보험처리사(보험금 지급)
        System.out.println("*----------------------------------------------------------------------------*");
        System.out.println("5. 자산 조회"); // 회계
        System.out.println("6. 보험료 조회"); // 회계
        System.out.println("7. 고객 조회"); // 고객 관리 -> 고객 정보 변경/삭제 미구현
        System.out.println("*-------------------------------* [종료 메뉴] *-------------------------------*");
        System.out.println("8. 보험사 메뉴");
        System.out.println("0. 프로그램 종료");
        System.out.print("   메뉴번호 입력: ");
        String insuranceMainMenuNum = scanner.next();
        printInsuranceSubMenu(insuranceMainMenuNum);
    }

    /**
     * Client Main Menu 출력
     * @param client
     */
    public static void printClientMainMenu(Client client) {
        System.out.println("\n*-------------------------------* [고객 메뉴] *-------------------------------*");
        System.out.println("+. (" + client.getClientId() + ")님, 환영합니다. (" + printTimeStamp() + ")");
        System.out.println("*-----------------------------------------------------------------------------*");
        System.out.println("1. 보험상품 계약");
        System.out.println("2. 계약 조회");
        System.out.println("3. 자산 조회");
        System.out.println("4. 보험료 납부");
        System.out.println("5. 보험금 청구");
        System.out.println("6. 계정 조회");
        System.out.println("*-------------------------------* [종료 메뉴] *-------------------------------*");
        System.out.println("7. 로그아웃 및 보험사 메뉴");
        System.out.println("0. 프로그램 종료");
        System.out.print("   메뉴번호 입력: ");
        String clientMainMenuNum = scanner.next();
        printClientSubMenu(clientMainMenuNum, client);
    }

    /**
     * Insurance Sub Menu 출력
     * @param mainMenuNum
     */
    public static void printInsuranceSubMenu(String mainMenuNum) {
        switch (mainMenuNum) {
            case "1":
                System.out.println("\n*------------------------------* [보험상품 설계] *-----------------------------*");
                System.out.println("1. 보험상품 설계(자동차 보험)");
                System.out.println("2. 보험상품 설계(화재 보험)");
                System.out.println("3. 보험상품 설계(운전자 보험)");
                System.out.println("4. 보험상품 설계(상해 보험)");
                System.out.println("0. 취소");
                System.out.print("   메뉴번호 입력: ");
                String designInsuranceMenuNum = scanner.next();
                if (designInsuranceMenuNum.equals(CANCELLATION)) { printInsuranceMainMenu(); return; }
                designInsurance(designInsuranceMenuNum);
                break;
            case "2":
                System.out.println("\n*------------------------------* [보험상품 관리] *-----------------------------*");
                System.out.println("1. 보험상품 관리(자동차)");
                System.out.println("2. 보험상품 관리(화재)");
                System.out.println("3. 보험상품 관리(운전자)");
                System.out.println("4. 보험상품 관리(상해)");
                System.out.println("0. 취소");
                System.out.print("   메뉴번호 입력: ");
                String manageInsuranceMenuNum = scanner.next();
                if (manageInsuranceMenuNum.equals(CANCELLATION)) { printInsuranceMainMenu(); return; }
                manageInsurance(manageInsuranceMenuNum);
                break;
            case "3":
                System.out.println("\n*-------------------------------* [계약 관리] *-------------------------------*");
                System.out.println("1. 계약 관리(미승인 계약)");
                System.out.println("2. 계약 관리(승인 계약)");
                System.out.println("3. 보험료/보험금 문서 생성(승인 계약)");
                System.out.println("0. 취소");
                System.out.print("   메뉴번호 입력: ");
                String manageContractMenuNum = scanner.next();
                if (manageContractMenuNum.equals(CANCELLATION)) { printInsuranceMainMenu(); return; }
                manageContract(manageContractMenuNum);
                break;
            case "4":
                System.out.println("\n*-------------------------------* [보상 관리] *-------------------------------*");
                System.out.println("1. 보험금 청구 심사(대기/보류)");
                System.out.println("2. 보험금 청구 조회");
                System.out.println("0. 취소");
                System.out.print("   메뉴번호 입력: ");
                String manageClaimMenuNum = scanner.next();
                if (manageClaimMenuNum.equals(CANCELLATION)) { printInsuranceMainMenu(); return; }
                manageClaim(manageClaimMenuNum);
                break;
            case "5":
                System.out.println("\n*-------------------------------* [자산 조회] *-------------------------------*");
                System.out.println("1. 등록 차량 조회");
                System.out.println("2. 등록 건물 조회");
                System.out.println("3. 등록 자산 조회(차량/건물)");
                System.out.println("0. 취소");
                System.out.print("   메뉴번호 입력: ");
                String managePropertyMenuNum = scanner.next();
                if (managePropertyMenuNum.equals(CANCELLATION)) { printInsuranceMainMenu(); return; }
                manageProperty(managePropertyMenuNum);
                break;
            case "6":
                System.out.println("\n*------------------------------* [보험료 조회] *------------------------------*");
                System.out.println("1. 보험료 조회(미납)");
                System.out.println("2. 보험료 조회(납부완료)");
                System.out.println("0. 취소");
                System.out.print("   메뉴번호 입력: ");
                String managePremiumMenuNum = scanner.next();
                if (managePremiumMenuNum.equals(CANCELLATION)) { printInsuranceMainMenu(); return; }
                managePremium(managePremiumMenuNum);
                break;
            case "7":
                System.out.println("\n*-------------------------------* [고객 조회] *-------------------------------*");
                showClientList();
                printInsuranceMainMenu();
                break;
            case "8":
                printMainMenu();
                break;
            case "0":
                System.exit(0);
                break;
            default:
                System.out.println("잘못된 코드(" + mainMenuNum + ") 입력입니다. 다시 입력하세요.");
                break;
        }
    }

    /**
     * Client Sub Menu 출력
     * @param mainMenuNum, loginData
     */
    public static void printClientSubMenu(String mainMenuNum, Client client) {
        switch (mainMenuNum) {
            case "1":
                System.out.println("\n*------------------------------* [보험상품 계약] *-----------------------------*");
                System.out.println("1. 보험상품 계약(자동차 보험)");
                System.out.println("2. 보험상품 계약(화재 보험)");
                System.out.println("3. 보험상품 계약(운전자 보험)");
                System.out.println("4. 보험상품 계약(상해 보험)");
                System.out.println("0. 취소");
                System.out.print("   메뉴번호 입력: ");
                String contractInsuranceMenuNum = scanner.next();
                if (contractInsuranceMenuNum.equals(CANCELLATION)) { printClientMainMenu(client); return; }
                contractInsurance(contractInsuranceMenuNum, client);
                break;
            case "2":
                System.out.println("\n*-------------------------------* [계약 조회] *-------------------------------*");
                System.out.println("1. 계약 조회");
                System.out.println("0. 취소");
                System.out.print("   메뉴번호 입력: ");
                String inquireContractMenuNum = scanner.next();
                if (inquireContractMenuNum.equals(CANCELLATION)) { printClientMainMenu(client); return; }
                inquireContractList(ALL_CONTRACT.getOption(), client); // 승인여부 구분 없이 모든 계약 조회
                printClientMainMenu(client);
                break;
            case "3":
                System.out.println("\n*-------------------------------* [자산 조회] *-------------------------------*");
                System.out.println("1. 등록 차량 조회");
                System.out.println("2. 등록 건물 조회");
                System.out.println("0. 취소");
                System.out.print("   메뉴번호 입력: ");
                String inquirePropertyMenuNum = scanner.next();
                if (inquirePropertyMenuNum.equals(CANCELLATION)) { printClientMainMenu(client); return; }
                inquirePropertyList(inquirePropertyMenuNum, client);
                printClientMainMenu(client);
                break;
            case "4":
                System.out.println("\n*------------------------------* [보험료 납부] *------------------------------*");
                System.out.println("1. 보험료 납부");
                System.out.println("2. 보험료 납부내역 조회");
                System.out.println("0. 취소");
                System.out.print("   메뉴번호 입력: ");
                String payPremiumMenuNum = scanner.next();
                if (payPremiumMenuNum.equals(CANCELLATION)) { printClientMainMenu(client); return; }
                managePremium(payPremiumMenuNum, client);
                break;
            case "5":
                System.out.println("\n*------------------------------* [보험금 청구] *------------------------------*");
                System.out.println("1. 보험금 청구");
                System.out.println("2. 보험금 청구내역 조회");
                System.out.println("0. 취소");
                System.out.print("   메뉴번호 입력: ");
                String claimClaimsMenuNum = scanner.next();
                if (claimClaimsMenuNum.equals(CANCELLATION)) { printClientMainMenu(client); return; }
                manageClaim(claimClaimsMenuNum, client);
                break;
            case "6":
                System.out.println("\n*-------------------------------* [계정 조회] *-------------------------------*");
                inquireClient(client).showInfo();
                printClientMainMenu(client);
                break;
            case "7":
                printMainMenu();
                break;
            case "0":
                System.exit(0);
                break;
            default:
                System.out.println("잘못된 코드(" + mainMenuNum + ") 입력입니다. 다시 입력하세요.");
                break;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////// [보험상품 설계]

    /**
     * 보험상품 설계 조건 구분 및 설계 메소드 호출(구현O)
     * @param designInsuranceMenuNum
     */
    public static void designInsurance(String designInsuranceMenuNum) {
        switch (designInsuranceMenuNum) {
            case "1": designAutomobileInsurance(designBasicInsurance(AUTOMOBILE.getOption())); break;
            case "2": designFireInsurance(designBasicInsurance(FIRE.getOption())); break;
            case "3": designDriverInsurance(designBasicInsurance(DRIVER.getOption())); break;
            case "4": designAccidentInsurance(designBasicInsurance(ACCIDENT.getOption())); break;
            default: break;
        }
    }

    /**
     * 기본 보험상품 설계(구현O)
     * @param insuranceType
     */
    public static Map<String, Object> designBasicInsurance(String insuranceType) {
        Map<String, Object> basicDataMap = new LinkedHashMap<>();
        System.out.println("\n* 보험상품 설계(기본 정보 입력) *");
        System.out.println("* 다중 데이터 입력 시, 공백문자(Space Key)로 구분하여 입력하세요. *");
        System.out.println("+ 보험상품 ID(최대 10자)와 이름(최대 20자)을 입력하세요. (Ex auto-001 자동차보험I)");
        String insuranceId = scanner.next();
        String insuranceName = scanner.next();
        // ... 중복 확인
        Insurance insurance = Insurance.getInstance(AutomobileInsurance.class); // 중복 검사를 위해 임시 자동차 보험상품 객체 생성 (추상 클래스는 객체 생성 불가하므로)
        insurance.setInsuranceId(insuranceId);
        if (insuranceDao.selectInsurance(CHECK_INSURANCE_ID.getOption(), insurance) != null) {
            System.out.println("* 이미 등록된 보험상품 ID 입니다.");
            printInsuranceMainMenu();
            return null;
        }
        System.out.println("+ 보험상품 설명을 입력하세요. (최대 300자) (Ex 자동차보험I입니다.)");
        String insuranceDescription = scanner.next();
        System.out.println("+ 기본 요율을 입력하세요. (Ex 1.0)");
        String insuranceRate = scanner.next();
        System.out.println("+ 월 기본 보험료를 입력하세요. (Ex 10000)");
        String insurancePremium = scanner.next();
        String[] keyArr = new String[]{"insurance_id", "insurance_type", "insurance_name", "insurance_description", "insurance_rate", "insurance_premium"};
        String[] valueArr = new String[]{insuranceId, insuranceType, insuranceName, insuranceDescription, insuranceRate, insurancePremium};
        basicDataMap = (LinkedHashMap<String, Object>) addEntry((LinkedHashMap<String, Object>) basicDataMap, keyArr, valueArr);
        return basicDataMap;
    }

    /**
     * 자동차 보험상품 설계(구현O)
     * @param basicDataMap
     */
    public static void designAutomobileInsurance(Map<String, Object> basicDataMap) {
        Map<String, Object> automobileInsuranceDataMap = new LinkedHashMap<>();
        Insurance insurance = Insurance.getInstance(AutomobileInsurance.class);
        System.out.println("* 자동차 보험상품 설계(요율 정보 입력) *");
        System.out.println("+ 차량 사고등급별(A등급, B등급, C등급) 요율을 입력하세요. (Ex 1.0 1.1 1.2)");
        String accident_rate_a = scanner.next();
        String accident_rate_b = scanner.next();
        String accident_rate_c = scanner.next();
        System.out.println("+ 차량 종류별(소형, 중형, 대형) 요율을 입력하세요. (Ex 1.0 1.1 1.2)");
        String type_rate_compact = scanner.next();
        String type_rate_mid_size = scanner.next();
        String type_rate_full_size = scanner.next();
        System.out.println("+ 차량 가액별(0~3000만원, 3000~6000만원, 6000만원~) 요율을 입력하세요. (Ex 1.0 1.1 1.2)");
        String value_rate_lt30m = scanner.next();
        String value_rate_lt60m = scanner.next();
        String value_rate_mt60m = scanner.next();
        System.out.println("+ 운전자 성별(남성, 여성) 요율을 입력하세요. (Ex 1.0 1.0)");
        String gender_rate_man = scanner.next();
        String gender_rate_woman = scanner.next();
        System.out.println("+ 운전자 연령대별(20대, 30대, 40대, 50대, 기타) 요율을 입력하세요. (Ex 1.0 1.1 1.2 1.3 1.4)");
        String age_rate_twenty = scanner.next();
        String age_rate_thirty = scanner.next();
        String age_rate_forty = scanner.next();
        String age_rate_fifty = scanner.next();
        String age_rate_etc = scanner.next();
        System.out.println("+ 운전자 범위별(1인, 2인, 3인) 요율을 입력하세요. (Ex 1.0 1.1 1.2)");
        String scope_rate_one_person = scanner.next();
        String scope_rate_two_person = scanner.next();
        String scope_rate_three_person = scanner.next();
        System.out.println("* 자동차 보험상품 설계(보상 정보 입력) *");
        System.out.println("+ 대인배상 최대 보장금액(콤마 생략)을 입력하세요. (Ex 100,000,000)");
        String guaranteed_other_person = scanner.next();
        System.out.println("+ 대물배상 최대 보장금액(콤마 생략)을 입력하세요. (Ex 100,000,000)");
        String guaranteed_other_property = scanner.next();
        System.out.println("+ 신체사고 최대 보장금액(콤마 생략)을 입력하세요. (Ex 100,000,000)");
        String guaranteed_person = scanner.next();
        System.out.println("+ 차량손해 최대 보장금액(콤마 생략)을 입력하세요. (Ex 100,000,000)");
        String guaranteed_property = scanner.next();
        String[] automobileKeyArr = new String[]{
                "accident_rate_a", "accident_rate_b", "accident_rate_c",
                "type_rate_compact", "type_rate_mid_size", "type_rate_full_size",
                "value_rate_lt30m", "value_rate_lt60m", "value_rate_mt60m",
                "gender_rate_man", "gender_rate_woman",
                "age_rate_twenty", "age_rate_thirty", "age_rate_forty", "age_rate_fifty", "age_rate_etc",
                "scope_rate_one_person", "scope_rate_two_person", "scope_rate_three_person",
                "guaranteed_other_person", "guaranteed_other_property", "guaranteed_person", "guaranteed_property"
        };
        String[] automobileValueArr = new String[]{
                accident_rate_a, accident_rate_b, accident_rate_c,
                type_rate_compact, type_rate_mid_size, type_rate_full_size,
                value_rate_lt30m, value_rate_lt60m, value_rate_mt60m,
                gender_rate_man, gender_rate_woman,
                age_rate_twenty, age_rate_thirty, age_rate_forty, age_rate_fifty, age_rate_etc,
                scope_rate_one_person, scope_rate_two_person, scope_rate_three_person,
                guaranteed_other_person, guaranteed_other_property, guaranteed_person, guaranteed_property
        };
        automobileInsuranceDataMap = (LinkedHashMap<String, Object>) addEntry((LinkedHashMap<String, Object>) automobileInsuranceDataMap, automobileKeyArr, automobileValueArr);

        addInsurance(insurance, basicDataMap, automobileInsuranceDataMap, "자동차");
        printInsuranceMainMenu();
    }

    /**
     * 화재 보험상품 설계(구현O)
     * @param basicDataMap
     */
    public static void designFireInsurance(Map<String, Object> basicDataMap) {
        Map<String, Object> fireInsuranceDataMap = new LinkedHashMap<>();
        Insurance insurance = Insurance.getInstance(FireInsurance.class);
        System.out.println("* 화재 보험상품 설계(요율 정보 입력) *");
        System.out.println("+ 건물 화재등급별(A등급, B등급, C등급) 요율을 입력하세요. (Ex 1.0 1.1 1.2)");
        String fire_rate_a = scanner.next();
        String fire_rate_b = scanner.next();
        String fire_rate_c = scanner.next();
        System.out.println("+ 건물 업종별(가정, 상가, 사무, 공장) 요율을 입력하세요. (Ex 1.0 1.1 1.2 1.3)");
        String type_rate_house = scanner.next();
        String type_rate_store = scanner.next();
        String type_rate_office = scanner.next();
        String type_rate_factory = scanner.next();
        System.out.println("+ 건물 층수별(0~30층, 31~60층, 61층~) 요율을 입력하세요. (Ex 1.0 1.1 1.2)");
        String floor_rate_lt30 = scanner.next();
        String floor_rate_lt60 = scanner.next();
        String floor_rate_mt60 = scanner.next();
        System.out.println("+ 건물 평수별(0~150평, 151~500평, 501평~) 요율을 입력하세요. (Ex 1.0 1.1 1.2)");
        String pyeong_rate_lt150 = scanner.next();
        String pyeong_rate_lt500 = scanner.next();
        String pyeong_rate_mt500 = scanner.next();
        System.out.println("+ 건물 가액별(0~5억원, 5억원~10억원, 10억원~) 요율을 입력하세요. (Ex 1.0 1.1 1.2)");
        String value_rate_lt500m = scanner.next();
        String value_rate_lt1000m = scanner.next();
        String value_rate_mt1000m = scanner.next();
        System.out.println("* 화재 보험상품 설계(보상 정보 입력) *");
        System.out.println("+ 화재손해 최대 보장금액(콤마 생략)을 입력하세요. (Ex 100,000,000)");
        String guaranteed_fire_damage = scanner.next();
        System.out.println("+ 붕괴손해 최대 보장금액(콤마 생략)을 입력하세요. (Ex 100,000,000)");
        String guaranteed_collapse_damage = scanner.next();
        System.out.println("+ 화재배상책임 최대 보장금액(콤마 생략)을 입력하세요. (Ex 100,000,000)");
        String guaranteed_fire_compensation = scanner.next();
        String[] fireKeyArr = new String[]{
                "fire_rate_a", "fire_rate_b", "fire_rate_c",
                "type_rate_house", "type_rate_store", "type_rate_office", "type_rate_factory",
                "floor_rate_lt30", "floor_rate_lt60", "floor_rate_mt60",
                "pyeong_rate_lt150", "pyeong_rate_lt500", "pyeong_rate_mt500",
                "value_rate_lt500m", "value_rate_lt1000m", "value_rate_mt1000m",
                "guaranteed_fire_damage", "guaranteed_collapse_damage", "guaranteed_fire_compensation"
        };
        String[] fireValueArr = new String[]{
                fire_rate_a, fire_rate_b, fire_rate_c,
                type_rate_house, type_rate_store, type_rate_office, type_rate_factory,
                floor_rate_lt30, floor_rate_lt60, floor_rate_mt60,
                pyeong_rate_lt150, pyeong_rate_lt500, pyeong_rate_mt500,
                value_rate_lt500m, value_rate_lt1000m, value_rate_mt1000m,
                guaranteed_fire_damage, guaranteed_collapse_damage, guaranteed_fire_compensation
        };
        fireInsuranceDataMap = (LinkedHashMap<String, Object>) addEntry((LinkedHashMap<String, Object>) fireInsuranceDataMap, fireKeyArr, fireValueArr);

        addInsurance(insurance, basicDataMap, fireInsuranceDataMap, "화재");
        printInsuranceMainMenu();
    }

    /**
     * 운전자 보험상품 설계(구현O)
     * @param basicDataMap
     */
    public static void designDriverInsurance(Map<String, Object> basicDataMap) {
        Map<String, Object> driverInsuranceDataMap = new LinkedHashMap<>();
        Insurance insurance = Insurance.getInstance(DriverInsurance.class);
        System.out.println("* 운전자 보험상품 설계(요율 정보 입력) *");
        System.out.println("+ 운전자 성별(남성, 여성) 요율을 입력하세요. (Ex 1.0 1.0)");
        String gender_rate_man = scanner.next();
        String gender_rate_woman = scanner.next();
        System.out.println("+ 운전자 연령대별(20대, 30대, 40대, 50대, 기타) 요율을 입력하세요. (Ex 1.0 1.1 1.2 1.3 1.4)");
        String age_rate_twenty = scanner.next();
        String age_rate_thirty = scanner.next();
        String age_rate_forty = scanner.next();
        String age_rate_fifty = scanner.next();
        String age_rate_etc = scanner.next();
        showOccupationGroup(); // 직업군 선택문 출력
        System.out.println("+ 운전자 직업군(위험군, 비위험군) 요율을 입력하세요. (Ex 1.0 1.0)");
        String occupation_rate_risk = scanner.next();
        String occupation_rate_non_risk = scanner.next();
        System.out.println("* 운전자 보험상품 설계(보상 정보 입력) *");
        System.out.println("+ 교통사고 최대 보장금액(콤마 생략)을 입력하세요. (Ex 100,000,000)");
        String guaranteed_traffic_accident = scanner.next();
        System.out.println("+ 교통상해후유장해 최대 보장금액(콤마 생략)을 입력하세요. (Ex 100,000,000)");
        String guaranteed_traffic_accident_injury = scanner.next();
        System.out.println("+ 교통상해사망 최대 보장금액(콤마 생략)을 입력하세요. (Ex 100,000,000)");
        String guaranteed_traffic_accident_death = scanner.next();
        String[] driverKeyArr = new String[]{
                "gender_rate_man", "gender_rate_woman",
                "age_rate_twenty", "age_rate_thirty", "age_rate_forty", "age_rate_fifty", "age_rate_etc",
                "occupation_rate_risk", "occupation_rate_non_risk",
                "guaranteed_traffic_accident", "guaranteed_traffic_accident_injury", "guaranteed_traffic_accident_death"
        };
        String[] driverValueArr = new String[]{
                gender_rate_man, gender_rate_woman,
                age_rate_twenty, age_rate_thirty, age_rate_forty, age_rate_fifty, age_rate_etc,
                occupation_rate_risk, occupation_rate_non_risk,
                guaranteed_traffic_accident, guaranteed_traffic_accident_injury, guaranteed_traffic_accident_death
        };
        driverInsuranceDataMap = (LinkedHashMap<String, Object>) addEntry((LinkedHashMap<String, Object>) driverInsuranceDataMap, driverKeyArr, driverValueArr);

        addInsurance(insurance, basicDataMap, driverInsuranceDataMap, "운전자");
        printInsuranceMainMenu();
    }

    /**
     * 상해 보험상품 설계(구현O)
     * @param basicDataMap
     */
    public static void designAccidentInsurance(Map<String, Object> basicDataMap) {
        Map<String, Object> accidentInsuranceDataMap = new LinkedHashMap<>();
        Insurance insurance = Insurance.getInstance(AccidentInsurance.class);
        System.out.println("* 상해 보험상품 설계(요율 정보 입력) *");
        System.out.println("+ 피보험자 성별(남성, 여성) 요율을 입력하세요. (Ex 1.0 1.0)");
        String gender_rate_man = scanner.next();
        String gender_rate_woman = scanner.next();
        System.out.println("+ 피보험자 연령대별(20대, 30대, 40대, 50대, 기타) 요율을 입력하세요. (Ex 1.0 1.1 1.2 1.3 1.4)");
        String age_rate_twenty = scanner.next();
        String age_rate_thirty = scanner.next();
        String age_rate_forty = scanner.next();
        String age_rate_fifty = scanner.next();
        String age_rate_etc = scanner.next();
        showOccupationGroup(); // 직업군 선택문 출력
        System.out.println("+ 피보험자 직업군(위험군, 비위험군) 요율을 입력하세요. (Ex 1.0 1.0)");
        String occupation_rate_risk = scanner.next();
        String occupation_rate_non_risk = scanner.next();
        System.out.println("* 상해 보험상품 설계(보상 정보 입력) *");
        System.out.println("+ 일반상해후유장해 최대 보장금액(콤마 생략)을 입력하세요. (Ex 100,000,000)");
        String guaranteed_general_accident_injury = scanner.next();
        System.out.println("+ 일반상해사망 최대 보장금액(콤마 생략)을 입력하세요. (Ex 100,000,000)");
        String guaranteed_general_accident_death = scanner.next();
        System.out.println("+ 특수상해후유장해 최대 보장금액(콤마 생략)을 입력하세요. (Ex 100,000,000)");
        String guaranteed_specific_accident_injury = scanner.next();
        System.out.println("+ 특수상해사망 최대 보장금액(콤마 생략)을 입력하세요. (Ex 100,000,000)");
        String guaranteed_specific_accident_death = scanner.next();
        String[] accidentKeyArr = new String[]{
                "gender_rate_man", "gender_rate_woman",
                "age_rate_twenty", "age_rate_thirty", "age_rate_forty", "age_rate_fifty", "age_rate_etc",
                "occupation_rate_risk", "occupation_rate_non_risk",
                "guaranteed_general_accident_injury", "guaranteed_general_accident_death", "guaranteed_specific_accident_injury", "guaranteed_specific_accident_death"
        };
        String[] accidentValueArr = new String[]{
                gender_rate_man, gender_rate_woman,
                age_rate_twenty, age_rate_thirty, age_rate_forty, age_rate_fifty, age_rate_etc,
                occupation_rate_risk, occupation_rate_non_risk,
                guaranteed_general_accident_injury, guaranteed_general_accident_death, guaranteed_specific_accident_injury, guaranteed_specific_accident_death
        };
        accidentInsuranceDataMap = (LinkedHashMap<String, Object>) addEntry((LinkedHashMap<String, Object>) accidentInsuranceDataMap, accidentKeyArr, accidentValueArr);

        addInsurance(insurance, basicDataMap, accidentInsuranceDataMap, "상해");
        printInsuranceMainMenu();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////// [보험상품 계약]

    /**
     * 보험상품 계약(구현O)
     * @param contractInsuranceMenuNum, loginData
     */
    public static void contractInsurance(String contractInsuranceMenuNum, Client client) {
        if (showInsuranceList(contractInsuranceMenuNum, APPROVED_INSURANCE.getOption()) == false) {
            System.out.println("\n*-----------------------------* [보험상품 계약] *-----------------------------*");
            System.out.println("1. 계약 신청");
            System.out.println("0. 취소");
            System.out.print("   메뉴번호와 보험상품 ID를 공백문자(Space Key)로 구분하여 입력: ");
            String contractNum = scanner.next();
            if (contractNum.equals(CANCELLATION)) { printClientMainMenu(client); return; }
            String insuranceId = scanner.next();

            switch (contractInsuranceMenuNum) {
                case "1": contractAutomobileInsurance(insuranceId, client); break;
                case "2": contractFireInsurance(insuranceId, client); break;
                case "3": contractDriverInsurance(insuranceId, client); break;
                case "4": contractAccidentInsurance(insuranceId, client); break;
                default: break;
            }
        } else {
            printClientMainMenu(client);
        }
    }

    /**
     * 자동차 보험상품 계약(구현O)
     * @param insuranceId, loginData
     */
    public static void contractAutomobileInsurance(String insuranceId, Client client) {
        Insurance insurance = inquireInsurance(APPROVED_INSURANCE.getOption(), AutomobileInsurance.class, insuranceId); // 요율 및 보험료 산출에 필요하므로 고객이 신청한 보험상품 객체를 가져온다.
        Client tempClient = inquireClient(client); // 고객(로그인 데이터만 가진 객체를 통해서) 객체를 가져온다.
        // client 객체는 로그인 데이터만 가진 객체이고 tempClient 객체는 DB 에서 가져온 고객의 모든 데이터를 가진 객체이다.

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.putAll(tempClient.getClientDataMap()); // DAO 를 통해 get/set 한 데이터 맵을 새로 생성한 맵에 할당한다.
        String[] keyArr = null;
        String[] valueArr = null;
        String property_type = AUTOMOBILE.getOption();

        System.out.println("\n* 자동차 보험상품 계약 *");
        System.out.println("+ 현재 로그인된 계정(" + client.getClientId() + ") 정보로 계약 신청을 진행합니다. +");

        System.out.println("\n* 자동차 보험상품 계약 시, 등록할 차량 정보를 입력하세요. *");
        System.out.println("+ 차량 번호를 입력하세요. (Ex 12가3456)");
        String automobile_id = scanner.next();

        // ... automobile_id 중복 확인
        Property property = inquireProperty(property_type, Automobile.class, automobile_id);
        if (property != null) { // automobile_id 가 이미 DB 에 등록되었을 경우,
            checkProperty(client, property, property_type, automobile_id); // 자산 등록 정보 및 본인 소유 확인
            dataMap.putAll(property.getPropertyDataMap());

            System.out.println("+ 운전자 범위(1인, 2인, 3인)를 입력하세요. (Ex 1인)");
            String driver_scope = scanner.next();

            if (driver_scope.equals("1인")) { driver_scope = "one_person"; }
            else if (driver_scope.equals("2인")) { driver_scope = "two_person"; }
            else if (driver_scope.equals("3인")) { driver_scope = "three_person"; }

            keyArr = new String[]{"property_type", "driver_scope", "is_registered"};
            valueArr = new String[]{property_type, driver_scope, "true"};
        } else {
            System.out.println("+ 차량 사고등급(A등급, B등급, C등급)를 입력하세요. (Ex a)");
            String accident_grade = scanner.next();
            System.out.println("+ 차량 종류(소형, 중형, 대형)를 입력하세요. (Ex 소형)");
            String automobile_type = scanner.next();
            System.out.println("+ 차량 가액(콤마 생략)을 입력하세요. (Ex 20,000,000)");
            String automobile_value = scanner.next();
            System.out.println("+ 운전자 범위(1인, 2인, 3인)를 입력하세요. (Ex 1인)");
            String driver_scope = scanner.next();

            if (driver_scope.equals("1인")) { driver_scope = "one_person"; }
            else if (driver_scope.equals("2인")) { driver_scope = "two_person"; }
            else if (driver_scope.equals("3인")) { driver_scope = "three_person"; }
            if (automobile_type.equals("소형")) { automobile_type = "compact"; }
            else if (automobile_type.equals("중형")) { automobile_type = "mid_size"; }
            else if (automobile_type.equals("대형")) { automobile_type = "full_size"; }

            keyArr = new String[]{"property_type", "automobile_id", "accident_grade", "automobile_type", "automobile_value", "driver_scope"};
            valueArr = new String[]{property_type, automobile_id, accident_grade, automobile_type, automobile_value, driver_scope};
        }

        dataMap = (LinkedHashMap<String, Object>) addEntry((LinkedHashMap<String, Object>) dataMap, keyArr, valueArr);
        computePremiumAndContract("자동차", scanner, insurance, tempClient, dataMap);
        printClientMainMenu(client);
    }

    /**
     * 화재 보험상품 계약(구현O)
     * @param insuranceId, loginData
     */
    public static void contractFireInsurance(String insuranceId, Client client) {
        Insurance insurance = inquireInsurance(APPROVED_INSURANCE.getOption(), FireInsurance.class, insuranceId); // 요율 및 보험료 산출에 필요하므로 고객이 신청한 보험상품 객체를 가져온다.
        Client tempClient = inquireClient(client); // 고객(로그인 데이터만 가진 객체를 통해서) 객체를 가져온다.
        // client 객체는 로그인 데이터만 가진 객체이고 tempClient 객체는 DB 에서 가져온 고객의 모든 데이터를 가진 객체이다.

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.putAll(tempClient.getClientDataMap()); // DAO 를 통해 get/set 한 데이터 맵을 새로 생성한 맵에 할당한다.
        String[] keyArr = null;
        String[] valueArr = null;
        String property_type = BUILDING.getOption();

        System.out.println("\n* 화재 보험상품 계약 *");
        System.out.println("+ 현재 로그인된 계정(" + client.getClientId() + ") 정보로 계약 신청을 진행합니다. +");

        System.out.println("\n* 화재 보험상품 계약 시, 등록할 건물 정보를 입력하세요. *");
        System.out.println("+ 건물 번호를 입력하세요. (Ex 거북골로34)");
        String building_id = scanner.next();

        // ... building_id 중복 확인
        Property property = inquireProperty(property_type, Building.class, building_id);
        if (property != null) { // building_id 가 이미 DB 에 등록되었을 경우,
            checkProperty(client, property, property_type, building_id); // 자산 등록 정보 및 본인 소유 확인
            dataMap.putAll(property.getPropertyDataMap());

            keyArr = new String[]{"property_type", "is_registered"};
            valueArr = new String[]{property_type, "true"};
        } else {
            System.out.println("+ 건물 화재등급(A등급, B등급, C등급)를 입력하세요. (Ex a)");
            String fire_grade = scanner.next();
            System.out.println("+ 건물 업종(가정, 상가, 사무, 공장)를 입력하세요. (Ex 가정)");
            String building_type = scanner.next();
            System.out.println("+ 건물 층수를 입력하세요. (Ex 30)");
            String building_floor = scanner.next();
            System.out.println("+ 건물 평수를 입력하세요. (Ex 50)");
            String building_pyeong = scanner.next();
            System.out.println("+ 건물 가액(콤마 생략)을 입력하세요. (Ex 500,000,000)");
            String building_value = scanner.next();

            if (building_type.equals("가정")) { building_type = "house"; }
            else if (building_type.equals("상가")) { building_type = "store"; }
            else if (building_type.equals("사무")) { building_type = "office"; }
            else if (building_type.equals("공장")) { building_type = "factory"; }

            keyArr = new String[]{"property_type", "building_id", "fire_grade", "building_type", "building_floor", "building_pyeong", "building_value"};
            valueArr = new String[]{property_type, building_id, fire_grade, building_type, building_floor, building_pyeong, building_value};
        }
        dataMap = (LinkedHashMap<String, Object>) addEntry((LinkedHashMap<String, Object>) dataMap, keyArr, valueArr);

        computePremiumAndContract("화재", scanner, insurance, tempClient, dataMap);
        printClientMainMenu(client);
    }

    /**
     * 운전자 보험상품 계약(구현O)
     * @param insuranceId, loginData
     */
    public static void contractDriverInsurance(String insuranceId, Client client) {
        Insurance insurance = inquireInsurance(APPROVED_INSURANCE.getOption(), DriverInsurance.class, insuranceId); // 요율 및 보험료 산출에 필요하므로 고객이 신청한 보험상품 객체를 가져온다.
        Client tempClient = inquireClient(client); // 고객(로그인 데이터만 가진 객체를 통해서) 객체를 가져온다.
        // client 객체는 로그인 데이터만 가진 객체이고 tempClient 객체는 DB 에서 가져온 고객의 모든 데이터를 가진 객체이다.

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.putAll(tempClient.getClientDataMap()); // DAO 를 통해 get/set 한 데이터 맵을 새로 생성한 맵에 할당한다.

        System.out.println("\n* 운전자 보험상품 계약 *");
        System.out.println("+ 현재 로그인된 계정(" + client.getClientId() + ") 정보로 계약 신청을 진행합니다. +");

        computePremiumAndContract("운전자", scanner, insurance, tempClient, dataMap);
        printClientMainMenu(client);
    }

    /**
     * 상해 보험상품 계약(구현O)
     * @param insuranceId, loginData
     */
    public static void contractAccidentInsurance(String insuranceId, Client client) {
        Insurance insurance = inquireInsurance(APPROVED_INSURANCE.getOption(), AccidentInsurance.class, insuranceId); // 요율 및 보험료 산출에 필요하므로 고객이 신청한 보험상품 객체를 가져온다.
        Client tempClient = inquireClient(client); // 고객(로그인 데이터만 가진 객체를 통해서) 객체를 가져온다.
        // client 객체는 로그인 데이터만 가진 객체이고 tempClient 객체는 DB 에서 가져온 고객의 모든 데이터를 가진 객체이다.

        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.putAll(tempClient.getClientDataMap()); // DAO 를 통해 get/set 한 데이터 맵을 새로 생성한 맵에 할당한다.

        System.out.println("\n* 상해 보험상품 계약 *");
        System.out.println("+ 현재 로그인된 계정(" + client.getClientId() + ") 정보로 계약 신청을 진행합니다. +");

        computePremiumAndContract("상해", scanner, insurance, tempClient, dataMap);
        printClientMainMenu(client);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////// [각종 관리]

    /**
     * 보험상품 관리(구현O)
     * @param manageInsuranceMenuNum
     */
    public static void manageInsurance(String manageInsuranceMenuNum) {
        System.out.println("\n*-----------------------------* [보험상품 관리] *-----------------------------*");
        System.out.println("1. 보험상품 조회(미승인 상품)");
        System.out.println("2. 보험상품 조회(승인 상품)");
        System.out.println("0. 취소");
        System.out.print("   메뉴번호 입력: ");
        String evaluateInsuranceMenuNum = scanner.next();
        if (evaluateInsuranceMenuNum.equals(CANCELLATION)) { printInsuranceMainMenu(); return; }

        switch (evaluateInsuranceMenuNum) {
            case "1":
                if (showInsuranceList(manageInsuranceMenuNum, UNAPPROVED_INSURANCE.getOption()) == false) {
                    System.out.println("\n1. 보험상품 승인"); // 미승인 보험상품(승인/기각)
                    System.out.println("2. 보험상품 기각");
                    System.out.println("3. 보험상품 삭제(해당 보험상품이 영구 삭제되며 계약, 보험료 및 보험금 내역도 삭제됨)");
                    System.out.println("0. 취소");
                    System.out.print("   메뉴번호와 보험상품 ID를 공백문자(Space Key)로 구분하여 입력: ");
                    String unapprovedMenuNum = scanner.next();
                    if (unapprovedMenuNum.equals(CANCELLATION)) { printInsuranceMainMenu(); return; }
                    String unapprovedId = scanner.next();
                    checkInputValueAndInvokeMethod(MANAGE_INSURANCE.getOption(), insuranceList, unapprovedMenuNum, unapprovedId, "보험상품 ID");
//                    evaluateInsurance(unapprovedMenuNum, unapprovedId);
                }
                break;
            case "2":
                if (showInsuranceList(manageInsuranceMenuNum, APPROVED_INSURANCE.getOption()) == false) {
                    System.out.println("\n1. 보험상품 승인"); // 승인 보험상품(승인/해지)
                    System.out.println("2. 보험상품 해지");
                    System.out.println("3. 보험상품 삭제(해당 보험상품이 영구 삭제되며 계약, 보험료 및 보험금 내역도 삭제됨)");
                    System.out.println("0. 취소");
                    System.out.print("   메뉴번호와 보험상품 ID를 공백문자(Space Key)로 구분하여 입력: ");
                    String approvedMenuNum = scanner.next();
                    if (approvedMenuNum.equals(CANCELLATION)) { printInsuranceMainMenu(); return; }
                    String approvedId = scanner.next();
                    checkInputValueAndInvokeMethod(MANAGE_INSURANCE.getOption(), insuranceList, approvedMenuNum, approvedId, "보험상품 ID");
//                    evaluateInsurance(approvedMenuNum, approvedId);
                }
                break;
            default:
                break;
        }
        printInsuranceMainMenu();
    }

    /**
     * 계약 관리(구현O)
     * @param manageContractMenuNum
     */
    public static void manageContract(String manageContractMenuNum) {
        switch (manageContractMenuNum) {
            case "1":
                if (showContractList(UNAPPROVED_CONTRACT.getOption()) == false) {
                    System.out.println("\n1. 계약 승인"); // 미승인 계약(승인/기각)
                    System.out.println("2. 계약 기각");
                    System.out.println("3. 계약 삭제(해당 계약이 영구 삭제되며 보험료 및 보험금 내역도 삭제됨)");
                    System.out.println("0. 취소");
                    System.out.print("   메뉴번호와 계약번호를 공백문자(Space Key)로 구분하여 입력: ");
                    String unapprovedMenuNum = scanner.next();
                    if (unapprovedMenuNum.equals(CANCELLATION)) { printInsuranceMainMenu(); return; }
                    String unapprovedContractNum = scanner.next();
                    checkInputValueAndInvokeMethod(MANAGE_CONTRACT.getOption(), contractList, unapprovedMenuNum, unapprovedContractNum, "계약번호");
//                    evaluateContract(new String[]{unapprovedMenuNum, unapprovedContractNum});
                }
                break;
            case "2":
                if (showContractList(APPROVED_CONTRACT.getOption()) == false) {
                    System.out.println("\n1. 계약 승인"); // 승인 계약(승인/해지)
                    System.out.println("2. 계약 해지");
                    System.out.println("3. 계약 삭제(해당 계약이 영구 삭제되며 보험료 및 보험금 내역도 삭제됨)");
                    System.out.println("0. 취소");
                    System.out.print("   메뉴번호와 계약번호를 공백문자(Space Key)로 구분하여 입력: ");
                    String approvedMenuNum = scanner.next();
                    if (approvedMenuNum.equals(CANCELLATION)) { printInsuranceMainMenu(); return; }
                    String approvedContractNum = scanner.next();
                    checkInputValueAndInvokeMethod(MANAGE_CONTRACT.getOption(), contractList, approvedMenuNum, approvedContractNum, "계약번호");
//                    evaluateContract(approvedMenuNum, approvedContractNum);
                }
                break;
            case "3":
                if (showContractList(APPROVED_CONTRACT.getOption()) == false) {
                    System.out.println("\n1. 보험료 납부요청서 생성"); // 승인 계약 보험료 납부요청서 생성 (생성해야 고객이 보험료를 납부할 수 있다.)
                    System.out.println("2. 보험금 청구신청서 생성"); // 승인 계약 보험금 청구신청서 생성 (생성해야 고객이 보험금을 청구할 수 있다.)
                    System.out.println("0. 취소");
                    System.out.print("   메뉴번호와 계약번호를 공백문자(Space Key)로 구분하여 입력: ");
                    String createRequestMenuNum = scanner.next();
                    if (createRequestMenuNum.equals(CANCELLATION)) { printInsuranceMainMenu(); return; }
                    String approvedContractNum = scanner.next();
                    checkInputValueAndInvokeMethod(CREATE_DOCUMENT.getOption(), contractList, createRequestMenuNum, approvedContractNum, "계약번호");
//                    createRequestDocument(createRequestMenuNum, approvedContractNum);
                }
                break;
            default:
                break;
        }
        printInsuranceMainMenu();
    }

    /**
     * 보험금 청구 관리(구현O)
     * @param claimMenuNum
     */
    public static void manageClaim(String claimMenuNum) {
        switch (claimMenuNum) {
            case "1":
                if (showClaimList(REQUESTED_CLAIM.getOption()) == false || showClaimList(UNDER_EXAMINATION.getOption()) == false) { // 대기/보류 심사
                    System.out.println("\n1. 지급 승인");
                    System.out.println("2. 지급 거절");
                    System.out.println("3. 지급 보류");
                    System.out.println("4. 청구 삭제");
                    System.out.println("0. 취소");
                    System.out.print("   메뉴번호와 청구번호를 공백문자(Space Key)로 구분하여 입력: ");
                    String unapprovedMenuNum = scanner.next();
                    if (unapprovedMenuNum.equals(CANCELLATION)) { printInsuranceMainMenu(); return; }
                    String unapprovedClaimNum = scanner.next();
                    checkInputValueAndInvokeMethod(MANAGE_CLAIM.getOption(), claimList, unapprovedMenuNum, unapprovedClaimNum, "청구번호");
//                    evaluateClaim(unapprovedMenuNum, unapprovedClaimNum);
                }
                break;
            case "2":
                showClaimList(ALL_CLAIM.getOption()); // 모든 청구 조회
                break;
            default:
                break;
        }
        printInsuranceMainMenu();
    }

    /**
     * 등록 자산 조회(구현O)
     * @param managePropertyMenuNum
     */
    public static void manageProperty(String managePropertyMenuNum) {
        switch (managePropertyMenuNum) {
            case "1":
                showPropertyList(AUTOMOBILE_NUM);
                break;
            case "2":
                showPropertyList(FIRE_NUM);
                break;
            case "3":
                showPropertyList(PROPERTY_NUM);
            default:
                break;
        }
        printInsuranceMainMenu();
    }

    /**
     * 보험료 관리(구현O)
     * @param managePremiumMenuNum
     */
    public static void managePremium(String managePremiumMenuNum) {
        switch (managePremiumMenuNum) {
            case "1":
                showPaymentList(UNPAID.getOption()); // 미납
                break;
            case "2":
                showPaymentList(COMPLETED_PAYMENT.getOption()); // 납부완료
                break;
            default:
                break;
        }
        printInsuranceMainMenu();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////// [보험료 납부/보험금 청구 및 내역 조회]

    /**
     * 보험료 납부 및 납부내역 조회(구현O)
     * @param paymentMenuNum, client
     */
    public static void managePremium(String paymentMenuNum, Client client) {
        switch (paymentMenuNum) {
            case "1":
                if (inquirePaymentList(UNPAID.getOption(), client) == false) { // 보험료 미납 조회
                    System.out.println("\n* 보험료를 납부하실 계약의 납부번호를 입력하세요. (0: 취소) *");
                    String paymentNum = scanner.next();
                    if (paymentNum.equals(CANCELLATION)) { printClientMainMenu(client); return; }

                    checkInputValueAndInvokeMethod(PAY_PREMIUM.getOption(), paymentList, "", paymentNum, "납부번호");
                }
                break;
            case "2":
                inquirePaymentList(COMPLETED_PAYMENT.getOption(), client); // 보험료 납부내역 조회
                break;
            default:
                break;
        }
        printClientMainMenu(client);
    }

    /**
     * 보험금 청구 및 청구내역 조회(구현O)
     * @param claimMenuNum, client
     */
    public static void manageClaim(String claimMenuNum, Client client) {
        switch (claimMenuNum) {
            case "1":
                if (inquireClaimList(UNCLAIMED.getOption(), client) == false) { // 보험금 미청구 조회
                    System.out.println("\n* 보험금을 청구하실 계약의 청구번호를 입력하세요. (0: 취소) *");
                    String claimNum = scanner.next();
                    if (claimNum.equals(CANCELLATION)) { printClientMainMenu(client); return; }
                    System.out.println("+ 청구 금액(콤마 생략)을 입력하세요. (Ex 30,000,000)");
                    String claimAmount = scanner.next();

                    checkInputValueAndInvokeMethod(CLAIM_CLAIMS.getOption(), claimList, claimAmount, claimNum, "청구번호");
                }
                break;
            case "2":
                inquireClaimList(ALL_CLAIM.getOption(), client); // 보험금 청구내역 조회
                break;
            default:
                break;
        }
        printClientMainMenu(client);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////// [각종 조회(n개)]

    // show 메소드(Client 객체를 필요로 하지 않는다.) : 보험 메뉴에서 사용하는 메소드
    // inquire 메소드(Client 객체를 필요로 한다.)     : 고객 메뉴에서 사용하는 메소드

    /**
     * 전체 보험상품 조회(구현O)
     * @param insuranceNum, option
     */
    public static boolean showInsuranceList(String insuranceNum, String option) {
        if (insuranceNum.equals(AUTOMOBILE_NUM)) {
            insuranceList = Insurance.getInstance(AutomobileInsurance.class).getInsuranceList(option);
        } else if (insuranceNum.equals(FIRE_NUM)) {
            insuranceList = Insurance.getInstance(FireInsurance.class).getInsuranceList(option);
        } else if (insuranceNum.equals(DRIVER_NUM)) {
            insuranceList = Insurance.getInstance(DriverInsurance.class).getInsuranceList(option);
        } else if (insuranceNum.equals(ACCIDENT_NUM)) {
            insuranceList = Insurance.getInstance(AccidentInsurance.class).getInsuranceList(option);
        }
        return isEmptyList(SHOW_INFO.getOption(), insuranceList, "보험상품");
    }
    /**
     * 전체 고객 조회(구현O)
     */
    public static boolean showClientList() {
        clientList = Client.getInstance().getClientList();
        return isEmptyList(SHOW_INFO.getOption(), clientList, "고객");
    }
    /**
     * 전체 계약 조회(구현O)
     * @param option
     */
    public static boolean showContractList(String option) {
        contractList = Contract.getInstance().getContractList(option);
        return isEmptyList(SHOW_SPECIFIC_INFO.getOption(), contractList, "계약");
    }
    /**
     * 전체 자산 조회(구현O)
     * @param propertyMenuNum
     */
    public static boolean showPropertyList(String propertyMenuNum) {
        if (propertyMenuNum.equals(AUTOMOBILE_NUM)) {
            propertyList = Property.getInstance(Automobile.class).getPropertyList(AUTOMOBILE.getOption(), null);
        } else if (propertyMenuNum.equals(FIRE_NUM)) {
            propertyList = Property.getInstance(Building.class).getPropertyList(BUILDING.getOption(), null);
        } else if (propertyMenuNum.equals(PROPERTY_NUM)) {
            // 미구현 (Property 클래스가 추상 클래스
        }
        return isEmptyList(SHOW_INFO.getOption(), propertyList, "자산");
    }
    /**
     * 전체 보험료 납부 조회(구현O)
     * @param option
     */
    public static boolean showPaymentList(String option) {
        paymentList = Payment.getInstance().getPaymentList(option, null);
        return isEmptyList(SHOW_INFO.getOption(), paymentList, "보험료 납부");
    }
    /**
     * 전체 보험금 청구 조회(구현O)
     * @param option
     */
    public static boolean showClaimList(String option) {
        String outputStr = "";
        if (option.equals(REQUESTED_CLAIM.getOption())) {
            outputStr = "심사 대기 중인 청구";
        } else if (option.equals(UNDER_EXAMINATION.getOption())) {
            outputStr = "심사 중인 청구";
        } else if (option.equals(ALL_CLAIM.getOption())) {
            outputStr = "보험금 청구";
        }
        claimList = Claim.getInstance().getClaimList(option, null);
        return isEmptyList(SHOW_INFO.getOption(), claimList, outputStr);
    }
    /**
     * 특정 고객의 계약 리스트 조회(구현O)
     * @param client
     */
    public static boolean inquireContractList(String option, Client client) {
        if (option.equals(ALL_CONTRACT.getOption())) { // 계약 조회 시 사용
            contractList = Contract.getInstance().getContract(ALL_CONTRACT.getOption(), client);
            return isEmptyList(SHOW_SPECIFIC_INFO.getOption(), contractList, "계약");
        } else if (option.equals(APPROVED_CONTRACT.getOption())) { // 보험료 납부 및 보험금 청구 시 사용
            contractList = Contract.getInstance().getContract(APPROVED_CONTRACT.getOption(), client);
            return isEmptyList(SHOW_INFO.getOption(), contractList, "계약");
        }
        return false;
    }
    /**
     * 특정 고객의 자산 리스트 조회(구현O)
     * @param propertyMenuNum, client
     */
    public static boolean inquirePropertyList(String propertyMenuNum, Client client) {
        String outputStr = "";
        if (propertyMenuNum.equals(AUTOMOBILE_NUM)) {
            outputStr = "등록된 차량";
            propertyList = Property.getInstance(Automobile.class).getPropertyList(AUTOMOBILE.getOption(), client);
        } else if (propertyMenuNum.equals(FIRE_NUM)) {
            outputStr = "등록된 건물";
            propertyList = Property.getInstance(Building.class).getPropertyList(BUILDING.getOption(), client);
        }
        return isEmptyList(SHOW_INFO.getOption(), propertyList, outputStr);
    }
    /**
     * 특정 고객의 전체 보험료 납부내역 조회(구현O)
     * @param client
     */
    public static boolean inquirePaymentList(String option, Client client) {
        String outputStr = "";
        if (option.equals(UNPAID.getOption())) {
            outputStr = "보험료 납부가 가능한 계약";
        } else if (option.equals(COMPLETED_PAYMENT.getOption())) {
            outputStr = "보험료 납부내역";
        }
        paymentList = Payment.getInstance().getPaymentList(option, client);
        return isEmptyList(SHOW_INFO.getOption(), paymentList, outputStr);
    }
    /**
     * 특정 고객의 전체 보험료 청구내역 조회(구현O)
     * @param client
     */
    public static boolean inquireClaimList(String option, Client client) {
        String outputStr = "";
        if (option.equals(UNCLAIMED.getOption())) {
            outputStr = "보험금 청구가 가능한 계약";
        } else if (option.equals(ALL_CLAIM.getOption())) {
            outputStr = "보험금 청구내역";
        }
        claimList = Claim.getInstance().getClaimList(option, client);
        return isEmptyList(SHOW_INFO.getOption(), claimList, outputStr);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////// [각종 조회(1개)]

    /**
     * 특정 보험상품 조회(구현O)
     * @param option, clazz, insuranceId
     */
    public static Insurance inquireInsurance(String option, Class<? extends Insurance> clazz, String insuranceId) {
        Insurance insurance = Insurance.getInstance(clazz);
        return insurance.getInsurance(option, insuranceId);
    }
    /**
     * 특정 고객 조회(구현O)
     * @param client
     */
    public static Client inquireClient(Client client) {
        return client.getClient(INQUIRE_CLIENT.getOption());
    }
    /**
     * 특정 자산 조회(구현O)
     * @param propertyId
     */
    public static Property inquireProperty(String option, Class<? extends Property> clazz, String propertyId) {
        Property property = Property.getInstance(clazz);
        return property.getProperty(option, propertyId);
    }
    /**
     * 특정 계약 조회(구현O)
     * @param option, contractNum
     */
    public static void inquireContract(String option, String contractNum) {
        Contract contract = Contract.getInstance().getContract(option, contractNum);
        contract.showSpecificInfo();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////// [각종 심사]

    /**
     * 특정 보험상품 심사(구현O)
     * @param evaluationNum, String insuranceId
     */
    public static void evaluateInsurance(String evaluationNum, String insuranceId) {
        int result = Insurance.evaluateInsurance(evaluationNum, insuranceId);
        if (result != CHANGED_ROW_COUNT) {
            System.out.println("\n* 해당 보험상품 ID(" + insuranceId + ")가 심사되었습니다. *");
        } else {
            System.err.println("\n* 해당 보험상품 ID(" + insuranceId + ")의 심사가 실패하였습니다. *");
        }
        printInsuranceMainMenu();
    }
    /**
     * 특정 계약 심사(구현O)
     * @param evaluationNum, contractNum
     */
    public static void evaluateContract(String evaluationNum, String contractNum) {
        if (Contract.getInstance().evaluateContract(evaluationNum, contractNum) != CHANGED_ROW_COUNT) {
            System.out.println("\n* 해당 계약번호(" + contractNum + ")가 심사되었습니다. *");
        } else {
            System.err.println("\n* 해당 계약번호(" + contractNum + ")의 심사가 실패하였습니다. *");
        }
        printInsuranceMainMenu();
    }
    /**
     * 특정 보험금 청구 심사(구현O)
     * @param evaluationNum, claimNum
     */
    public static void evaluateClaim(String evaluationNum, String claimNum) {
        if (Claim.getInstance().evaluateClaim(evaluationNum, claimNum) != CHANGED_ROW_COUNT) {
            System.out.println("\n* 해당 청구번호(" + claimNum + ")의 보험금 청구 요청이 심사되었습니다. *");
        } else {
            System.err.println("\n* 해당 청구번호(" + claimNum + ")의 보험금 청구 심사가 실패하였습니다. *");
        }
        printInsuranceMainMenu();
    }
    /**
     * 보험료 납부요청서 및 보험금 청구신청서 생성(구현O)
     * @param documentNum, contractNum
     */
    public static void createRequestDocument(String documentNum, String contractNum) {
        if (documentNum.equals(PAYMENT)) {
            if (Payment.getInstance().createPayment(contractNum) != CHANGED_ROW_COUNT) {
                System.out.println("\n* 해당 계약번호(" + contractNum + ")의 보험료 납부요청서가 생성되었습니다. *");
            } else {
                System.err.println("\n* 해당 계약번호(" + contractNum + ")의 보험료 납부요청서 생성이 실패하였습니다. *");
            }
        } else if (documentNum.equals(CLAIM)) {
            if (Claim.getInstance().createClaim(contractNum) != CHANGED_ROW_COUNT) {
                System.out.println("\n* 해당 계약번호(" + contractNum + ")의 보험금 청구신청서가 생성되었습니다. *");
            } else {
                System.err.println("\n* 해당 계약번호(" + contractNum + ")의 보험금 청구신청서 생성이 실패하였습니다. *");
            }
        }
        printInsuranceMainMenu();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////// [보험료 납부 및 보험금 청구]

    /**
     * 보험료 납부 및 납부내역 조회(구현O)
     * @param paymentNum
     */
    public static void payPremium(String paymentNum) {
        int result = Payment.getInstance().payPremium(COMPLETED_PAYMENT.getOption(), paymentNum);

        if (result != CHANGED_ROW_COUNT) {
            System.out.println("\n* 해당 납부번호(" + paymentNum + ")의 보험료 납부가 완료되었습니다. *");
        } else {
            System.err.println("\n* 해당 납부번호(" + paymentNum + ")의 보험료 납부가 실패하였습니다. *");
        }
    }

    /**
     * 보험금 청구 및 청구내역 조회(구현O)
     * @param claimAmount, claimNum
     */
    public static void claimClaims(String claimAmount, String claimNum) {
        int result = Claim.getInstance().claimClaims(REQUESTED_CLAIM.getOption(), claimNum, claimAmount);

        if (result != CHANGED_ROW_COUNT) {
            System.out.println("\n* 해당 청구번호(" + claimNum + ")의 보험금 청구 신청이 완료되었습니다. *");
        } else {
            System.err.println("\n* 해당 청구번호(" + claimNum + ")의 보험금 청구 신청이 실패하였습니다. *");
        }
    }
}