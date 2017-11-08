package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.TextArea;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;

import java.awt.*;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent("TextArea")
public class HandlerTextArea implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft() || !meta.hasWidthAndHeight())
            return null;

        TextArea txt = new TextArea(meta.getLeft(), meta.getTop(), meta.getWidth(), meta.getHeight());

        if (meta.hasText())
            txt.setText(meta.getText());

        String placeholder = meta.getCdmlHandler().getI18nValue(meta.getAttributes(), "placeholder");
        if (placeholder != null)
            txt.setPlaceholder(placeholder);

        String focused = meta.getAttributes().getValue("focused");
        if (focused != null)
            txt.setFocused(Boolean.parseBoolean(focused));

        String padding = meta.getAttributes().getValue("padding");
        if (padding != null)
            txt.setPadding(Integer.parseInt(padding));

        String editable = meta.getAttributes().getValue("editable");
        if (editable != null)
            txt.setEditable(Boolean.parseBoolean(editable));

        String textColour = meta.getAttributes().getValue("textColour");
        if (textColour != null)
            txt.setTextColour(Color.decode(textColour));

        String backgroundColour = meta.getAttributes().getValue("backgroundColour");
        if (backgroundColour != null)
            txt.setBackgroundColour(Color.decode(backgroundColour));

        String borderColour = meta.getAttributes().getValue("borderColour");
        if (borderColour != null)
            txt.setBorderColour(Color.decode(borderColour));

        return CdmlComponentHandler.doDefaultProcessing(meta, txt);
    }
}
