package com.group3.tool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author group3
 * @date 2024/04
 */
public class OnlyOneUserSingle<K,V> {
    private volatile Map<K,V> map = new ConcurrentHashMap<>();
    private static class Single{
        private static final OnlyOneUserSingle onlyOneUserSingle = new OnlyOneUserSingle();
    }
    private OnlyOneUserSingle(){}
    public static OnlyOneUserSingle getSingle(){
        return Single.onlyOneUserSingle;
    }
    public void setMap(K k,V v){
        map.put(k, v);
    }
    public Map<K,V> getMap(){
        return map;
    }
}
