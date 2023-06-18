package com.example.myapplication;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
public class ImageInverter {
    protected static void invertImageInDarkMode(ImageView imageView, boolean isDarkMode) {
        Drawable drawable = imageView.getDrawable();

        if (drawable != null) {
            if (isDarkMode) {

                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setSaturation(0); // Convert to grayscale

                ColorMatrix invertMatrix = new ColorMatrix();
                invertMatrix.set(new float[] {
                        -1,  0,  0,  0, 255,  // Red component (invert)
                        0, -1,  0,  0, 255,  // Green component (invert)
                        0,  0, -1,  0, 255,  // Blue component (invert)
                        0,  0,  0,  1, 0  // Alpha component
                });

                colorMatrix.postConcat(invertMatrix);

                ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
                drawable.setColorFilter(colorFilter);
                imageView.setBackgroundColor(Color.TRANSPARENT);
            } else {
                drawable.clearColorFilter();
                imageView.setBackgroundColor(Color.TRANSPARENT);
            }

            imageView.invalidate();
        }
    }
}
