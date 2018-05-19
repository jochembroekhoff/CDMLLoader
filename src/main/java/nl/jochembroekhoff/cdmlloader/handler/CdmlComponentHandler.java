package nl.jochembroekhoff.cdmlloader.handler;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import lombok.val;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import nl.jochembroekhoff.cdmlloader.util.XMLUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * If the ouput component extends {@link com.mrcrayfish.device.api.app.Layout}
 *
 * @author Jochem Broekhoff
 */
public abstract class CdmlComponentHandler<T extends Component> {

    abstract public T createComponent(ComponentMeta meta);

    public <TT extends Layout> void handleChildElements(Element layoutElement, TT thisComponent, CDMLDocumentHandler documentHandler) {
        XMLUtil.iterateChildren(layoutElement, Node.ELEMENT_NODE, childElem -> {
            val elem = (Element) childElem;
            switch (elem.getNodeName()) {
                case "components":
                    XMLUtil.iterateChildren(elem, Node.ELEMENT_NODE, component -> {
                        documentHandler.handleComponent((Element) component, thisComponent);
                    });
                    break;
                case "listeners":
                    // Listeners are automatically handled by CDMLDocumentHandler#handleComponent(...)
                    break;
            }
        });
    }

    /**
     * Do common processing tasks:
     * - Set enabled
     * - Set visible
     * - (more to come)
     *
     * @param meta      component metadata
     * @param component a component
     * @return the component
     */
    public static <T extends Component> T doDefaultComponentProcessing(ComponentMeta meta, T component) {
        if (component == null)
            return null;

        component.setEnabled(meta.getEnabled());
        component.setVisible(meta.getVisible());

        return component;
    }

    /**
     * Do common Layout processing tasks:
     * - Set title
     *
     * @param meta   component meta
     * @param layout a layout
     * @param <T>    layout type
     * @return the layout
     */
    public static <T extends Layout> T doDefaultLayoutProcessing(ComponentMeta meta, T layout) {
        if (layout == null)
            return null;

        layout.setTitle(meta.getCdmlHandler().getI18nValue(meta.getElement(), "title"));

        return layout;
    }
}
