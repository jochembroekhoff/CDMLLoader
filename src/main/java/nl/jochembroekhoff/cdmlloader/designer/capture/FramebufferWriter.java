package nl.jochembroekhoff.cdmlloader.designer.capture;

import lombok.Getter;
import org.lwjgl.util.Dimension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.zip.GZIPOutputStream;

/**
 * Based on: https://raw.githubusercontent.com/ata4/mineshot/master/src/main/java/info/ata4/minecraft/mineshot/client/capture/FramebufferWriter.java
 */
public class FramebufferWriter {

    protected static final int HEADER_SIZE = 18;

    @Getter
    protected final FramebufferCapturer framebufferCapturer;
    protected final File file;

    public FramebufferWriter(File file, FramebufferCapturer framebufferCapturer) {
        this.file = file;
        this.framebufferCapturer = framebufferCapturer;
    }

    public void write() throws IOException {
        framebufferCapturer.capture();

        //Make sure the file exists
        file.getParentFile().mkdirs();
        file.createNewFile();

        //Write the compressed image
        Dimension dim = framebufferCapturer.getCaptureDimension();
        try (WritableByteChannel fc = Channels.newChannel(new GZIPOutputStream(new FileOutputStream(file)))) {
            fc.write(buildTARGAHeader(dim.getWidth(), dim.getHeight(), framebufferCapturer.getBytesPerPixel() * 8));
            fc.write(framebufferCapturer.getByteBufferMC());
        }
    }

    protected ByteBuffer buildTARGAHeader(int width, int height, int bpp) {
        ByteBuffer bb = ByteBuffer.allocate(HEADER_SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.position(2);
        bb.put((byte) 2); // image type - uncompressed true-color image
        bb.position(12);
        bb.putShort((short) (width & 0xffff));
        bb.putShort((short) (height & 0xffff));
        bb.put((byte) (bpp & 0xff)); // bits per pixel
        bb.rewind();
        return bb;
    }
}