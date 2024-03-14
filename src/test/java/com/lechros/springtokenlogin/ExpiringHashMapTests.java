package com.lechros.springtokenlogin;

import com.lechros.springtokenlogin.util.ExpiringHashMap;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.Map;

public class ExpiringHashMapTests {

    @Test
    public void get_doNotRenewOnAccess_shouldExpire() throws InterruptedException {
        Map<Integer, String> map = new ExpiringHashMap<>(500);
        map.put(1, "1");
        map.put(2, "2");
        map.put(3, "3");
        Thread.sleep(200);
        map.put(4, "4");
        Thread.sleep(200);
        Assert.notNull(map.get(1), "1 is null");
        Assert.notNull(map.get(2), "2 is null");
        Assert.notNull(map.get(3), "3 is null");
        Thread.sleep(200);
        Assert.isNull(map.get(1), "1 is not null");
        Assert.isNull(map.get(2), "2 is not null");
        Assert.isNull(map.get(3), "3 is not null");
        Assert.notNull(map.get(4), "4 is null");
        Thread.sleep(200);
        Assert.isNull(map.get(4), "4 is not null");
    }

    @Test
    public void get_doRenewOnAccess_shouldNotExpire() throws InterruptedException {
        Map<Integer, String> map = new ExpiringHashMap<>(500, true);
        map.put(1, "1");
        map.put(2, "2");
        map.put(3, "3");
        Thread.sleep(200);
        map.put(4, "4");
        Thread.sleep(200);
        Assert.notNull(map.get(1), "1 is null");
        Thread.sleep(200);
        Assert.notNull(map.get(1), "1 is not null");
        Assert.isNull(map.get(2), "2 is not null");
        Assert.isNull(map.get(3), "3 is not null");
        Assert.notNull(map.get(4), "4 is null");
        Thread.sleep(500);
        Assert.isNull(map.get(4), "4 is not null");
    }
}
