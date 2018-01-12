package nl.jochembroekhoff.cdmlloader.handler;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.IIcon;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.RadioGroup;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.programs.system.object.ColorScheme;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.resources.I18n;
import nl.jochembroekhoff.cdmlloader.CDMLLoader;
import nl.jochembroekhoff.cdmlloader.exception.FieldNotFoundException;
import nl.jochembroekhoff.cdmlloader.meta.ApplicationMeta;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import nl.jochembroekhoff.cdmlloader.meta.ListenerDefinition;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Jochem Broekhoff
 */
@RequiredArgsConstructor
public class CDMLHandler extends DefaultHandler {

    final Logger LOGGER;
    final String modId;
    final Application app;
    final Field[] fields;
    final Map<String, Field> fieldRemapping;
    final Map<String, Method> methodRemapping;
    final Map<String, RadioGroup> radioGroups;
    final Runnable loadStart;
    final Consumer<Boolean> loadComplete;

    final Map<String, Component> components = new HashMap<>();

    boolean inApplication = false;

    boolean inLayouts = false;

    boolean inLayout = false;
    String layoutId = "";
    Layout layout = null;
    Map<String, Layout> layoutMap = new HashMap<>();

    boolean inLayoutComponents = false;
    boolean processingComponent = false;
    boolean skipComponent = false;
    Component processingComponentInstance = null;
    String processingComponentType = "";
    CdmlComponentHandler processingComponentHandler = null;
    ComponentMeta processingComponentMeta = null;

    boolean inListeners = false;

    boolean otherProcessing = false;
    String otherQName = "";

