package com.express.yto.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Detective
 * @date Created in 2026/4/21
 */
public class CustomerSearchInput extends PageInput {

    private String kName;

    private String kCode;

    public String getkName() {
        return kName;
    }

    public void setkName(String kName) {
        this.kName = kName;
    }

    public String getkCode() {
        return kCode;
    }

    public void setkCode(String kCode) {
        this.kCode = kCode;
    }
}
