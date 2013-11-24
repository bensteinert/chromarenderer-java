package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class ArcPiece {
    public double x;
    public double y;
    public double w;
    public double h;
    public double startAngle;
    public double arcExtent;
    public double strokeWidth = 2;
    public double pixelsToMove = 2;
    public Color strokeColor;
    public boolean clockwise=false;

    long startTime = 0;
    public long displayTimePerFrameMillis = 60;
    private long displayTimePerFrameNano = 60  * 1000000;

    public void update(long now) {
        if (startTime == 0){
            startTime = now;
            displayTimePerFrameNano = displayTimePerFrameMillis * 1000000;
        }

        long elapsed = now - startTime;
        if (elapsed > displayTimePerFrameNano) {
            if (!clockwise){
                startAngle = startAngle + pixelsToMove;
                if (startAngle > 360){
                    startAngle = 0;
                }
            } else {
                startAngle = startAngle - pixelsToMove;
                if (startAngle < -360){
                    startAngle = 0;
                }
            }
            startTime = 0;
        }
    }

    public void draw(GraphicsContext gc) {
        gc.setStroke(strokeColor);
        gc.setLineWidth(strokeWidth);
        gc.strokeArc(x,
                y,
                w,
                h,
                startAngle,
                arcExtent,
                ArcType.OPEN);
    }
}