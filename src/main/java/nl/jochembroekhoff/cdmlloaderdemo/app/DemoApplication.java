package nl.jochembroekhoff.cdmlloaderdemo.app;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.*;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.app.listener.InitListener;
import com.mrcrayfish.device.api.app.listener.KeyListener;
import com.mrcrayfish.device.api.app.listener.SlideListener;
import com.mrcrayfish.device.api.task.TaskManager;
import net.minecraft.nbt.NBTTagCompound;
import nl.jochembroekhoff.cdmlloader.CDMLLoader;
import nl.jochembroekhoff.cdmlloader.annotate.Cdml;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlApp;
import nl.jochembroekhoff.cdmlloader.handler.CDMLDocumentHandler;
import nl.jochembroekhoff.cdmlloaderdemo.CDMLDemoMod;
import nl.jochembroekhoff.cdmlloaderdemo.task.TaskTestCDMLNotification;

/**
 * @author Jochem Broekhoff
 */
@CdmlApp
public class DemoApplication extends Application {

    /* CDML Components */

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

    /* Other things */
    private CDMLDocumentHandler handler;

    @Override
    public void init() {

        try {
            handler = CDMLLoader.init(this,
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
    InitListener layout1init = () -> CDMLDemoMod.getLogger().info("Layout one has been initialized.");

    @Cdml
    ClickListener clickHandler = (x, y, mousebtn) -> {
        CDMLDemoMod.getLogger().info("Clicked with mouse button {}", mousebtn);
        TaskManager.sendTask(new TaskTestCDMLNotification(handler.getNotificationMeta("notifyTest1")));

        /*
        try {
            val tmp = new File(System.getProperty("java.io.tmpdir"));
            val img = new File(tmp, "cdmlloader/capture.tga.gz");
            val capt = new FramebufferCapturer();
            val fbw = new FramebufferWriter(img, capt);
            fbw.write();
            CDMLDemoMod.getLogger().info("[Capture] Captured Minecraft. Saved at: {}", img);
            CDMLDemoMod.getLogger().info("[Capture] Laptop screen rectangle: {}", fbw.getFramebufferCapturer().getScreenRectangle());
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    };

    @Cdml
    SlideListener slideHandler = (percentage) -> progressBar.setProgress((int) (percentage * 100));

    @Cdml
    KeyListener keyPressed = (key) -> {
        CDMLDemoMod.getLogger().info("Pressed key: {}", key);
        return true;
    };

    @Override
    public void load(NBTTagCompound nbtTagCompound) {

    }

    @Override
    public void save(NBTTagCompound nbtTagCompound) {

    }
}
