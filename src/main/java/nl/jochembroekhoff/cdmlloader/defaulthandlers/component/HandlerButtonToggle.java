package nl.jochembroekhoff.cdmlloader.defaulthandlers.component;

import com.mrcrayfish.device.api.app.component.ButtonToggle;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent(type = "ButtonToggle")
public class HandlerButtonToggle extends CdmlComponentHandler {
    @Override
    public ButtonToggle createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft())
            return null;

        ButtonToggle btnt = new ButtonToggle(meta.getLeft(), meta.getTop(), meta.getText());

        HandlerButton.processButton(meta, btnt);

        String selected = meta.getElement().getAttribute("selected");
        if (!selected.isEmpty())
            btnt.setSelected(Boolean.parseBoolean(selected));

        String radioGroup = meta.getElement().getAttribute("radioGroup");
        if (!radioGroup.isEmpty())
            btnt.setRadioGroup(meta.getCdmlHandler().getRadioGroup(radioGroup));

        return CdmlComponentHandler.doDefaultComponentProcessing(meta, btnt);
    }
}
