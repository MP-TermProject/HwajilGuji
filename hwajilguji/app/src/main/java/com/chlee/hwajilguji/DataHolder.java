package com.chlee.hwajilguji;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DataHolder {
    private static Map<String, Object> mDataHolder = new ConcurrentHashMap<>();

    public static String putDataHolder(Object data)
    {
        String dataHolderId = UUID.randomUUID().toString();
        mDataHolder.put(dataHolderId, data);
        return dataHolderId;
    }

    public static Object popDataHolder(String key){
        Object obj = mDataHolder.get(key);
        mDataHolder.remove(key);
        return obj;
    }
}
