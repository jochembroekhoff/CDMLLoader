package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.Text;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;

import java.awt.*;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent(type = "Text")
public class HandlerText implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft() || meta.getAttrWidth().isEmpty())
            return null;

        Text txt = new Text(meta.getText(), meta.getLeft(), meta.getTop(), meta.getWidth());

        Color textColor = meta.getCdmlHandler().getColorFromColorScheme(meta, "textColor", "text");
        if (textColor != null)
            txt.setTextColor(textColor);

        return CdmlComponentHandler.doDefaultProcessing(meta, txt);
    }
}
