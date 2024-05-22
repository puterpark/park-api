package us.puter.park.util;

import java.util.List;

public class SafeCaster {

    @SuppressWarnings("unchecked")
    public static <T> List<T> castToList(Object obj) {
        if (obj instanceof List<?>) {
            return (List<T>) obj;
        } else {
            return null;
        }
    }
}
