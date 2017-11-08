package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.NumberSelector;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;

import java.text.DecimalFormat;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent("NumberSelector")
public class HandlerNumberSelector implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft() || meta.getAttrWidth() == null)
            return null;

        NumberSelector ns = new NumberSelector(meta.getLeft(), meta.getTop(), meta.getWidth());

        String min = meta.getAttributes().getValue("min");
        if (min != null)
            ns.setMin(Integer.parseInt(min));
        String max = meta.getAttributes().getValue("max");
        if (max != null)
            ns.setMax(Integer.parseInt(max));
        String number = meta.getAttributes().getValue("number");
        if (number != null)
            ns.setNumber(Integer.parseInt(number));
        String format = meta.getCdmlHandler().getI18nValue(meta.getAttributes(), "format");
        if (format != null)
            ns.setFormat(new DecimalFormat(format));

        return CdmlComponentHandler.doDefaultProcessing(meta, ns);
    }
}
