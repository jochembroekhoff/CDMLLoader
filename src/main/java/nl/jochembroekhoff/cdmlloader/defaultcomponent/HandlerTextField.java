package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import nl.jochembroekhoff.cdmlloader.CDMLLoader;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent("TextField")
public class HandlerTextField implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft() || meta.getAttrWidth() == null)
            return null;

        //TextField is a subclass of TextArea. It is basically a TextArea with a height of 15.
        if (!CDMLLoader.hasComponentHandler("TextArea"))
            return null;

        meta.setAttrHeight("15");

        return CDMLLoader.getComponentHandler("TextArea").createComponent(meta);
    }
}
