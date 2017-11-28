package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.Slider;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import org.xml.sax.Attributes;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent("Slider")
public class HandlerSlider implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if(!meta.hasTopAndLeft() || meta.getAttrWidth() == null)
            return null;

        Slider sld = new Slider(meta.getLeft(), meta.getTop(), meta.getWidth());

        String percentage = meta.getAttributes().getValue("percentage");
        if (percentage != null)
            sld.setPercentage(Float.parseFloat(percentage));

        return CdmlComponentHandler.doDefaultProcessing(meta, sld);
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
