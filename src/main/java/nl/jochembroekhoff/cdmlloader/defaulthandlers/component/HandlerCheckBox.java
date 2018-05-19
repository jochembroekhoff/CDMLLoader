package nl.jochembroekhoff.cdmlloader.defaulthandlers.component;

import com.mrcrayfish.device.api.app.component.CheckBox;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent(type = "CheckBox")
public class HandlerCheckBox extends CdmlComponentHandler {
    @Override
    public CheckBox createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft())
            return null;

        CheckBox cb = new CheckBox(meta.getCdmlHandler().getI18nValue(meta.getElement(), "name"), meta.getLeft(), meta.getTop());

        String selected = meta.getElement().getAttribute("selected");
        if (selected.isEmpty())
            cb.setSelected(Boolean.parseBoolean(selected));

        String radioGroup = meta.getElement().getAttribute("radioGroup");
        if (!radioGroup.isEmpty())
            cb.setRadioGroup(meta.getCdmlHandler().getRadioGroup(radioGroup));

        return CdmlComponentHandler.doDefaultComponentProcessing(meta, cb);
    }
}
