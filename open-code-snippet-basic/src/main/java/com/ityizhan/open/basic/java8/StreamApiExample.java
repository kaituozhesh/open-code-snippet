package com.ityizhan.open.basic.java8;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
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

        // 3.1.BigDecimal字段累加 3.2.根据字段分组计算组内BigDecimal总和 EXP: {10002=465, 10001=127}
        Map<String, BigDecimal> reduceAdd = reduceAdd();
    }

    public static Map<String, List<Integer>> groupByFieldCollectValues() {
        System.out.println("\n====groupByFieldCollectValues====");
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
        System.out.println("\n====nestedMap====");
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

    private static Map<String, BigDecimal> reduceAdd() {

        System.out.println("\n====reduceAdd====");

        @Getter
        @AllArgsConstructor
        class FeeItem {
            String orderNo;
            String feeId;
            BigDecimal amount;
        }

        List<FeeItem> feeItems = new ArrayList<>();
        feeItems.add(new FeeItem("10001", "100010001", BigDecimal.valueOf(10)));
        feeItems.add(new FeeItem("10001", "100010002", BigDecimal.valueOf(13)));
        feeItems.add(new FeeItem("10001", "100010003", BigDecimal.valueOf(104)));

        feeItems.add(new FeeItem("10002", "100020001", BigDecimal.valueOf(111)));
        feeItems.add(new FeeItem("10002", "100020002", BigDecimal.valueOf(222)));
        feeItems.add(new FeeItem("10002", "100020003", BigDecimal.valueOf(132)));

        BigDecimal sum = feeItems.stream().map(FeeItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("3.1:TotalSum:" + sum);

        Map<String, BigDecimal> reduceAdd = feeItems.stream().collect(
                Collectors.groupingBy(FeeItem::getOrderNo,
                        Collectors.mapping(FeeItem::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)))
        );

        System.out.println("3.2:GroupSum:" + reduceAdd);

        return reduceAdd;
    }
}
