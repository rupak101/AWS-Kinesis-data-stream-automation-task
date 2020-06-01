package com.base;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Rupak Mansingh
 * @Desc: Base class set to environment
 */

public class BaseTest {

    static {
        setEnvironment("AWS_CBOR_DISABLE", "1");
    }

    /**
     * Set the environment value as AWS_CBOR_DISABLE
     * @param key
     * @param value of the key
     * @return the list of shards from the given stream
     */
    public static void setEnvironment(String key, String value) {
        Map<String, String> newEnv = new HashMap<String, String>(System.getenv());
        newEnv.put(key, value);
        setEnvironment(newEnv);
    }

    private static void setEnvironment(Map<String, String> newEnv) {
        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
            env.putAll(newEnv);
            Field theCaseInsensitiveEnvironmentField = processEnvironmentClass
                    .getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
            cienv.putAll(newEnv);
        } catch (NoSuchFieldException e) {
            try {
                Class[] classes = Collections.class.getDeclaredClasses();
                Map<String, String> env = System.getenv();
                for (Class cl : classes) {
                    if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                        Field field = cl.getDeclaredField("m");
                        field.setAccessible(true);
                        Object obj = field.get(env);
                        Map<String, String> map = (Map<String, String>) obj;
                        map.clear();
                        map.putAll(newEnv);
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
