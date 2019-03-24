package com.github.dadiyang.equator;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 基于属性的比对器
 *
 * @author dadiyang
 * date 2018/11/22
 */
public class FieldBaseEquator extends AbstractEquator {
    /**
     * {@inheritDoc}
     */
    @Override
    public List<FieldInfo> getDiffFields(Object first, Object second) {
        if (first == second) {
            return Collections.emptyList();
        }
        // 先尝试判断是否为原始数据类型
        if (isPrimitive(first, second)) {
            return comparePrimitive(first, second);
        }
        Object obj = first == null ? second : first;
        Class<?> clazz = obj.getClass();
        List<FieldInfo> diffField = new LinkedList<>();
        // 获取所有字段
        Field[] fields = clazz.getDeclaredFields();
        // 遍历所有的字段
        for (Field field : fields) {
            String fieldName = field.getName();
            try {
                // 开启访问权限，否则获取私有字段会报错
                field.setAccessible(true);
                Object firstVal = first == null ? null : field.get(first);
                Object secondVal = second == null ? null : field.get(second);
                // 封装字段信息
                FieldInfo fieldInfo = new FieldInfo(fieldName, field.getType(), firstVal, secondVal);
                boolean eq = isFieldEquals(fieldInfo);
                if (!eq) {
                    // 记录不相等的字段
                    diffField.add(fieldInfo);
                }
            } catch (IllegalAccessException e) {
                // 只要调用了 field.setAccessible(true) 就不会报这个异常
                throw new IllegalStateException("获取属性进行比对发生异常: " + fieldName, e);
            }
        }
        return diffField;
    }


}
