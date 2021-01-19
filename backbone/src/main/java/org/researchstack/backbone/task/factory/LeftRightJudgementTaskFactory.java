package org.researchstack.backbone.task.factory;

import android.content.Context;

import org.researchstack.backbone.R;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.CountdownStep;
import org.researchstack.backbone.step.active.LeftRightJudgementStep;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.utils.ResUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static org.researchstack.backbone.task.factory.TaskFactory.Constants.*;
import static org.researchstack.backbone.task.factory.TaskOptions.ImageOption.*;

/**
 * Created by Dr David W. Evans, Janury 2021.
 */

public class LeftRightJudgementTaskFactory {

    private static final int DEFAULT_COUNTDOWN_DURATION = 5; // seconds

    /**
     * Returns a predefined Left/Right Judgement task that tests participants ability to recognise the laterality (side of the body) of a limb presented in an image.
     * The left/right judgement task is essentially an image recognition task. The participant is shown a series of images. Each image displays a limb (a hand or foot, depending on image options set), but the 3D orientation of the displayed limb varies. In each attempt of the task, the number of which can be set in the parameters, the participant has to press the button that corresponds to the side of the body to which the limb belongs (i.e. left or right).
     * <p>
     * A left/right judgement task finishes when the user has completed all of the attempts, irrespective of correct or incorrect answers.
     * <p>
     * Data collected by the task is in the form of a `LeftRightJudgementResult` object for each image presented.
     *
     * @param context                       Can be app or activity, used for resources
     * @param identifier                    The task identifier to use for this task, appropriate to
     *                                      the study.
     * @param intendedUseDescription        A localized string describing the intended use of the data
     *                                      collected. If the value of this parameter is `nil`, the
     *                                      default localized text is displayed.
     * @param imageOption                   Options for determining which images to display as stimuli.
     * @param minimumInterStimulusInterval  The minimum interval (in seconds) before each stimulus
     *                                      (image) is delivered, during which the screen remains empty.
     * @param maximumInterStimulusInterval  The maximum interval (in seconds) before each stimulus
     *                                      (image) is delivered, during which the screen remains empty.
     * @param timeout                       The time period (in seconds) permitted after the stimulus
     *                                      begins, until the attempt fails, if a button is not pressed.
     * @param shouldDisplayAnswer           Option to display whether an image was correctly identified
     *                                      or not.
     * @param numberOfAttempts              Total number of images to be presented during the task.
     * @param optionList                    Options that affect the features of the predefined task.
     * @return                              An active left/right judgement task that can be presented
     *                                      with an `ActiveTaskActivity` object.
     **/

