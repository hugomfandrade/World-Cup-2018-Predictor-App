package org.hugoandrade.worldcup2018.predictor.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Provides some general utility League helper methods.
 */
public final class BitmapUtils {
    /**
     * Logging tag.
     */
    @SuppressWarnings("unused")
    private static final String TAG = BitmapUtils.class.getSimpleName();

    /**
     * Ensure this class is only used as a utility.
     */
    private BitmapUtils() {
        throw new AssertionError();
    }

    private Bitmap downscaleBitmapUsingDensities(Context context,
                                                 ImageView imageView,
                                                 final int sampleSize,
                                                 final int imageResId)
    {
        final BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inDensity = sampleSize;
        bitmapOptions.inTargetDensity=1;
        final Bitmap scaledBitmap=BitmapFactory.decodeResource(context.getResources(),imageResId,bitmapOptions);
        scaledBitmap.setDensity(Bitmap.DENSITY_NONE);

        /*final int newWidth = width / bitmapOptions.inSampleSize, newHeight = height / bitmapOptions.inSampleSize;
        if (newWidth > reqWidth || newHeight > reqHeight) {
            if (newWidth * reqHeight > newHeight * reqWidth) {
                // prefer width, as the width ratio is larger
                bitmapOptions.inTargetDensity = reqWidth;
                bitmapOptions.inDensity = newWidth;
            } else {
                // prefer height
                bitmapOptions.inTargetDensity = reqHeight;
                bitmapOptions.inDensity = newHeight;
            }
        }/**/


        return scaledBitmap;
    }
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth,
                                            int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        //android.util.Log.e(BitmapUtils.class.getSimpleName(), "::" + Integer.toString(height) + " , " + Integer.toString(width));
        int inSampleSize = 1;


        int reqqWidth = reqWidth;
        int reqqHeight = reqHeight;
        int bitmapHeight = height;
        int bitmapWidth = width;
        float ratio = ((float) bitmapHeight) / ((float) bitmapWidth);
        float scaleHeight = ((float) bitmapHeight) / reqHeight;
        float scaleWidth = ((float) bitmapWidth) / reqWidth;
        //android.util.Log.e(BitmapUtils.class.getSimpleName(), "::" + Integer.toString((int)scaleHeight) + " , " + Integer.toString((int)scaleWidth));
        if (scaleWidth > scaleHeight) {
            return (int) scaleWidth;
            //reqqWidth = (int) (reqqWidth / ratio);
            //reqqWidth = (int) (bitmap.getHeight() / scaleWidth);
        }
        else {
            return (int) scaleHeight;
            //reqqHeight = (int) (reqqHeight * ratio);
            //reqqHeight = (int) (bitmap.getWidth() / scaleHeight);
        }
        /*


        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 8;
                //inSampleSize *= 2;
            }
        }

        return inSampleSize;/**/
    }

    private static int getDimensionCode(int reqWidth, int reqHeight) {
        return reqWidth * 1000 + reqHeight;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                         int resId,
                                                         int reqWidth,
                                                         int reqHeight) {

        if (reqHeight == 0 || reqWidth == 0) {
            return null;
        }
        if (mImageResMap.get(resId) != null &&
                mImageResMap.get(resId).get(getDimensionCode(reqWidth, reqHeight)) != null &&
                mImageResMap.get(resId).get(getDimensionCode(reqWidth, reqHeight)).get() != null) {
            return mImageResMap.get(resId).get(getDimensionCode(reqWidth, reqHeight)).get();
        }
        else {

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            android.util.Log.e(TAG, "decode::one");
            BitmapFactory.decodeResource(res, resId, options);
            android.util.Log.e(TAG, "decode::one::end");

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            android.util.Log.e(TAG, "decode::two");
            Bitmap bitmap = BitmapFactory.decodeResource(res, resId, options);
            android.util.Log.e(TAG, "decode::two::end");

            if (bitmap == null) {
                return null;
            }
            int reqqWidth = reqWidth;
            int reqqHeight = reqHeight;
            int bitmapHeight = bitmap.getHeight();
            int bitmapWidth = bitmap.getWidth();
            float ratio = ((float) bitmapHeight) / ((float) bitmapWidth);
            float scaleHeight = ((float) bitmapHeight) / reqHeight;
            float scaleWidth = ((float) bitmapWidth) / reqWidth;
            if (scaleWidth > scaleHeight) {
                reqqWidth = (int) (reqqWidth / ratio);
                //reqqWidth = (int) (bitmap.getHeight() / scaleWidth);
            }
            else /*if (scaleHeight > scaleWidth) */ {
                reqqHeight = (int) (reqqHeight * ratio);
                //reqqHeight = (int) (bitmap.getWidth() / scaleHeight);
            }
            android.util.Log.e(TAG, "decode::scaled");
            bitmap = Bitmap.createScaledBitmap(bitmap, reqqWidth, reqqHeight, false);
            android.util.Log.e(TAG, "decode::scaled::end");

            if (mImageResMap.get(resId) == null) {
                mImageResMap.put(resId, new SparseArray<WeakReference<Bitmap>>());
            }
            mImageResMap.get(resId).put(getDimensionCode(reqWidth, reqHeight), new WeakReference<>(bitmap));
            return bitmap;
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                         int resId,
                                                         ImageView imageView) {

        return decodeSampledBitmapFromResource(res,
                    resId,
                    imageView.getMeasuredWidth(),
                    imageView.getMeasuredHeight());
    }

    // Map hashCode of View - Listener
    private static SparseArray<View.OnLayoutChangeListener> mOnLayoutChangeListenerMap = new SparseArray<>();

    // Map imageRes - Map Width * 1000 + Height - Bitmap
    private static SparseArray<SparseArray<WeakReference<Bitmap>>> mImageResMap = new SparseArray<>();

    public static void decodeSampledBitmapFromResource(final Context context,
                                                       final ImageView imageView,
                                                       final int imageRes,
                                                       final boolean showTAG) {
        if (imageView != null && context != null) {
            int hashCode  = imageView.hashCode();

            if (mOnLayoutChangeListenerMap.get(hashCode) != null) {
                imageView.removeOnLayoutChangeListener(mOnLayoutChangeListenerMap.get(hashCode));
            }

            View.OnLayoutChangeListener listener = new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v,
                                           int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    imageView.setImageBitmap(decodeSampledBitmapFromResource(context.getResources(), imageRes, imageView));
                }
            };

            imageView.setImageBitmap(decodeSampledBitmapFromResource(context.getResources(), imageRes, imageView));
            imageView.addOnLayoutChangeListener(listener);
            mOnLayoutChangeListenerMap.put(hashCode, listener);
        }
    }
}

