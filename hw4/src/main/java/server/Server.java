package server;

/**
 * Interface that represents Server.
 */
public interface Server {
    void start() throws RuntimeException;
    void stop();
}