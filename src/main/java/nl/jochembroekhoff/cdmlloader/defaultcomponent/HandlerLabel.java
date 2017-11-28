package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.Label;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import org.xml.sax.Attributes;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent("Label")
public class HandlerLabel implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft())
            return null;

        return CdmlComponentHandler.doDefaultProcessing(meta,
                new Label(meta.getText(), meta.getLeft(), meta.getTop()));
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
