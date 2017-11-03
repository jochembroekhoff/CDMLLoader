package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.CheckBox;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;

@CdmlComponent("CheckBox")
public class HandlerCheckBox implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft())
            return null;

        CheckBox cb = new CheckBox(meta.getAttributes().getValue("name"), meta.getLeft(), meta.getTop());

        String selected = meta.getAttributes().getValue("selected");
        if (selected != null)
            cb.setSelected(Boolean.parseBoolean(selected));

        return CdmlComponentHandler.doDefaultProcessing(meta, cb);
    }
}
