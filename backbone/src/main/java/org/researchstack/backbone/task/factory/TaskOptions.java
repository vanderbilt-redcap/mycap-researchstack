package org.researchstack.backbone.task.factory;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.model.ProfileInfoOption;

/**
 * Created by Dr David W. Evans, 2019.
 *
 * Values that are used within the instructions of an active task.
 *
 */
public class TaskOptions {

    public static final String SERIALIZED_NAME_LIMB_LEFT = "left";
    public static final String SERIALIZED_NAME_LIMB_RIGHT = "right";
    public static final String SERIALIZED_NAME_LIMB_BOTH = "both";

    public static final String SERIALIZED_NAME_IMAGE_UNSPECIFIED = "unspecified";
    public static final String SERIALIZED_NAME_IMAGE_HANDS = "hands";
    public static final String SERIALIZED_NAME_IMAGE_FEET = "feet";
    public static final String SERIALIZED_NAME_IMAGE_BOTH = "both";

    public static final String SERIALIZED_NAME_MOVEMENT_FLEXION = "flexion";
    public static final String SERIALIZED_NAME_MOVEMENT_EXTENSION = "extension";
    public static final String SERIALIZED_NAME_MOVEMENT_ABDUCTION = "abduction";
    public static final String SERIALIZED_NAME_MOVEMENT_ADDUCTION = "adduction";
    public static final String SERIALIZED_NAME_MOVEMENT_BENDING_FORWARDS = "bendingForwards";
    public static final String SERIALIZED_NAME_MOVEMENT_BENDING_BACKWARDS = "bendingBackwards";
    public static final String SERIALIZED_NAME_MOVEMENT_BENDING_LEFT = "bendingLeft";
    public static final String SERIALIZED_NAME_MOVEMENT_BENDING_RIGHT = "bendingRight";
    public static final String SERIALIZED_NAME_MOVEMENT_ROTATING_LEFT = "rotatingLeft";
    public static final String SERIALIZED_NAME_MOVEMENT_ROTATING_RIGHT = "rotatingRight";
    public static final String SERIALIZED_NAME_MOVEMENT_ROTATING_BOTH = "rotatingBoth";
    public static final String SERIALIZED_NAME_MOVEMENT_BENDING_BOTH_SIDES = "bendingBothSides";
    public static final String SERIALIZED_NAME_MOVEMENT_BENDING_BOTH_SAGITTAL = "bendingBothSagittal";
    public static final String SERIALIZED_NAME_MOVEMENT_BENDING_ALL = "bendingAll";

    static Gson gson;

    public enum Limb {
        // Task should test the left limb
        @SerializedName(SERIALIZED_NAME_LIMB_LEFT)
        LEFT,
        // Task should test the right limb
        @SerializedName(SERIALIZED_NAME_LIMB_RIGHT)
        RIGHT,
        // Task should test both limbs
        @SerializedName(SERIALIZED_NAME_LIMB_BOTH)
        BOTH;
    }

    public static Limb toLimbOption(String LimbSerializedName) {
        if (gson == null) {
            gson = new Gson();
        }
        return gson.fromJson(LimbSerializedName, Limb.class);
    }

    public enum Movement {
        // Movement should be flexion
        @SerializedName(SERIALIZED_NAME_MOVEMENT_FLEXION)
        FLEXION,
        // Movement should be extension
        @SerializedName(SERIALIZED_NAME_MOVEMENT_EXTENSION)
        EXTENSION,
        // Movement should be abduction
        @SerializedName(SERIALIZED_NAME_MOVEMENT_ABDUCTION)
        ABDUCTION,
        // Movement should be adduction
        @SerializedName(SERIALIZED_NAME_MOVEMENT_ADDUCTION)
        ADDUCTION,
        //Movement should be bending forwards
        @SerializedName(SERIALIZED_NAME_MOVEMENT_BENDING_FORWARDS)
        BENDING_FORWARDS,
        //Movement should be bending backwards
        @SerializedName(SERIALIZED_NAME_MOVEMENT_BENDING_BACKWARDS)
        BENDING_BACKWARDS,
        //Movement should be bending to the left
        @SerializedName(SERIALIZED_NAME_MOVEMENT_BENDING_LEFT)
        BENDING_LEFT,
        //Movement should be bending to the right
        @SerializedName(SERIALIZED_NAME_MOVEMENT_BENDING_RIGHT)
        BENDING_RIGHT,
        //Movement should be bending to the left
        @SerializedName(SERIALIZED_NAME_MOVEMENT_ROTATING_LEFT)
        ROTATING_LEFT,
        //Movement should be bending to the right
        @SerializedName(SERIALIZED_NAME_MOVEMENT_ROTATING_RIGHT)
        ROTATING_RIGHT,
        // Movement should be rotation to the left and right (random order)
        @SerializedName(SERIALIZED_NAME_MOVEMENT_ROTATING_BOTH)
        ROTATING_BOTH,
        // Movement should be bending to the left and right (random order)
        @SerializedName(SERIALIZED_NAME_MOVEMENT_BENDING_BOTH_SIDES)
        BENDING_BOTH_SIDES,
        // Movement should be forwards and backwards bending (random order)
        @SerializedName(SERIALIZED_NAME_MOVEMENT_BENDING_BOTH_SAGITTAL)
        BENDING_BOTH_SAGITTAL,
        // Movement should be forwards, backwards, left side and right side bending (random order)
        @SerializedName(SERIALIZED_NAME_MOVEMENT_BENDING_ALL)
        BENDING_ALL;
    }

    public static Movement toMovementOption(String MovementSerializedName) {
        if (gson == null) {
            gson = new Gson();
        }
        return gson.fromJson(MovementSerializedName, Movement.class);
    }

    public enum ImageOption {
        // Task images not specified
        @SerializedName(SERIALIZED_NAME_IMAGE_UNSPECIFIED)
        UNSPECIFIED,
        // Task should use the hand images
        @SerializedName(SERIALIZED_NAME_IMAGE_HANDS)
        HANDS,
        // Task should use the foot images
        @SerializedName(SERIALIZED_NAME_IMAGE_FEET)
        FEET,
        // Task should test both hand and foot image sequences (random order)
        @SerializedName(SERIALIZED_NAME_IMAGE_BOTH)
        BOTH;
    }

    public static ImageOption toImageOption(String ImageOptionSerializedName) {
        if (gson == null) {
            gson = new Gson();
        }
        return gson.fromJson(ImageOptionSerializedName, ImageOption.class);
    }
}
