package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.ComboBox;
import com.mrcrayfish.device.api.app.component.ItemList;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import org.xml.sax.Attributes;

import java.lang.reflect.Field;

/**
 * @author Jochem Broekhoff
 */
public abstract class HandlerComboBox {

    @CdmlComponent("ComboBox-List")
    public static class List implements CdmlComponentHandler {
        @Override
        public Component createComponent(ComponentMeta meta) {
            if (!meta.hasTopAndLeft())
                return null;

            ComboBox.List cb;
            Object[] items = new Object[]{};

            String listWidth = meta.getAttributes().getValue("listWidth");
            if (meta.getAttrWidth() != null && listWidth != null)
                cb = new ComboBox.List<>(meta.getLeft(), meta.getTop(), meta.getWidth(), Integer.parseInt(listWidth), items);
            else if (meta.getAttrWidth() != null)
                cb = new ComboBox.List<>(meta.getLeft(), meta.getTop(), meta.getWidth(), items);
            else
                cb = new ComboBox.List<>(meta.getLeft(), meta.getTop(), items);

            String selectedIndex = meta.getAttributes().getValue("selectedIndex");
            if (selectedIndex != null)
                cb.setSelectedItem(Integer.parseInt(selectedIndex));

            return CdmlComponentHandler.doDefaultProcessing(meta, cb);
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
                ComboBox.List cbl = (ComboBox.List) component;

                try {
                    //Dirty way of adding items to a ComboBox.List
                    Field field = cbl.getClass().getDeclaredField("list");
                    field.setAccessible(true);
                    ItemList il = (ItemList) field.get(cbl);
                    Object[] items = new Object[il.getItems().size() + 1];
                    il.getItems().toArray(items);
                    items[items.length - 1] = chars;
                    cbl.setItems(items);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
