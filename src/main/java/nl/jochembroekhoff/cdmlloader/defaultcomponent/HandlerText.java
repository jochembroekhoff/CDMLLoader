package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.Text;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import org.xml.sax.Attributes;

import java.awt.*;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent("Text")
public class HandlerText implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft() || meta.getAttrWidth() == null)
            return null;

        Text txt = new Text(meta.getText(), meta.getLeft(), meta.getTop(), meta.getWidth());

        Color textColour = meta.getCdmlHandler().getColourFromColourScheme(meta, "textColour", "text");
        if (textColour != null)
            txt.setTextColour(textColour);

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
