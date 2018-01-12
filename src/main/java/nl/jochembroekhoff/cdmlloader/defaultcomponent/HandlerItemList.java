package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.ItemList;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import org.xml.sax.Attributes;

import java.awt.*;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent("ItemList")
public class HandlerItemList implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft() || meta.getAttrWidth() == null)
            return null;

        String visibleItems = meta.getAttributes().getValue("visibleItems");
        String showAll = meta.getAttributes().getValue("showAll");

        if (visibleItems == null)
            return null;

        ItemList il;
        if (showAll != null)
            il = new ItemList(meta.getLeft(), meta.getTop(), meta.getWidth(), Integer.parseInt(visibleItems), Boolean.parseBoolean(showAll));
        else
            il = new ItemList(meta.getLeft(), meta.getTop(), meta.getWidth(), Integer.parseInt(visibleItems));

        String selectedIndex = meta.getAttributes().getValue("selectedIndex");
        if (selectedIndex != null)
            il.setSelectedIndex(Integer.parseInt(selectedIndex));

        /* Colors */
        Color backgroundColor = meta.getCdmlHandler().getColorFromColorScheme(meta, "backgroundColor", "background");
        Color borderColor = meta.getCdmlHandler().getColorFromColorScheme(meta, "borderColor");
        Color textColor = meta.getCdmlHandler().getColorFromColorScheme(meta, "textColor", "text");
        if (backgroundColor != null)
            il.setBackgroundColor(backgroundColor);
        if (borderColor != null)
            il.setBorderColor(borderColor);
        if (textColor != null)
            il.setTextColor(textColor);

        return CdmlComponentHandler.doDefaultProcessing(meta, il);
    }

    @Override
    public void startElement(Component component, ComponentMeta meta, String qName, Attributes attributes) {
        if (qName.equals("items"))
            meta.getCustomProperties().put("processingItems", true);
    }

    @Override
    public void endElement(Component component, ComponentMeta meta, String qName) {
        if (qName.equals("items"))
            meta.getCustomProperties().put("processingItems", false);
    }

    @Override
    public void elementContent(Component component, ComponentMeta meta, String chars) {
        if ((boolean) meta.getCustomProperties().getOrDefault("processingItems", false)) {
            ((ItemList) component).addItem(meta.getCdmlHandler().getI18nValue(chars));
        }
    }
}
