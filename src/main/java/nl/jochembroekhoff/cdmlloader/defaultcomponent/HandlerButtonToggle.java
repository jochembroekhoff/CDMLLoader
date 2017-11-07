package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.ButtonToggle;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent("ButtonToggle")
public class HandlerButtonToggle implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft())
            return null;

        ButtonToggle btnt = new ButtonToggle(meta.getLeft(), meta.getTop(), meta.getText());

        HandlerButton.processButton(meta, btnt);

        String selected = meta.getAttributes().getValue("selected");
        if (selected != null)
            btnt.setSelected(Boolean.parseBoolean(selected));

        return CdmlComponentHandler.doDefaultProcessing(meta, btnt);
    }
}
