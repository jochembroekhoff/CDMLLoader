package nl.jochembroekhoff.cdmlloaderdemo.app;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Image;
import net.minecraft.nbt.NBTTagCompound;
import nl.jochembroekhoff.cdmlloader.CDMLLoader;
import nl.jochembroekhoff.cdmlloader.annotate.Cdml;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlApp;
import nl.jochembroekhoff.cdmlloaderdemo.Reference;

/**
 * Author: Jochem Broekhoff
 */
@CdmlApp(modId = Reference.MOD_ID, applicationId = "app1")
public class DemoApplication extends Application {
    @Cdml
    private Button btnLeft;
    @Cdml
    private Button btnRight;
    @Cdml
    private Image imageDisplay;
    @Cdml
    private Layout layout1;

    private int currentIndex = 0;

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

    @Override
    public void load(NBTTagCompound nbtTagCompound) {

    }

    @Override
    public void save(NBTTagCompound nbtTagCompound) {

    }
}
