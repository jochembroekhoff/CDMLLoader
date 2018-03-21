package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.Slider;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import org.xml.sax.Attributes;

import java.awt.*;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent("Slider")
public class HandlerSlider implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft() || meta.getAttrWidth().isEmpty())
            return null;

        Slider sld = new Slider(meta.getLeft(), meta.getTop(), meta.getWidth());

        String percentage = meta.getElement().getAttribute("percentage");
        if (!percentage.isEmpty())
            sld.setPercentage(Float.parseFloat(percentage));

        /* Colors */
        Color backgroundColor = meta.getCdmlHandler().getColorFromColorScheme(meta, "backgroundColor", "background");
        Color borderColor = meta.getCdmlHandler().getColorFromColorScheme(meta, "borderColor");
        Color sliderColor = meta.getCdmlHandler().getColorFromColorScheme(meta, "sliderColor");
        if (backgroundColor != null)
            sld.setBackgroundColor(backgroundColor);
        if (borderColor != null)
            sld.setBorderColor(borderColor);
        if (sliderColor != null)
            sld.setSliderColor(sliderColor);

        return CdmlComponentHandler.doDefaultProcessing(meta, sld);
    }
}