    public static OrderedTask leftRightJudgementTaskFactory(
            Context context,
            String identifier,
            String intendedUseDescription,
            TaskOptions.ImageOption imageOption,
            double minimumInterStimulusInterval,
            double maximumInterStimulusInterval,
            double timeout,
            boolean shouldDisplayAnswer,
            int numberOfAttempts,
            List<TaskExcludeOption> optionList)
    {
        List<Step> stepList = new ArrayList<>();

        // Setup which image sets (hands or feet) to start with and how many image sets (1 or both) to add, based on the imageOption parameter. If both image sets are selected, the order is randomly allocated
        int imageSetCount = (imageOption == BOTH) ? 2 : 1;
        boolean doingBoth = (imageSetCount == 2);
        boolean handImages;

        switch (imageOption) {
            case FEET:
                handImages = false;
                break;
            case HANDS:
            case UNSPECIFIED:
                handImages = true;
                break;
            default:
                // Coin toss for which images to present first (in case we're doing both)
                handImages = (new Random()).nextBoolean();
                break;
        }

        for (int imageSets = 1; imageSets <= imageSetCount; imageSets++) {

            if (!optionList.contains(TaskExcludeOption.INSTRUCTIONS)) {

                { // Instruction step 0

                    if (doingBoth) {
                        // Set the title and instructions based on the image set(s) selected
                        if (imageSets == 1) {
                            if (handImages) {
                                String title = context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_TITLE);
                                InstructionStep instructionStep0 = new InstructionStep(stepIdentifierWithImageSetId(
                                        Instruction0StepIdentifier, ActiveTaskHandImagesIdentifier), title, intendedUseDescription);
                                instructionStep0.setMoreDetailText(context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_INTRO1_DETAIL_TEXT_HAND_FIRST)); // different instructions for hand images being first
                                instructionStep0.setImage(ResUtils.LeftRightJudgement.PHONE_LEFT_RIGHT_HAND_BUTTON);
                                stepList.add(instructionStep0);
                            } else {
                                String title = context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_TITLE);
                                InstructionStep instructionStep0 = new InstructionStep(stepIdentifierWithImageSetId(
                                        Instruction0StepIdentifier, ActiveTaskFootImagesIdentifier), title, intendedUseDescription);
                                instructionStep0.setMoreDetailText(context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_INTRO1_DETAIL_TEXT_FOOT_FIRST)); // different instructions for foot images being first
                                instructionStep0.setImage(ResUtils.LeftRightJudgement.PHONE_LEFT_RIGHT_FOOT_BUTTON);
                                stepList.add(instructionStep0);
                            }
                        } else { // imageSets == 2
                            if (handImages) {
                                String title = context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_TITLE);
                                InstructionStep instructionStep0 = new InstructionStep(stepIdentifierWithImageSetId(
                                        Instruction0StepIdentifier, ActiveTaskHandImagesIdentifier), title, intendedUseDescription);
                                instructionStep0.setMoreDetailText(context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_INTRO1_DETAIL_TEXT_HAND_SECOND)); // different instruction for hand images being second
                                instructionStep0.setImage(ResUtils.LeftRightJudgement.PHONE_LEFT_RIGHT_HAND_BUTTON);
                                stepList.add(instructionStep0);
                            } else {
                                String title = context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_TITLE);
                                InstructionStep instructionStep0 = new InstructionStep(stepIdentifierWithImageSetId(
                                        Instruction0StepIdentifier, ActiveTaskFootImagesIdentifier), title, intendedUseDescription);
                                instructionStep0.setMoreDetailText(context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_INTRO1_DETAIL_TEXT_FOOT_SECOND)); // different instructions for foot images being second
                                instructionStep0.setImage(ResUtils.LeftRightJudgement.PHONE_LEFT_RIGHT_FOOT_BUTTON);
                                stepList.add(instructionStep0);
                            }
                        }
                    } else { // not doing both
                        if (handImages) {
                            String title = context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_TITLE);
                            InstructionStep instructionStep0 = new InstructionStep(stepIdentifierWithImageSetId(
                                    Instruction0StepIdentifier, ActiveTaskHandImagesIdentifier), title, intendedUseDescription);
                            instructionStep0.setMoreDetailText(context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_INTRO1_DETAIL_TEXT_HAND)); // different instructions for hand images being first
                            instructionStep0.setImage(ResUtils.LeftRightJudgement.PHONE_LEFT_RIGHT_HAND_BUTTON);
                            stepList.add(instructionStep0);
                        } else {
                            String title = context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_TITLE);
                            InstructionStep instructionStep0 = new InstructionStep(stepIdentifierWithImageSetId(
                                    Instruction0StepIdentifier, ActiveTaskFootImagesIdentifier), title, intendedUseDescription);
                            instructionStep0.setMoreDetailText(context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_INTRO1_DETAIL_TEXT_FOOT)); // different instructions for foot images being first
                            instructionStep0.setImage(ResUtils.LeftRightJudgement.PHONE_LEFT_RIGHT_FOOT_BUTTON);
                            stepList.add(instructionStep0);
                        }
                    }

                }

                {   // Instruction step 1

                    if (imageSets == 1) {
                        if (handImages) {
                            String title = context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_TITLE_HAND);
                            InstructionStep instructionStep1 = new InstructionStep(stepIdentifierWithImageSetId(
                                    Instruction1StepIdentifier, ActiveTaskHandImagesIdentifier), title, intendedUseDescription);
                            instructionStep1.setMoreDetailText(String.format(
                                    context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_INTRO2_DETAIL_TEXT_HAND),
                                    String.valueOf(numberOfAttempts),
                                    significantFigures(timeout)));
                            instructionStep1.setImage(ResUtils.LeftRightJudgement.PHONE_LEFT_RIGHT_HAND_BUTTON);
                            stepList.add(instructionStep1);
                        } else {
                            String title = context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_TITLE_FOOT);
                            InstructionStep instructionStep1 = new InstructionStep(stepIdentifierWithImageSetId(
                                    Instruction1StepIdentifier, ActiveTaskFootImagesIdentifier), title, intendedUseDescription);
                            instructionStep1.setMoreDetailText(String.format(
                                    context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_INTRO2_DETAIL_TEXT_FOOT),
                                    String.valueOf(numberOfAttempts),
                                    significantFigures(timeout)));
                            instructionStep1.setImage(ResUtils.LeftRightJudgement.PHONE_LEFT_RIGHT_FOOT_BUTTON);
                            stepList.add(instructionStep1);
                        }
                    } else { // imageSets == 2
                        if (handImages) {
                            String title = context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_TITLE_HAND);
                            InstructionStep instructionStep1 = new InstructionStep(stepIdentifierWithImageSetId(
                                    Instruction1StepIdentifier, ActiveTaskHandImagesIdentifier), title, intendedUseDescription);
                            instructionStep1.setMoreDetailText(String.format(
                                    context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_INTRO2_DETAIL_TEXT_HAND),
                                    String.valueOf(numberOfAttempts),
                                    significantFigures(timeout)));
                            instructionStep1.setImage(ResUtils.LeftRightJudgement.PHONE_LEFT_RIGHT_HAND_BUTTON);
                            stepList.add(instructionStep1);
                        } else {
                            String title = context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_TITLE_FOOT);
                            InstructionStep instructionStep1 = new InstructionStep(stepIdentifierWithImageSetId(
                                    Instruction1StepIdentifier, ActiveTaskFootImagesIdentifier), title, intendedUseDescription);
                            instructionStep1.setMoreDetailText(String.format(
                                    context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_INTRO2_DETAIL_TEXT_FOOT),
                                    String.valueOf(numberOfAttempts),
                                    significantFigures(timeout)));
                            instructionStep1.setImage(ResUtils.LeftRightJudgement.PHONE_LEFT_RIGHT_FOOT_BUTTON);
                            stepList.add(instructionStep1);
                        }
                    }
                }
            }

            {   // Countdown step

                if (handImages) {
                    String countdownStepText = context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_STEP_TEXT_HAND);
                    CountdownStep countdownStep = new CountdownStep(
                            stepIdentifierWithImageSetId(CountdownStepIdentifier, ActiveTaskHandImagesIdentifier));
                    countdownStep.setStepDuration(DEFAULT_COUNTDOWN_DURATION);
                    countdownStep.setSpokenInstruction(countdownStepText); // spoken instructions during countdown
                    countdownStep.setTitle(context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_TITLE_HAND));
                    countdownStep.setOptional(false);
                    stepList.add(countdownStep);
                } else {
                    String countdownStepText = context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_STEP_TEXT_FOOT);
                    CountdownStep countdownStep = new CountdownStep(
                            stepIdentifierWithImageSetId(CountdownStepIdentifier, ActiveTaskFootImagesIdentifier));
                    countdownStep.setStepDuration(DEFAULT_COUNTDOWN_DURATION);
                    countdownStep.setSpokenInstruction(countdownStepText); // spoken instructions during countdown
                    countdownStep.setTitle(context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_TITLE_FOOT));
                    countdownStep.setOptional(false);
                    stepList.add(countdownStep);
                }
            }

            {   // Left/Right Judgement step

                if (handImages) {
                    LeftRightJudgementStep leftRightJudgementStep = new LeftRightJudgementStep(stepIdentifierWithImageSetId(
                            LeftRightJudgementStepIdentifier, ActiveTaskHandImagesIdentifier));
                    leftRightJudgementStep.setImageOption(HANDS);
                    String leftRightTitle = context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_TITLE);
                    leftRightJudgementStep.setTitle(leftRightTitle);
                    String leftRightText = context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_STEP_TEXT_HAND);
                    leftRightJudgementStep.setText(leftRightText);
                    leftRightJudgementStep.setSpokenInstruction(leftRightText);
                    // set parameters
                    leftRightJudgementStep.setMinimumInterStimulusInterval(minimumInterStimulusInterval);
                    leftRightJudgementStep.setMaximumInterStimulusInterval(maximumInterStimulusInterval);
                    leftRightJudgementStep.setNumberOfAttempts(numberOfAttempts);
                    leftRightJudgementStep.setTimeout(timeout);
                    leftRightJudgementStep.setShouldDisplayAnswer(shouldDisplayAnswer);
                    stepList.add(leftRightJudgementStep);
                } else {
                    LeftRightJudgementStep leftRightJudgementStep = new LeftRightJudgementStep(stepIdentifierWithImageSetId(
                            LeftRightJudgementStepIdentifier, ActiveTaskFootImagesIdentifier));
                    leftRightJudgementStep.setImageOption(FEET);
                    String leftRightTitle = context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_TITLE);
                    leftRightJudgementStep.setTitle(leftRightTitle);
                    String leftRightText = context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_STEP_TEXT_FOOT);
                    leftRightJudgementStep.setText(leftRightText);
                    leftRightJudgementStep.setSpokenInstruction(leftRightText);
                    // set parameters
                    leftRightJudgementStep.setMinimumInterStimulusInterval(minimumInterStimulusInterval);
                    leftRightJudgementStep.setMaximumInterStimulusInterval(maximumInterStimulusInterval);
                    leftRightJudgementStep.setNumberOfAttempts(numberOfAttempts);
                    leftRightJudgementStep.setTimeout(timeout);
                    leftRightJudgementStep.setShouldDisplayAnswer(shouldDisplayAnswer);
                    stepList.add(leftRightJudgementStep);
                }
            }
            // Flip to the other image set if doing both (ignored if imageSetCount == 1)
            handImages = !handImages;
        }
        // Conclusion step
        if (!optionList.contains(TaskExcludeOption.CONCLUSION)) {
            stepList.add(TaskFactory.makeCompletionStep(context));
        }
        return new OrderedTask(identifier, stepList);
    }

    private static String stepIdentifierWithImageSetId(String stepId, String imageId) {
        if (imageId == null) {
            return stepId;
        }
        return String.format("%s.%s", stepId, imageId);
    }
    
    private static String significantFigures(double number) {
        String string = String.valueOf(number);
        if (string.contains(".")) {
            string = string.replaceAll("0*$","").replaceAll("\\.$","");
        }
        return string;
    }
}

