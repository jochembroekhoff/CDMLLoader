package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.CheckBox;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import org.xml.sax.Attributes;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent("CheckBox")
public class HandlerCheckBox implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft())
            return null;

        CheckBox cb = new CheckBox(meta.getCdmlHandler().getI18nValue(meta.getAttributes(), "name"), meta.getLeft(), meta.getTop());

        String selected = meta.getAttributes().getValue("selected");
        if (selected != null)
            cb.setSelected(Boolean.parseBoolean(selected));

        String radioGroup = meta.getAttributes().getValue("radioGroup");
        if (radioGroup != null)
            cb.setRadioGroup(meta.getCdmlHandler().getRadioGroup(radioGroup));

        return CdmlComponentHandler.doDefaultProcessing(meta, cb);
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
