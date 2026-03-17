package repository;

public class RepositoryException extends Exception {

    private String customMessage;

    public RepositoryException(String customMessage) {
        this.customMessage = customMessage;
    }

    public String getCustomMessage() {
        return customMessage;
    }
}
