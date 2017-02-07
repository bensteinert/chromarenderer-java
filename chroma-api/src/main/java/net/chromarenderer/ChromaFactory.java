package net.chromarenderer;

/**
 * @author bensteinert
 */
public class ChromaFactory {

    static {
        checkForCoreFactory();
    }

    public static Chroma create() {
        return (Chroma) (ChromaBinder.get()).createCore();
    }

    private static void checkForCoreFactory() {
        try {
            ChromaBinder.get();
        } catch (NoClassDefFoundError error) {
            System.err.println("Unable to retrieve Chroma Core Factory. It looks like your version of Chroma does not bind to a core module dependency. Fatal Exit");
            System.exit(-1);
        }
    }
}
