package net.chromarenderer;

/**
 * @author bensteinert
 */
public class YouGotMeException extends RuntimeException {

    public YouGotMeException() {
        super("You se this exception, because the developer was a lazy fool ... That is his very own NotYetImplementedException ...");
    }
}
