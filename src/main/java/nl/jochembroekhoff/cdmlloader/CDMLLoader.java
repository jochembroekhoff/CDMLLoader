package nl.jochembroekhoff.cdmlloader;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.IIcon;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Notification;
import com.mrcrayfish.device.api.app.component.RadioGroup;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import nl.jochembroekhoff.cdmlloader.annotate.Cdml;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlApp;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.exception.NoCdmlAppException;
import nl.jochembroekhoff.cdmlloader.handler.CDMLDocumentHandler;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ComponentMeta;
import nl.jochembroekhoff.cdmlloader.meta.ListenerDefinition;
import nl.jochembroekhoff.cdmlloader.meta.NotificationMeta;
import nl.jochembroekhoff.cdmlloaderdemo.CDMLDemoMod;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Jochem Broekhoff
 */
public class CDMLLoader {

    private final static Map<String, Map<String, CdmlComponentHandler>> componentHandlers = new HashMap<>();
    private final static Map<String, ListenerDefinition> listeners = new HashMap<>();
    private final static Map<String, Class<? extends IIcon>> iconSets = new HashMap<>();
    private final static Map<String, List<String>> iconNames = new HashMap<>();

    public static final Logger LOGGER = CDMLDemoMod.getLogger();

    /**
     * Load a CDML document and inject the constructed values in this class (controller class).
     *
     * @param app application class instance
     * @throws NoCdmlAppException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static void init(Application app) throws
            NoCdmlAppException,
            IOException, ParserConfigurationException, SAXException {
        init(app, null, null);
    }

    /**
     * Load a CDML document and inject the constructed values in this class (controller class).
     *
     * @param app          application class instance
     * @param loadStart    consumer which is called with the ResourceLocation of the CDML file
     * @param loadComplete consumer which is called wether the loading succeeded or not
     * @throws NoCdmlAppException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static CDMLDocumentHandler init(Application app, Runnable loadStart, Consumer<Boolean> loadComplete) throws
            NoCdmlAppException,
            IOException, ParserConfigurationException, SAXException {
        if (!app.getClass().isAnnotationPresent(CdmlApp.class))
            throw new NoCdmlAppException();

        String modId = app.getInfo().getId().getResourceDomain();
        String appId = app.getInfo().getId().getResourcePath();

        LOGGER.info("--CdmlApp-- ModID: {}, AppID: {}", modId, appId);

        if (loadStart != null)
            loadStart.run();

        ResourceLocation cdmlLocation = new ResourceLocation(modId, "cdml/" + appId + ".cdml");
        InputStream cdml = Minecraft.getMinecraft().getResourceManager().getResource(cdmlLocation).getInputStream();

        //Load fields
        Field[] cdmlFields = Arrays.stream(app.getClass().getDeclaredFields())
                .filter(a -> a.isAnnotationPresent(Cdml.class))
                //.filter(a -> Component.class.isAssignableFrom(a.getType())) //TODO: Awaits fixing of MrCrayfish/MrCrayfishDeviceMod#22
                .toArray(Field[]::new);
        //Load methods
        Method[] cdmlMethods = Arrays.stream(app.getClass().getDeclaredMethods())
                .filter(a -> a.isAnnotationPresent(Cdml.class))
                .toArray(Method[]::new);

        //Remap fields
        Map<String, Field> fieldRemapping = new HashMap<>();
        Arrays.stream(cdmlFields).forEach(field -> fieldRemapping.put(field.getName(), field));
        fieldRemapping.values().forEach(f -> f.setAccessible(true));
        //Remap methods
        Map<String, Method> methodRemapping = new HashMap<>();
        Arrays.stream(cdmlMethods).forEach(method -> methodRemapping.put(method.getName(), method));
        methodRemapping.values().forEach(m -> m.setAccessible(true));

        //Map radiogroups
        Map<String, RadioGroup> radioGroups = new HashMap<>();
        fieldRemapping.values().stream().filter(f -> RadioGroup.class.isAssignableFrom(f.getType()))
                .forEach(rg -> {
                    try {
                        RadioGroup rgg = (RadioGroup) rg.get(app);
                        if (rgg == null)
                            throw new NullPointerException();
                        radioGroups.put(rg.getName(), rgg);
                        LOGGER.info("--> Registered RadioGroup(1) {}", rg.getName());
                    } catch (IllegalAccessException | NullPointerException npe) {
                        try {
                            RadioGroup newRg = new RadioGroup();
                            radioGroups.put(rg.getName(), newRg);
                            rg.set(app, newRg);
                            LOGGER.info("--> Registered RadioGroup(2) {}", rg.getName());
                        } catch (IllegalAccessException iae) {
                            iae.printStackTrace();
                        }
                    }
                });

        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(cdml);
        val handler = new CDMLDocumentHandler(LOGGER, modId, app, cdmlFields, fieldRemapping, methodRemapping, radioGroups);
        val result = handler.handle(doc);

        if (loadComplete != null)
            loadComplete.accept(result);

        return handler;
    }

    /*
    COMPONENT HANDLERS
     */

