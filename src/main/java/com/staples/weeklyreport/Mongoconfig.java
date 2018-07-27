package com.staples.weeklyreport;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.CustomConversions;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class Mongoconfig {

//    @Bean
//    public CustomConversions customConversions() {
//        List<Converter<?, ?>> converters = new ArrayList<Converter<?, ?>>();
//        converters.add(new com.staples.weeklyreport.Repository.HashMapConvertor());
//        converters.add(new com.staples.weeklyreport.Repository.ArrayListConverter());
//        return new CustomConversions(converters);
//    }

}