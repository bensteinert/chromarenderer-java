package net.chromarenderer;

/**
 * @author bensteinert
 */
public class ChromaBinder {

    public static ChromaBinder get() {
        throw new MockBinderError();
    }

    public Object createCore() {
        throw new MockBinderError();
    }

    public static class MockBinderError extends Error {
    }
}
