package org.main.vision.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Map;

/** Utility that recursively replaces occurrences of a string inside packet objects. */
public class PacketStringReplacer {
    public static void replaceStrings(Object obj, String from, String to) {
        replaceStrings(obj, from, to, new IdentityHashMap<>());
    }

    private static void replaceStrings(Object obj, String from, String to, Map<Object, Boolean> visited) {
        if (obj == null || from == null || from.isEmpty() || to == null || visited.containsKey(obj)) {
            return;
        }
        visited.put(obj, Boolean.TRUE);

        Class<?> cls = obj.getClass();
        if (obj instanceof String) {
            // nothing
            return;
        } else if (obj instanceof ITextComponent) {
            ITextComponent comp = (ITextComponent) obj;
            String txt = comp.getString();
            if (txt.contains(from)) {
                if (comp instanceof StringTextComponent) {
                    try {
                        Field f = StringTextComponent.class.getDeclaredField("text");
                        f.setAccessible(true);
                        f.set(comp, txt.replace(from, to));
                    } catch (Exception ignore) {}
                }
            }
        } else if (cls.isArray()) {
            int len = Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                Object item = Array.get(obj, i);
                if (item instanceof String) {
                    String s = (String) item;
                    if (s.contains(from)) Array.set(obj, i, s.replace(from, to));
                } else {
                    replaceStrings(item, from, to, visited);
                }
            }
            return;
        } else if (obj instanceof Iterable) {
            for (Object item : (Iterable<?>) obj) {
                replaceStrings(item, from, to, visited);
            }
            return;
        } else if (obj instanceof Map) {
            for (Object val : ((Map<?, ?>) obj).values()) {
                replaceStrings(val, from, to, visited);
            }
            return;
        }

        for (Field f : cls.getDeclaredFields()) {
            if ((f.getModifiers() & Modifier.STATIC) != 0) continue;
            f.setAccessible(true);
            try {
                Object value = f.get(obj);
                if (value instanceof String) {
                    String s = (String) value;
                    if (s != null && s.contains(from)) {
                        f.set(obj, s.replace(from, to));
                    }
                } else {
                    replaceStrings(value, from, to, visited);
                }
            } catch (IllegalAccessException ignored) {}
        }
    }
}
