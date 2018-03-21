package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.Inventory;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import org.xml.sax.Attributes;

import java.awt.*;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent("Inventory")
public class HandlerInventory implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft())
            return null;

        Inventory inv = new Inventory(meta.getLeft(), meta.getTop());

        Color selectedColor = meta.getCdmlHandler().getColorFromColorScheme(meta, "selectedColor", "itemBackground");
        if (selectedColor != null)
            inv.setSelectedColor(selectedColor.getRGB());

        Color hoverColor = meta.getCdmlHandler().getColorFromColorScheme(meta, "hoverColor", "itemHighlight");
        if (hoverColor != null)
            inv.setHoverColor(hoverColor.getRGB());

        return CdmlComponentHandler.doDefaultProcessing(meta, inv);
    }
}
