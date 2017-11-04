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
     * Component type. Usually so
     * @return
     */
    String value();
}
