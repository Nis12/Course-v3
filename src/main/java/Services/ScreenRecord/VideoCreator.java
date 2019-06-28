package Services.ScreenRecord;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

import java.awt.*;
import java.awt.image.BufferedImage;

public class VideoCreator implements Runnable {

    private static final double FRAME_RATE = 50;
    private static final int SECONDS_TO_RUN_FOR = 20;
    private static final String OUTPUT_STREAM = "record/stream/stream.mp4";

    private BufferedImage convertToType(BufferedImage sourceImage) {

        BufferedImage image;

        // if the source image is already the target type, return the source image
        if (sourceImage.getType() == BufferedImage.TYPE_3BYTE_BGR) image = sourceImage;
            // otherwise create a new image of the target type and draw the new image
        else {
            image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }

    @Override
    public void run() {

        // let's make a IMediaWriter to write the file.
        final IMediaWriter writer = ToolFactory.makeWriter(OUTPUT_STREAM);
        Dimension screenBounds = Toolkit.getDefaultToolkit().getScreenSize();

        // We tell it we're going to add one video stream, with id 0,
        // at position 0, and that it will have a fixed frame rate of FRAME_RATE.
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, screenBounds.width / 2, screenBounds.height / 2);

        long startTime = System.nanoTime();

//        for (int index = 0; index < SECONDS_TO_RUN_FOR * FRAME_RATE; index++) {
//
//            // take the screen shot
//            BufferedImage screen = getDesktopScreenshot();
//            // convert to the right image type
//            BufferedImage bgrScreen = convertToType(screen);
//            // encode the image to stream #0
//            writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
//
//            // sleep for frame rate milliseconds
//            try {
//                Thread.sleep((long) (1000 / FRAME_RATE));
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        // tell the writer to close and write the trailer if  needed
        writer.close();
    }
}
