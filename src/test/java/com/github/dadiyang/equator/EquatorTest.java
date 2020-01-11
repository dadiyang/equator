package com.github.dadiyang.equator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static org.junit.Assert.*;

/**
 * 考虑各种情况进行参数化的单元测试
 *
 * @author dadiyang
 * date 2018/11/22
 */
@RunWith(Parameterized.class)
public class EquatorTest {
    private Equator equator;
    private User user1;
    private User user2;
    private boolean equal;
    private List<FieldInfo> expectDiffField;

    @Parameterized.Parameters
    public static List<Object[]> getParams() {
        List<Object[]> ps = new LinkedList<>();
        Object[] params = new Object[5];
        Equator fieldBaseEquator = new FieldBaseEquator();
        Equator getterBaseEquator = new GetterBaseEquator();

        // 测试 expired 属性
        params[0] = getterBaseEquator;
        params[1] = new User(1, "yang", new Date(System.currentTimeMillis() - 1000), new String[]{"program", "coding"});
        params[2] = new User(1, "yang", new Date(System.currentTimeMillis() + 86400000), new String[]{"program", "coding"});
        params[3] = false;
        // 使用 getter 时会比对是否过期
        params[4] = Collections.singletonList(new FieldInfo("expired", boolean.class, true, false));
        ps.add(params);

        // 测试 expireTime 属性不一致
        params = new Object[5];
        params[0] = fieldBaseEquator;
        Date exp1 = new Date(System.currentTimeMillis() - 1000);
        Date exp2 = new Date(System.currentTimeMillis() + 86400000);
        params[1] = new User(2, "yang", exp1, new String[]{"program", "coding"});
        params[2] = new User(2, "yang", exp2, new String[]{"program", "coding"});
        params[4] = Collections.singletonList(new FieldInfo("expireTime", Date.class, exp1, exp2));
        params[3] = false;
        ps.add(params);

        // 测试完全相等的情况
        params = new Object[5];
        params[0] = fieldBaseEquator;
        exp1 = new Date(System.currentTimeMillis() + 120000);
        exp2 = new Date(System.currentTimeMillis() + 120000);
        params[1] = new User(3, "yang", exp1, new String[]{"program", "coding"});
        params[2] = new User(3, "yang", exp2, new String[]{"program", "coding"});
        params[4] = Collections.emptyList();
        params[3] = true;
        ps.add(params);

        // 测试属性不同的情况
        String[] hobby1 = new String[]{"program", "coding"};
        String[] hobby2 = new String[]{"program", "play"};
        params = new Object[5];
        params[0] = fieldBaseEquator;
        params[1] = new User(4, "dadi", new Date(), hobby1);
        params[2] = new User(4, "yang", new Date(), hobby2);
        params[3] = false;
        params[4] = Arrays.asList(new FieldInfo("username", String.class, "dadi", "yang"),
                new FieldInfo("hobbies", String[].class, hobby1, hobby2));
        ps.add(params);

        // 测试属性不同的情况，因为getter方法不会获取 expireTime,所以只要没有过期，则这个属性不会进行比较
        params = new Object[5];
        params[0] = getterBaseEquator;
        params[1] = new User(5, "dadi", new Date(System.currentTimeMillis() + 10000), hobby1);
        params[2] = new User(5, "yang", new Date(System.currentTimeMillis() + 86400000), hobby2);
        params[3] = false;
        params[4] = Arrays.asList(new FieldInfo("username", String.class, "dadi", "yang"),
                new FieldInfo("hobbies", String[].class, hobby1, hobby2));
        ps.add(params);

        // 测试有一个对象为空的情况
        params = new Object[5];
        params[0] = fieldBaseEquator;
        params[1] = null;
        params[2] = new User(6L, "yang", exp2, hobby2);
        params[3] = false;
        params[4] = Arrays.asList(new FieldInfo("id", long.class, null, 6L),
                new FieldInfo("username", String.class, null, "yang"),
                new FieldInfo("expireTime", Date.class, null, exp2),
                new FieldInfo("hobbies", String[].class, null, hobby2));
        ps.add(params);

        // 测试有一个对象为空的情况
        params = new Object[5];
        params[0] = getterBaseEquator;
        params[1] = null;
        params[2] = new User(6L, "yang", exp2, hobby2);
        params[3] = false;
        params[4] = Arrays.asList(new FieldInfo("id", long.class, null, 6L),
                new FieldInfo("username", String.class, null, "yang"),
                new FieldInfo("hobbies", String[].class, null, hobby2),
                new FieldInfo("expired", boolean.class, null, false));
        ps.add(params);

        // 测试排除字段
        Date now = new Date();
        params = new Object[5];
        params[0] = new GetterBaseEquator(null, Arrays.asList("id", "username"));
        params[1] = new User(0, "noteq", now, new String[]{"program", "coding"});
        params[2] = new User(1, "yang", now, new String[]{"program", "coding"});
        params[3] = true;
        params[4] = Collections.emptyList();
        ps.add(params);

        // 测试只包含特定字段
        params = new Object[5];
        params[0] = new GetterBaseEquator(Collections.singletonList("expireTime"), null);
        params[1] = new User(0, "noteq2", now, new String[]{"program", "coding"});
        params[2] = new User(1, "yang", now, new String[]{"program", "coding"});
        params[3] = true;
        params[4] = Collections.emptyList();
        ps.add(params);
        return ps;
    }

    public EquatorTest(Equator equator, User user1, User user2, boolean equal, List<FieldInfo> expectDiffField) {
        this.equator = equator;
        this.user1 = user1;
        this.user2 = user2;
        this.equal = equal;
        this.expectDiffField = expectDiffField;
    }

    @Test
    public void isEquals() {
        assertEquals("与预期结果不一致", equal, equator.isEquals(user1, user2));
    }

    @Test
    public void getDiffFields() {
        List<FieldInfo> fields = equator.getDiffFields(user1, user2);
        if (expectDiffField.isEmpty()) {
            assertTrue("有不同的属性出现", fields.isEmpty());
        }
        fields.sort(getStringComparator());
        expectDiffField.sort(getStringComparator());
        System.out.println(Arrays.toString(expectDiffField.toArray()) + " " + Arrays.toString(fields.toArray()));
        assertArrayEquals("不等的属性与预期不一致", expectDiffField.toArray(), fields.toArray());
    }

    @Test
    public void name() {
        Equator equator = new GetterBaseEquator() {
            @Override
            protected boolean isFieldEquals(FieldInfo fieldInfo) {
                if ("id".equalsIgnoreCase(fieldInfo.getFieldName())) {
                    return true;
                }
                return super.isFieldEquals(fieldInfo);
            }
        };
        equator.isEquals(user1, user2);
    }

    private Comparator<? super FieldInfo> getStringComparator() {
        return new Comparator<FieldInfo>() {
            @Override
            public int compare(FieldInfo o1, FieldInfo o2) {
                return o1.getFieldName().compareTo(o2.getFieldName());
            }
        };
    }
}