package nl.jochembroekhoff.cdmlloader.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A CDML component. Used to apply metadata to a CdmlComponentHandler.
 *
 * @author Jochem Broekhoff
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CdmlComponent {
    /**
     * Component type.
     * This should start with an uppercase letter and may be followed by any valid XML tag character(s).
     *
     * @return
     */
    String type();

    /**
     * Component namespace. No namespace given means global registration.
     * It is recommended to set the value of the namespace equal to the mod id.
     *
     * @return
     */
    String namespace() default "";
}
