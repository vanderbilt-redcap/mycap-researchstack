package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.StroopResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.StroopStep;

import java.util.Random;

/**
 * Created by Dr David W. Evans, January 2021.
 *
 * The StroopStepLayout has four buttons at the bottom of the screen, through which the user
 * is instructed to select their answer.
 *
 */

public class StroopStepLayout extends ActiveStepLayout {

    protected StroopStep stroopStep;
    protected StroopResult stroopResult;

    private Handler interStimulusIntervalHandler;
    private Handler timeoutHandler;
    private Handler timeoutNotificationHandler;
    private Runnable interStimulusIntervalRunnable;
    private Runnable timeoutRunnable;
    private Runnable timeoutNotificationRunnable;

    private String _redString;
    private String _greenString;
    private String _blueString;
    private String _yellowString;
    private String _colorPresented;
    private String _textPresented;
    private double _startTime;
    private double _endTime;
    private double _meanReactionTime;
    private double _stdReactionTime;
    private double _varianceReactionTime;
    private double _prevM;
    private double _newM;
    private double _prevS;
    private double _newS;
    private double _percentCorrect;
    private double _percentTimedOut;
    private int _questionCount;
    private int _timedOutCount;
    private int _sumCorrect;
    private boolean colorLabelHidden;
    private boolean _timedOut;
    private boolean _match;

    private static final int R_BUTTON = 1;
    private static final int G_BUTTON = 2;
    private static final int B_BUTTON = 3;
    private static final int Y_BUTTON = 4;

    private Button RButton;
    private Button GButton;
    private Button BButton;
    private Button YButton;
    private int buttonID;

    protected TextView stroopColorLabelTextView;
    protected TextView stroopTimeoutTextView;

    public StroopStepLayout(Context context) {
        super(context);
    }

