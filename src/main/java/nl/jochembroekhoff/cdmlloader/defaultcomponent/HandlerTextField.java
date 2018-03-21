package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.IIcon;
import com.mrcrayfish.device.api.app.component.TextField;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import org.xml.sax.Attributes;

import java.awt.*;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent("TextField")
public class HandlerTextField implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft() || meta.getAttrWidth().isEmpty())
            return null;

        TextField txt = new TextField(meta.getLeft(), meta.getTop(), meta.getWidth());

        if (meta.hasText())
            txt.setText(meta.getText());

        String placeholder = meta.getCdmlHandler().getI18nValue(meta.getElement(), "placeholder");
        if (!placeholder.isEmpty())
            txt.setPlaceholder(placeholder);

        String focused = meta.getElement().getAttribute("focused");
        if (!focused.isEmpty())
            txt.setFocused(Boolean.parseBoolean(focused));

        String padding = meta.getElement().getAttribute("padding");
        if (!padding.isEmpty())
            txt.setPadding(Integer.parseInt(padding));

        String editable = meta.getElement().getAttribute("editable");
        if (!editable.isEmpty())
            txt.setEditable(Boolean.parseBoolean(editable));

        String wrapText = meta.getElement().getAttribute("wrapText");
        if (!wrapText.isEmpty())
            txt.setWrapText(Boolean.parseBoolean(wrapText));

        /* Colors */

        Color textColor = meta.getCdmlHandler().getColorFromColorScheme(meta, "textColor", "text");
        if (textColor != null)
            txt.setTextColor(textColor);

        Color backgroundColor = meta.getCdmlHandler().getColorFromColorScheme(meta, "backgroundColor", "background");
        if (backgroundColor != null)
            txt.setBackgroundColor(backgroundColor);

        Color borderColor = meta.getCdmlHandler().getColorFromColorScheme(meta, "borderColor");
        if (borderColor != null)
            txt.setBorderColor(borderColor);

        IIcon icon = meta.getCdmlHandler().getIcon(meta);
        if (icon != null)
            txt.setIcon(icon);

        return CdmlComponentHandler.doDefaultProcessing(meta, txt);
    }
}
