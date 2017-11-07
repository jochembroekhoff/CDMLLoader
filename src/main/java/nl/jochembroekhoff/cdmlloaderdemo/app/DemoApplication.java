package nl.jochembroekhoff.cdmlloaderdemo.app;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import net.minecraft.nbt.NBTTagCompound;
import nl.jochembroekhoff.cdmlloader.CDMLLoader;
import nl.jochembroekhoff.cdmlloader.annotate.Cdml;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlApp;
import nl.jochembroekhoff.cdmlloaderdemo.CDMLDemoMod;

/**
 * @author Jochem Broekhoff
 */
@CdmlApp
public class DemoApplication extends Application {
    @Cdml
    private Button btnLeft;
    @Cdml
    private Button btnRight;
    @Cdml
    private Image imageDisplay;
    @Cdml
    private Layout layout1;

    @Override
    public void init() {

        try {
            CDMLLoader.load(this,
                    () -> {
                        //started loading CDML file
                    },
                    (success) -> {
                        //done loading CDML file
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Cdml
    ClickListener clickHandler = (component, mousebtn) -> {
        CDMLDemoMod.getLogger().info("Clicked with mouse button {}", mousebtn);
    };

    @Override
    public void load(NBTTagCompound nbtTagCompound) {

    }

    @Override
    public void save(NBTTagCompound nbtTagCompound) {

    }
}
