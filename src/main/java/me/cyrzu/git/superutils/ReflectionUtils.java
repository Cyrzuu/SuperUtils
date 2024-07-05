package me.cyrzu.git.superutils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@UtilityClass
public class ReflectionUtils {

    public static Class<?> getClass(@NotNull String path, @NotNull String name) {
        return getClass(path + "." + name);
    }

    public static Class<?> getInnerClass(@NotNull String path, @NotNull String name) {
        return getClass(path + "$" + name);
    }

    @NotNull
    private static Class<?> getClass(@NotNull String path) {
        try {
            return Class.forName(path);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static Constructor<?> getConstructor(@NotNull Class<?> clazz, Class<?>... types) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(types);
            constructor.setAccessible(true);
            return constructor;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(@NotNull Method method, @Nullable Object obj, Class<T> clazz, @Nullable Object... args) {
        try {
            return (T) invokeMethod(method, obj, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static Object invokeMethod(@NotNull Method method, @Nullable Object obj, @Nullable Object... args) {
        method.setAccessible(true);
        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static Object invokeConstructor(@NotNull Constructor<?> constructor, Object... obj) {
        try {
            return constructor.newInstance(obj);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static Method getMethod(@NotNull Class<?> clazz, @NotNull String fieldName, @NotNull Class<?>... o) {
        try {
            return clazz.getDeclaredMethod(fieldName, o);
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            return superClass == null ? null : getMethod(superClass, fieldName);
        }
    }

    @Nullable
    public static Field getField(@NotNull Class<?> clazz, @NotNull String fieldName, boolean accessible) {
        try {
            Field field = clazz.getField(fieldName);
            if(accessible) {
                field.setAccessible(true);
            }

            return field;
        } catch (Exception ignore) {
            return null;
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(@NotNull Field field, @NotNull Object object, @NotNull Class<T> clazz) {
        try {
            return (T) field.get(object);
        } catch (Exception e) {
            return null;
        }
    }

}