package net.idik.lib.slimintent.api;

import android.content.Intent;

/**
 * Created by linshuaibin on 2017/7/11.
 */

public class IntentBindingUtils {

    public static <T> T getExtra(Intent intent, String key, String type) {
        return getExtra(intent, key, type, null);
    }

    public static <T> T getExtra(Intent intent, String key, String type, Object defaultValue) {

        Object data = null;

        if ("int".equals(type)) {
            if (defaultValue == null) {
                defaultValue = 0;
            }
            data = intent.getIntExtra(key, (Integer) defaultValue);
        } else if ("java.lang.String".equals(type)) {
            data = intent.getStringExtra(key);
            if (data == null) {
                data = defaultValue;
            }
        }


        return (T) data;
    }
}
