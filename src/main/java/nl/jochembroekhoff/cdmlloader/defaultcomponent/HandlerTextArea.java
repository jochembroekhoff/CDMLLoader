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

        Color textColor = meta.getCdmlHandler().getColorFromColorScheme(meta, "textColor", "text");
        if (textColor != null)
            txt.setTextColor(textColor);

        Color backgroundColor = meta.getCdmlHandler().getColorFromColorScheme(meta, "backgroundColor", "background");
        if (backgroundColor != null)
            txt.setBackgroundColor(backgroundColor);

        Color borderColor = meta.getCdmlHandler().getColorFromColorScheme(meta, "borderColor");
        if (borderColor != null)
            txt.setBorderColor(borderColor);

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
