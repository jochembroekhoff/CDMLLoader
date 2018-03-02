package nl.jochembroekhoff.cdmlloader.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import nl.jochembroekhoff.cdmlloader.util.ReflectionUtil;
import nl.jochembroekhoff.cdmlloaderdemo.CDMLDemoMod;
import nl.jochembroekhoff.cdmlloaderdemo.Reference;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.util.Map;
import java.util.Optional;

/**
 * Partialy used source: https://github.com/Choonster-Minecraft-Mods/TestMod3/blob/b3d71dfddf7f212f0d86ef36e6ae1d06b8493ebc/src/main/java/choonster/testmod3/config/ModConfig.java
 */
@Config(modid = Reference.MOD_ID)
public class ModConfig {

    @Config.Comment("If set to false, the demo/example application isn't registered and cannot be used.")
    public static boolean demoApplicationEnabled = true;

    @Mod.EventBusSubscriber
    static class ConfigurationHolder {
        /**
         * The {@link ConfigManager#CONFIGS} getter.
         */
        private static final MethodHandle CONFIGS_GETTER = ReflectionUtil.findFieldGetter(ConfigManager.class, "CONFIGS");

        /**
         * The {@link Configuration} instance.
         */
        private static Configuration configuration;

        /**
         * Get the {@link Configuration} instance from {@link ConfigManager}.
         * <p>
         * TODO: Use a less hackish method of getting the {@link Configuration}/{@link IConfigElement}s when possible.
         *
         * @return The Configuration instance
         */
        static Configuration getConfiguration() {
            if (configuration == null) {
                try {
                    final String fileName = Reference.MOD_ID + ".cfg";

                    @SuppressWarnings("unchecked")
                    final Map<String, Configuration> configsMap = (Map<String, Configuration>) CONFIGS_GETTER.invokeExact();

                    final Optional<Map.Entry<String, Configuration>> entryOptional = configsMap.entrySet().stream()
                            .filter(entry -> fileName.equals(new File(entry.getKey()).getName()))
                            .findFirst();

                    if (entryOptional.isPresent()) {
                        configuration = entryOptional.get().getValue();
                    }
                } catch (Throwable throwable) {
                    CDMLDemoMod.getLogger().error("Failed to get Configuration instance", throwable);
                }
            }

            return configuration;
        }

        /**
         * Inject the new values and save to the config file when the config has been changed from the GUI.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Reference.MOD_ID)) {
                ConfigManager.load(Reference.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }

}
