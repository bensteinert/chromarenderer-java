package net.chromarenderer;

import net.chromarenderer.main.ChromaCore;

/**
 * @author bensteinert
 */
public class ChromaBinder {

    private static ChromaBinder INSTANCE = new ChromaBinder();

    public static ChromaBinder get() {
        return INSTANCE;
    }

    public Chroma createCore() {
        return new ChromaCore();
    }
}
