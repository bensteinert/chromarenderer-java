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

    public float fps() {
        frameCount += 1;
        long actual = System.currentTimeMillis();
        float elapsed = (float) (actual - lastTimeStamp) / 1000;

        fps = (float) frameCount / elapsed;
        frameCount = 0;
        lastTimeStamp = System.currentTimeMillis();
        return fps;

    };
}
