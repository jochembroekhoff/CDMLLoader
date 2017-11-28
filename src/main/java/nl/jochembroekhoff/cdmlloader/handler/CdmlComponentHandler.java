package nl.jochembroekhoff.cdmlloader.handler;

import com.mrcrayfish.device.api.app.Component;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import org.xml.sax.Attributes;

/**
 * @author Jochem Broekhoff
 */
public interface CdmlComponentHandler {

    Component createComponent(ComponentMeta meta);

    /*
     * Custom XML processing
     */

    void startElement(Component component, ComponentMeta meta, String qName, Attributes attributes);

    void endElement(Component component, ComponentMeta meta, String qName);

    void elementContent(Component component, ComponentMeta meta, String chars);

    /**
     * Do common processing tasks:
     * - Set enabled
     * - Set visible
     * - (more to come)
     *
     * @param meta      component metadata
     * @param component a component
     * @return
     */
    static Component doDefaultProcessing(ComponentMeta meta, Component component) {
        if (component == null)
            return component;

        component.setEnabled(meta.getEnabled());
        component.setVisible(meta.getVisible());

        return component;
    }
}
