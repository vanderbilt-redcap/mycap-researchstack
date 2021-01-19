package org.researchstack.backbone.step.active;

import org.researchstack.backbone.step.active.ActiveStep;
import org.researchstack.backbone.task.factory.TaskOptions;
import org.researchstack.backbone.ui.step.layout.LeftRightJudgementStepLayout;

/**
 * Created by Dr David W. Evans in January 2021.
 */

public class LeftRightJudgementStep extends ActiveStep {

    private int numberOfAttempts;
    private double minimumInterStimulusInterval;
    private double maximumInterStimulusInterval;
    private double timeout;
    private boolean shouldDisplayAnswer;
    private TaskOptions.ImageOption imageOption; //enum

    /* Default constructor needed for serialization/deserialization of object */
    LeftRightJudgementStep() {
        super();
    }

    public LeftRightJudgementStep(String identifier) {
        super(identifier);
        commonInit();
    }

    public LeftRightJudgementStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
        commonInit();
    }

    private void commonInit() {
        setOptional(true);
        setShouldStartTimerAutomatically(true);
        setShouldShowDefaultTimer(false);
        setShouldVibrateOnStart(true);
        setShouldPlaySoundOnStart(false); // changed
        setShouldContinueOnFinish(true);
        setShouldStartTimerAutomatically(true);
        setShouldVibrateOnFinish(true);
        setShouldPlaySoundOnFinish(false); // changed
        setEstimateTimeInMsToSpeakEndInstruction(0); // do not wait to proceed
    }

    @Override
    public Class getStepLayoutClass() {
        return LeftRightJudgementStepLayout.class;
    }

    public int getNumberOfAttempts() {
        return numberOfAttempts;
    }

    public void setNumberOfAttempts(int numberOfAttempts) {
        this.numberOfAttempts = numberOfAttempts;
    }

    public double getMinimumInterStimulusInterval() {
        return minimumInterStimulusInterval;
    }

    public void setMinimumInterStimulusInterval(double minimumInterStimulusInterval) {
        this.minimumInterStimulusInterval = minimumInterStimulusInterval;
    }

    public double getMaximumInterStimulusInterval() {
        return maximumInterStimulusInterval;
    }

    public void setMaximumInterStimulusInterval(double maximumInterStimulusInterval) {
        this.maximumInterStimulusInterval = maximumInterStimulusInterval;
    }

    public double getTimeout() {
        return timeout;
    }

    public void setTimeout(double timeout) {
        this.timeout = timeout;
    }

    public boolean getShouldDisplayAnswer() {
        return shouldDisplayAnswer;
    }

    public void setShouldDisplayAnswer(boolean shouldDisplayAnswer) {
        this.shouldDisplayAnswer = shouldDisplayAnswer;
    }

    public TaskOptions.ImageOption getImageOption() {
        return imageOption;
    }

    public void setImageOption(TaskOptions.ImageOption imageOption) {
        this.imageOption = imageOption;
    }
}
