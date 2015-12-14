package com.developerphil.adbidea.test;

public class Holder<T> {

    private T value;

    public Holder() {

    }

    public Holder(T defaultValue) {
        value = defaultValue;
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }
}
