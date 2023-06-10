/**
 * 
 */
package com.github.jerryxia.actuator.ui.support.json;

/**
 * @author guqk
 *
 */
public interface JsonComponentProvider {
    String toJson(Object object);

    <T> T fromJson(String jsonString, Class<T> clazz);
}
