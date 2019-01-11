package com.github.dadiyang.equator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 基于 getter 方法比对两个对象
 * <p>
 * 所有无参的 get 和 is 方法都认为是对象的属性
 *
 * @author dadiyang
 * date 2018/11/22
 */
public class GetterBaseEquator extends AbstractEquator {
    private static final String GET = "get";
    private static final String IS = "is";
    private static final String GET_IS = "get|is";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FieldInfo> getDiffFields(Object first, Object second) {
        if (first == null && second == null) {
            return Collections.emptyList();
        }
        List<FieldInfo> diffField = new LinkedList<>();
        Object obj = first == null ? second : first;
        Method[] methods = obj.getClass().getMethods();
        List<Method> getters = new LinkedList<>();
        for (Method method : methods) {
            // 获取所有 get 和 is 开头的方法
            if (method.getName().startsWith(GET)
                    || method.getName().startsWith(IS)) {
                if (method.getParameterTypes().length == 0) {
                    getters.add(method);
                }
            }
        }
        for (Method method : getters) {
            // 去掉前缀并将首字母小写
            String fieldName = uncapitalize(method.getName().replaceFirst(GET_IS, ""));
            try {
                boolean eq;
                Object firstVal = first == null ? null : method.invoke(first);
                Object secondVal = second == null ? null : method.invoke(second);
                FieldInfo fieldInfo = new FieldInfo(fieldName, method.getReturnType(), firstVal, secondVal);
                eq = isFieldEquals(fieldInfo);
                if (!eq) {
                    diffField.add(fieldInfo);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("获取属性进行比对发生异常: " + fieldName, e);
            }
        }
        return diffField;
    }

    /**
     * 来自commons-lang3包的StringUtils
     * <p>
     * 用于使首字母小写
     */
    private String uncapitalize(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        final int firstCodepoint = str.codePointAt(0);
        final int newCodePoint = Character.toLowerCase(firstCodepoint);
        if (firstCodepoint == newCodePoint) {
            return str;
        }
        final int newCodePoints[] = new int[strLen];
        int outOffset = 0;
        newCodePoints[outOffset++] = newCodePoint;
        for (int inOffset = Character.charCount(firstCodepoint); inOffset < strLen; ) {
            final int codepoint = str.codePointAt(inOffset);
            newCodePoints[outOffset++] = codepoint;
            inOffset += Character.charCount(codepoint);
        }
        return new String(newCodePoints, 0, outOffset);
    }
}
