package com.github.jerryxia.devutil;

import java.util.function.Consumer;

/**
 * @author Administrator
 * @date 2021/09/29
 */
public class NoOPAction<T> implements Consumer<T> {
    public static final NoOPAction INSTANCE = new NoOPAction();

    @Override
    public void accept(T t) {
        // ignore
    }
}