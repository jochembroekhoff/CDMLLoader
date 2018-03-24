package nl.jochembroekhoff.cdmlloaderdemo.proxy;

import com.mrcrayfish.device.api.app.Alphabet;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.listener.*;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import nl.jochembroekhoff.cdmlloader.CDMLLoader;
import nl.jochembroekhoff.cdmlloader.defaultcomponent.*;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClientProxy implements CommonProxy {
    @Override
    public void init(FMLInitializationEvent e) {
        //Register default listeners
        CDMLLoader.registerListener("change", "setChangeListener", ChangeListener.class);
        CDMLLoader.registerListener("click", "setClickListener", ClickListener.class);
        CDMLLoader.registerListener("init", "setInitListener", InitListener.class);
        CDMLLoader.registerListener("itemClick", "setItemClickListener", ItemClickListener.class);
        CDMLLoader.registerListener("key", "setKeyListener", KeyListener.class);
        CDMLLoader.registerListener("release", "setReleaseListener", ReleaseListener.class);
        CDMLLoader.registerListener("slide", "setSlideListener", SlideListener.class);

        //Register default CDML Component Handlers
        CDMLLoader.registerComponentHandler(new HandlerButton());
        CDMLLoader.registerComponentHandler(new HandlerButtonToggle());
        CDMLLoader.registerComponentHandler(new HandlerCheckBox());
        CDMLLoader.registerComponentHandler(new HandlerComboBox.List());
        CDMLLoader.registerComponentHandler(new HandlerItemList());
        CDMLLoader.registerComponentHandler(new HandlerImage());
        CDMLLoader.registerComponentHandler(new HandlerInventory());
        CDMLLoader.registerComponentHandler(new HandlerLabel());
        CDMLLoader.registerComponentHandler(new HandlerNumberSelector());
        CDMLLoader.registerComponentHandler(new HandlerProgressBar());
        CDMLLoader.registerComponentHandler(new HandlerSlider());
        CDMLLoader.registerComponentHandler(new HandlerSpinner());
        CDMLLoader.registerComponentHandler(new HandlerText());
        CDMLLoader.registerComponentHandler(new HandlerTextArea());
        CDMLLoader.registerComponentHandler(new HandlerTextField());
    }
}
