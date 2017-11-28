package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.ItemList;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ApplicationMeta;
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

        /* Colours */
        String backgroundColour = meta.getAttributes().getValue("backgroundColour");
        String borderColour = meta.getAttributes().getValue("borderColour");
        String textColour = meta.getAttributes().getValue("textColour");
        if (backgroundColour != null)
            il.setBackgroundColour(Color.decode(backgroundColour));
        if (borderColour != null)
            il.setBorderColour(Color.decode(borderColour));
        if (textColour != null)
            il.setTextColour(Color.decode(textColour));

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
            ((ItemList) component).addItem(chars);
        }
    }
}
