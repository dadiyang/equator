package com.github.dadiyang.equator;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 对比器抽象类
 *
 * @author dadiyang
 * date 2018/11/22
 */
public abstract class AbstractEquator implements Equator {
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

    private boolean nullableEquals(Object first, Object second) {
        if (first instanceof Collection
                && second instanceof Collection) {
            return Objects.deepEquals(((Collection) first).toArray(), ((Collection) second).toArray());
        }
        return Objects.deepEquals(first, second);
    }

}
