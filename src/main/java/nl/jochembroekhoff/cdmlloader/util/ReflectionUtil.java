package nl.jochembroekhoff.cdmlloader.util;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

/**
 * Utility methods for reflection.
 * Used from: https://github.com/Choonster-Minecraft-Mods/TestMod3/blob/b3d71dfddf7f212f0d86ef36e6ae1d06b8493ebc/src/main/java/choonster/testmod3/util/ReflectionUtil.java
 *
 * @author Choonster
 */
public class ReflectionUtil {

    /**
     * Get a {@link MethodHandle} for a field's getter.
     *
     * @param clazz      The class
     * @param fieldNames The possible names of the field
     * @return The MethodHandle
     */
    public static MethodHandle findFieldGetter(Class<?> clazz, String... fieldNames) {
        final Field field = ReflectionHelper.findField(clazz, fieldNames);

        try {
            return MethodHandles.lookup().unreflectGetter(field);
        } catch (IllegalAccessException e) {
            throw new ReflectionHelper.UnableToAccessFieldException(fieldNames, e);
        }
    }

}