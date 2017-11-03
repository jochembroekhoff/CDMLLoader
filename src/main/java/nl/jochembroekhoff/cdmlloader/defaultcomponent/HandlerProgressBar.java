package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.ProgressBar;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;

@CdmlComponent("ProgressBar")
public class HandlerProgressBar implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft() || !meta.hasWidthAndHeight())
            return null;

        ProgressBar pb = new ProgressBar(meta.getLeft(), meta.getTop(), meta.getWidth(), meta.getHeight());

        String max = meta.getAttributes().getValue("max");
        if (max != null)
            pb.setMax(Integer.parseInt(max));
        String progress = meta.getAttributes().getValue("progress");
        if (progress != null)
            pb.setProgress(Integer.parseInt(progress));

        return CdmlComponentHandler.doDefaultProcessing(meta, pb);
    }
}
