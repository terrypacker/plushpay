package com.plushpay.repository.user;

import com.plushpay.repository.IdEntity;
import com.plushpay.repository.beneficiary.Beneficiary;
import java.util.List;

/**
 * @author Terry Packer
 */
public class User implements IdEntity<Integer> {

    private Integer id;
    private String username;
    private String password;
    private String email;
    private boolean canTrade;
    private List<Beneficiary> beneficiaries;

    public User() {
    }

    public User(Integer id, String username, String password, String email, boolean canTrade,
        List<Beneficiary> beneficiaries) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.canTrade = canTrade;
        this.beneficiaries = beneficiaries;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return 0;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isCanTrade() {
        return canTrade;
    }

    public void setCanTrade(boolean canTrade) {
        this.canTrade = canTrade;
    }

    public List<Beneficiary> getBeneficiaries() {
        return beneficiaries;
    }

    public void setBeneficiaries(
        List<Beneficiary> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }
}
