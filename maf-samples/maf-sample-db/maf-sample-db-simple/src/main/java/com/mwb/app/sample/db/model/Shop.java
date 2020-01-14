package com.mwb.app.sample.db.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Shop {

    private int id;
    private String name;
    private long ownerId;
    private String address;

}
