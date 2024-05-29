package com.ityizhan.open.basic.java8;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
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

        // 2. 嵌套Map EXP: 省->市->区  浙江省 -> { 宁波市 -> {鄞州区, 海曙区}, 台州市 -> {温岭市, 临海市} }
        Map<String, Map<String, List<String>>> nestedMap = nestedMap();
    }

    public static Map<String, List<Integer>> groupByFieldCollectValues() {
        System.out.println("====groupByFieldCollectValues====");
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

    public static Map<String, Map<String, List<String>>> nestedMap() {
        System.out.println("====nestedMap====");
        @Getter
        @AllArgsConstructor
        class Address {
            String province;
            String city;
            String area;
        }

        List<Address> addresses = new ArrayList<>();
        addresses.add(new Address("浙江省", "宁波市", "鄞州区"));
        addresses.add(new Address("浙江省", "宁波市", "海曙区"));
        addresses.add(new Address("浙江省", "台州市", "温岭市"));
        addresses.add(new Address("浙江省", "台州市", "临海市"));

        // 没有Java8之前
        Map<String, Map<String, List<String>>> nestedMap = new HashMap<>();
        for (Address address : addresses) {
            nestedMap.computeIfAbsent(address.getProvince(), e -> new HashMap<>())
                    .computeIfAbsent(address.getCity(), e -> new ArrayList<>())
                    .add(address.getArea());
        }
        System.out.println("java:" + nestedMap);

        // Java8
        nestedMap = addresses.stream().collect(
                Collectors.groupingBy(Address::getProvince,
                        Collectors.groupingBy(Address::getCity, Collectors.mapping(Address::getArea, Collectors.toList())))
        );
        System.out.println("java8:" + nestedMap);

        return nestedMap;
    }
}
