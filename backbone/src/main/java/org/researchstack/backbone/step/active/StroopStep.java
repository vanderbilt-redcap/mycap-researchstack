package org.researchstack.backbone.step.active;

import org.researchstack.backbone.ui.step.layout.StroopStepLayout;

/**
 * Created by Dr David W. Evans, January 2021.
 */

public class StroopStep extends ActiveStep {

    private int numberOfAttempts;
    private double minimumInterStimulusInterval;
    private double maximumInterStimulusInterval;
    private double timeout;

    /* Default constructor needed for serialization/deserialization of object */
    StroopStep() {
        super();
    }

    public StroopStep(String identifier) {
        super(identifier);
        commonInit();
    }

    public StroopStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
        commonInit();
    }

    private void commonInit() {
        setOptional(true);
        setShouldStartTimerAutomatically(true);
        setShouldShowDefaultTimer(false);
        setShouldVibrateOnStart(true);
        setShouldPlaySoundOnStart(false);
        setShouldContinueOnFinish(true);
        setShouldStartTimerAutomatically(true);
        setShouldVibrateOnFinish(true);
        setShouldPlaySoundOnFinish(false);
        setEstimateTimeInMsToSpeakEndInstruction(0); // do not wait to proceed
    }

    @Override
    public Class getStepLayoutClass() {
        return StroopStepLayout.class;
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
}
