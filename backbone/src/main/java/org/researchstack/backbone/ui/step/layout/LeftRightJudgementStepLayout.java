package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.LeftRightJudgementResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.LeftRightJudgementStep;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


import static org.researchstack.backbone.task.factory.TaskOptions.ImageOption.*;

/**
 * Created by Dr David W. Evans in January 2021.
 *
 * The LeftRightJudgementStepLayout displays a series of images selected by name from the assets folder.
 * There are two buttons at the bottom of the screen ('left' and 'right'), through which the user
 * is instructed to match their response to the image presented.
 *
 */

public class LeftRightJudgementStepLayout extends ActiveStepLayout {

    protected LeftRightJudgementStep leftRightJudgementStep;
    protected LeftRightJudgementResult leftRightJudgementResult;
    protected Drawable drawable;

    private double _startTime;
    Handler interStimulusIntervalHandler;
    Handler timeoutHandler;
    Handler timeoutNotificationHandler;
    Handler displayAnswerHandler;
    Runnable interStimulusIntervalRunnable;
    Runnable timeoutRunnable;
    Runnable timeoutNotificationRunnable;
    Runnable displayAnswerRunnable;
    private String[] _fileNameArray;
    private String[] _imagePaths;
    private int _imageCount;
    private int _leftCount;
    private int _rightCount;
    private int _leftSumCorrect;
    private int _rightSumCorrect;
    private int _timedOutCount;
    private double _percentTimedOut;
    private double _leftPercentCorrect;
    private double _rightPercentCorrect;
    private double _meanLeftDuration;
    private double _varianceLeftDuration;
    private double _stdLeftDuration;
    private double _prevMl;
    private double _newMl;
    private double _prevSl;
    private double _newSl;
    private double _meanRightDuration;
    private double _varianceRightDuration;
    private double _stdRightDuration;
    private double _prevMr;
    private double _newMr;
    private double _prevSr;
    private double _newSr;
    private boolean _match;
    private boolean _timedOut;
    private boolean _imageHidden;
    private boolean _shuffled = false;

    private static final int LEFT_BUTTON = 1;
    private static final int RIGHT_BUTTON = 2;
    private static final String ImageDirectory = "images";

    private Button leftButton;
    private Button rightButton;
    private int buttonID;

    protected TextView leftRightJudgementCountTextView;
    protected TextView leftRightJudgementTimeoutTextView;
    protected TextView leftRightJudgementAnswerTextView;

    public LeftRightJudgementStepLayout(Context context) {
        super(context);
    }

