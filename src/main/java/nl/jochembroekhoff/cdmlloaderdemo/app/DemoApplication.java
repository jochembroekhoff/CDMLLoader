package nl.jochembroekhoff.cdmlloaderdemo.app;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.ComboBox;
import com.mrcrayfish.device.api.app.component.ItemList;
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
    private ComboBox.List<String> cblExample;
    @Cdml
    private ItemList<String> itemList;

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
                        itemList.addItem("Item #1");
                        itemList.addItem("Item #2");
                        itemList.addItem("Item #3");

                        cblExample.setItems(new String[]{"First", "Second"});
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
