package utils;

/**
 * @author steinerb
 */
public class FpsCounter {

    private int frameCount;
    private long lastTimeStamp;
    private float fps;

    public FpsCounter() {
        lastTimeStamp = System.currentTimeMillis();
        fps = 0.0f;
        frameCount = 0;
    }

    public void frame() {
        frameCount += 1;
        long actual = System.currentTimeMillis();
        float elapsed = (float) (actual - lastTimeStamp) / 1000;
        // just refresh every sencond
        if(elapsed > 1.0f) {
            fps = (float) frameCount / elapsed;
            frameCount = 0;
            lastTimeStamp = System.currentTimeMillis();
        }
    }

    public float getFps() {
        return fps;
    }

}
