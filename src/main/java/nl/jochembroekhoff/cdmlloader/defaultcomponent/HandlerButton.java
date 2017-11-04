package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Icon;
import com.mrcrayfish.device.api.app.component.Button;
import net.minecraft.util.ResourceLocation;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;

@CdmlComponent("Button")
public class HandlerButton implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft())
            return null;

        Button btn = new Button(meta.getLeft(), meta.getTop(), meta.getText());

        if (meta.hasWidthAndHeight())
            btn.setSize(meta.getWidth(), meta.getHeight());

        String icon = meta.getAttributes().getValue("icon");
        if (icon != null)
            btn.setIcon(Icon.valueOf(icon));

        //TODO: Test how this works
        String iconResource = meta.getAttributes().getValue("iconResource");
        String iconU = meta.getAttributes().getValue("iconU");
        String iconV = meta.getAttributes().getValue("iconV");
        String iconWidth = meta.getAttributes().getValue("iconWidth");
        String iconHeight = meta.getAttributes().getValue("iconHeight");
        if (iconResource != null && iconU != null && iconV != null && iconWidth != null && iconHeight != null) {
            ResourceLocation res;
            if (!iconResource.contains(":"))
                res = new ResourceLocation(meta.getModId(), iconResource);
            else
                res = new ResourceLocation(iconResource);
            btn.setIcon(res, Integer.parseInt(iconU), Integer.parseInt(iconV), Integer.parseInt(iconWidth), Integer.parseInt(iconHeight));
        }

        return CdmlComponentHandler.doDefaultProcessing(meta, btn);
    }
}