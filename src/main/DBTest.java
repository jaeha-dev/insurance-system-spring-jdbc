package main;

import client.Client;

import java.util.List;

import static main.Main.*;

/**
 * Trade-off
 * Quality: 안정성(정규화를 통한 데이터 무결성, 데이터 영구 보관), 유지보수, 보안(DB 컬럼 암호화 등), 성능(메모리보다 속도는 감소하지만 ), 사용성(연관 없음)
 * 비정규화: 중복 데이터 허용 (성능 개선을 위해)
 * Entity Object, Control Object, Boundary Object
 *
 * Query 문은 반드시 DAO에만 존재해야 한다. But, DAO에는 비즈니스 로직 코드가 존재해서는 안된다. (print문도 DAO에 존재해서는 안되고 DAO를 호출한 비즈니스 로직에 존재해야 한다.)
 * 각 DAO에는 해당 DAO의 목적에 맞는 insertXXX, selectXXX 등으로 이름을 달리 해야 한다.
 * Separation of Concern: 각기 역할에 맞는 기능만 수행해야 한다.
 */
public class DBTest {
    public void getCounts() {
        System.out.println("insurance 테이블 레코드 수: " + insuranceDao.selectInsuranceRowCount());
        System.out.println("client 테이블 레코드 수: " + clientDao.selectClientRowCount());
        System.out.println("property 테이블 레코드 수: " + propertyDao.selectPropertyRowCount());
        System.out.println("contract 테이블 레코드 수: " + contractDao.selectContractRowCount());
    }

//    public List<Insurance> showFireInsuranceList() {
//        return insuranceDao.selectInsurance(FIRE);
//    }

    public List<Client> showAllClientList() {
        return clientDao.selectClientList();
    }
}