package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.ItemList;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import nl.jochembroekhoff.cdmlloader.util.XMLUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.awt.*;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent("ItemList")
public class HandlerItemList implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft() || meta.getAttrWidth().isEmpty())
            return null;

        String visibleItems = meta.getElement().getAttribute("visibleItems");
        String showAll = meta.getElement().getAttribute("showAll");

        if (visibleItems.isEmpty())
            return null;

        ItemList il;
        if (!showAll.isEmpty())
            il = new ItemList(meta.getLeft(), meta.getTop(), meta.getWidth(), Integer.parseInt(visibleItems), Boolean.parseBoolean(showAll));
        else
            il = new ItemList(meta.getLeft(), meta.getTop(), meta.getWidth(), Integer.parseInt(visibleItems));

        String selectedIndex = meta.getElement().getAttribute("selectedIndex");
        if (!selectedIndex.isEmpty())
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

        /* Items */
        XMLUtil.iterateChildren(meta.getElement(), Node.ELEMENT_NODE, node -> {
            Element element = (Element) node;
            if (element.getTagName().equals("items")) {
                XMLUtil.iterateChildren(element, Node.ELEMENT_NODE, itemNode -> {
                    Element itemElement = (Element)itemNode;
                    il.addItem(meta.getCdmlHandler().getI18nValue(itemElement.getTextContent()));
                });
            }
        });

        return CdmlComponentHandler.doDefaultProcessing(meta, il);
    }
}
