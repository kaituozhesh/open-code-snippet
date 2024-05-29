package com.ityizhan.open.basic.java8;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 常用的Stream流处理
 * @ClassName: StreamApiExample
 * @Auther: lin
 * @Date: 2024/5/29 16:00
 * @Version: 1.0
 */
public class StreamApiExample {
    public static void main(String[] args) {
        // 1. 根据某个字段分组后取另一个字段集合 EXP: Map<String, List<Integer>> key:10001 values:[1, 2, 3]
        Map<String, List<Integer>> customerTypeMap = groupByFieldCollectValues();
    }

    public static Map<String, List<Integer>> groupByFieldCollectValues() {
        @Data
        @AllArgsConstructor
        class SettlementDate {
            String customerId;
            Integer feeType;
        }

        List<SettlementDate> settleList = new ArrayList<>();
        settleList.add(new SettlementDate("10001", 1));
        settleList.add(new SettlementDate("10001", 2));
        settleList.add(new SettlementDate("10001", 3));
        settleList.add(new SettlementDate("10002", 3));

        Map<String, List<Integer>> customerTypeMap = settleList.stream()
                .collect(
                        Collectors.groupingBy(SettlementDate::getCustomerId, Collectors.mapping(SettlementDate::getFeeType, Collectors.toList()))
                );
        System.out.println(customerTypeMap);
        return customerTypeMap;
    }
}
