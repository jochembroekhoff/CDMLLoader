package nl.jochembroekhoff.cdmlloader;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import nl.jochembroekhoff.cdmlloader.annotate.Cdml;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlApp;
import nl.jochembroekhoff.cdmlloader.exception.ApplicationNotFoundException;
import nl.jochembroekhoff.cdmlloader.exception.NoCdmlAppException;
import nl.jochembroekhoff.cdmlloader.handler.CDMLHandler;
import nl.jochembroekhoff.cdmlloaderdemo.CDMLDemoMod;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CDMLLoader {

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

        String modId = app.getClass().getAnnotation(CdmlApp.class).modId();
        String appId = app.getClass().getAnnotation(CdmlApp.class).applicationId();

        /*
        //TODO: Get AppInfo
        MrCrayfishDeviceMod.proxy.getAllowedApplications().stream()
                .forEach(applic -> {
                    LOGGER.debug("--app:--");
                    LOGGER.debug(applic.getId().getResourceDomain());
                    LOGGER.debug(applic.getId().getResourcePath());
                });

        Optional<AppInfo> optAppInfo = MrCrayfishDeviceMod.proxy.getAllowedApplications().stream()
                .filter(a -> a.getId().getResourceDomain().equals(modId))
                .filter(a -> a.getId().getResourcePath().equals(appId))
                .findFirst();
        if(!optAppInfo.isPresent())
            throw new ApplicationNotFoundException();
        AppInfo appInfo = optAppInfo.get();
        */

        LOGGER.info("--CdmlApp-- ModID: {}, AppID: {}", modId, appId);

        ResourceLocation cdmlLocation = new ResourceLocation(modId, "cdml/" + appId + ".cdml");
        InputStream cdml = Minecraft.getMinecraft().getResourceManager().getResource(cdmlLocation).getInputStream();

        //Load fields
        Field[] cdmlFields = Arrays.stream(app.getClass().getDeclaredFields())
                .filter(a -> a.isAnnotationPresent(Cdml.class))
                .filter(a -> Component.class.isAssignableFrom(a.getType()))
                .toArray(Field[]::new);


        //Remap fields
        Map<String, Field> fieldRemapping = new HashMap<>();
        Arrays.stream(cdmlFields).forEach(field -> fieldRemapping.put(field.getName(), field));
        fieldRemapping.values().stream().forEach(f -> f.setAccessible(true));

        SAXParserFactory.newInstance().newSAXParser().parse(cdml, new CDMLHandler(LOGGER, modId, app, cdmlFields, fieldRemapping, loadStart, loadComplete));

        /*
        //app.getClass().getField("btnRightClicked")
        Method listener = app.getClass().getMethod("btnRightClicked", ClickListener.class);
        Button btn = new Button(5, 5, "");
        btn.setClickListener(listener);
        */
    }
}
