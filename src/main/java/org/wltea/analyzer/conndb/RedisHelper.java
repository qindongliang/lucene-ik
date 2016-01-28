package org.wltea.analyzer.conndb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by qindongliang on 2015/12/25.
 */
public class RedisHelper {

    static  Jedis jedis=new Jedis("192.168.1.182",6379);

    public static Logger log= LoggerFactory.getLogger(RedisHelper.class);
    /***
     * 根据一个key读取对应的的redis里面的词库
     * @param key
     * @return
     */
    public static String  getKey(String key){
        log.info("从redis加载,key:{}",key);
        Set<String> sets=jedis.smembers(key);
        StringBuffer sb=new StringBuffer();
        for(String dic:sets){
            sb.append(dic).append("\n");
        }
        sb.deleteCharAt(sb.toString().length()-1);
        return sb.toString();
    }

    public static void addList(String key, Collection<String> datas){
        try {
            if (datas instanceof List) {
                jedis.rpush(key, datas.toArray(new String[]{}));
            } else {
                jedis.sadd(key,datas.toArray(new String[]{}));
            }
            log.info("添加成功!");
        }catch (Exception e){
            log.error("向redis添加数据异常！{}",e);
        }
    }

    public static  void deleteKey(String key){
        jedis.del(key);
        log.info("删除成功！");
    }



    public static void close(){
        jedis.close();
    }


}
