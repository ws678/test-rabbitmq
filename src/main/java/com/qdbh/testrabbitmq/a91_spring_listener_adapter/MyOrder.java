package com.qdbh.testrabbitmq.a91_spring_listener_adapter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyOrder implements Serializable {

    //自定义Long类型的版本ID
    private static final long serialVersionUID = 455616419841641L;

    private long id;

    private String name;

    private BigDecimal price;
}