    @Getter
    ApplicationMeta applicationMeta;

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        try {
            if (qName.equals("application")) {
                inApplication = true;

                //Extract application meta
                String main = attributes.getValue("main");
                String useColorScheme_ = attributes.getValue("useColorScheme");
                boolean useColorScheme = false;
                if (useColorScheme_ != null)
                    useColorScheme = Boolean.parseBoolean(useColorScheme_);

                //Construct application meta
                applicationMeta = new ApplicationMeta(main, useColorScheme);

                LOGGER.info("--> Found Main Layout ID: {}", applicationMeta.getMainLayoutId());
            }
            if (!inApplication)
                return;

            if (qName.equals("layouts")) {
                inLayouts = true;
            }
            if (!inLayouts)
                return;

            /*
            Create layout
             */

            if (qName.equals("layout")) {
                inLayout = true;

                layoutId = attributes.getValue("id");
                String layoutTitle = getI18nValue(attributes, "title");
                String layoutWidth = attributes.getValue("width");
                String layoutHeight = attributes.getValue("height");
                String layoutLeft = attributes.getValue("left");
                String layoutTop = attributes.getValue("top");
                boolean hasWH = layoutWidth != null && layoutHeight != null;
                boolean hasLT = layoutLeft != null && layoutTop != null;

                if (hasWH && hasLT) {
                    /*x, y, w, h*/
                    layout = new Layout(
                            Integer.parseInt(layoutLeft),
                            Integer.parseInt(layoutTop),
                            Integer.parseInt(layoutWidth),
                            Integer.parseInt(layoutHeight)
                    );
                } else if (hasWH) {
                    /*w, h*/
                    layout = new Layout(
                            Integer.parseInt(layoutWidth),
                            Integer.parseInt(layoutHeight)
                    );
                } else {
                    /*-*/
                    layout = new Layout();
                }

                if (layoutTitle != null)
                    layout.setTitle(layoutTitle);

                layoutMap.put(layoutId, layout);

                LOGGER.info("--> Created Layout: {}", layoutId);

                setToId(layoutId, layout);
            }
            if (!inLayout)
                return;

            /*
            Process layout contents
             */
            if (qName.equals("components")) {
                inLayoutComponents = true;
                return; //Go directly to processing new components
            }

            if (inLayoutComponents) {
                if (skipComponent)
                    return;

                if (!processingComponent) {
                    /*
                    Process new component
                     */

                    processingComponentInstance = null;
                    processingComponent = true;
                    processingComponentType = qName;
                    if (!CDMLLoader.hasComponentHandler(processingComponentType)) {
                        skipComponent = true;
                        LOGGER.warn("==> No component handler found for component type: {}", qName);
                        return;
                    }
                    processingComponentHandler = CDMLLoader.getComponentHandler(qName);

                    processingComponentMeta = new ComponentMeta(
                            this,
                            modId,
                            attributes,
                            attributes.getValue("id"),
                            attributes.getValue("top"),
                            attributes.getValue("left"),
                            attributes.getValue("width"),
                            attributes.getValue("height"),
                            attributes.getValue("enabled"),
                            attributes.getValue("visible"),
                            getI18nValue(attributes, "text"),
                            attributes.getValue("iconName"),
                            attributes.getValue("iconSet"),
                            new HashMap<>()
                    );

                    processingComponentInstance = processingComponentHandler.createComponent(processingComponentMeta);

                    if (processingComponentInstance == null) {
                        skipComponent = true;
                        return;
                    }

                    if (processingComponentMeta.hasId())
                        setToId(processingComponentMeta.getId(), processingComponentInstance);
                    if (processingComponentInstance != null)
                        layout.addComponent(processingComponentInstance);
                } else {
                    /*
                    Process active component
                    - Listeners
                    - Items
                     */
                    if (qName.equals("listeners")) {
                        inListeners = true;
                    } else {
                        otherProcessing = true;
                        otherQName = qName;
                    }

                    if (inListeners && qName.equals("listener")) {
                        String event = attributes.getValue("event");
                        String id = attributes.getValue("id");

                        if (event == null || id == null || processingComponentInstance == null || !CDMLLoader.hasListener(event))
                            return;

                        if (!fieldRemapping.containsKey(id)) {
                            //TODO: Skip listener processing like skipComponent?

                            LOGGER.error("==> No listener found for event {}: no field found for {}", event, id);
                            return;
                        }

                        Field listenerInstance = fieldRemapping.get(id);
                        ListenerDefinition ld = CDMLLoader.getListener(event);

                        try {
                            Method m = processingComponentInstance.getClass().getDeclaredMethod(ld.getMethodName(), ld.getParameters());
                            m.setAccessible(true);
                            m.invoke(processingComponentInstance, listenerInstance.get(app));
                            LOGGER.info("--> Attached listener {} to event {}", id, event);
                        } catch (NoSuchMethodException nsme) {
                            nsme.printStackTrace();
                            LOGGER.warn("==> Couldn't set listener for event {}: {}", event, nsme.getMessage());
                        }
                    } else if (otherProcessing && processingComponentHandler != null) {
                        processingComponentHandler.startElement(processingComponentInstance, processingComponentMeta, qName, attributes);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endElement(String uri, String localName,
                           String qName) throws SAXException {

        if (qName.equals("layouts")) {
            inLayouts = false;
            if (!layoutMap.containsKey(applicationMeta.getMainLayoutId())) {
                LOGGER.error("==> Could not find main layout: #{}", applicationMeta.getMainLayoutId());
                return;
            }
            try {
                Method m = Application.class.getDeclaredMethod("setCurrentLayout", Layout.class);
                m.setAccessible(true);
                m.invoke(app, layoutMap.get(applicationMeta.getMainLayoutId()));

                LOGGER.info("--> Set Main Layout: {}", applicationMeta.getMainLayoutId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (qName.equals("listeners")) {
            inListeners = false;
        } else if (qName.equals("components")) {
            inLayoutComponents = false;
        } else if (qName.equals(processingComponentType)) {
            //Reset component processing state
            processingComponentHandler = null;
            processingComponentMeta = null;
            processingComponent = false;
            processingComponentType = "";
            skipComponent = false;
        } else if (qName.equals(otherQName)) {
            otherProcessing = true;
        } else if (otherProcessing && processingComponentHandler != null) {
            processingComponentHandler.endElement(processingComponentInstance, processingComponentMeta, qName);
        }

    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (otherProcessing && processingComponentHandler != null) {
            String content = new String(ch, start, length);
            if (!StringUtils.isBlank(content))
                processingComponentHandler.elementContent(processingComponentInstance, processingComponentMeta, content);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();

        if (loadStart != null)
            loadStart.run();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();

        if (loadComplete != null)
            loadComplete.accept(true);
    }

    private void setToId(String id, Component component) throws IllegalAccessException, FieldNotFoundException {
        components.put(id, component);

        if (!fieldRemapping.containsKey(id)) {
            LOGGER.warn("==> Couldn't find declared field {} in application. No field has been set.", id);
            return;
        }
        fieldRemapping.get(id).set(app, component);
    }

    /**
     * Quick tool call to extract an i18n value if applicable.
     *
     * @param attributes sax attributes
     * @param key        attribute key
     * @return a processed string
     */
    public String getI18nValue(Attributes attributes, String key) {
        String rawValue = attributes.getValue(key);
        if (rawValue == null)
            return null;

        return getI18nValue(rawValue);
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
        String attrVal = meta.getAttributes().getValue(key);
        if (attrVal != null && StringUtils.startsWithAny(attrVal, "#", "0x", "0X"))
            return Color.decode(attrVal);
        else {
            if (!getApplicationMeta().isUseColorScheme())
                return null;

            if (attrVal != null)
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

    public IIcon getIcon(ComponentMeta meta) {
        if (meta.getIconName() != null) {
            if (meta.getIconSet() == null)
                meta.setIconSet("Icons");

            if (CDMLLoader.hasIconSet(meta.getIconSet()))
                return CDMLLoader.getIconSet(meta.getIconSet())
                        .getOrDefault(meta.getIconName(), null);
        }

        return null;
    }
}
