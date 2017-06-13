package server;

/**
 * Interface that represents Server.
 */
public interface Server {
    /**
     * Starts the server.
     * @throws RuntimeException if connection went wrong
     */
    void start() throws RuntimeException;

    /**
     * Stops the server.
     */
    void stop();
}