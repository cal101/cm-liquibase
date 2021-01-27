package liquibase.changelog.filter;

import liquibase.changelog.ChangeSet;
import liquibase.changelog.RanChangeSet;

import java.util.List;

public class ActuallyExecutedChangeSetFilter extends RanChangeSetFilter {

    public ActuallyExecutedChangeSetFilter(List<RanChangeSet> ranChangeSets) {
        super(ranChangeSets);
    }

    @Override
    public ChangeSetFilterResult accepts(ChangeSet changeSet) {
        RanChangeSet ranChangeSet = getRanChangeSet(changeSet);
        if ((ranChangeSet != null) && ((ranChangeSet.getExecType() == null) || ChangeSet.ExecType.EXECUTED.equals
            (ranChangeSet.getExecType()) || ChangeSet.ExecType.RERAN.equals(ranChangeSet.getExecType()))) {
            return new ChangeSetFilterResult(true, "Change set was executed previously", this.getClass());
        } else {
            return new ChangeSetFilterResult(false, "Change set was not previously executed", this.getClass());
        }
    }
}
