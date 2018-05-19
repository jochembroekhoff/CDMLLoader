package nl.jochembroekhoff.cdmlloader.defaulthandlers.layout;

import com.mrcrayfish.device.api.app.ScrollableLayout;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;

@CdmlComponent(type = "ScrollableLayout")
public class HandlerScrollableLayout extends CdmlComponentHandler {
    @Override
    public ScrollableLayout createComponent(ComponentMeta meta) {
        if (!meta.hasWidthAndHeight() && !meta.hasTopAndLeft())
            return null;

        int visibleHeight = 100;
        String visibleHeightStr = meta.getElement().getAttribute("visibleHeight");
        if (!visibleHeightStr.isEmpty())
            visibleHeight = Integer.parseInt(visibleHeightStr);

        ScrollableLayout scrollable;
        if (meta.hasTopAndLeft()) {
            scrollable = new ScrollableLayout(meta.getLeft(), meta.getTop(), meta.getWidth(), meta.getHeight(), visibleHeight);
        } else {
            scrollable = new ScrollableLayout(meta.getWidth(), meta.getHeight(), visibleHeight);
        }

        String scrollSpeed = meta.getElement().getAttribute("scrollSpeed");
        if (!scrollSpeed.isEmpty())
            scrollable.setScrollSpeed(Integer.parseInt(scrollSpeed));

        CdmlComponentHandler.doDefaultComponentProcessing(meta, scrollable);

        return CdmlComponentHandler.doDefaultLayoutProcessing(meta, scrollable);
    }
}
