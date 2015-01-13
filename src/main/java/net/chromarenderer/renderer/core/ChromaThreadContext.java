package net.chromarenderer.renderer.core;

/**
 * @author steinerb
 */
public class ChromaThreadContext {

    private static ThreadLocal<Integer> currentX = new ThreadLocal<>() ;
    private static ThreadLocal<Integer> currentY = new ThreadLocal<>() ;

    public static void setX(int x) {
        currentX.set(x);
    }

    public static Integer getX() {
        return currentX.get();
    }

    public static void setY(int y) {
        currentY.set(y);
    }

    public static Integer getY() {
        return currentY.get();
    }
}
