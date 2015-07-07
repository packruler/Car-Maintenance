package com.packruler.carmaintenance.ui.utilities;

import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;

/**
 * Created by Packruler on 6/12/15.
 */
public class Swatch {
//    private final String TAG = getClass().getSimpleName();

//    private static final float MIN_CONTRAST_TITLE_TEXT = 4.0f;
//    private static final float MIN_CONTRAST_BODY_TEXT = 4.5f;

//    private final int mRed, mGreen, mBlue;
//    private final int mRgb;

//    private boolean mGeneratedTextColors;
//    private int mTitleTextColor;
//    private int mBodyTextColor;

    private static int foregroundColor = -1;


    public static synchronized int setBackgroundColor(int backgroundColor) {
        int mRed = Color.red(backgroundColor);
        int mGreen = Color.green(backgroundColor);
        int mBlue = Color.blue(backgroundColor);
        int mRgb = backgroundColor;

        float[] hsl = new float[3];
        ColorUtils.RGBToHSL(mRed, mGreen, mBlue, hsl);
        if (ColorUtils.calculateLuminance(mRgb) < 0.2f)
            hsl[2] = 0.9f;
        else
            hsl[2] = 0.07f;
        foregroundColor = ColorUtils.HSLToColor(hsl);
        return foregroundColor;
    }

    public static synchronized int getForegroundColor() {
        return foregroundColor;
    }
//
//    public Swatch(int color) {
//        mRed = Color.red(color);
//        mGreen = Color.green(color);
//        mBlue = Color.blue(color);
//        mRgb = color;
//    }
//
//    Swatch(int red, int green, int blue) {
//        mRed = red;
//        mGreen = green;
//        mBlue = blue;
//        mRgb = Color.rgb(red, green, blue);
//    }
//
//    /**
//     * @return this swatch's RGB color value
//     */
//    public int getRgb() {
//        return mRgb;
//    }
//
//    /**
//     * Return this swatch's HSL values.
//     * hsv[0] is Hue [0 .. 360)
//     * hsv[1] is Saturation [0...1]
//     * hsv[2] is Lightness [0...1]
//     */
//    public float[] getHsl() {
//        if (hsl == null) {
//            hsl = new float[3];
//            ColorUtils.RGBToHSL(mRed, mGreen, mBlue, hsl);
//        }
//        return hsl;
//    }
//
//    /**
//     * Returns an appropriate color to use for any 'title' text which is displayed over this
//     * {@link Swatch}'s color. This color is guaranteed to have sufficient contrast.
//     */
//    public int getTitleTextColor() {
//        ensureTextColorsGenerated();
//        return mTitleTextColor;
//    }
//
//    /**
//     * Returns an appropriate color to use for any 'body' text which is displayed over this
//     * {@link Swatch}'s color. This color is guaranteed to have sufficient contrast.
//     */
//    public int getBodyTextColor() {
////        Log.v(TAG, "Luminance: " + ColorUtils.calculateLuminance(mRgb));
//        float[] hsl = getHsl();
//        if (ColorUtils.calculateLuminance(mRgb) < 0.2f)
//            hsl[2] = 0.9f;
//        else
//            hsl[2] = 0.07f;
//        return ColorUtils.HSLToColor(hsl);
////        ensureTextColorsGenerated();
////        return mBodyTextColor;
//    }
//
//    private void ensureTextColorsGenerated() {
//        if (!mGeneratedTextColors) {
//            // First check white, as most colors will be dark
//            final int lightBodyAlpha = ColorUtils.calculateMinimumAlpha(
//                    Color.WHITE, mRgb, MIN_CONTRAST_BODY_TEXT);
//            final int lightTitleAlpha = ColorUtils.calculateMinimumAlpha(
//                    Color.WHITE, mRgb, MIN_CONTRAST_TITLE_TEXT);
//
//            if (lightBodyAlpha != -1 && lightTitleAlpha != -1) {
//                // If we found valid light values, use them and return
//                mBodyTextColor = ColorUtils.setAlphaComponent(Color.WHITE, lightBodyAlpha);
//                mTitleTextColor = ColorUtils.setAlphaComponent(Color.WHITE, lightTitleAlpha);
//                mGeneratedTextColors = true;
//                return;
//            }
//
//            final int darkBodyAlpha = ColorUtils.calculateMinimumAlpha(
//                    Color.BLACK, mRgb, MIN_CONTRAST_BODY_TEXT);
//            final int darkTitleAlpha = ColorUtils.calculateMinimumAlpha(
//                    Color.BLACK, mRgb, MIN_CONTRAST_TITLE_TEXT);
//
//            if (darkBodyAlpha != -1 && darkTitleAlpha != -1) {
//                // If we found valid dark values, use them and return
//                mBodyTextColor = ColorUtils.setAlphaComponent(Color.BLACK, darkBodyAlpha);
//                mTitleTextColor = ColorUtils.setAlphaComponent(Color.BLACK, darkTitleAlpha);
//                mGeneratedTextColors = true;
//                return;
//            }
//
//            // If we reach here then we can not find title and body values which use the same
//            // lightness, we need to use mismatched values
//            mBodyTextColor = lightBodyAlpha != -1
//                    ? ColorUtils.setAlphaComponent(Color.WHITE, lightBodyAlpha)
//                    : ColorUtils.setAlphaComponent(Color.BLACK, darkBodyAlpha);
//            mTitleTextColor = lightTitleAlpha != -1
//                    ? ColorUtils.setAlphaComponent(Color.WHITE, lightTitleAlpha)
//                    : ColorUtils.setAlphaComponent(Color.BLACK, darkTitleAlpha);
//            mGeneratedTextColors = true;
//        }
//    }
}
