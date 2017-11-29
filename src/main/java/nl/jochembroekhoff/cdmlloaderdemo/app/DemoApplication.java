package nl.jochembroekhoff.cdmlloaderdemo.app;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.ComboBox;
import com.mrcrayfish.device.api.app.component.ItemList;
import com.mrcrayfish.device.api.app.component.ProgressBar;
import com.mrcrayfish.device.api.app.component.RadioGroup;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.app.listener.SlideListener;
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
    private ComboBox.List<String> cblExample;
    @Cdml
    private ItemList<String> itemList;
    @Cdml
    private ProgressBar progressBar;
    @Cdml
    private RadioGroup rg1;

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
                        //rg1.deselect();
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Cdml
    ClickListener clickHandler = (component, mousebtn) -> {
        CDMLDemoMod.getLogger().info("Clicked with mouse button {}", mousebtn);
    };

    @Cdml
    SlideListener slideHandler = (percentage) -> progressBar.setProgress((int) (percentage * 100));

    @Override
    public void load(NBTTagCompound nbtTagCompound) {

    }

    @Override
    public void save(NBTTagCompound nbtTagCompound) {

    }
}
