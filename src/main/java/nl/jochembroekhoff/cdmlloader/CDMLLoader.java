package nl.jochembroekhoff.cdmlloader;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.IIcon;
import com.mrcrayfish.device.api.app.component.RadioGroup;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import nl.jochembroekhoff.cdmlloader.annotate.Cdml;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlApp;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.exception.ApplicationNotFoundException;
import nl.jochembroekhoff.cdmlloader.exception.NoCdmlAppException;
import nl.jochembroekhoff.cdmlloader.handler.CDMLDocumentHandler;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ListenerDefinition;
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
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Jochem Broekhoff
 */
public class CDMLLoader {

    final static Map<String, CdmlComponentHandler> componentHandlers = new HashMap<>();
    final static Map<String, ListenerDefinition> listeners = new HashMap<>();
    final static Map<String, Map<String, IIcon>> iconSets = new HashMap<>();

    public static final Logger LOGGER = CDMLDemoMod.getLogger();

    /**
     * Load a CDML document and inject the constructed values in this class (controller class).
     *
     * @param app application class instance
     * @throws NoCdmlAppException
     * @throws ApplicationNotFoundException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static void load(Application app) throws
            NoCdmlAppException, ApplicationNotFoundException,
            IOException, ParserConfigurationException, SAXException {
        load(app, null, null);
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
    public static void load(Application app, Runnable loadStart, Consumer<Boolean> loadComplete) throws
            NoCdmlAppException,
            IOException, ParserConfigurationException, SAXException {
        if (!app.getClass().isAnnotationPresent(CdmlApp.class))
            throw new NoCdmlAppException();

        String modId = app.getInfo().getId().getResourceDomain();
        String appId = app.getInfo().getId().getResourcePath();

        LOGGER.info("--CdmlApp-- ModID: {}, AppID: {}", modId, appId);

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
        fieldRemapping.values().stream().forEach(f -> f.setAccessible(true));
        //Remap methods
        Map<String, Method> methodRemapping = new HashMap<>();
        Arrays.stream(cdmlMethods).forEach(method -> methodRemapping.put(method.getName(), method));
        methodRemapping.values().stream().forEach(m -> m.setAccessible(true));

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
        val handler = new CDMLDocumentHandler(LOGGER, modId, app, cdmlFields, fieldRemapping, methodRemapping, radioGroups, loadStart, loadComplete);
        handler.handle(doc);
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

        String componentType = handler.getClass().getAnnotation(CdmlComponent.class).value();
        if (!componentType.matches("^[A-Z].*$"))
            throw new IllegalArgumentException("Component type must start with uppercase letter.");

        if (hasComponentHandler(componentType))
            throw new IllegalArgumentException("A component handler for component type "
                    + componentType + " has been registered already.");

        componentHandlers.put(componentType, handler);
        return true;
    }

    public static boolean hasComponentHandler(String componentType) {
        return componentHandlers.containsKey(componentType);
    }

    public static CdmlComponentHandler getComponentHandler(String componentType) {
        return componentHandlers.getOrDefault(componentType, null);
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

    public static boolean registerIconSet(String iconSetName, Map<String, IIcon> icons) {
        if (hasIconSet(iconSetName))
            throw new IllegalArgumentException("An icon set called " + iconSetName + " has been registered already.");

        iconSets.put(iconSetName, icons);
        return true;
    }

    public static boolean hasIconSet(String iconSetName) {
        return iconSets.containsKey(iconSetName);
    }

    public static Map<String, IIcon> getIconSet(String iconSetName) {
        return iconSets.getOrDefault(iconSetName, null);
    }
}
