package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.IIcon;
import com.mrcrayfish.device.api.app.component.Image;
import net.minecraft.util.ResourceLocation;
import nl.jochembroekhoff.cdmlloader.CDMLLoader;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import nl.jochembroekhoff.cdmlloaderdemo.CDMLDemoMod;

import java.awt.*;
import java.lang.reflect.Method;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent(type = "Image")
public class HandlerImage implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft())
            return null;

        Image img = null;

        String imageResource = meta.getElement().getAttribute("imageResource");
        String imageU = meta.getElement().getAttribute("imageU");
        String imageV = meta.getElement().getAttribute("imageV");
        String imageWidth = meta.getElement().getAttribute("imageWidth");
        String imageHeight = meta.getElement().getAttribute("imageHeight");
        IIcon icon = CDMLLoader.getIcon(meta);
        if (icon != null) {
            if (meta.hasTopAndLeft() && meta.hasWidthAndHeight()) {
                img = new Image(meta.getLeft(), meta.getTop(), meta.getWidth(), meta.getHeight(), icon);
            } else if (meta.hasTopAndLeft()) {
                img = new Image(meta.getLeft(), meta.getTop(), icon);
            }
        } else if (!imageResource.isEmpty() && !imageU.isEmpty() && !imageV.isEmpty() && !imageWidth.isEmpty() && !imageHeight.isEmpty()) {
            ResourceLocation res;
            if (!imageResource.contains(":"))
                res = new ResourceLocation(meta.getModId(), imageResource);
            else
                res = new ResourceLocation(imageResource);

            if (meta.hasWidthAndHeight()) {
                img = new Image(meta.getLeft(), meta.getTop(), meta.getWidth(), meta.getHeight(),
                        Integer.parseInt(imageU), Integer.parseInt(imageV), Integer.parseInt(imageWidth), Integer.parseInt(imageHeight), res);
            } else {
                img = new Image(meta.getLeft(), meta.getTop(),
                        Integer.parseInt(imageU), Integer.parseInt(imageV), Integer.parseInt(imageWidth), Integer.parseInt(imageHeight), res);
            }
        }

        if (img == null) {
            if (!meta.hasWidthAndHeight())
                return null;

            img = new Image(meta.getLeft(), meta.getTop(), meta.getWidth(), meta.getHeight(), meta.getElement().getAttribute("url"));
        }

        String alpha = meta.getElement().getAttribute("alpha");
        if (!alpha.isEmpty())
            img.setAlpha(Float.parseFloat(alpha));

        String borderVisible = meta.getElement().getAttribute("borderVisible");
        if (!borderVisible.isEmpty())
            img.setBorderVisible(Boolean.parseBoolean(borderVisible));

        Color borderColor = meta.getCdmlHandler().getColorFromColorScheme(meta, "borderColor", null);
        if (borderColor != null)
            try {
                //FIXME: Do not use reflection. Awaits fixing from MrCrayfish/MrCrayfishDeviceMod#24
                Method m = img.getClass().getDeclaredMethod("setBorderColor", Color.class);
                m.setAccessible(true);
                m.invoke(img, borderColor);
            } catch (Exception e) {
                CDMLDemoMod.getLogger().error("==> Couldn't set border color: {}", e.getMessage());
            }


        String borderThickness = meta.getElement().getAttribute("borderThickness");
        if (!borderThickness.isEmpty())
            img.setBorderThickness(Integer.parseInt(borderThickness));

        return CdmlComponentHandler.doDefaultProcessing(meta, img);
    }
}
