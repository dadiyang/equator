package com.github.dadiyang.equator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * 原始数据类型比对参数化测试
 *
 * @author dadiyang
 * @since 2019/3/24
 */
@RunWith(Parameterized.class)
public class EquatorPrimitiveTest {
    private Object first;
    private Object second;
    private Equator equator;
    private boolean expectEq;
    private List<FieldInfo> expectDiffField;

    public EquatorPrimitiveTest(Object first, Object second, Equator equator, boolean expectEq, List<FieldInfo> expectDiffField) {
        this.first = first;
        this.second = second;
        this.equator = equator;
        this.expectEq = expectEq;
        this.expectDiffField = expectDiffField;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        List<Object[]> ps = new LinkedList<>();
        FieldBaseEquator fieldBaseEquator = new FieldBaseEquator();
        GetterBaseEquator getterBaseEquator = new GetterBaseEquator();
        ps.add(new Object[]{1, 2, fieldBaseEquator, false, Collections.singletonList(new FieldInfo(Integer.class.getSimpleName(), Integer.class, 1, 2))});
        ps.add(new Object[]{1, 2, getterBaseEquator, false, Collections.singletonList(new FieldInfo(Integer.class.getSimpleName(), Integer.class, 1, 2))});

        ps.add(new Object[]{1, 1, fieldBaseEquator, true, Collections.emptyList()});
        ps.add(new Object[]{1, 1, getterBaseEquator, true, Collections.emptyList()});

        ps.add(new Object[]{new String("1"), new String("12"), fieldBaseEquator, false, Collections.singletonList(new FieldInfo(String.class.getSimpleName(), String.class, "1", "12"))});
        ps.add(new Object[]{new String("1"), new String("12"), getterBaseEquator, false, Collections.singletonList(new FieldInfo(String.class.getSimpleName(), String.class, "1", "12"))});

        ps.add(new Object[]{new String("1"), new String("1"), fieldBaseEquator, true, Collections.emptyList()});
        ps.add(new Object[]{new String("1"), new String("1"), getterBaseEquator, true, Collections.emptyList()});
        return ps;
    }

    @Test
    public void testPrimitive() {
        assertEquals(expectEq, equator.isEquals(first, second));
        assertEquals(expectDiffField, equator.getDiffFields(first, second));
    }
}