    /**
     * Register a new component handler.
     *
     * @param handler a instance of the CdmlComponentHandler class
     * @return true if the component handler was registered successfully
     */

    public static boolean registerComponentHandler(CdmlComponentHandler handler) {
        if (!handler.getClass().isAnnotationPresent(CdmlComponent.class))
            throw new IllegalArgumentException("Couldn't find @CdmlComponent in handler class.");

        String componentType = handler.getClass().getAnnotation(CdmlComponent.class).type();
        String componentNamespace = handler.getClass().getAnnotation(CdmlComponent.class).namespace();

        if (componentType == null || componentType.isEmpty())
            throw new IllegalArgumentException("Invalid component type: null or empty.");

        if (componentNamespace == null)
            componentNamespace = "";

        if (!componentType.matches("^[A-Z].*$"))
            throw new IllegalArgumentException("Invalid component type: component type must start with uppercase letter.");

        if (hasComponentHandler(componentType, componentNamespace))
            throw new IllegalArgumentException("A component handler for component type "
                    + componentNamespace + ":" + componentType + " has been registered already.");

        if (!componentHandlers.containsKey(componentNamespace))
            componentHandlers.put(componentNamespace, new HashMap<>());

        componentHandlers.get(componentNamespace).put(componentType, handler);

        return true;
    }

    public static boolean hasComponentHandler(String componentType, String namespace) {
        return componentHandlers.containsKey(namespace)
                && componentHandlers.get(namespace).containsKey(componentType);
    }

    public static CdmlComponentHandler getComponentHandler(String componentType, String namespace) {
        if (!componentHandlers.containsKey(namespace))
            return null;

        return componentHandlers.get(namespace).getOrDefault(componentType, null);
    }

    /*
    LISTENERS
     */

    public static boolean registerListener(String eventName, String methodName, Class<?>... parameters) {
        if (hasListener(eventName))
            throw new IllegalArgumentException("A listener for event " + eventName + " has been registered already.");

        listeners.put(eventName, new ListenerDefinition(eventName, methodName, parameters));
        return true;
    }

    public static boolean hasListener(String eventName) {
        return listeners.containsKey(eventName);
    }

    public static ListenerDefinition getListener(String eventName) {
        return listeners.getOrDefault(eventName, null);
    }

    /*
    ICON SETS
     */

    /**
     * Register an icon set.
     *
     * @param iconSetName the name for the icon set, can be any String
     * @param iconSet     a class of an {@link IIcon} implementation
     * @param <T>         implementation type
     * @return whether or not the registration succeeded
     * @throws IllegalArgumentException when an icon set with the same name has been registered already
     */
    public static <T extends IIcon> boolean registerIconSet(String iconSetName, Class<T> iconSet)
            throws IllegalArgumentException {
        //Extract icon names
        val iconNames = Arrays.stream(iconSet.getDeclaredFields())
                .filter(Field::isEnumConstant)
                .map(Field::getName)
                .collect(Collectors.toList());

        if (hasIconSet(iconSetName))
            throw new IllegalArgumentException("An icon set called " + iconSetName + " has been registered already.");

        //Store
        iconSets.put(iconSetName, iconSet);
        CDMLLoader.iconNames.put(iconSetName, iconNames);

        return true;
    }

    public static boolean hasIconSet(String iconSetName) {
        return iconSets.containsKey(iconSetName);
    }

    public static Class<? extends IIcon> getIconSet(String iconSet) {
        return iconSets.get(iconSet);
    }

    public static List<String> getIconNames(String iconSet) {
        return iconNames.get(iconSet);
    }

    public static IIcon getIcon(String iconName, String iconSet) {
        if (iconName == null || iconName.isEmpty())
            return null;

        if (iconSet == null || iconSet.isEmpty())
            iconSet = "Icons";

        if (!hasIconSet(iconSet))
            return null;

        if (!getIconNames(iconSet).contains(iconName))
            return null;

        return getIconSet(iconSet)
                .getEnumConstants()
                [getIconNames(iconSet).indexOf(iconName)];
    }

    public static IIcon getIcon(ComponentMeta meta) {
        return getIcon(meta.getIconName(), meta.getIconSet());
    }

    /*
    NOTIFICATIONS
     */

    /**
     * Create a new instance of a {@link Notification} from a {@link NotificationMeta}.
     *
     * @param meta the notification meta data
     * @return a new {@link Notification} with the contents of the meta
     */
    public static Notification createNotification(NotificationMeta meta) {
        //FIXME: Awaits fixing from MrCrayfish/MrCrayfishDeviceMod#96. A pull request is opened, see MrCrayfish/MrCrayfishDeviceMod#97.
        //IIcon icon = getIcon(meta.getIconName(), meta.getIconSet());
        Icons icon = (Icons) getIcon(meta.getIconName(), "Icons");

        return meta.getSubTitle().isEmpty()
                ? new Notification(icon, meta.getTitle())
                : new Notification(icon, meta.getTitle(), meta.getSubTitle());
    }
}
