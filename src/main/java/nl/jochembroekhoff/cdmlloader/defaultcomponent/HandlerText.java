package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.Text;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent("Text")
public class HandlerText implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if(!meta.hasTopAndLeft() || meta.getAttrWidth() == null)
            return null;

        return CdmlComponentHandler.doDefaultProcessing(meta,
                new Text(meta.getText(), meta.getLeft(), meta.getTop(), meta.getWidth()));
    }
}
