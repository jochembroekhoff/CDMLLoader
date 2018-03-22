package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.ProgressBar;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent(type = "ProgressBar")
public class HandlerProgressBar implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft() || !meta.hasWidthAndHeight())
            return null;

        ProgressBar pb = new ProgressBar(meta.getLeft(), meta.getTop(), meta.getWidth(), meta.getHeight());

        String max = meta.getElement().getAttribute("max");
        if (!max.isEmpty())
            pb.setMax(Integer.parseInt(max));
        String progress = meta.getElement().getAttribute("progress");
        if (!progress.isEmpty())
            pb.setProgress(Integer.parseInt(progress));

        return CdmlComponentHandler.doDefaultProcessing(meta, pb);
    }
}
