package nl.jochembroekhoff.cdmlloader.defaulthandlers.layout;

import com.mrcrayfish.device.api.app.Layout;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;

@CdmlComponent(type = "layout")
public class HandlerLayout extends CdmlComponentHandler {
    @Override
    public Layout createComponent(ComponentMeta meta) {
        Layout layout;
        if (meta.hasWidthAndHeight() && meta.hasTopAndLeft()) {
            layout = new Layout(meta.getLeft(), meta.getTop(), meta.getWidth(), meta.getHeight());
        } else if (meta.hasWidthAndHeight()) {
            layout = new Layout(meta.getWidth(), meta.getHeight());
        } else {
            layout = new Layout();
        }

        CdmlComponentHandler.doDefaultComponentProcessing(meta, layout);

        return CdmlComponentHandler.doDefaultLayoutProcessing(meta, layout);
    }
}
