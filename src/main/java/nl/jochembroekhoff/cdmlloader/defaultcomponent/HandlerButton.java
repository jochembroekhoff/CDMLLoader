package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.IIcon;
import com.mrcrayfish.device.api.app.component.Button;
import net.minecraft.util.ResourceLocation;
import nl.jochembroekhoff.cdmlloader.CDMLLoader;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent(type = "Button")
public class HandlerButton implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft())
            return null;

        Button btn = new Button(meta.getLeft(), meta.getTop(), meta.getText());

        processButton(meta, btn);

        return CdmlComponentHandler.doDefaultProcessing(meta, btn);
    }

    public static void processButton(ComponentMeta meta, Button btn) {

        //FIXME: This is a temporary solution. Awaits fixing from MrCrayfish/MrCrayfishDeviceMod#94
        if (!meta.hasText())
            btn.setText(null);

        if (meta.hasWidthAndHeight())
            btn.setSize(meta.getWidth(), meta.getHeight());

        String toolTipTitle = meta.getCdmlHandler().getI18nValue(meta.getElement(), "toolTipTitle");
        String toolTipText = meta.getCdmlHandler().getI18nValue(meta.getElement(), "toolTipText");
        if (!toolTipTitle.isEmpty() && !toolTipText.isEmpty())
            btn.setToolTip(toolTipTitle, toolTipText);

        IIcon icon = CDMLLoader.getIcon(meta);
        if (icon != null)
            btn.setIcon(icon);

        //TODO: Test how this works
        String iconResource = meta.getElement().getAttribute("iconResource");
        String iconU = meta.getElement().getAttribute("iconU");
        String iconV = meta.getElement().getAttribute("iconV");
        String iconWidth = meta.getElement().getAttribute("iconWidth");
        String iconHeight = meta.getElement().getAttribute("iconHeight");
        if (!iconResource.isEmpty() && !iconU.isEmpty() && !iconV.isEmpty() && !iconWidth.isEmpty() && !iconHeight.isEmpty()) {
            ResourceLocation res;
            if (!iconResource.contains(":"))
                res = new ResourceLocation(meta.getModId(), iconResource);
            else
                res = new ResourceLocation(iconResource);
            btn.setIcon(res, Integer.parseInt(iconU), Integer.parseInt(iconV), Integer.parseInt(iconWidth), Integer.parseInt(iconHeight));
        }

        //Note that padding only works when the size is non-explicit
        String padding = meta.getElement().getAttribute("padding");
        if (!padding.isEmpty())
            btn.setPadding(Integer.parseInt(padding));
    }
}
