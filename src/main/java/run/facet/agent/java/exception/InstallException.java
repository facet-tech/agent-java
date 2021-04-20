package run.facet.agent.java.exception;

public class InstallException extends Exception{
    public InstallException(String message) {
        super(message);
    }

    public InstallException(Exception e) {
        super(e);
    }

    public InstallException(String message, Exception e) {
        super(message,e);
    }

}
