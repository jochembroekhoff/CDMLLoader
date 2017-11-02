package nl.jochembroekhoff.cdmlloaderdemo;

import com.mrcrayfish.device.api.ApplicationManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import nl.jochembroekhoff.cdmlloader.CDMLLoader;
import nl.jochembroekhoff.cdmlloader.defaultcomponent.HandlerButton;
import nl.jochembroekhoff.cdmlloader.defaultcomponent.HandlerLabel;
import nl.jochembroekhoff.cdmlloaderdemo.app.DemoApplication;
import org.apache.logging.log4j.Logger;

/**
 * Author: Jochem Broekhoff
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
        //Register CDML Component Handlers
        CDMLLoader.registerComponentHandler(new HandlerButton());
        CDMLLoader.registerComponentHandler(new HandlerLabel());

        //Register CDM applications
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "app1"), DemoApplication.class);
    }

    public static Logger getLogger() {
        return logger;
    }
}
