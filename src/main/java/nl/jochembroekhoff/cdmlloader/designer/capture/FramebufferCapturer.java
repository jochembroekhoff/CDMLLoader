package nl.jochembroekhoff.cdmlloader.designer.capture;

import com.mrcrayfish.device.core.Laptop;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.util.Dimension;

import java.awt.*;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_BGR;

/**
 * Based on https://github.com/ata4/mineshot/blob/master/src/main/java/info/ata4/minecraft/mineshot/client/capture/FramebufferCapturer.java
 */
public class FramebufferCapturer {

    private static final int BPP = 3; // bytes per pixel
    private static final int TYPE = GL_UNSIGNED_BYTE;
    private static final Minecraft MC = Minecraft.getMinecraft();

    private final ByteBuffer bb;
    private final Dimension dimMC;
    @Getter
    private final Rectangle screenRectangle;
    private final byte[] line1;
    private final byte[] line2;
    @Getter
    @Setter
    private boolean flipColors = true;
    @Getter
    @Setter
    private boolean flipLines = false;

    public FramebufferCapturer() {
        dimMC = getCurrentDimension();
        screenRectangle = calculateScreenRectangle();
        bb = ByteBuffer.allocateDirect(dimMC.getWidth() * dimMC.getHeight() * BPP);
        line1 = new byte[MC.displayWidth * BPP];
        line2 = new byte[MC.displayWidth * BPP];
    }

    public void setFlipColors(boolean flipColors) {
        this.flipColors = flipColors;
    }

    public int getBytesPerPixel() {
        return BPP;
    }

    public ByteBuffer getByteBufferMC() {
        bb.rewind();
        return bb.duplicate();
    }

    public Dimension getCaptureDimension() {
        return dimMC;
    }

    private Dimension getCurrentDimension() {
        return new Dimension(MC.displayWidth, MC.displayHeight);
    }

    private Rectangle calculateScreenRectangle() {
        int screenW = 0;
        int screenH = 0;
        try {
            //Extract laptop screenRectangle size
            Field w = Laptop.class.getDeclaredField("SCREEN_WIDTH");
            Field h = Laptop.class.getDeclaredField("SCREEN_HEIGHT");
            w.setAccessible(true);
            h.setAccessible(true);
            //Apply scale factor
            ScaledResolution res = new ScaledResolution(MC);
            screenW = w.getInt(null) * res.getScaleFactor();
            screenH = h.getInt(null) * res.getScaleFactor();
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        int upperLeftX = (dimMC.getWidth() - screenW) / 2;
        int upperLeftY = (dimMC.getHeight() - screenH) / 2;

        return new Rectangle(upperLeftX, upperLeftY, screenW, screenH);
    }

    public void capture() {
        // check if the dimensions are still the same
        Dimension dim1 = getCurrentDimension();
        Dimension dim2 = getCaptureDimension();
        if (!dim1.equals(dim2)) {
            throw new IllegalStateException(String.format(
                    "Display size changed! %dx%d != %dx%d",
                    dim1.getWidth(), dim1.getHeight(),
                    dim2.getWidth(), dim2.getHeight()));
        }

        // set alignment flags
        glPixelStorei(GL_PACK_ALIGNMENT, 1);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        int format = isFlipColors()
                ? GL_BGR
                : GL_RGB;

        // read texture from framebuffer if enabled, otherwise use slower glReadPixels
        if (OpenGlHelper.isFramebufferEnabled()) {
            Framebuffer fb = MC.getFramebuffer();
            glBindTexture(GL_TEXTURE_2D, fb.framebufferTexture);
            glGetTexImage(GL_TEXTURE_2D, 0, format, TYPE, bb);
        } else {
            glReadPixels(0, 0, MC.displayWidth, MC.displayHeight, format, TYPE, bb);
        }

        if (isFlipLines()) {
            // flip buffer vertically
            for (int i = 0; i < MC.displayHeight / 2; i++) {
                int ofs1 = i * MC.displayWidth * BPP;
                int ofs2 = (MC.displayHeight - i - 1) * MC.displayWidth * BPP;

                // read lines
                bb.position(ofs1);
                bb.get(line1);
                bb.position(ofs2);
                bb.get(line2);

                // write lines at swapped positions
                bb.position(ofs2);
                bb.put(line1);
                bb.position(ofs1);
                bb.put(line2);
            }
        }
    }
}