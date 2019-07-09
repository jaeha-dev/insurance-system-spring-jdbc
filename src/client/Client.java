package client;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static global.Constants.ClientQueryOption.*;
import static global.Util.printClient;
import static main.Main.clientDao;

public class Client implements Serializable {
    private static final long serialVersionUID = 1L;
    private int clientNum; // 고객 번호(-> PK)
    private String clientId; // 고객 ID(-> UNIQUE)
    private String clientPw; // 고객 비밀번호
    private String clientName; // 고객 이름
    private String clientRRN; // 고객 주민등록번호(-> UNIQUE)
    private GenderType clientGender; // 고객 성별
    private AgeGroupType clientAge; // 고객 연령
    private OccupationType clientOccupation; // 고객 직업 종류
    private String clientPhoneNum; // 고객 전화번호
    private Map<String, Object> clientDataMap; // 고객 개인 정보 맵

    public enum GenderType {
        MAN("man"), WOMAN("woman");
        private String type;
        private GenderType(String type) { this.type = type; }
        public String getType() { return type; }
    }

    public enum AgeGroupType {
        TWENTY("twenty"), THIRTY("thirty"), FORTY("forty"), FIFTY("fifty"), ETC("etc");
        private String type;
        private AgeGroupType(String type) { this.type = type; }
        public String getType() { return type; }
    }

    public enum OccupationType {
        RISK("risk"), NON_RISK("non_risk");
        private String type;
        private OccupationType(String type) { this.type = type; }
        public String getType() { return type; }
    }

    public Client() {
        this.clientDataMap = new LinkedHashMap<String, Object>();
    }

    public static Client getInstance() {
        return new Client();
    }

    public int registerClient(Map<String, Object> dataMap) { // 고객 등록
        this.clientDataMap.put("client_id", dataMap.get("client_id"));
        this.clientDataMap.put("client_pw", dataMap.get("client_pw"));
        this.clientDataMap.put("client_name", dataMap.get("client_name"));
        this.clientDataMap.put("client_rrn", dataMap.get("client_rrn"));

        if (dataMap.get("client_gender").equals("man")) {
            this.clientDataMap.put("client_gender", GenderType.MAN);
        } else if (dataMap.get("client_gender").equals("woman")) {
            this.clientDataMap.put("client_gender", GenderType.WOMAN);
        }
        int age = Integer.parseInt((String) dataMap.get("client_age"));
        if (age < 20) {
            return 0;
        } else if (20 <= age && age < 30) {
            this.clientDataMap.put("client_age", AgeGroupType.TWENTY);
        } else if (30 <= age && age < 40) {
            this.clientDataMap.put("client_age", AgeGroupType.THIRTY);
        } else if (40 <= age && age < 50) {
            this.clientDataMap.put("client_age", AgeGroupType.FORTY);
        } else if (50 <= age && age < 60) {
            this.clientDataMap.put("client_age", AgeGroupType.FIFTY);
        } else {
            this.clientDataMap.put("client_age", AgeGroupType.ETC);
        }
        if (dataMap.get("client_occupation").equals("risk")) {
            this.clientDataMap.put("client_occupation", OccupationType.RISK);
        } else if (dataMap.get("client_occupation").equals("non_risk")) {
            this.clientDataMap.put("client_occupation", OccupationType.NON_RISK);
        }
        this.clientDataMap.put("client_phone", dataMap.get("client_phone"));
        return clientDao.insertClient(this.clientDataMap); // 객체 -> DB (DB에 고객이 등록된다.)
    }

    /**
     * 사용자가 입력한 고객 데이터와 DB에 저장된 실제 고객 데이터의 일치 여부를 판단한다.
     * @param option, client
     */
    public boolean validateClient(String option, Client client) {
        Client selectedClient = clientDao.selectClient(option, client); // 객체 <- DB (DB에 저장된 고객을 가져온다.)

        Optional<Client> optClient = Optional.ofNullable(selectedClient);
        if (optClient.isPresent()) { // Not Null
            if (option.equals(CHECK_CLIENT_ID.getOption())) { // ID 중복 검사
                if (selectedClient.getClientId().equals(client.getClientId())) {
                    return true;
                } else {
                    return false;
                }
            } else if (option.equals(CHECK_CLIENT_RRN.getOption())) { // 주민등록번호 중복 검사
                if (selectedClient.getClientRRN().equals(client.getClientRRN())) {
                    return true;
                } else {
                    return false;
                }
            } else if (option.equals(CHECK_LOGIN_VALIDATION.getOption())) { // 로그인 검증
                if (selectedClient.getClientId().equals(client.getClientId()) && selectedClient.getClientId().equals(client.getClientId())) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * DB에 저장된 모든 고객 데이터를 가져온다.
     */
    public List<Client> getClientList() {
        return clientDao.selectClientList();
    }

    /**
     * DB에 저장된 특정 고객 데이터를 가져온다.
     * @param option
     */
    public Client getClient(String option) {
        return clientDao.selectClient(option, this);
    }

    public void showInfo() {
        System.out.println("\n*------------* [고객 정보] *---------------*");
        System.out.println("+ 고객 번호: " + this.getClientDataMap().get("client_num"));
        System.out.println("- 고객 ID: " + this.getClientDataMap().get("client_id"));
        System.out.println("- 고객 비밀번호: " + this.getClientDataMap().get("client_pw"));
        System.out.println("- 고객 이름: " + this.getClientDataMap().get("client_name"));
        System.out.println("- 고객 주민등록번호: " + this.getClientDataMap().get("client_rrn"));
        System.out.println("- 고객 성별: " + printClient(this.getClientDataMap().get("client_gender")));
        System.out.println("- 고객 연령: " + printClient(this.getClientDataMap().get("client_age")));
        System.out.println("- 고객 직업: " + printClient(this.getClientDataMap().get("client_occupation")));
        System.out.println("- 고객 전화번호: " + this.getClientDataMap().get("client_phone"));
        System.out.println("*------------------------------------------*");
    }

    // getters & setters
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getClientPw() { return clientPw; }
    public void setClientPw(String clientPw) { this.clientPw = clientPw; }
    public String getClientRRN() { return clientRRN; }
    public void setClientRRN(String clientRRN) { this.clientRRN = clientRRN; }

    public Map<String, Object> getClientDataMap() { return clientDataMap; }
    public void setClientDataMap(Map<String, Object> clientDataMap) { this.clientDataMap = clientDataMap; }
}