package nl.jochembroekhoff.cdmlloader.handler;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.Notification;
import com.mrcrayfish.device.api.app.component.RadioGroup;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.programs.system.object.ColorScheme;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.client.resources.I18n;
import nl.jochembroekhoff.cdmlloader.CDMLLoader;
import nl.jochembroekhoff.cdmlloader.meta.ApplicationMeta;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import nl.jochembroekhoff.cdmlloader.meta.ListenerDefinition;
import nl.jochembroekhoff.cdmlloader.meta.NotificationMeta;
import nl.jochembroekhoff.cdmlloader.util.XMLUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class CDMLDocumentHandler {
    final Logger LOGGER;
    final String modId;
    final Application app;
    final Field[] fields;
    final Map<String, Field> fieldRemapping;
    final Map<String, Method> methodRemapping;
    final Map<String, RadioGroup> radioGroups;

    @Getter
    ApplicationMeta applicationMeta;

    /*
    Element mappings
     */
    final Map<String, Layout> mappingLayouts = new HashMap<>();
    final Map<String, Component> mappingComponents = new HashMap<>();
    final Map<String, NotificationMeta> mappingNotifications = new HashMap<>();

    public boolean handle(Document doc) {
        AtomicReference<Element> applicationDoc = new AtomicReference<>(null);

        XMLUtil.iterateChildren(doc.getChildNodes(), Node.ELEMENT_NODE, node -> {
            val childElement = (Element) node;
            if (childElement.getTagName().equals("application"))
                applicationDoc.set(childElement);
        });

        if (applicationDoc.get() == null) {
            return false;
        } else {
            if (!handleApplication(applicationDoc.get())) {
                return false;
            } else {
                if (!mappingLayouts.containsKey(applicationMeta.getMainLayoutId())) {
                    LOGGER.error("==> Could not find main layout: #{}", applicationMeta.getMainLayoutId());
                    return false;
                }

                try {
                    Method m = Application.class.getDeclaredMethod("setCurrentLayout", Layout.class);
                    m.setAccessible(true);
                    m.invoke(app, mappingLayouts.get(applicationMeta.getMainLayoutId()));

                    LOGGER.info("--> Set Main Layout: {}", applicationMeta.getMainLayoutId());
                } catch (Exception ex) {
                    LOGGER.error("==> Failed to set Main Layout!", ex);
                    return false;
                }

                return true;
            }
        }
    }

    private boolean handleApplication(Element appElem) {
        AtomicReference<Boolean> state = new AtomicReference<>(true);

        //Extract metadata
        boolean useColorScheme = false;
        if (appElem.hasAttribute("useColorScheme"))
            useColorScheme = Boolean.parseBoolean(appElem.getAttribute("useColorScheme"));
        applicationMeta = new ApplicationMeta(appElem.getAttribute("main"), useColorScheme);
        LOGGER.info("--> Found Main Layout with ID: {}", applicationMeta.getMainLayoutId());

        //Process child elements
        XMLUtil.iterateChildren(appElem, Node.ELEMENT_NODE, node -> {
            val elem = (Element) node;
            switch (elem.getNodeName()) {
                case "layouts":
                    XMLUtil.iterateChildren(elem, Node.ELEMENT_NODE, layout -> {
                        if (!handleComponent((Element) layout, null))
                            state.set(false);
                    });
                    break;
                case "notifications":
                    XMLUtil.iterateChildren(elem, Node.ELEMENT_NODE, notification -> {
                        handleNotification((Element) notification);
                    });
                    break;
            }
        });

        return state.get();
    }

    public boolean handleComponent(Element elem, Layout layout) {
        /*
        Process component via Component Handler
         */

        val fullComponentType = elem.getNodeName();
        val splittedComponentType = fullComponentType.split(":");
        String componentType = splittedComponentType[0];
        String componentNamespace = "";

        if (splittedComponentType.length == 2) {
            componentNamespace = splittedComponentType[0];
            componentType = splittedComponentType[1];
        }


        if (!CDMLLoader.hasComponentHandler(componentType, componentNamespace)) {
            LOGGER.warn("==> No component handler found for component type: {} (in namespace {})",
                    componentType, componentNamespace);
            return false;
        }

        val componentHandler = CDMLLoader.getComponentHandler(componentType, componentNamespace);
        val componentMeta = new ComponentMeta(
                this,
                modId,
                elem,
                elem.getAttribute("id"),
                elem.getAttribute("top"),
                elem.getAttribute("left"),
                elem.getAttribute("width"),
                elem.getAttribute("height"),
                elem.getAttribute("enabled"),
                elem.getAttribute("visible"),
                getI18nValue(elem, "text"),
                elem.getAttribute("iconName"),
                elem.getAttribute("iconSet"),
                new HashMap<>()
        );
        val component = componentHandler.createComponent(componentMeta);

        if (component == null) {
            LOGGER.error("==> Unable to process component: {}", componentType);
            return false;
        }

        if (componentMeta.hasId())
            inject(componentMeta.getId(), component);

        /*
        Process listeners
         */

        XMLUtil.iterateChildren(elem, Node.ELEMENT_NODE, childNode -> {
            val childElem = (Element) childNode;
            switch (childElem.getNodeName()) {
                case "listeners":
                    handleListeners(childElem, component);
                    break;
            }
        });

        /*
        Process (sub)layout if the component is a layout
         */

        if (Layout.class.isAssignableFrom(component.getClass())) {
            componentHandler.handleChildElements(elem, (Layout) component, this);
        }

        /*
        Add to containing layout
         */

        if (layout != null)
            layout.addComponent(component);

        return true;
    }

    private void handleListeners(Element listenersElem, Component component) {
        XMLUtil.iterateChildren(listenersElem, Node.ELEMENT_NODE, listenerNode -> {
            val listenerElem = (Element) listenerNode;

            val id = listenerElem.getAttribute("id");
            val event = listenerElem.getAttribute("event");
            if (id.isEmpty() || event.isEmpty() || !CDMLLoader.hasListener(event))
                return;

            if (!fieldRemapping.containsKey(id)) {
                //TODO: Skip listener processing like skipComponent?
                LOGGER.warn("==> No listener found for event {}: no field found for {}", event, id);
                return;
            }

            Field listenerInstance = fieldRemapping.get(id);
            ListenerDefinition ld = CDMLLoader.getListener(event);

            try {
                Method m = component.getClass().getMethod(ld.getMethodName(), ld.getParameters());
                m.setAccessible(true);
                m.invoke(component, listenerInstance.get(app));
                LOGGER.info("--> Attached listener {} to event {}", id, event);
            } catch (Exception ex) {
                ex.printStackTrace();
                LOGGER.warn("==> Couldn't set listener for event {}: {}", event, ex.getMessage());
            }
        });
    }

    private void handleNotification(Element elem) {
        String name = elem.getAttribute("name");
        String title = getI18nValue(elem, "title");
        String subTitle = getI18nValue(elem, "subTitle");
        String iconName = elem.getAttribute("iconName");
        String iconSet = elem.getAttribute("iconSet");

        if (iconSet == null || iconSet.isEmpty())
            iconSet = "Icons";

        if (mappingNotifications.containsKey(name))
            LOGGER.warn("==> Duplicate creation of notification with name: {}. Overwriting previously registered notification.", name);

        //Register notification
        mappingNotifications.put(name, new NotificationMeta(name, title, subTitle, iconName, iconSet));
    }

    private void inject(String id, Component component, boolean isRootLayout) {
        if (!isRootLayout && !Layout.class.isAssignableFrom(component.getClass()))
            mappingComponents.put(id, component);
        else if (Layout.class.isAssignableFrom(component.getClass()))
            mappingLayouts.put(id, (Layout) component);

        if (!fieldRemapping.containsKey(id)) {
            LOGGER.warn("==> Couldn't find declared field {} in application. No component has been injected.",
                    id);
            return;
        }
        try {
            fieldRemapping.get(id).set(app, component);
        } catch (IllegalAccessException ex) {
            LOGGER.error("==> Failed to inject component!", ex);
        }
    }

    private void inject(String id, Component component) {
        inject(id, component, false);
    }

    /**
     * Quick tool call to extract an i18n value if applicable.
     *
     * @param elem XML element
     * @param key  attribute key
     * @return a processed string
     */
    public String getI18nValue(Element elem, String key) {
        if (!elem.hasAttribute(key))
            return "";

        return getI18nValue(elem.getAttribute(key));
    }

    /**
     * Process the i18n value for a string. An i18n value is searched if the string starts with a colon.
     * If you want a literal colon, you should prefix it with a backslash to escape the i18n processing.
     * <br/>
     * Example 1: <code>:some_key</code> would result in the the translated value of the key with the id
     * <code>app.&lt;modId&gt;.value.&lt;appId&gt;.some_key</code>.
     * <br/>
     * Example 2: <code>\:some_key</code> would result in the following value: <code>:some_key</code>.
     *
     * @param rawValue the raw string value
     * @return a possible i18n-ified string
     */
    public String getI18nValue(String rawValue) {
        if (rawValue == null || rawValue.isEmpty())
            return "";

        if (rawValue.startsWith("\\:"))
            return rawValue.substring(1);

        if (!rawValue.startsWith(":") || rawValue.length() <= 1)
            return rawValue;

        String i18nKey = "app." + app.getInfo().getFormattedId() + ".value." + rawValue.substring(1);

        if (!I18n.hasKey(i18nKey))
            return rawValue;

        return I18n.format(i18nKey);
    }

    /**
     * Get the instance of a RadioGroup by its identifier.
     * If no instance is found, a new one is created.
     *
     * @param identifier RadioGroup identifier
     * @return RadioGroup instance
     */
    public RadioGroup getRadioGroup(String identifier) {
        if (radioGroups.containsKey(identifier)) {
            return radioGroups.get(identifier);
        } else {
            //Create new instance if no existing one is found
            RadioGroup newRg = new RadioGroup();
            radioGroups.put(identifier, newRg);
            return newRg;
        }
    }

    public NotificationMeta getNotificationMeta(String cdmlRegisteredName) {
        return mappingNotifications.getOrDefault(cdmlRegisteredName, null);
    }

    public Notification getNotification(String cdmlRegisteredName) {
        val meta = getNotificationMeta(cdmlRegisteredName);
        if (meta == null)
            return null;

        return CDMLLoader.createNotification(meta);
    }

    /**
     * @param meta
     * @param key
     * @return
     * @see #getColorFromColorScheme(ComponentMeta, String, String)
     */
    public Color getColorFromColorScheme(ComponentMeta meta, String key) {
        return getColorFromColorScheme(meta, key, null);
    }

    /**
     * Available colors from the color scheme:
     * <ul>
     * <li>text</li>
     * <li>textSecondary</li>
     * <li>header</li>
     * <li>background</li>
     * <li>backgroundSecondary</li>
     * <li>itemBackground</li>
     * <li>itemHighlight</li>
     * </ul>
     *
     * @param meta                  component meta
     * @param key                   sax attribute key
     * @param defaultColorSchemeKey key of the color scheme color to look up if needed
     *                              and not overridden by the attribute value
     * @return
     */
    public Color getColorFromColorScheme(ComponentMeta meta, String key, String defaultColorSchemeKey) {
        String attrVal = meta.getElement().getAttribute(key);
        if (!attrVal.isEmpty() && StringUtils.startsWithAny(attrVal, "#", "0x", "0X"))
            return Color.decode(attrVal);
        else {
            if (!getApplicationMeta().isUseColorScheme())
                return null;

            if (!attrVal.isEmpty())
                defaultColorSchemeKey = attrVal;

            LOGGER.info("--> Color from color scheme: {}", defaultColorSchemeKey);

            ColorScheme cs = Laptop.getSystem().getSettings().getColorScheme();
            String methodName = "get" + StringUtils.capitalize(defaultColorSchemeKey) + "Color";
            try {
                Method m = cs.getClass().getDeclaredMethod(methodName);
                m.setAccessible(true);
                int color = (int) m.invoke(cs);
                return new Color(color);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
