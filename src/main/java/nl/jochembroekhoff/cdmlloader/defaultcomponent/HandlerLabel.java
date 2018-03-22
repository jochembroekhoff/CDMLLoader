package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.Label;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;

import java.awt.*;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent(type = "Label")
public class HandlerLabel implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft())
            return null;

        Label lbl = new Label(meta.getText(), meta.getLeft(), meta.getTop());

        Color textColor = meta.getCdmlHandler().getColorFromColorScheme(meta, "textColor", "text");
        if (textColor != null)
            lbl.setTextColor(textColor);

        return CdmlComponentHandler.doDefaultProcessing(meta, lbl);
    }
}
