package Services.ScreenRecord;

import rx.subjects.PublishSubject;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ScreenCreator implements Runnable {

    private final PublishSubject<BufferedImage> bufferedImagePublishSubject = PublishSubject.create();

    private boolean isRunning = true;

    private BufferedImage getDesktopScreenshot() {
        try {
            Robot robot = new Robot();
            Rectangle captureSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            return robot.createScreenCapture(captureSize);
        } catch (AWTException e) {
            stopThread();
            e.printStackTrace();
            return null;
        }
    }

    public PublishSubject<BufferedImage> getBufferedImagePublishSubject() {
        return bufferedImagePublishSubject;
    }

    public void stopThread() {
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            bufferedImagePublishSubject.onNext(getDesktopScreenshot());
        }
    }
}