package com.staples.weeklyreport.Repository;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ArrayListConverter  implements Converter<ArrayList, Object> {

    @Override
    public Object convert(ArrayList list) {
        Object obj = new Object();
        obj = (Object) list;
        return obj;
    }

}
