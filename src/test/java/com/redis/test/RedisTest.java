package com.redis.test;

import com.google.common.collect.Sets;
import org.wltea.analyzer.conndb.RedisHelper;

import java.util.Set;

/**
 * Created by qindongliang on 2015/12/25.
 */
public class RedisTest {

    public static void main(String[] args) {

        Set<String> sets= Sets.newHashSet();

        sets.add("百度,baidu,百度科技有限公司");
        sets.add("中国,china,祖国,中华人民共和国");

        RedisHelper.addList("synonym",sets);

    }
}
