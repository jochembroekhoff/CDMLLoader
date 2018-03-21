package nl.jochembroekhoff.cdmlloader.defaultcomponent;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.component.NumberSelector;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import org.xml.sax.Attributes;

import java.text.DecimalFormat;

/**
 * @author Jochem Broekhoff
 */
@CdmlComponent("NumberSelector")
public class HandlerNumberSelector implements CdmlComponentHandler {
    @Override
    public Component createComponent(ComponentMeta meta) {
        if (!meta.hasTopAndLeft() || meta.getAttrWidth().isEmpty())
            return null;

        NumberSelector ns = new NumberSelector(meta.getLeft(), meta.getTop(), meta.getWidth());

        String min = meta.getElement().getAttribute("min");
        if (!min.isEmpty())
            ns.setMin(Integer.parseInt(min));
        String max = meta.getElement().getAttribute("max");
        if (!max.isEmpty())
            ns.setMax(Integer.parseInt(max));
        String number = meta.getElement().getAttribute("number");
        if (!number.isEmpty())
            ns.setNumber(Integer.parseInt(number));
        String format = meta.getCdmlHandler().getI18nValue(meta.getElement(), "format");
        if (!format.isEmpty())
            ns.setFormat(new DecimalFormat(format));

        return CdmlComponentHandler.doDefaultProcessing(meta, ns);
    }
}
