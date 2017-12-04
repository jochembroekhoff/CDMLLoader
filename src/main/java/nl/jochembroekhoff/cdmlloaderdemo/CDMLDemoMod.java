package nl.jochembroekhoff.cdmlloaderdemo;

import com.mrcrayfish.device.api.ApplicationManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import nl.jochembroekhoff.cdmlloaderdemo.app.DemoApplication;
import nl.jochembroekhoff.cdmlloaderdemo.proxy.CommonProxy;
import org.apache.logging.log4j.Logger;

/**
 * @author Jochem Broekhoff
 */
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, acceptedMinecraftVersions = Reference.MC_VERSION, dependencies = Reference.DEPENDS)
public class CDMLDemoMod {
    private static Logger logger;

    @SidedProxy(clientSide = Reference.Proxy.CLIENT_SIDE, serverSide = Reference.Proxy.SERVER_SIDE)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        //Register demo CDM application
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "demo_app"), DemoApplication.class);
    }

    public static Logger getLogger() {
        return logger;
    }
}
