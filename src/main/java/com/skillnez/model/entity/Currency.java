package com.skillnez.model.entity;


import lombok.*;

@Data
@AllArgsConstructor
public class Currency {
    private int id;
    private String code;
    private String fullName;
    private String sign;

    public Currency(String code, String fullName, String sign) {
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }
}
