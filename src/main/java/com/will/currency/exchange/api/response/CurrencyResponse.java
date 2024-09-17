package com.will.currency.exchange.api.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CurrencyResponse {
    private int id;
    private String code;
    private String fullName;
    private String sign;

    public CurrencyResponse(String code, String fullName, String sign) {
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }
}
