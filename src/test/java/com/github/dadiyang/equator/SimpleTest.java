package com.github.dadiyang.equator;

import org.junit.Test;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimpleTest {
    @Test
    public void includeTest() {
        Date now = new Date();
        User user1 = new User(0, "noteq2", now, new String[]{"program", "coding"});
        User user2 = new User(1, "yang", now, new String[]{"program2", "coding2"});
        Equator equator = new GetterBaseEquator(Collections.singletonList("expireTime"), null);
        List<FieldInfo> infos = equator.getDiffFields(user1, user2);
        assertEquals(0, infos.size());
        assertTrue(equator.isEquals(user1, user2));
    }
}
