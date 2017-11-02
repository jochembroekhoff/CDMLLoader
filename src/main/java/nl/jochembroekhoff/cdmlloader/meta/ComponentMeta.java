package nl.jochembroekhoff.cdmlloader.meta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.xml.sax.Attributes;

@AllArgsConstructor
public class ComponentMeta {

    @Getter
    String modId;

    /* All attributes */
    @Getter
    Attributes attributes;

    /* ID */

    @Getter
    String attrId;

    public boolean hasId() {
        return getAttrId() != null;
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
        return getAttrTop() != null
                && getAttrLeft() != null;
    }

    public int getTop() {
        if (getAttrTop() == null)
            return 0;
        return Integer.parseInt(getAttrTop());
    }

    public int getLeft() {
        if (getAttrLeft() == null)
            return 0;
        return Integer.parseInt(getAttrLeft());
    }

    /* WIDTH & HEIGHT */

    @Getter
    String attrWidth;
    @Getter
    String attrHeight;

    public boolean hasWidthAndHeight() {
        return getAttrWidth() != null
                && getAttrHeight() != null;
    }

    public int getWidth() {
        if (getAttrWidth() == null)
            return 0;
        return Integer.parseInt(getAttrWidth());
    }

    public int getHeight() {
        if (getAttrHeight() == null)
            return 0;
        return Integer.parseInt(getAttrHeight());
    }

    /* ENABLED & VISIBLE */

    @Getter
    String attrEnabled;
    @Getter
    String attrVisible;

    public boolean getEnabled() {
        if (getAttrEnabled() == null)
            return true;
        return Boolean.parseBoolean(getAttrEnabled());
    }

    public boolean getVisible() {
        if (getAttrVisible() == null)
            return true;
        return Boolean.parseBoolean(getAttrVisible());
    }

    /* TEXT */
    public boolean hasText(){
        return getText() != null;
    }

    public String getText(){
        return getAttributes().getValue("text");
    }
}
