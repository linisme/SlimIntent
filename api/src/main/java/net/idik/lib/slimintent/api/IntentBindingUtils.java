package net.idik.lib.slimintent.api;

import android.content.Intent;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by linshuaibin on 2017/7/11.
 */

public class IntentBindingUtils {

    public interface ITypeDataExtrator {
        Object extrat(Intent intent, String key, String type, Object defaultValue);
    }

    private static HashMap<String, ITypeDataExtrator> extratorMap = new HashMap<>();

    static {
        extratorMap.put("java.lang.Integer", new ITypeDataExtrator() {
            @Override
            public Object extrat(Intent intent, String key, String type, Object defaultValue) {
                if (defaultValue == null) {
                    defaultValue = 0;
                }
                return intent.getIntExtra(key, (int) defaultValue);
            }
        });
        extratorMap.put("java.lang.Float", new ITypeDataExtrator() {
            @Override
            public Object extrat(Intent intent, String key, String type, Object defaultValue) {
                if (defaultValue == null) {
                    defaultValue = 0;
                }
                return intent.getFloatExtra(key, (int) defaultValue);
            }
        });
        extratorMap.put("java.lang.Double", new ITypeDataExtrator() {
            @Override
            public Object extrat(Intent intent, String key, String type, Object defaultValue) {
                if (defaultValue == null) {
                    defaultValue = 0;
                }
                return intent.getDoubleExtra(key, (int) defaultValue);
            }
        });
        extratorMap.put("java.lang.Long", new ITypeDataExtrator() {
            @Override
            public Object extrat(Intent intent, String key, String type, Object defaultValue) {
                if (defaultValue == null) {
                    defaultValue = 0;
                }
                return intent.getLongExtra(key, (int) defaultValue);
            }
        });
        extratorMap.put("java.lang.Character", new ITypeDataExtrator() {
            @Override
            public Object extrat(Intent intent, String key, String type, Object defaultValue) {
                if (defaultValue == null) {
                    defaultValue = '\0';
                }
                return intent.getCharExtra(key, (Character) defaultValue);
            }
        });
        extratorMap.put("java.lang.String", new ITypeDataExtrator() {
            @Override
            public Object extrat(Intent intent, String key, String type, Object defaultValue) {
                String data = intent.getStringExtra(key);
                if (data == null) {
                    data = (String) defaultValue;
                }
                return data;
            }
        });
        extratorMap.put("java.lang.CharSequence", new ITypeDataExtrator() {
            @Override
            public Object extrat(Intent intent, String key, String type, Object defaultValue) {
                CharSequence charSequence = intent.getCharSequenceExtra(key);
                if (charSequence == null) {
                    charSequence = (CharSequence) defaultValue;
                }
                return charSequence;
            }
        });
        extratorMap.put("java.lang.Boolean", new ITypeDataExtrator() {
            @Override
            public Object extrat(Intent intent, String key, String type, Object defaultValue) {
                return intent.getBooleanExtra(key, (Boolean) defaultValue);
            }
        });
    }


    public static <T> T getExtra(Intent intent, String key, String type) {
        return getExtra(intent, key, type, null);
    }

    public static <T> T getExtra(Intent intent, String key, String type, Object defaultValue) {

        if ("int".equals(type)) {
            type = "java.lang.Integer";
        } else if ("float".equals(type)) {
            type = "java.lang.Float";
        } else if ("double".equals(type)) {
            type = "java.lang.Double";
        } else if ("long".equals(type)) {
            type = "java.lang.Long";
        } else if ("char".equals(type)) {
            type = "java.lang.Character";
        } else if ("boolean".equals(type)) {
            type = "java.lang.Boolean";
        }
        ITypeDataExtrator extrator = extratorMap.get(type);
        if (extrator != null) {
            return (T) extrator.extrat(intent, key, type, defaultValue);
        } else try {
            if (Parcelable.class.isAssignableFrom(Class.forName(type))) {
                return intent.getParcelableExtra(key);
            }
        } catch (ClassNotFoundException e) {
            try {
                int pos = type.lastIndexOf(".");
                type = type.substring(0, pos) + "$" + type.substring(pos + 1);
                if (Parcelable.class.isAssignableFrom(Class.forName(type))) {
                    return intent.getParcelableExtra(key);
                }
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        }

        return null;
    }
}
