package com.jdy.ddj.common.utils;

import org.apache.commons.lang3.ArrayUtils;

/**
 * 扩展.ArrayUtils ，实现数组转字符串功能
 * User: zhangjunhui
 * Date: 12-4-19
 * Time: 下午2:22
 */
public class ArrayUtilsExt {

    /**
     * Join an array of strings into one delimited string.
     *
     * @param array   Array of objects to join as strings.
     * @param delim   Delimiter to join strings with or null.
     * @return        Joined string.
     */
    public static String join(final Object array[], final String delim) {
        if(ArrayUtils.isEmpty(array)){
        	return "";
        }
        StringBuffer buff = new StringBuffer();
        boolean haveDelim = (delim != null);
        for (int i = 0; i < array.length; i++) {
            buff.append(array[i]);
            if (haveDelim && (i + 1)<array.length) {
                buff.append(delim);
            }
        }
        return buff.toString();
    }

    /**
     * Convert and join an array of objects into one string.
     *
     * @param array   Array of objects to join as strings.
     * @return        Converted and joined objects.
     */
    public static String join(final Object array[]) {
        return join(array, null);
    }

}
