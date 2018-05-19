package nl.jochembroekhoff.cdmlloader.defaulthandlers.component;

import com.mrcrayfish.device.api.app.component.ComboBox;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;

/**
 * @author Jochem Broekhoff
 */
public abstract class HandlerComboBox {

    @CdmlComponent(type = "ComboBox-List")
    public static class List extends CdmlComponentHandler {
        @Override
        public ComboBox.List createComponent(ComponentMeta meta) {
            if (!meta.hasTopAndLeft())
                return null;

            ComboBox.List cb;
            Object[] items = new Object[]{};

            String listWidth = meta.getElement().getAttribute("listWidth");
            if (!meta.getAttrWidth().isEmpty() && !listWidth.isEmpty())
                cb = new ComboBox.List<>(meta.getLeft(), meta.getTop(), meta.getWidth(), Integer.parseInt(listWidth), items);
            else if (!meta.getAttrWidth().isEmpty())
                cb = new ComboBox.List<>(meta.getLeft(), meta.getTop(), meta.getWidth(), items);
            else
                cb = new ComboBox.List<>(meta.getLeft(), meta.getTop(), items);

            String selectedIndex = meta.getElement().getAttribute("selectedIndex");
            if (!selectedIndex.isEmpty())
                cb.setSelectedItem(Integer.parseInt(selectedIndex));

            String selectedItem = meta.getElement().getAttribute("selectedItem");
            if (!selectedItem.isEmpty())
                cb.setSelectedItem(selectedItem);

            return CdmlComponentHandler.doDefaultComponentProcessing(meta, cb);
        }

        /*
        //TODO: Process items!
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
                    items[items.length - 1] = meta.getCdmlHandler().getI18nValue(chars);
                    cbl.setItems(items);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        */
    }

}
