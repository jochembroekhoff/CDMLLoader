package nl.jochembroekhoff.cdmlloaderdemo.app;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Icon;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Image;
import net.minecraft.nbt.NBTTagCompound;
import nl.jochembroekhoff.cdmlloader.CDMLLoader;
import nl.jochembroekhoff.cdmlloader.annotate.Cdml;
import nl.jochembroekhoff.cdmlloader.annotate.CdmlApp;
import nl.jochembroekhoff.cdmlloaderdemo.Reference;

import java.util.ArrayList;
import java.util.List;

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
        layout1 = new Layout();

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

        // EVERYTHING BELOW THIS LINE IS JUST COPIED FROM THE DEMO APP

        final List<String> URLS = new ArrayList<>();
        URLS.add("https://d3n8a8pro7vhmx.cloudfront.net/promise/pages/202/attachments/original/1455641831/StartWithHello_logo.jpg?1455641831");
        URLS.add("https://media.mojang.com/blog-image/0db18353862a2f2d4d029b757914935a930311a4/0_17_Update_Mojang_Blog_1024x576.png");
        URLS.add("http://is4.mzstatic.com/image/thumb/Purple122/v4/f9/a8/3b/f9a83b28-e38a-b70b-284f-3d89918f016e/source/720x405bb.jpg");

        btnLeft = new Button(5, 5, "");
        btnLeft.setIcon(Icon.PREVIOUS);
        btnLeft.setClickListener((component, mouseButton) ->
        {
            if (mouseButton == 0) {
                if (currentIndex > 0) {
                    imageDisplay.setImage(URLS.get(--currentIndex));
                }
            }
        });
        super.addComponent(btnLeft);

        btnRight = new Button(175, 5, "");
        btnRight.setIcon(Icon.NEXT);
        btnRight.setClickListener((component, mouseButton) ->
        {
            if (mouseButton == 0) {
                if (currentIndex < URLS.size() - 1) {
                    imageDisplay.setImage(URLS.get(++currentIndex));
                }
            }
        });
        super.addComponent(btnRight);

        imageDisplay = new Image(30, 5, 140, 90, URLS.get(currentIndex));
        super.addComponent(imageDisplay);
    }

    @Override
    public void load(NBTTagCompound nbtTagCompound) {

    }

    @Override
    public void save(NBTTagCompound nbtTagCompound) {

    }
}
