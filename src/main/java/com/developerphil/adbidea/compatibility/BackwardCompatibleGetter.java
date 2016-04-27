package com.developerphil.adbidea.compatibility;

import org.joor.ReflectException;

/**
 * Abstracts the logic to call the current implementation and fall back on reflection for previous versions
 */
public abstract class BackwardCompatibleGetter<T> {

    public final T get() {
        try {
            return getCurrentImplementation();
        } catch (LinkageError error) {
            return getPreviousImplementation();
        } catch (Throwable e) {
            if (isReflectiveException(e)) {
                return getPreviousImplementation();
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean isReflectiveException(Throwable t) {
        return t instanceof ClassNotFoundException ||
                t instanceof NoSuchFieldException ||
                t instanceof LinkageError ||
                t instanceof NoSuchMethodException ||
                t instanceof ReflectException
                ;
    }

    protected abstract T getCurrentImplementation() throws Throwable;

    protected abstract T getPreviousImplementation();
}
