package com.bhoey.demo.springbatch.babynames;

import lombok.Data;

@Data
public class BabyName {
    private long id;
    private int year;
    private String name;
    private int num;
}
