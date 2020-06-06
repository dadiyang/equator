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
    private Object obj1;
    private Object obj2;
    private boolean equal;
    private List<FieldInfo> expectDiffField;
    private String message;

    @Parameterized.Parameters
    public static List<Object[]> getParams() {
        List<Object[]> ps = new LinkedList<>();
        Object[] params = new Object[6];
        Equator fieldBaseEquator = new FieldBaseEquator();
        Equator getterBaseEquator = new GetterBaseEquator();

        // 测试 expired 属性
        params[0] = getterBaseEquator;
        params[1] = new User(1, "yang", new Date(System.currentTimeMillis() - 1000), new String[]{"program", "coding"});
        params[2] = new User(1, "yang", new Date(System.currentTimeMillis() + 86400000), new String[]{"program", "coding"});
        params[3] = false;
        // 使用 getter 时会比对是否过期
        params[4] = Collections.singletonList(new FieldInfo("expired", boolean.class, true, false));
        params[5] = "测试 expired 属性，使用 getter 时会比对是否过期";
        ps.add(params);

        // 测试 expireTime 属性不一致
        params = new Object[6];
        params[0] = fieldBaseEquator;
        Date exp1 = new Date(System.currentTimeMillis() - 1000);
        Date exp2 = new Date(System.currentTimeMillis() + 86400000);
        params[1] = new User(2, "yang", exp1, new String[]{"program", "coding"});
        params[2] = new User(2, "yang", exp2, new String[]{"program", "coding"});
        params[3] = false;
        params[4] = Collections.singletonList(new FieldInfo("expireTime", Date.class, exp1, exp2));
        params[5] = "测试 expireTime 属性不一致";
        ps.add(params);

        // 测试完全相等的情况
        params = new Object[6];
        params[0] = fieldBaseEquator;
        exp1 = new Date(System.currentTimeMillis() + 120000);
        exp2 = new Date(System.currentTimeMillis() + 120000);
        params[1] = new User(3, "yang", exp1, new String[]{"program", "coding"});
        params[2] = new User(3, "yang", exp2, new String[]{"program", "coding"});
        params[3] = true;
        params[4] = Collections.emptyList();
        params[5] = "测试完全相等的情况";
        ps.add(params);

        // 测试属性不同的情况
        String[] hobby1 = new String[]{"program", "coding"};
        String[] hobby2 = new String[]{"program", "play"};
        params = new Object[6];
        params[0] = fieldBaseEquator;
        params[1] = new User(4, "dadi", new Date(), hobby1);
        params[2] = new User(4, "yang", new Date(), hobby2);
        params[3] = false;
        params[4] = Arrays.asList(new FieldInfo("username", String.class, "dadi", "yang"),
                new FieldInfo("hobbies", String[].class, hobby1, hobby2));
        ps.add(params);

        // 测试属性不同的情况，因为getter方法不会获取 expireTime,所以只要没有过期，则这个属性不会进行比较
        params = new Object[6];
        params[0] = getterBaseEquator;
        params[1] = new User(5, "dadi", new Date(System.currentTimeMillis() + 10000), hobby1);
        params[2] = new User(5, "yang", new Date(System.currentTimeMillis() + 86400000), hobby2);
        params[3] = false;
        params[4] = Arrays.asList(new FieldInfo("username", String.class, "dadi", "yang"),
                new FieldInfo("hobbies", String[].class, hobby1, hobby2));
        params[5] = "测试属性不同的情况，因为getter方法不会获取 expireTime,所以只要没有过期，则这个属性不会进行比较";
        ps.add(params);

        // 测试有一个对象为空的情况
        params = new Object[6];
        params[0] = fieldBaseEquator;
        params[1] = null;
        params[2] = new User(6L, "yang", exp2, hobby2);
        params[3] = false;
        params[4] = Arrays.asList(new FieldInfo("id", null, long.class, null, 6L),
                new FieldInfo("username", null, String.class, null, "yang"),
                new FieldInfo("expireTime", null, Date.class, null, exp2),
                new FieldInfo("hobbies", null, String[].class, null, hobby2));
        params[5] = "测试 fieldBaseEquator 有一个对象为空的情况";
        ps.add(params);

        // 测试有一个对象为空的情况
        params = new Object[6];
        params[0] = getterBaseEquator;
        params[1] = null;
        params[2] = new User(6L, "yang", exp2, hobby2);
        params[3] = false;
        params[4] = Arrays.asList(new FieldInfo("id", null, long.class, null, 6L),
                new FieldInfo("username", null, String.class, null, "yang"),
                new FieldInfo("hobbies", null, String[].class, null, hobby2),
                new FieldInfo("expired", null, boolean.class, null, false));
        params[5] = "测试 getterBaseEquator 有一个对象为空的情况";
        ps.add(params);

        // 测试排除字段
        Date now = new Date();
        params = new Object[6];
        params[0] = new GetterBaseEquator(null, Arrays.asList("id", "username"));
        params[1] = new User(0, "noteq", now, new String[]{"program", "coding"});
        params[2] = new User(1, "yang", now, new String[]{"program", "coding"});
        params[3] = true;
        params[4] = Collections.emptyList();
        params[5] = "测试排除字段";
        ps.add(params);

        // 测试只包含特定字段
        params = new Object[6];
        params[0] = new GetterBaseEquator(Collections.singletonList("expireTime"), null);
        params[1] = new User(0, "noteq2", now, new String[]{"program", "coding"});
        params[2] = new User(1, "yang", now, new String[]{"program", "coding"});
        params[3] = true;
        params[4] = Collections.emptyList();
        params[5] = "测试只包含特定字段";
        ps.add(params);

        // 测试两个不同类型的对象的比对，只比对两个类共有的字段
        params = new Object[6];
        params[0] = new FieldBaseEquator(true);
        params[1] = new User(1, "noteq2", now, new String[]{"program", "coding"});
        params[2] = new UserDTO(1, "noteq2", now, new String[]{"program", "coding"}, "uniq");
        params[3] = true;
        params[4] = Collections.emptyList();
        params[5] = "FieldBaseEquator 测试两个不同类型的对象的比对，只比对两个类共有的字段";
        ps.add(params);

        // 测试两个不同类型的对象的比对，只比对两个类共有的字段
        params = new Object[6];
        params[0] = new GetterBaseEquator(true);
        params[1] = new User(1, "noteq2", now, new String[]{"program", "coding"});
        params[2] = new UserDTO(1, "noteq2", now, new String[]{"program", "coding"}, "uniq");
        params[3] = true;
        params[4] = Collections.emptyList();
        params[5] = "测试两个不同类型的对象的比对，只比对两个类共有的字段";
        ps.add(params);

        // 测试两个不同类型的对象的比对，比对两个类的所有字段
        params = new Object[6];
        params[0] = new FieldBaseEquator(false);
        params[1] = new User(1, "noteq2", now, new String[]{"program", "coding"});
        params[2] = new UserDTO(1, "noteq2", now, new String[]{"program", "coding"}, "uniq");
        params[3] = false;
        FieldInfo diff = new FieldInfo("uniqField", null, String.class);
        diff.setSecondVal("uniq");
        params[4] = Collections.singletonList(diff);
        params[5] = "FieldBaseEquator 测试两个不同类型的对象的比对，比对两个类的所有字段";
        ps.add(params);

        // 测试两个不同类型的对象的比对，比对两个类的所有字段
        params = new Object[6];
        params[0] = new GetterBaseEquator(false);
        params[1] = new User(1, "noteq2", now, new String[]{"program", "coding"});
        params[2] = new UserDTO(1, "noteq2", now, new String[]{"program", "coding"}, "uniq");
        params[3] = false;
        params[4] = Collections.singletonList(diff);
        params[5] = "测试两个不同类型的对象的比对，比对两个类的所有字段";
        ps.add(params);
        return ps;
    }

    public EquatorTest(Equator equator, Object obj1, Object obj2, boolean equal, List<FieldInfo> expectDiffField, String message) {
        this.equator = equator;
        this.obj1 = obj1;
        this.obj2 = obj2;
        this.equal = equal;
        this.expectDiffField = expectDiffField;
        this.message = message;
    }

    @Test
    public void isEquals() {
        assertEquals("与预期结果不一致:" + message, equal, equator.isEquals(obj1, obj2));
    }

    @Test
    public void getDiffFields() {
        List<FieldInfo> fields = equator.getDiffFields(obj1, obj2);
        if (expectDiffField.isEmpty()) {
            assertTrue("有不同的属性出现: " + fields + message, fields.isEmpty());
        }
        fields.sort(getStringComparator());
        expectDiffField.sort(getStringComparator());
        System.out.println(Arrays.toString(expectDiffField.toArray()) + " " + Arrays.toString(fields.toArray()));
        assertArrayEquals("不等的属性与预期不一致: " + message, expectDiffField.toArray(), fields.toArray());
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
        equator.isEquals(obj1, obj2);
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