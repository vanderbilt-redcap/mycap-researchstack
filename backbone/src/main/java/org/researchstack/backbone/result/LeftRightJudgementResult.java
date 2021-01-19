package org.researchstack.backbone.result;

/**
 * Created by Dr David W. Evans in January 2021.
 */

public class LeftRightJudgementResult extends Result {

    /**
     The `imageNumber` property is the current number of the image (beginning with '1') presented within the entire sequence of the task.
     */
    private int imageNumber;

    /**
     The `imageName` property is the file name of the image presented (without the .png extension).
     */
    private String imageName;

    /**
     The `viewPresented` property is the view of the hand or foot presented in the image and as seen by the user.
     */
    private String viewPresented;

    /**
     The `orientationPresented` property is the relative anatomical description (medial, lateral or neutral), of the direction towards which the fingers (hand) or toes (foot) point, as presented within the image.
     */
    private String orientationPresented;

    /**
     The `rotationPresented` property is the angle (measured in degrees, with 30 degree intervals), in either a clockwise or anti-clockwise direction (with 12 o'clock as zero), towards which the fingers (hand) or toes (foot) point as presented within the image.
     */
    private int rotationPresented;

    /**
     The `sidePresented` property is the side of the body presented in the image.
     */
    private String sidePresented;

    /**
     The `sideSelected` property corresponds to the button tapped by the user as an answer.
     */
    private String sideSelected;

    /**
     The 'sideMatch' property is a Boolean value indicating whether the value of sideSelected matches that of sidePresented. The value of this property is `YES` when there is a match, and `NO` otherwise.
     */
    private boolean sideMatch;

    /**
     The 'timedOut' property is a Boolean value indicating whether the the attempt timed out, based on the value set in the non-zero 'timeout' parameter (in seconds), before a selection was made. The value of this property is `YES` when the timeout value was reached, and `NO` otherwise. When YES, reactionTime values will not contribute to task summaries (means and standard deviations).
     */
    private boolean timedOut;

    /**
     The `reactionTime` property is the time taken (in seconds) for a response to an image, whether the 'sideMatch' value is YES or NO, equal to the difference between timestamps from when the image is displayed to when the button is pressed.
     */
    private double reactionTime;

    /**
     The `leftImages` property is the total number of left images presented so far during the task.
     */
    private int leftImages;

    /**
     The `rightImages` property is the total number of right images presented so far during the task.
     */
    private int rightImages;

    /**
     The `leftPercentCorrect` property is the percentage of correct 'sideMatch' answers for all images in the task so far where sidePresented value was 'Left'. This updates with every image presented in the set.
     */
    private double leftPercentCorrect;

    /**
     The `rightPercentCorrect` property is the percentage of correct 'sideMatch' answers for all images in the task so far where sidePresented value was 'Right'. This updates with every image presented in the set.
     */
    private double rightPercentCorrect;

    /**
     The `timedOutPercent` property is the percentage of attempts in the task so far where 'timedOut' value was YES. This updates with every image presented in the set.
     */
    private double percentTimedOut;

    /**
     The `leftMeanReactionTime` property is the mean (average) of reactionTime (in seconds) for all images in the task so far where sidePresented value was 'Left' and sideMatch value was YES (i.e. correct matches only). This updates with every image presented in the set.
     */
    private double leftMeanReactionTime;

    /**
     The `rightMeanReactionTime` property is the mean (average) of reactionTime (in seconds) for all images in the task so far where sidePresented value was 'Right' and sideMatch value was YES (i.e. correct matches only). This updates with every image presented in the set.
     */
    private double rightMeanReactionTime;

    /**
     The `leftSDReactionTime` property is the standard deviation (a measure of distribution) of reactionTime (in seconds) for all images in the task so far where sidePresented value was 'Left'. This updates with every image presented in the set.
     */
    private double leftSDReactionTime;

    /**
     The `rightSDReactionTime` property is the standard deviation (a measure of distribution) of reactionTime (in seconds) for all images in the task so far where sidePresented value was 'Right'. This updates with every image presented in the set.
     */
    private double rightSDReactionTime;


    /* Default identifier for serialization/deserialization */
    LeftRightJudgementResult() {
        super();
    }

    public LeftRightJudgementResult(String identifier) {
        super(identifier);
    }

    public int getImageNumber() {
        return imageNumber;
    }

    public void setImageNumber(int imageNumber) {
        this.imageNumber = imageNumber;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getViewPresented() {
        return viewPresented;
    }

    public void setViewPresented(String viewPresented) {
        this.viewPresented = viewPresented;
    }

    public String getOrientationPresented() {
        return orientationPresented;
    }

    public void setOrientationPresented(String orientationPresented) {
        this.imageName = orientationPresented;
    }

    public int getRotationPresented() {
        return rotationPresented;
    }

    public void setRotationPresented(int rotationPresented) {
        this.rotationPresented = rotationPresented;
    }

    public String getSidePresented() {
        return sidePresented;
    }

    public void setSidePresented(String sidePresented) {
        this.sidePresented = sidePresented;
    }

    public String getSideSelected() {
        return sideSelected;
    }

    public void setSideSelected(String sideSelected) {
        this.sideSelected = sideSelected;
    }

    public boolean getSideMatch() {
        return sideMatch;
    }

    public void setSideMatch(boolean sideMatch) {
        this.sideMatch = sideMatch;
    }

    public boolean getTimedOut() {
        return timedOut;
    }

    public void setTimedOut(boolean timedOut) {
        this.timedOut = timedOut;
    }

    public double getReactionTime() {
        return reactionTime;
    }

    public void setReactionTime(double reactionTime) {
        this.reactionTime = reactionTime;
    }

    public int getLeftImages() {
        return leftImages;
    }

    public void setLeftImages(int leftImages) {
        this.leftImages = leftImages;
    }

    public int getRightImages() {
        return rightImages;
    }

    public void setRightImages(int rightImages) {
        this.rightImages = rightImages;
    }

    public double getLeftPercentCorrect() {
        return leftPercentCorrect;
    }

    public void setLeftPercentCorrect(double leftPercentCorrect) {
        this.leftPercentCorrect = leftPercentCorrect;
    }

    public double getRightPercentCorrect() {
        return rightPercentCorrect;
    }

    public void setRightPercentCorrect(double rightPercentCorrect) {
        this.rightPercentCorrect = rightPercentCorrect;
    }

    public double getPercentTimedOut() {
        return percentTimedOut;
    }

    public void setPercentTimedOut(double percentTimedOut) {
        this.percentTimedOut = percentTimedOut;
    }

    public double getLeftMeanReactionTime() {
        return leftMeanReactionTime;
    }

    public void setLeftMeanReactionTime(double leftMeanReactionTime) {
        this.leftMeanReactionTime = leftMeanReactionTime;
    }

    public double getRightMeanReactionTime() {
        return rightMeanReactionTime;
    }

    public void setRightMeanReactionTime(double rightMeanReactionTime) {
        this.rightMeanReactionTime = rightMeanReactionTime;
    }

    public double getLeftSDReactionTime() {
        return leftSDReactionTime;
    }

    public void setLeftSDReactionTime(double leftSDReactionTime) {
        this.leftSDReactionTime = leftSDReactionTime;
    }

    public double getRightSDReactionTime() {
        return rightSDReactionTime;
    }

    public void setRightSDReactionTime(double rightSDReactionTime) {
        this.rightSDReactionTime = rightSDReactionTime;
    }

}
