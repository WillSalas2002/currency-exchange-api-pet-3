package com.will.currency.exchange.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
public class Currency {
    private Integer id;
    private String fullName;
    private String code;
    private String sign;
}
