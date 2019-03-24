package com.github.dadiyang.equator;

import java.util.*;

/**
 * 对比器抽象类
 *
 * @author dadiyang
 * date 2018/11/22
 */
public abstract class AbstractEquator implements Equator {
    private static final List<Class<?>> WRAPPER = Arrays.asList(Byte.class, Short.class,
            Integer.class, Long.class, Float.class, Double.class, Character.class,
            Boolean.class, String.class);

    /**
     * 只要没有不相等的属性，两个对象就全相等
     *
     * @param first  对象1
     * @param second 对象2
     * @return 两个对象是否全相等
     */
    @Override
    public boolean isEquals(Object first, Object second) {
        List<FieldInfo> diff = getDiffFields(first, second);
        return diff == null || diff.isEmpty();
    }

    /**
     * 对比两个对象的指定属性是否相等，默认为两个对象是否 equals
     * <p>
     * 子类可以通过覆盖此方法对某些特殊属性进行比对
     *
     * @param fieldInfo 当前比对属性信息
     * @return 属性是否相等
     */
    protected boolean isFieldEquals(FieldInfo fieldInfo) {
        return nullableEquals(fieldInfo.getFirstVal(), fieldInfo.getSecondVal());
    }

    /**
     * 如果原始数据类型的对象则直接进行比对
     *
     * @param first  对象1
     * @param second 对象2
     * @return 不同的字段信息，相等返回空集，不等则 FieldInfo 的字段名为对象的类型名称
     */
    protected List<FieldInfo> comparePrimitive(Object first, Object second) {
        Object obj = first == null ? second : first;
        Class<?> clazz = obj.getClass();
        boolean eq = Objects.equals(first, second);
        if (eq) {
            return Collections.emptyList();
        } else {
            // 不等的字段名称使用类的名称
            return Collections.singletonList(new FieldInfo(clazz.getSimpleName(), clazz, first, second));
        }
    }

    /**
     * 判断是否为原始数据类型
     *
     * @param first  对象1
     * @param second 对象2
     * @return 是否为原始数据类型
     */
    protected boolean isPrimitive(Object first, Object second) {
        Object obj = first == null ? second : first;
        Class<?> clazz = obj.getClass();
        return clazz.isPrimitive() || WRAPPER.contains(clazz);
    }

    private boolean nullableEquals(Object first, Object second) {
        if (first instanceof Collection
                && second instanceof Collection) {
            // 如果两个都是集合类型，尝试转换为数组再进行深度比较
            return Objects.deepEquals(((Collection) first).toArray(), ((Collection) second).toArray());
        }
        return Objects.deepEquals(first, second);
    }

}