    public LeftRightJudgementStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LeftRightJudgementStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LeftRightJudgementStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        super.initialize(step, result);
    }

    @Override
    protected void validateStep(Step step) {
        super.validateStep(step);

        leftRightJudgementStep = (LeftRightJudgementStep) step;

        if (!(step instanceof LeftRightJudgementStep)) {
            throw new IllegalStateException("LeftRightJudgementStepLayout must have a LeftRightJudgementStep");
        }
        int minimumAttempts = 10;
        if (leftRightJudgementStep.getNumberOfAttempts() < minimumAttempts) {
            throw new IllegalStateException(String.format("number of attempts should be greater or equal to %1$s.", String.valueOf(minimumAttempts)));
        }
        if (leftRightJudgementStep.getMinimumInterStimulusInterval() <= 0) {
            throw new IllegalStateException("minimumInterStimulusInterval must be greater than zero");
        }
        if (leftRightJudgementStep.getMaximumInterStimulusInterval() < leftRightJudgementStep.getMinimumInterStimulusInterval()) {
            throw new IllegalStateException("maximumInterStimulusInterval cannot be less than minimumInterStimulusInterval");
        }
        if (leftRightJudgementStep.getTimeout() <= 0) {
            throw new IllegalStateException("timeout must be greater than zero");
        }
        if (!(leftRightJudgementStep.getImageOption().equals(HANDS)) &&
                !(leftRightJudgementStep.getImageOption().equals(FEET)) &&
                !(leftRightJudgementStep.getImageOption().equals(BOTH))) {
            throw new IllegalStateException("LEFT_RIGHT_JUDGEMENT_IMAGE_OPTION_ERROR");
        }
        if (leftRightJudgementStep.getNumberOfAttempts() > arrayOfShuffledFileNamesFromDirectory().length)  {
            throw new IllegalStateException("Number of requested attempts is beyond the number of available images");
        }
    }

    @Override
    public void setupSubmitBar() {
        super.setupSubmitBar();
        submitBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void setupActiveViews() {
        super.setupActiveViews();

        LayoutInflater.from(getContext())
                .inflate(R.layout.rsb_step_layout_left_right_judgement, this, true);

        leftButton = (Button) findViewById(R.id.rsb_left_right_judgement_button_left);
        rightButton = (Button) findViewById(R.id.rsb_left_right_judgement_button_right);
        setupButtons(getContext());
        hideButtons();

        imageView = (ImageView) findViewById(R.id.rsb_left_right_judgement_image_view);
        hideImage();

        leftRightJudgementCountTextView = (TextView) findViewById(R.id.rsb_left_right_judgement_count_textview);
        leftRightJudgementTimeoutTextView = (TextView) findViewById(R.id.rsb_left_right_judgement_timeout_textview);
        leftRightJudgementAnswerTextView = (TextView) findViewById(R.id.rsb_left_right_judgement_answer_textview);
        hideCountText();
        hideTimeoutText();
        hideAnswerText();
    }

    @Override
    public void start() {
        super.start();

        startInterStimulusInterval();
    }

    private void setupButtons(Context context) {
        leftButton.setText(context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_LEFT_BUTTON));
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonID = LEFT_BUTTON;
                buttonPressed();
            }
        });
        rightButton.setText(context.getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_RIGHT_BUTTON));
        rightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonID = RIGHT_BUTTON;
                buttonPressed();
            }
        });
    }

    private void buttonPressed() {
        if (!_imageHidden) {
            hideButtons();
            stopTimerHandler(timeoutHandler, timeoutRunnable);
            _timedOut = false;
            double duration = reactionTime();
            String next = nextFileNameInQueue();
            String sidePresented = sidePresented();
            String view = viewPresented();
            String orientation = orientationPresented();
            int rotation = rotationPresented();
            // evaluate matches according to button pressed
            String sideSelected;
            if (buttonID == LEFT_BUTTON) {
                sideSelected = "Left";
                _match = sidePresented.equals(sideSelected);
                _leftSumCorrect = (_match) ? _leftSumCorrect + 1 : _leftSumCorrect;
                calculateMeanAndStdReactionTimes(sidePresented, duration, _match);
                calculatePercentagesForSides(sidePresented, _timedOut);
                createResultfromImage(next,view, rotation, orientation, _match, sidePresented, sideSelected, duration);
            }
            else if (buttonID == RIGHT_BUTTON) {
                sideSelected = "Right";
                _match = sidePresented.equals(sideSelected);
                _rightSumCorrect = (_match) ? _rightSumCorrect + 1 : _rightSumCorrect;
                calculateMeanAndStdReactionTimes(sidePresented, duration, _match);
                calculatePercentagesForSides(sidePresented, _timedOut);
                createResultfromImage(next, view, rotation, orientation, _match, sidePresented, sideSelected, duration);
            }
            if (leftRightJudgementStep.getShouldDisplayAnswer()) {
                displayAnswerWhenButtonPressed(sidePresented, _match);
            } else {
                startInterStimulusInterval();
            }
        }
    }

    @Override
    public void stop() {
        super.stop();

        // stop timers
        stopTimerHandler(timeoutHandler, timeoutRunnable);
        stopTimerHandler(timeoutNotificationHandler, timeoutNotificationRunnable);
        stopTimerHandler(displayAnswerHandler, displayAnswerRunnable);
        stopTimerHandler(interStimulusIntervalHandler, interStimulusIntervalRunnable);

        // remove listeners
        leftButton.setOnClickListener(null);
        rightButton.setOnClickListener(null);
    }

    private void configureCountText() {
        String countText = String.format(getContext().getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TASK_IMAGE_COUNT), 
                String.valueOf(_imageCount),
                String.valueOf(leftRightJudgementStep.getNumberOfAttempts()));
        setCountText(countText);
    }

    void startTimeoutTimer() {
        double timeout = leftRightJudgementStep.getTimeout();
        if (timeout > 0) {
            timeoutHandler = new Handler();
            timeoutRunnable = new Runnable() {
                public void run() {
                    timeoutTimerDidFire();
                }
            };
            timeoutHandler.postDelayed(
                    timeoutRunnable,
                    (long) timeout * 1000);
        }
    }

    void displayTimeoutNotification(String sidePresented) {
        hideImage();
        hideButtons();
        String timeoutText =
                getContext().getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_TIMEOUT_NOTIFICATION); 
        setTimeoutText(timeoutText);
        if (leftRightJudgementStep.getShouldDisplayAnswer()) {
            setAnswerText(answerForSidePresented(sidePresented));
        }
        // initiate timer
        timeoutNotificationHandler = new Handler();
        timeoutNotificationRunnable = new Runnable() {
            public void run() {
                startInterStimulusInterval();
            }
        };
        timeoutNotificationHandler.postDelayed(
                timeoutNotificationRunnable,
                (long) 2.0 * 1000); // display notification for 2.0 seconds
    }

    String answerForSidePresented(String sidePresented) {
        hideImage();
        hideButtons();
        String answerText = null;
        if (leftRightJudgementStep.getImageOption().equals(HANDS)) {
            if (sidePresented.equals("Left")) {
                answerText = getContext().getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_ANSWER_LEFT_HAND); 
            } else if (sidePresented.equals("Right")) {
                answerText = getContext().getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_ANSWER_RIGHT_HAND); 
            }
        } else if (leftRightJudgementStep.getImageOption().equals(FEET)) {
            if (sidePresented.equals("Left")) {
                answerText = getContext().getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_ANSWER_LEFT_FOOT); 
            } else if (sidePresented.equals("Right")) {
                answerText = getContext().getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_ANSWER_RIGHT_FOOT); 
            }
        }
        return answerText;
    }

    private void displayAnswerWhenButtonPressed(String sidePresented, boolean match) {
        String answerText = answerForSidePresented(sidePresented);
        String text;
        if (match) {
            text = String.format("%1$s\n%2$s", getContext().getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_ANSWER_CORRECT), answerText); 
        } else {
            text = String.format("%1$s\n%2$s", getContext().getString(R.string.rsb_LEFT_RIGHT_JUDGEMENT_ANSWER_INCORRECT), answerText); 
        }
        setAnswerText(text);
        // initiate timer handler
        displayAnswerHandler = new Handler();
        displayAnswerRunnable = new Runnable() {
            public void run() {
                startInterStimulusInterval();
            }
        };
        displayAnswerHandler.postDelayed(
                displayAnswerRunnable,
                (long) (2.0 * 1000)); // display notification for 2.0 seconds
    }

    private void startInterStimulusInterval() {
        stopTimerHandler(timeoutNotificationHandler, timeoutNotificationRunnable);
        stopTimerHandler(displayAnswerHandler, displayAnswerRunnable);
        hideImage();
        hideCountText();
        hideTimeoutText();
        hideAnswerText();
        double interStimulusInterval = interStimulusInterval();
        // initiate timer handler
        if (interStimulusInterval > 0) {
            interStimulusIntervalHandler = new Handler();
            interStimulusIntervalRunnable = new Runnable() {
                public void run() {
                    startNextQuestionOrFinish();
                }
            };
            interStimulusIntervalHandler.postDelayed(
                    interStimulusIntervalRunnable,
                    (long) interStimulusInterval * 1000);
        }
    }

    private double interStimulusInterval() {
        double timeInterval;
        double range = leftRightJudgementStep.getMaximumInterStimulusInterval() - leftRightJudgementStep.getMinimumInterStimulusInterval();
        if (range == 0 || leftRightJudgementStep.getMaximumInterStimulusInterval() == leftRightJudgementStep.getMinimumInterStimulusInterval() ||
                _imageCount == leftRightJudgementStep.getNumberOfAttempts()) { // use min interval after last image of set is presented
            timeInterval = leftRightJudgementStep.getMinimumInterStimulusInterval();
        } else {
            Random rand = new Random();
            double randomFactor = rand.nextInt((int)(range * 1000)) + 1; // non-zero random number of milliseconds between min/max limits
            timeInterval = (randomFactor / 1000) + leftRightJudgementStep.getMinimumInterStimulusInterval(); // in seconds
        }
        return timeInterval;
    }

    private void showButtons() {
        if (leftButton != null) {
            leftButton.setVisibility(View.VISIBLE);
        }
        if (rightButton != null){
            rightButton.setVisibility(View.VISIBLE);
        }
    }

    private void hideButtons() {
        if (leftButton != null) {
            leftButton.setVisibility(View.GONE);
        }
        if (rightButton != null) {
            rightButton.setVisibility(View.GONE);
        }
    }

    private void startQuestion() {
        _imageCount++; // increment on call
        String imageName = nextFileNameInQueue();
        if (_imageCount == 1) {
            hideButtons();
        }
        setImage(imageName);
        showButtons();
        configureCountText();
        _startTime = System.currentTimeMillis();
        startTimeoutTimer();
    }

    private void stopTimerHandler(Handler handler, Runnable runnable) {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    private void setCountText(String countText) {
        if (leftRightJudgementCountTextView != null) {
            leftRightJudgementCountTextView.setText(countText);
            leftRightJudgementCountTextView.setVisibility(View.VISIBLE);
        }
    }

    private void hideCountText() {
        if (leftRightJudgementCountTextView != null) {
            leftRightJudgementCountTextView.setVisibility(View.GONE);
        }
    }

    public void setTimeoutText(String timeoutText) {
        if (leftRightJudgementTimeoutTextView != null) {
            leftRightJudgementTimeoutTextView.setText(timeoutText);
            leftRightJudgementTimeoutTextView.setVisibility(View.VISIBLE);
        }
    }

    private void hideTimeoutText() {
        if (leftRightJudgementTimeoutTextView != null) {
            leftRightJudgementTimeoutTextView.setVisibility(View.GONE);
        }
    }

    public void setAnswerText(String answerText) {
        if (leftRightJudgementAnswerTextView != null) {
            leftRightJudgementAnswerTextView.setText(answerText);
            leftRightJudgementAnswerTextView.setVisibility(View.VISIBLE);
        }
    }

    private void hideAnswerText() {
        if (leftRightJudgementAnswerTextView != null) {
            leftRightJudgementAnswerTextView.setVisibility(View.GONE);
        }
    }

    private void hideImage() {
        imageView.setVisibility(GONE);
        _imageHidden = true;
    }

    private void setImage(String imageName) {
        String imageReference = String.format("%1$s/%2$s", ImageDirectory, imageName);
        AssetManager assetManager = getContext().getAssets(); 
        // get input stream
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(imageReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // load image as drawable
        drawable = Drawable.createFromStream(inputStream, null);
        // set image within ImageView on main thread
        imageView.post(
                new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageDrawable(drawable);
                    }
                });
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setVisibility(View.VISIBLE);
        _imageHidden = false;
    }

    private String sidePresented() {
        String fileName = nextFileNameInQueue();
        String sidePresented = null;
        if (fileName.contains("lh") || fileName.contains("lf")) {
            sidePresented = "Left";
            _leftCount ++;
        } else if (fileName.contains("rh") || fileName.contains("rf")) {
            sidePresented = "Right";
            _rightCount ++;
        }
        return sidePresented;
    }

    private String viewPresented() {
        String fileName = nextFileNameInQueue();
        String anglePresented = null;
        if (leftRightJudgementStep.getImageOption().equals(HANDS)) {
            if (fileName.contains("lh1") ||
            fileName.contains("rh1")) {
                anglePresented = "Back";
            } else if (fileName.contains("lh2") ||
                   fileName.contains("rh2")) {
                anglePresented = "Palm";
            } else if (fileName.contains("lh3") ||
                   fileName.contains("rh3")) {
                anglePresented = "Pinkie";
            } else if (fileName.contains("lh4") ||
                   fileName.contains("rh4")) {
                anglePresented = "Thumb";
            } else if (fileName.contains("lh5") ||
                   fileName.contains("rh5")) {
                anglePresented = "Wrist";
            }
        } else if (leftRightJudgementStep.getImageOption().equals(FEET)) {
            if (fileName.contains("lf1") ||
            fileName.contains("rf1")) {
                anglePresented = "Top";
            } else if (fileName.contains("lf2") ||
                   fileName.contains("rf2")) {
                anglePresented = "Sole";
            } else if (fileName.contains("lf3") ||
                   fileName.contains("rf3")) {
                anglePresented = "Heel";
            } else if (fileName.contains("lf4") ||
                   fileName.contains("rf4")) {
                anglePresented = "Toes";
            } else if (fileName.contains("lf5") ||
                   fileName.contains("rf5")) {
                anglePresented = "Inside";
            } else if (fileName.contains("lf6") ||
                   fileName.contains("rf6")) {
                anglePresented = "Outside";
            }
        }
        return anglePresented;
    }

    private String orientationPresented() {
        String fileName = nextFileNameInQueue();
        String anglePresented = null;
        String viewPresented = viewPresented();
        if (leftRightJudgementStep.getImageOption().equals(HANDS)) {
            if (fileName.contains("lh")) { // left hand
                if (viewPresented.equals("Back") ||
                viewPresented.equals("Palm") ||
                viewPresented.equals("Pinkie") ||
                viewPresented.equals("Thumb")) {
                    if (fileName.contains("000cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("030cw") ||
                               fileName.contains("060cw") ||
                               fileName.contains("090cw") ||
                               fileName.contains("120cw") ||
                               fileName.contains("150cw")) {
                        anglePresented = "Medial";
                    } else if (fileName.contains("180cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("210cw") ||
                               fileName.contains("240cw") ||
                               fileName.contains("270cw") ||
                               fileName.contains("300cw") ||
                               fileName.contains("330cw")) {
                        anglePresented = "Lateral";
                    }
                } else if (viewPresented.equals("Wrist")) {
                    if (fileName.contains("000cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("030cw") ||
                           fileName.contains("060cw") ||
                           fileName.contains("090cw") ||
                           fileName.contains("120cw") ||
                           fileName.contains("150cw")) {
                        anglePresented = "Lateral";
                    } else if (fileName.contains("180cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("210cw") ||
                           fileName.contains("240cw") ||
                           fileName.contains("270cw") ||
                           fileName.contains("300cw") ||
                           fileName.contains("330cw")) {
                        anglePresented = "Medial";
                    }
                }
            } else if (fileName.contains("rh")) { // right hand
                if (viewPresented.equals("Back") ||
                viewPresented.equals("Palm") ||
                viewPresented.equals("Pinkie") ||
                viewPresented.equals("Thumb")) {
                    if (fileName.contains("000cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("030cw") ||
                           fileName.contains("060cw") ||
                           fileName.contains("090cw") ||
                           fileName.contains("120cw") ||
                           fileName.contains("150cw")) {
                        anglePresented = "Lateral";
                    } else if (fileName.contains("180cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("210cw") ||
                           fileName.contains("240cw") ||
                           fileName.contains("270cw") ||
                           fileName.contains("300cw") ||
                           fileName.contains("330cw")) {
                        anglePresented = "Medial";
                    }
                } else if (viewPresented.equals("Wrist")) {
                    if (fileName.contains("000cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("030cw") ||
                           fileName.contains("060cw") ||
                           fileName.contains("090cw") ||
                           fileName.contains("120cw") ||
                           fileName.contains("150cw")) {
                        anglePresented = "Medial";
                    } else if (fileName.contains("180cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("210cw") ||
                           fileName.contains("240cw") ||
                           fileName.contains("270cw") ||
                           fileName.contains("300cw") ||
                           fileName.contains("330cw")) {
                        anglePresented = "Lateral";
                    }
                }
            }
        } else if (leftRightJudgementStep.getImageOption().equals(FEET)) {
            if (fileName.contains("lf")) { // left foot
                if (viewPresented.equals("Top") ||
                viewPresented.equals("Heel")) {
                    if (fileName.contains("000cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("030cw") ||
                               fileName.contains("060cw") ||
                               fileName.contains("090cw") ||
                               fileName.contains("120cw") ||
                               fileName.contains("150cw")) {
                        anglePresented = "Medial";
                    } else if (fileName.contains("180cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("210cw") ||
                               fileName.contains("240cw") ||
                               fileName.contains("270cw") ||
                               fileName.contains("300cw") ||
                               fileName.contains("330cw")) {
                        anglePresented = "Lateral";
                    }
                } else if (viewPresented.equals("Sole") ||
                       viewPresented.equals("Toes") ||
                       viewPresented.equals("Inside") ||
                       viewPresented.equals("Outside")) {
                    if (fileName.contains("000cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("030cw") ||
                           fileName.contains("060cw") ||
                           fileName.contains("090cw") ||
                           fileName.contains("120cw") ||
                           fileName.contains("150cw")) {
                        anglePresented = "Lateral";
                    } else if (fileName.contains("180cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("210cw") ||
                           fileName.contains("240cw") ||
                           fileName.contains("270cw") ||
                           fileName.contains("300cw") ||
                           fileName.contains("330cw")) {
                        anglePresented = "Medial";
                    }
                }
            } else if (fileName.contains("rf")) { // right foot
                if (viewPresented.equals("Top") ||
                viewPresented.equals("Heel")) {
                    if (fileName.contains("000cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("030cw") ||
                           fileName.contains("060cw") ||
                           fileName.contains("090cw") ||
                           fileName.contains("120cw") ||
                           fileName.contains("150cw")) {
                        anglePresented = "Lateral";
                    } else if (fileName.contains("180cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("210cw") ||
                           fileName.contains("240cw") ||
                           fileName.contains("270cw") ||
                           fileName.contains("300cw") ||
                           fileName.contains("330cw")) {
                        anglePresented = "Medial";
                    }
                } else if (viewPresented.equals("Sole") ||
                       viewPresented.equals("Toes") ||
                       viewPresented.equals("Inside") ||
                       viewPresented.equals("Outside")) {
                    if (fileName.contains("000cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("030cw") ||
                           fileName.contains("060cw") ||
                           fileName.contains("090cw") ||
                           fileName.contains("120cw") ||
                           fileName.contains("150cw")) {
                        anglePresented = "Medial";
                    } else if (fileName.contains("180cw")) {
                        anglePresented = "Neutral";
                    } else if (fileName.contains("210cw") ||
                           fileName.contains("240cw") ||
                           fileName.contains("270cw") ||
                           fileName.contains("300cw") ||
                           fileName.contains("330cw")) {
                        anglePresented = "Lateral";
                    }
                }
            }
        }
        return anglePresented;
    }

    private int rotationPresented() {
        String fileName = nextFileNameInQueue();
        int rotationPresented = 0;
        if (fileName.contains("000cw")) {
            rotationPresented = 0;
        } else if (fileName.contains("030cw") ||
        fileName.contains("330cw")) {
            rotationPresented = 30;
        } else if (fileName.contains("060cw") ||
            fileName.contains("300cw")) {
            rotationPresented = 60;
        } else if (fileName.contains("090cw") ||
            fileName.contains("270cw")) {
            rotationPresented = 90;
        } else if (fileName.contains("120cw") ||
            fileName.contains("240cw")) {
            rotationPresented = 120;
        } else if (fileName.contains("150cw") ||
            fileName.contains("210cw")) {
            rotationPresented = 150;
        } else if (fileName.contains("180cw")) {
            rotationPresented = 180;
        }
        return rotationPresented;
    }

    private void timeoutTimerDidFire() {
        stopTimerHandler(timeoutHandler, timeoutRunnable);
        hideButtons();
        double duration = reactionTime();
        String imageName = nextFileNameInQueue();
        String sidePresented = sidePresented();
        String view = viewPresented();
        String orientation = orientationPresented();
        int rotation = rotationPresented();
        String sideSelected = "None";
        _match = false;
        _timedOut = true;
        _timedOutCount++;
        calculatePercentagesForSides(sidePresented, _timedOut);
        createResultfromImage(imageName, view, rotation, orientation, _match, sidePresented, sideSelected, duration);
        displayTimeoutNotification(sidePresented);
    }

    private void calculatePercentagesForSides(String sidePresented, boolean timeout) {
        if (sidePresented.equals("Left")) {
            if (_leftCount > 0) { // prevent zero denominator
                _leftPercentCorrect = (100 * (double)_leftSumCorrect) / (double)_leftCount;
            }
        } else if (sidePresented.equals("Right")) {
            if (_rightCount > 0) { // prevent zero denominator
                _rightPercentCorrect = (100 * (double)_rightSumCorrect) / (double)_rightCount;
            }
        }
        if (_imageCount > 0) { // prevent zero denominator
            _percentTimedOut = (100 * (double)_timedOutCount) / (double)_imageCount;
        }
    }

    private void calculateMeanAndStdReactionTimes(String sidePresented, double duration, boolean match) {
        // calculate mean and unbiased standard deviation of duration for correct matches only
        // (using Welford's algorithm: Welford. (1962) Technometrics 4(3), 419-420)
        if (sidePresented.equals("Left") && (match)) {
            if (_leftSumCorrect == 1) {
                _prevMl = _newMl = duration;
                _prevSl = 0;
            } else {
                _newMl = _prevMl + (duration - _prevMl) / (double)_leftSumCorrect;
                _newSl += _prevSl + (duration - _prevMl) * (duration - _newMl);
                _prevMl = _newMl;
            }
            _meanLeftDuration = (_leftSumCorrect > 0) ? _newMl : 0;
            _varianceLeftDuration = (_leftSumCorrect > 1) ? _newSl / ((double)_leftSumCorrect - 1) : 0;
            if (_varianceLeftDuration > 0) {
                _stdLeftDuration = Math.sqrt(_varianceLeftDuration);
            }
        } else if (sidePresented.equals("Right") && (match)) {
            if (_rightSumCorrect == 1) {
                _prevMr = _newMr = duration;
                _prevSr = 0;
            } else {
                _newMr = _prevMr + (duration - _prevMr) / (double)_rightSumCorrect;
                _newSr += _prevSr + (duration - _prevMr) * (duration - _newMr);
                _prevMr = _newMr;
            }
            _meanRightDuration = (_rightSumCorrect > 0) ? _newMr : 0;
            _varianceRightDuration = (_rightSumCorrect > 1) ? _newSr / ((double)_rightSumCorrect - 1) : 0;
            if (_varianceRightDuration > 0) {
                _stdRightDuration = Math.sqrt(_varianceRightDuration);
            }
        }
    }

    private double reactionTime() {
        double endTime = System.currentTimeMillis();
        double duration = (endTime - _startTime);
        return duration / 1000;
    }

    private String nextFileNameInQueue() {
        String[] fileNameArray = arrayOfImageFileNamesForEachAttempt();
        String fileName = fileNameArray[_imageCount - 1];
        return fileName;
    }

    private String[] arrayOfImageFileNamesForEachAttempt() {
        int imageQueueLength = leftRightJudgementStep.getNumberOfAttempts();
        _imagePaths = arrayOfShuffledFileNamesFromDirectory();
        // Copy required number of image queue elements to local array
        String[] imageQueueArray = new String[imageQueueLength]; //NSMutableArray *imageQueueArray = [NSMutableArray arrayWithCapacity:imageQueueLength];
        System.arraycopy(_imagePaths, 0, imageQueueArray, 0, imageQueueLength);
        return imageQueueArray;
    }

    private String[] arrayOfShuffledFileNamesFromDirectory () {
        if (_shuffled == false) { // build shuffled array only once
            _fileNameArray = arrayOfAllFileNamesFromDirectory(ImageDirectory);
            Collections.shuffle(Arrays.asList(_fileNameArray)); // shuffle list
            _shuffled = true;
        }
        return _fileNameArray;
    }

    private String[] arrayOfAllFileNamesFromDirectory(String directory) {
        AssetManager assetManager = getContext().getAssets();
        List<String> listOfAllFiles = new ArrayList<>();
        try {
            listOfAllFiles = Arrays.asList(assetManager.list(directory));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> listOfEligibleFiles = new ArrayList<>();
        for (String fileName : listOfAllFiles) {
            if (fileFilterForImages(fileName)) {
                listOfEligibleFiles.add(fileName);
            }
        }
        //setNumberOfImages(listOfEligibleFiles.size());
        Collections.shuffle(listOfEligibleFiles); // shuffle list
        String[] fileNameArray = new String[listOfEligibleFiles.size()];
        listOfEligibleFiles.toArray(fileNameArray); // copy list elements to array
        return fileNameArray;
    }

    public boolean fileFilterForImages(String fileName) {
        boolean filter = false;
        if (leftRightJudgementStep.getImageOption().equals(HANDS)) {
            if (((fileName.contains("lh")) || (fileName.contains("rh"))) &&
                    (fileName.endsWith("cw.png"))) {
                filter = true;
            }
        } else if (leftRightJudgementStep.getImageOption().equals(FEET)) {
            if (((fileName.contains("lf")) || (fileName.contains("rf"))) &&
                    (fileName.endsWith("cw.png"))) {
                filter = true;
            }
        }
        return filter;
    }

    private void startNextQuestionOrFinish() {
        stopTimerHandler(interStimulusIntervalHandler, interStimulusIntervalRunnable);
        if ((_imageCount) == (leftRightJudgementStep.getNumberOfAttempts())) {
            stop();
         } else {
            startQuestion();
         }
    }

    private void createResultfromImage (String imageName,
                                String view,
                                int rotation,
                                String orientation,
                                boolean match,
                                String sidePresented,
                                String sideSelected,
                                double duration) {

        leftRightJudgementResult = new LeftRightJudgementResult(leftRightJudgementStep.getIdentifier());

        // image results
        leftRightJudgementResult.setImageNumber(_imageCount);
        leftRightJudgementResult.setImageName(imageName);
        leftRightJudgementResult.setViewPresented(view);
        leftRightJudgementResult.setOrientationPresented(orientation);
        leftRightJudgementResult.setRotationPresented(rotation);
        leftRightJudgementResult.setReactionTime(duration);
        leftRightJudgementResult.setSidePresented(sidePresented);
        leftRightJudgementResult.setSideSelected(sideSelected);
        leftRightJudgementResult.setSideMatch(match);
        leftRightJudgementResult.setTimedOut(_timedOut);
        // task results
        leftRightJudgementResult.setLeftImages(_leftCount);
        leftRightJudgementResult.setRightImages(_rightCount);
        leftRightJudgementResult.setLeftPercentCorrect(_leftPercentCorrect);
        leftRightJudgementResult.setRightPercentCorrect(_rightPercentCorrect);
        leftRightJudgementResult.setPercentTimedOut(_percentTimedOut);
        leftRightJudgementResult.setLeftMeanReactionTime(_meanLeftDuration);
        leftRightJudgementResult.setRightMeanReactionTime(_meanRightDuration);
        leftRightJudgementResult.setLeftSDReactionTime(_stdLeftDuration);
        leftRightJudgementResult.setRightSDReactionTime(_stdRightDuration);

        stepResult.setResultForIdentifier(leftRightJudgementResult.getIdentifier(), leftRightJudgementResult);
    }
}