    public StroopStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StroopStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StroopStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        super.initialize(step, result);
    }

    @Override
    protected void validateStep(Step step) {
        super.validateStep(step);

        stroopStep = (StroopStep) step;

        if (!(step instanceof StroopStep)) {
            throw new IllegalStateException("StroopStepLayout must have a StroopStep");
        }
        int minimumAttempts = 10;
        if (stroopStep.getNumberOfAttempts() < minimumAttempts) {
            throw new IllegalStateException(String.format("number of attempts should be greater or equal to %1$s.", String.valueOf(minimumAttempts)));
        }
        if (stroopStep.getMinimumInterStimulusInterval() <= 0) {
            throw new IllegalStateException("minimumInterStimulusInterval must be greater than zero");
        }
        if (stroopStep.getMaximumInterStimulusInterval() < stroopStep.getMinimumInterStimulusInterval()) {
            throw new IllegalStateException("maximumInterStimulusInterval cannot be less than minimumInterStimulusInterval");
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
                .inflate(R.layout.rsb_step_layout_stroop, this, true);

        RButton = (Button) findViewById(R.id.rsb_left_right_judgement_button_red);
        GButton = (Button) findViewById(R.id.rsb_left_right_judgement_button_green);
        BButton = (Button) findViewById(R.id.rsb_left_right_judgement_button_blue);
        YButton = (Button) findViewById(R.id.rsb_left_right_judgement_button_yellow);
        setupButtons(getContext());
        hideButtons();

        setUpColorLabelStrings();
        stroopColorLabelTextView = (TextView) findViewById(R.id.rsb_stroop_color_label_textview);
        stroopTimeoutTextView = (TextView) findViewById(R.id.rsb_stroop_timeout_textview);
        hideColorLabelText();
        hideTimeoutText();
    }

    @Override
    public void start() {
        super.start();

        startInterStimulusInterval();
    }

    private void setUpColorLabelStrings() {
        _redString = getContext().getString(R.string.rsb_STROOP_COLOR_RED);
        _greenString = getContext().getString(R.string.rsb_STROOP_COLOR_GREEN);
        _blueString = getContext().getString(R.string.rsb_STROOP_COLOR_BLUE);
        _yellowString = getContext().getString(R.string.rsb_STROOP_COLOR_YELLOW);
    }

    private void setupButtons(Context context) {
        RButton.setText(context.getString(R.string.rsb_STROOP_COLOR_RED_INITIAL));
        RButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonID = R_BUTTON;
                buttonPressed();
            }
        });
        GButton.setText(context.getString(R.string.rsb_STROOP_COLOR_GREEN_INITIAL));
        GButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonID = G_BUTTON;
                buttonPressed();
            }
        });
        BButton.setText(context.getString(R.string.rsb_STROOP_COLOR_BLUE_INITIAL));
        BButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonID = B_BUTTON;
                buttonPressed();
            }
        });
        YButton.setText(context.getString(R.string.rsb_STROOP_COLOR_YELLOW_INITIAL));
        YButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonID = Y_BUTTON;
                buttonPressed();
            }
        });
    }

    private void buttonPressed() {
        if (!colorLabelHidden) {
            hideButtons();
            hideColorLabelText();
            stopTimerHandler(timeoutHandler, timeoutRunnable);
            _timedOut = false;
            double duration = reactionTime();
            String textPresented = _textPresented;
            String colorPresented = _colorPresented;
            // evaluate matches according to button pressed
            String colorSelected;
            if (buttonID == R_BUTTON) {
                colorSelected = _redString;
                _match = colorPresented.equals(colorSelected);
                _sumCorrect = (_match) ? _sumCorrect + 1 : _sumCorrect;
                calculateMeanAndStdReactionTime(duration, _match);
                calculatePercentages(_timedOut);
                createResult(colorPresented, textPresented, colorSelected, _match, duration);
            }
            else if (buttonID == G_BUTTON) {
                colorSelected = _greenString;
                _match = colorPresented.equals(colorSelected);
                _sumCorrect = (_match) ? _sumCorrect + 1 : _sumCorrect;
                calculateMeanAndStdReactionTime(duration, _match);
                calculatePercentages(_timedOut);
                createResult(colorPresented, textPresented, colorSelected, _match, duration);
            }
            else if (buttonID == B_BUTTON) {
                colorSelected = _blueString;
                _match = colorPresented.equals(colorSelected);
                _sumCorrect = (_match) ? _sumCorrect + 1 : _sumCorrect;
                calculateMeanAndStdReactionTime(duration, _match);
                calculatePercentages(_timedOut);
                createResult(colorPresented, textPresented, colorSelected, _match, duration);
            }
            else if (buttonID == Y_BUTTON) {
                colorSelected = _yellowString;
                _match = colorPresented.equals(colorSelected);
                _sumCorrect = (_match) ? _sumCorrect + 1 : _sumCorrect;
                calculateMeanAndStdReactionTime(duration, _match);
                calculatePercentages(_timedOut);
                createResult(colorPresented, textPresented, colorSelected, _match, duration);
            }
            startInterStimulusInterval();
        }
    }

    String textPresented() {
        String[] textArray = new String[4];
        textArray[0] = _redString;
        textArray[1] = _greenString;
        textArray[2] = _blueString;
        textArray[3] = _yellowString;
        // randomly select an index from the array
        Random random = new Random();
        int select = random.nextInt(textArray.length);
        return textArray[select];
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setTextColor() {
        int[] colorArray = new int[4];
        colorArray[0] = Color.RED;
        colorArray[1] = Color.GREEN;
        colorArray[2] = Color.BLUE;
        colorArray[3] = Color.YELLOW;
        // randomly select an index from the array
        Random random = new Random();
        int index = random.nextInt(colorArray.length);
        int color = colorArray[index];
        // set text color
        stroopColorLabelTextView.setTextColor(color);
        // produce string for matching
        if (color == Color.RED) {
            _colorPresented = _redString;
        }
        else if (color == Color.GREEN) {
            _colorPresented = _greenString;
        }
        else if (color == Color.BLUE) {
            _colorPresented = _blueString;
        }
        else if (color == Color.YELLOW) {
            _colorPresented = _yellowString;
        }
    }

    @Override
    public void stop() {
        super.stop();

        // stop timers
        stopTimerHandler(timeoutHandler, timeoutRunnable);
        stopTimerHandler(timeoutNotificationHandler, timeoutNotificationRunnable);
        stopTimerHandler(interStimulusIntervalHandler, interStimulusIntervalRunnable);

        // remove listeners
        RButton.setOnClickListener(null);
        GButton.setOnClickListener(null);
        BButton.setOnClickListener(null);
        YButton.setOnClickListener(null);
    }

    void startTimeoutTimer() {
        double timeout = stroopStep.getTimeout();
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

    void displayTimeoutNotification() {
        hideColorLabelText();
        hideButtons();
        String timeoutText = getContext().getString(R.string.rsb_STROOP_TIMEOUT_NOTIFICATION);
        setTimeoutText(timeoutText);
        // initiate timer handler
        timeoutNotificationHandler = new Handler();
        timeoutNotificationRunnable = new Runnable() {
            public void run() {
                startInterStimulusInterval();
            }
        };
        timeoutNotificationHandler.postDelayed(
                timeoutNotificationRunnable,
                (long) 2.0 * 1000);
    }

    private void startInterStimulusInterval() {
        stopTimerHandler(timeoutNotificationHandler, timeoutNotificationRunnable);
        hideColorLabelText();
        hideTimeoutText();
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
        double range = stroopStep.getMaximumInterStimulusInterval() - stroopStep.getMinimumInterStimulusInterval();
        if (range == 0 || stroopStep.getMaximumInterStimulusInterval() == stroopStep.getMinimumInterStimulusInterval() ||
                _questionCount == stroopStep.getNumberOfAttempts()) { // use min interval after last image of set is presented
            timeInterval = stroopStep.getMinimumInterStimulusInterval();
        } else {
            Random rand = new Random();
            double randomFactor = rand.nextInt((int)(range * 1000)) + 1; // non-zero random number of milliseconds between min/max limits
            timeInterval = (randomFactor / 1000) + stroopStep.getMinimumInterStimulusInterval(); // in seconds
        }
        return timeInterval;
    }

    private void showButtons() {
        if (RButton != null) {
            RButton.setVisibility(View.VISIBLE);
        }
        if (GButton != null) {
            GButton.setVisibility(View.VISIBLE);
        }
        if (BButton != null) {
            BButton.setVisibility(View.VISIBLE);
        }
        if (YButton != null) {
            YButton.setVisibility(View.VISIBLE);
        }
    }

    private void hideButtons() {
        if (RButton != null) {
            RButton.setVisibility(View.GONE);
        }
        if (GButton != null) {
            GButton.setVisibility(View.GONE);
        }
        if (BButton != null) {
            BButton.setVisibility(View.GONE);
        }
        if (YButton != null) {
            YButton.setVisibility(View.GONE);
        }
    }

    private void startQuestion() {
        _questionCount++;  // increments on call
        showButtons();
        _textPresented = textPresented();
        setColorLabelText(_textPresented);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setTextColor();
        }
        _startTime = System.currentTimeMillis();
        startTimeoutTimer();
    }

    private void stopTimerHandler(Handler handler, Runnable runnable) {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    public void setColorLabelText(String colorLabelText) {
        if (stroopColorLabelTextView != null) {
            stroopColorLabelTextView.setText(colorLabelText);
            stroopColorLabelTextView.setVisibility(View.VISIBLE);
            colorLabelHidden = false;
        }
    }

    private void hideColorLabelText() {
        if (stroopColorLabelTextView != null) {
            stroopColorLabelTextView.setVisibility(View.GONE);
            colorLabelHidden = true;
        }
    }

    public void setTimeoutText(String timeoutText) {
        if (stroopTimeoutTextView != null) {
            stroopTimeoutTextView.setText(timeoutText);
            stroopTimeoutTextView.setVisibility(View.VISIBLE);
        }
    }

    private void hideTimeoutText() {
        if (stroopTimeoutTextView != null) {
            stroopTimeoutTextView.setVisibility(View.GONE);
        }
    }

    private void timeoutTimerDidFire() {
        stopTimerHandler(timeoutHandler, timeoutRunnable);
        hideButtons();
        double duration = reactionTime();
        String textPresented = _textPresented;
        String colorPresented = _colorPresented;
        String colorSelected = getContext().getString(R.string.rsb_STROOP_COLOR_NONE);
        _match = false;
        _timedOut = true;
        _timedOutCount++;
        calculatePercentages(_timedOut);
        createResult(colorPresented, textPresented, colorSelected, _match, duration);
        displayTimeoutNotification();
    }

    private void calculatePercentages(boolean timeout) {
        if (_questionCount > 0) { // prevent zero denominator
            _percentCorrect = (100 * (double)_sumCorrect) / (double)_questionCount;
        }
        if (_questionCount > 0) { // prevent zero denominator
            _percentTimedOut = (100 * (double)_timedOutCount) / (double)_questionCount;
        }
    }

    private void calculateMeanAndStdReactionTime(double duration, boolean match) {
        // calculate mean and unbiased standard deviation of duration for correct matches only
        // (using Welford's algorithm: Welford. (1962) Technometrics 4(3), 419-420)
        if (match) {
            if (_sumCorrect == 1) {
                _prevM = _newM = duration;
                _prevS = 0;
            } else {
                _newM = _prevM + (duration - _prevM) / (double)_sumCorrect;
                _newS += _prevS + (duration - _prevM) * (duration - _newM);
                _prevM = _newM;
            }
            _meanReactionTime = (_sumCorrect > 0) ? _newM : 0;
            _varianceReactionTime = (_sumCorrect > 1) ? _newS / ((double)_sumCorrect - 1) : 0;
            if (_varianceReactionTime > 0) {
                _stdReactionTime = Math.sqrt(_varianceReactionTime);
            }
        }
    }

    private double reactionTime() {
        _endTime = System.currentTimeMillis();
        double duration = (_endTime - _startTime);
        return duration / 1000;
    }

    private void startNextQuestionOrFinish() {
        stopTimerHandler(interStimulusIntervalHandler, interStimulusIntervalRunnable);
        if (_questionCount == stroopStep.getNumberOfAttempts()) {
            stop();
         } else {
            startQuestion();
         }
    }

    private void createResult (
                                String color,
                                String text,
                                String colorSelected,
                                boolean match,
                                double reactionTime) {

        stroopResult = new StroopResult(stroopStep.getIdentifier());

        // step results
        stroopResult.setStartTime(_startTime);
        stroopResult.setEndTime(_endTime);
        stroopResult.setReactionTime(reactionTime);
        stroopResult.setText(text);
        stroopResult.setColor(color);
        stroopResult.setColorSelected(colorSelected);
        stroopResult.setMatch(match);
        stroopResult.setTimedOut(_timedOut);
        // task results
        stroopResult.setPercentCorrect(_percentCorrect);
        stroopResult.setPercentTimedOut(_percentTimedOut);
        stroopResult.setMeanReactionTime(_meanReactionTime);
        stroopResult.setStdReactionTime(_stdReactionTime);

        stepResult.setResultForIdentifier(stroopResult.getIdentifier(), stroopResult);
    }
}
