package nl.jochembroekhoff.cdmlloaderdemo.task;

import com.mrcrayfish.device.api.app.Notification;
import com.mrcrayfish.device.api.task.Task;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import nl.jochembroekhoff.cdmlloader.CDMLLoader;
import nl.jochembroekhoff.cdmlloader.meta.NotificationMeta;
import nl.jochembroekhoff.cdmlloaderdemo.CDMLLoaderMod;

public class TaskTestCDMLNotification extends Task {

    NotificationMeta meta;

    public TaskTestCDMLNotification() {
        super("cdmlloaderdemo_testCDMLNotification");
    }

    public TaskTestCDMLNotification(NotificationMeta meta) {
        this();
        this.meta = meta;
    }

    @Override
    public void prepareRequest(NBTTagCompound nbt) {
        nbt.setString("name", meta.getName());
        nbt.setString("title", meta.getTitle());
        nbt.setString("subTitle", meta.getSubTitle());
        nbt.setString("iconName", meta.getIconName());
        nbt.setString("iconSet", meta.getIconSet());
    }

    @Override
    public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player) {
        this.meta = new NotificationMeta(
                nbt.getString("name"),
                nbt.getString("title"),
                nbt.getString("subTitle"),
                nbt.getString("iconName"),
                nbt.getString("iconSet")
        );

        CDMLLoaderMod.getLogger().info(nbt);

        Notification notification = CDMLLoader.createNotification(meta);
        notification.pushTo((EntityPlayerMP) player);
    }

    @Override
    public void prepareResponse(NBTTagCompound nbt) {

    }

    @Override
    public void processResponse(NBTTagCompound nbt) {

    }
}
