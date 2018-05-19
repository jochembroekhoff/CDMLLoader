package nl.jochembroekhoff.cdmlloader.defaulthandlers.component;

import com.mrcrayfish.device.api.app.component.Spinner;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent(type = "Spinner")
public class HandlerSpinner extends CdmlComponentHandler {
    @Override
    public Spinner createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft())
            return null;

        return CdmlComponentHandler.doDefaultComponentProcessing(meta,
                new Spinner(meta.getLeft(), meta.getTop()));
    }
}
