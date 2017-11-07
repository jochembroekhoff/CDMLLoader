package nl.jochembroekhoff.cdmlloaderdemo;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import nl.jochembroekhoff.cdmlloader.CDMLLoader;
import nl.jochembroekhoff.cdmlloader.defaultcomponent.*;
import nl.jochembroekhoff.cdmlloaderdemo.app.DemoApplication;
import org.apache.logging.log4j.Logger;

/**
 * @author Jochem Broekhoff
 */
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, acceptedMinecraftVersions = Reference.MC_VERSION, dependencies = Reference.DEPENDS)
public class CDMLDemoMod {
    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        //Register default listeners
        CDMLLoader.registerListener("click", "setClickListener", ClickListener.class);

        //Register default CDML Component Handlers
        CDMLLoader.registerComponentHandler(new HandlerButton());
        CDMLLoader.registerComponentHandler(new HandlerButtonToggle());
        CDMLLoader.registerComponentHandler(new HandlerCheckBox());
        CDMLLoader.registerComponentHandler(new HandlerImage());
        CDMLLoader.registerComponentHandler(new HandlerLabel());
        CDMLLoader.registerComponentHandler(new HandlerNumberSelector());
        CDMLLoader.registerComponentHandler(new HandlerProgressBar());
        CDMLLoader.registerComponentHandler(new HandlerSlider());
        CDMLLoader.registerComponentHandler(new HandlerSpinner());
        CDMLLoader.registerComponentHandler(new HandlerText());
        CDMLLoader.registerComponentHandler(new HandlerTextArea());
        CDMLLoader.registerComponentHandler(new HandlerTextField());

        //Register demo CDM application
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "demo_app"), DemoApplication.class);
    }

    public static Logger getLogger() {
        return logger;
    }
}
