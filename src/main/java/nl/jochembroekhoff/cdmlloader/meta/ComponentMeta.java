package nl.jochembroekhoff.cdmlloader.meta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nl.jochembroekhoff.cdmlloader.handler.CDMLDocumentHandler;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jochem Broekhoff
 */
@AllArgsConstructor
public class ComponentMeta {

    @Getter
    CDMLDocumentHandler cdmlHandler;

    @Getter
    String modId;

    /* Component element */
    @Getter
    Element element;

    /* ID */
    @Getter
    String attrId;

    public boolean hasId() {
        return !getAttrId().isEmpty();
    }

    public String getId() {
        if (!hasId())
            return "";
        return getAttrId();
    }

    /* TOP & LEFT */

    @Getter
    String attrTop;
    @Getter
    String attrLeft;

    public boolean hasTopAndLeft() {
        return !getAttrTop().isEmpty()
                && !getAttrLeft().isEmpty();
    }

    public int getTop() {
        if (getAttrTop().isEmpty())
            return 0;
        return Integer.parseInt(getAttrTop());
    }

    public int getLeft() {
        if (getAttrLeft().isEmpty())
            return 0;
        return Integer.parseInt(getAttrLeft());
    }

    /* WIDTH & HEIGHT */

    @Getter
    @Setter
    String attrWidth;
    @Getter
    @Setter
    String attrHeight;

    public boolean hasWidthAndHeight() {
        return !getAttrWidth().isEmpty()
                && !getAttrHeight().isEmpty();
    }

    public int getWidth() {
        if (getAttrWidth().isEmpty())
            return 0;
        return Integer.parseInt(getAttrWidth());
    }

    public int getHeight() {
        if (getAttrHeight().isEmpty())
            return 0;
        return Integer.parseInt(getAttrHeight());
    }

    /* ENABLED & VISIBLE */

    @Getter
    String attrEnabled;
    @Getter
    String attrVisible;

    public boolean getEnabled() {
        if (getAttrEnabled().isEmpty())
            return true;
        return Boolean.parseBoolean(getAttrEnabled());
    }

    public boolean getVisible() {
        if (getAttrVisible().isEmpty())
            return true;
        return Boolean.parseBoolean(getAttrVisible());
    }

    /* TEXT */

    @Getter
    String text;

    public boolean hasText() {
        return !getText().isEmpty();
    }

    /* ICON */

    @Getter
    String iconName;
    @Getter
    @Setter
    String iconSet;

    /* CUSTOM PROPERTIES */
    @Getter
    Map<String, Object> customProperties = new HashMap<>();

}
