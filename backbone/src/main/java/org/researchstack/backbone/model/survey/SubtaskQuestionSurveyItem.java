package org.researchstack.backbone.model.survey;

/**
 * Created by TheMDP on 1/2/17.
 */

public class SubtaskQuestionSurveyItem extends QuestionSurveyItem<SurveyItem> {

    @Override
    public boolean usesNavigation() {
        boolean usesNavigation = super.usesNavigation();
        if (usesNavigation) {
            return true;
        }
        if (items == null || items.isEmpty()) {
            return false;
        }
        for (SurveyItem item : items) {
            if (item instanceof QuestionSurveyItem) {
                usesNavigation = ((QuestionSurveyItem)item).usesNavigation();
                if (usesNavigation) {
                    return true;
                }
            }
        }
        return false;
    }
}
