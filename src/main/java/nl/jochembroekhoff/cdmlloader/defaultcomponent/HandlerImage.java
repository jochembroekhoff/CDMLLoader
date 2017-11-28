package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.Image;
import net.minecraft.util.ResourceLocation;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import nl.jochembroekhoff.cdmlloaderdemo.CDMLDemoMod;
import org.xml.sax.Attributes;

import java.awt.*;
import java.lang.reflect.Method;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent("Image")
public class HandlerImage implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft())
            return null;

        Image img = null;

        String imageResource = meta.getAttributes().getValue("imageResource");
        String imageU = meta.getAttributes().getValue("imageU");
        String imageV = meta.getAttributes().getValue("imageV");
        String imageWidth = meta.getAttributes().getValue("imageWidth");
        String imageHeight = meta.getAttributes().getValue("imageHeight");
        if (imageResource != null && imageU != null && imageV != null && imageWidth != null && imageHeight != null) {
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

            img = new Image(meta.getLeft(), meta.getTop(), meta.getWidth(), meta.getHeight(), meta.getAttributes().getValue("url"));
        }

        String alpha = meta.getAttributes().getValue("alpha");
        if (alpha != null)
            img.setAlpha(Float.parseFloat(alpha));

        String borderVisible = meta.getAttributes().getValue("borderVisible");
        if (borderVisible != null)
            img.setBorderVisible(Boolean.parseBoolean(borderVisible));

        String borderColour = meta.getAttributes().getValue("borderColour");
        if (borderColour != null)
            try {
                //FIXME: Do not use reflection. Awaits fixing from MrCrayfish/MrCrayfishDeviceMod#24
                Method m = img.getClass().getDeclaredMethod("setBorderColor", Color.class);
                m.setAccessible(true);
                m.invoke(img, Color.decode(borderColour));
            } catch (Exception e) {
                CDMLDemoMod.getLogger().error("==> Couldn't set border color: {}", e.getMessage());
            }


        String borderThickness = meta.getAttributes().getValue("borderThickness");
        if (borderThickness != null)
            img.setBorderThickness(Integer.parseInt(borderThickness));

        return CdmlComponentHandler.doDefaultProcessing(meta, img);
    }

    @Override
    public void startElement(Component component, ComponentMeta meta, String qName, Attributes attributes) {

    }

    @Override
    public void endElement(Component component, ComponentMeta meta, String qName) {

    }

    @Override
    public void elementContent(Component component, ComponentMeta meta, String chars) {

    }
}
