package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.Spinner;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import org.xml.sax.Attributes;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent("Spinner")
public class HandlerSpinner implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if(!meta.hasTopAndLeft())
            return null;

        return CdmlComponentHandler.doDefaultProcessing(meta,
                new Spinner(meta.getLeft(), meta.getTop()));
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
