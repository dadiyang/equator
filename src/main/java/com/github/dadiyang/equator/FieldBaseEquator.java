package com.github.dadiyang.equator;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 基于属性的比对器
 *
 * @author dadiyang
 * @date 2018/11/22
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
        List<FieldInfo> diffField = new LinkedList<>();
        Object obj = first == null ? second : first;
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 索引时间只在ES里有，不用判断
            if (Objects.equals("indexedTime", field.getName())) {
                continue;
            }
            String fieldName = field.getName();
            try {
                field.setAccessible(true);
                Object firstVal = first == null ? null : field.get(first);
                Object secondVal = second == null ? null : field.get(second);
                FieldInfo fieldInfo = new FieldInfo(fieldName, field.getType(), firstVal, secondVal);
                boolean eq = isFieldEquals(fieldInfo);
                if (!eq) {
                    diffField.add(fieldInfo);
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("获取属性进行比对发生异常: " + fieldName, e);
            }
        }
        return diffField;
    }
}
