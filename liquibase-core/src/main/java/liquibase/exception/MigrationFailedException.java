package liquibase.exception;

import liquibase.changelog.ChangeSet;

public class MigrationFailedException extends LiquibaseException {

    private static final long serialVersionUID = 1L;
    private final String failedChangeSetName;
    
    public MigrationFailedException() {
        failedChangeSetName = "(unknown)";
    }

    public MigrationFailedException(ChangeSet failedChangeSet, String message) {
        super(message);
        this.failedChangeSetName = failedChangeSet.toString(false);
    }


    public MigrationFailedException(ChangeSet failedChangeSet, String message, Throwable cause) {
        super(message, cause);
        this.failedChangeSetName = failedChangeSet.toString(false);
    }

    public MigrationFailedException(ChangeSet failedChangeSet, Throwable cause) {
        super(cause);
        this.failedChangeSetName = failedChangeSet.toString(false);
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder("Migration failed");
        if (failedChangeSetName != null) {
            message.append(" for change set ").append(failedChangeSetName);
        }
        message.append(":\n     Reason: ").append(super.getMessage());

        return message.toString();
    }
}
