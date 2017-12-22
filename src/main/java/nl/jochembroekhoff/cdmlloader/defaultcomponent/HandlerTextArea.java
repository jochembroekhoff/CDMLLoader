package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.TextArea;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import org.xml.sax.Attributes;

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

        Color textColour = meta.getCdmlHandler().getColourFromColourScheme(meta, "textColour", "text");
        if (textColour != null)
            txt.setTextColour(textColour);

        Color backgroundColor = meta.getCdmlHandler().getColourFromColourScheme(meta, "backgroundColour", "background");
        if (backgroundColor != null)
            txt.setBackgroundColour(backgroundColor);

        Color borderColour = meta.getCdmlHandler().getColourFromColourScheme(meta, "borderColour");
        if (borderColour != null)
            txt.setBorderColour(borderColour);

        return CdmlComponentHandler.doDefaultProcessing(meta, txt);
    }

    @Override
    public void startElement(Component component, ComponentMeta meta, String qName, Attributes attributes) {

    }

    @Override
    public void endElement(Component component, ComponentMeta meta, String qName) {

    }

    @Override
    public void elementContent(Component component, ComponentMeta meta, String chars) {

    }
}
