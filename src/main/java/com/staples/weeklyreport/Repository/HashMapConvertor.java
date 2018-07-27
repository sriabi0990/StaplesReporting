package com.staples.weeklyreport.Repository;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;


@Component
public class HashMapConvertor  implements Converter<HashMap, Object> {

    @Override
    public Object convert(HashMap list) {
        Object obj = new Object();
        obj = (HashMap) list;
        return obj;
    }

}