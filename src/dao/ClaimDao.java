package dao;

import client.Client;
import contract.Claim;

import java.util.List;

public interface ClaimDao {

    // Insert 보험금 청구내역
    public int insertClaim(String contractNum);

    // Select 보험금 청구내역 Row 개수
    public int selectClaimRowCount();

    // Select 보험금 청구내역
    public List<Claim> selectClaimList(String option, Client client);

    // Select 보험금 청구내역
    public Claim selectClaim(Client client);

    // Update 보험금 청구내역 (선택 옵션, 계약 번호) -> 보험금 청구 여부
    public int updateClaimByNum(String option, String claimNum, String claimsAmount);

    // Delete 보험금 청구내역 (청구 번호)
    public int deleteClaimByNum(String claimNum);
}
