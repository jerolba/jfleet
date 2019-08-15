package org.jfleet.parameterized;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class IsMySql5Condition implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (DatabaseArgumentProvider.isMySql5Present()) {
            return ConditionEvaluationResult.enabled("MySql 5.x driver is present");
        }
        return ConditionEvaluationResult.disabled("MySql 5.x driver is not present");
    }

}
