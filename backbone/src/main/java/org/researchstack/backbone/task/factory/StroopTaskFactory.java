package org.researchstack.backbone.task.factory;

import android.annotation.SuppressLint;
import android.content.Context;

import org.researchstack.backbone.R;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.CountdownStep;
import org.researchstack.backbone.step.active.StroopStep;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.utils.ResUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static org.researchstack.backbone.task.factory.TaskFactory.Constants.*;
import static org.researchstack.backbone.task.factory.TaskOptions.ImageOption.*;

/**
 * Created by Dr David W. Evans, 2021.
 */

public class StroopTaskFactory {

    private static final int DEFAULT_COUNTDOWN_DURATION = 5; // seconds

    /**
     * Returns a predefined Stroop task that tests participants selective attention and cognitive flexibility.
     * In a stroop task, the participant is shown a text. The text is a name of a color, but the text
     * is printed in a color that may or may not be denoted by the name. In each attempt of the task,
     * the participant has to press the button that corresponds to the first letter of the color in which
     * the text is printed. The participant has to ignore the name of the color written in the text,
     * but respond based on the color of the text.
     * <p>
     * A stroop task finishes when the user has completed all the attempts, or when all of the questions
     * have timed out, irrespective of correct or incorrect answers.
     * <p>
     * Data collected by the task is in the form of a `StroopResult` object.
     *
     * @param context                       Can be app or activity, used for resources
     * @param identifier                    The task identifier to use for this task, appropriate to
     *                                      the study.
     * @param intendedUseDescription        A localized string describing the intended use of the data
     *                                      collected. If the value of this parameter is `nil`, the
     *                                      default localized text is displayed.
     * @param minimumInterStimulusInterval  The minimum interval before the stimulus is delivered.
     * @param maximumInterStimulusInterval  The maximum interval before the stimulus is delivered.
     * @param timeout                       The time period (in seconds) permitted after the stimulus
     *                                      begins, until the attempt fails, if a button is not pressed.
     * @param numberOfAttempts              Total number of stroop questions to include in the task.
     * @param optionList                    Options that affect the features of the predefined task.
     * @return                              An active left/right judgement task that can be presented
     *                                      with an `ActiveTaskActivity` object.
     **/


    public static OrderedTask stroopTaskFactory(
            Context context,
            String identifier,
            String intendedUseDescription,
            double minimumInterStimulusInterval,
            double maximumInterStimulusInterval,
            double timeout,
            int numberOfAttempts,
            List<TaskExcludeOption> optionList)
    {
        List<Step> stepList = new ArrayList<>();

        if (!optionList.contains(TaskExcludeOption.INSTRUCTIONS)) {

            { // Instruction step 1

                String title = context.getString(R.string.rsb_STROOP_TASK_TITLE);
                InstructionStep instructionStep1 = new InstructionStep(Instruction1StepIdentifier, title, intendedUseDescription);
                instructionStep1.setMoreDetailText(context.getString(R.string.rsb_STROOP_TASK_INTRO1_DETAIL_TEXT));
                instructionStep1.setImage(ResUtils.Stroop.PHONE_STROOP_LABEL);
                stepList.add(instructionStep1);

            }

            {   // Instruction step 2

                String title = context.getString(R.string.rsb_STROOP_TASK_TITLE);
                InstructionStep instructionStep2 = new InstructionStep(Instruction2StepIdentifier, title, intendedUseDescription);
                instructionStep2.setMoreDetailText(String.format(
                        context.getString(R.string.rsb_STROOP_TASK_INTRO2_DETAIL_TEXT),
                        String.valueOf(numberOfAttempts),
                        significantFigures(timeout)));
                instructionStep2.setImage(ResUtils.Stroop.PHONE_STROOP_BUTTON);
                stepList.add(instructionStep2);

            }
        }

        {   // Countdown step

            String countdownStepText = context.getString(R.string.rsb_STROOP_TASK_STEP_TEXT);
            CountdownStep countdownStep = new CountdownStep(CountdownStepIdentifier);
            countdownStep.setStepDuration(DEFAULT_COUNTDOWN_DURATION);
            countdownStep.setSpokenInstruction(countdownStepText); // spoken instructions during countdown
            countdownStep.setTitle(context.getString(R.string.rsb_STROOP_TASK_TITLE));
            countdownStep.setOptional(false);
            stepList.add(countdownStep);

        }

        {   // Stroop step

            StroopStep stroopStep = new StroopStep(StroopStepIdentifier);
            String stroopTitle = context.getString(R.string.rsb_STROOP_TASK_TITLE);
            stroopStep.setTitle(stroopTitle);
            String stroopText = context.getString(R.string.rsb_STROOP_TASK_STEP_TEXT);
            stroopStep.setText(stroopText);
            stroopStep.setSpokenInstruction(stroopText);
            // set parameters
            stroopStep.setNumberOfAttempts(numberOfAttempts);
            stroopStep.setMinimumInterStimulusInterval(minimumInterStimulusInterval);
            stroopStep.setMaximumInterStimulusInterval(maximumInterStimulusInterval);
            stroopStep.setTimeout(timeout);
            stepList.add(stroopStep);
        }

        // Conclusion step
        if (!optionList.contains(TaskExcludeOption.CONCLUSION)) {
            stepList.add(TaskFactory.makeCompletionStep(context));
        }
        return new OrderedTask(identifier, stepList);
    }

    private static String significantFigures(double number) {
        String string = String.valueOf(number);
        if (string.contains(".")) {
            string = string.replaceAll("0*$","").replaceAll("\\.$","");
        }
        return string;
    }
}

