package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.ComboBox;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;

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
    }

}
