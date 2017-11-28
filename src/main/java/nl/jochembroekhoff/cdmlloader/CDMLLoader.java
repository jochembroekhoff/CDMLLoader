package nl.jochembroekhoff.cdmlloader;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.component.RadioGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import nl.jochembroekhoff.cdmlloader.annotate.Cdml;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlApp;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlComponent;
import nl.jochembroekhoff.cdmlloader.exception.ApplicationNotFoundException;
import nl.jochembroekhoff.cdmlloader.exception.NoCdmlAppException;
import nl.jochembroekhoff.cdmlloader.handler.CDMLHandler;
import nl.jochembroekhoff.cdmlloader.handler.CdmlComponentHandler;
import nl.jochembroekhoff.cdmlloader.meta.ListenerDefinition;
import nl.jochembroekhoff.cdmlloaderdemo.CDMLDemoMod;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
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
     * @throws ApplicationNotFoundException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static void load(Application app, Runnable loadStart, Consumer<Boolean> loadComplete) throws
            NoCdmlAppException, ApplicationNotFoundException,
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
                        radioGroups.put(rg.getName(), (RadioGroup) rg.get(app));
                        LOGGER.info("--> Registered RadioGroup {}", rg.getName());
                    } catch (Exception e) {
                        /* //This can probably be removed
                        try {
                            RadioGroup newRg = new RadioGroup();
                            radioGroups.put(rg.getName(), newRg);
                            rg.set(app, newRg);
                            LOGGER.info("--> Registered RadioGroup(2) {}", rg.getName());
                        } catch (Exception ex) {
                        */
                        e.printStackTrace();
                        //}
                    }
                });

        SAXParserFactory.newInstance().newSAXParser().parse(cdml,
                new CDMLHandler(LOGGER, modId, app, cdmlFields, fieldRemapping, methodRemapping, radioGroups, loadStart, loadComplete));

        /*
        //app.getClass().getField("btnRightClicked")
        Method listener = app.getClass().getMethod("btnRightClicked", ClickListener.class);
        Button btn = new Button(5, 5, "");
        btn.setClickListener(listener);
        */
    }

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
}
