package org.hugoandrade.worldcup2018.predictor.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
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

    // Map hashCode of View - Listener
    private static SparseArray<View.OnLayoutChangeListener> mOnLayoutChangeListenerMap = new SparseArray<>();

    // Map imageRes - Map Width * 1000 + Height - Bitmap
    private static SparseArray<SparseArray<WeakReference<Bitmap>>> mImageResMap = new SparseArray<>();

    public static void decodeSampledBitmapFromResourceAsync(final Context context,
                                                            ImageView imageView,
                                                            final int imageRes) {
        if (imageView != null && context != null) {
            int hashCode  = imageView.hashCode();

            if (mOnLayoutChangeListenerMap.get(hashCode) != null) {
                imageView.removeOnLayoutChangeListener(mOnLayoutChangeListenerMap.get(hashCode));
            }

            final WeakReference<ImageView> imageViewWeakRef = new WeakReference<>(imageView);

            View.OnLayoutChangeListener listener = new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v,
                                           int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (imageViewWeakRef.get() == null)
                        return;

                    new DisplayImageAsyncTask(context, (ImageView) v)
                            .setOnFinishedListener(new DisplayImageAsyncTask.OnFinishedListener() {
                                @Override
                                public void onFinished(Bitmap result) {
                                    if (imageViewWeakRef.get() == null)
                                        return;

                                    if (result != null)
                                        imageViewWeakRef.get().setImageBitmap(result);
                                    else
                                        imageViewWeakRef.get().setImageResource(0);
                                }
                            })
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imageRes);
                }
            };

            //imageView.setImageBitmap(decodeSampledBitmapFromResourceRaw(context.getResources(), imageRes, imageView));
            imageView.setImageResource(0);

            new DisplayImageAsyncTask(context, imageView)
                    .setOnFinishedListener(new DisplayImageAsyncTask.OnFinishedListener() {
                        @Override
                        public void onFinished(Bitmap result) {
                            if (imageViewWeakRef.get() == null)
                                return;

                            if (result != null)
                                imageViewWeakRef.get().setImageBitmap(result);
                            else
                                imageViewWeakRef.get().setImageResource(0);
                        }
                    })
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imageRes);

            imageView.addOnLayoutChangeListener(listener);
            mOnLayoutChangeListenerMap.put(hashCode, listener);
        }
    }

    public static void decodeSampledBitmapFromResourceSync(final Context context,
                                                           final ImageView imageView,
                                                           final int imageRes) {
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

    private static class DisplayImageAsyncTask extends AsyncTask<Integer, Void, Bitmap> {

        @SuppressWarnings("unused") private String TAG = getClass().getSimpleName();

        private final WeakReference<Context> mContextWeakRef;
        private final int mWidth;
        private final int mHeight;

        private DisplayImageAsyncTask.OnFinishedListener mListener;

        DisplayImageAsyncTask(Context context, ImageView imageView) {
            mContextWeakRef = new WeakReference<>(context);
            if (imageView != null) {
                mWidth = imageView.getMeasuredWidth();
                mHeight = imageView.getMeasuredHeight();
            }
            else {
                mWidth = 0;
                mHeight = 0;
            }
        }

        @Override
        protected Bitmap doInBackground(Integer... imageRess) {


            if (imageRess == null || imageRess.length == 0)
                return null;

            int imageRes = imageRess[0];

            if (mContextWeakRef.get() == null)
                return null;

            return decodeSampledBitmapFromResource(mContextWeakRef.get().getResources(), imageRes, mWidth, mHeight);
        }

        @Override
        protected void onPostExecute(final Bitmap result) {
            if (mListener != null)
                mListener.onFinished(result);
        }

        DisplayImageAsyncTask setOnFinishedListener(OnFinishedListener listener) {
            mListener = listener;
            return this;
        }

        public interface OnFinishedListener {
            void onFinished(Bitmap result);
        }
    }

    private static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                         int resId,
                                                         ImageView imageView) {

        return decodeSampledBitmapFromResource(res,
                resId,
                imageView.getMeasuredWidth(),
                imageView.getMeasuredHeight());
    }

    private static Bitmap decodeSampledBitmapFromResource(Resources res,
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

            BitmapFactory.decodeResource(res, resId, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSizeStrict(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeResource(res, resId, options);

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
            }
            else {
                reqqHeight = (int) (reqqHeight * ratio);
            }

            bitmap = Bitmap.createScaledBitmap(bitmap, reqqWidth, reqqHeight, false);

            if (mImageResMap.get(resId) == null) {
                mImageResMap.put(resId, new SparseArray<WeakReference<Bitmap>>());
            }
            mImageResMap.get(resId).put(getDimensionCode(reqWidth, reqHeight), new WeakReference<>(bitmap));
            return bitmap;
        }
    }

    private static Bitmap decodeSampledBitmapFromResourceRaw(Resources res,
                                                             int resId,
                                                             ImageView imageView) {

        return decodeSampledBitmapFromResourceRaw(res,
                resId,
                imageView.getMeasuredWidth(),
                imageView.getMeasuredHeight());
    }

    private static Bitmap decodeSampledBitmapFromResourceRaw(Resources res,
                                                             int resId,
                                                             int reqWidth,
                                                             int reqHeight) {

        if (reqHeight == 0 || reqWidth == 0) {
            return null;
        }

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSizeRaw(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeResource(res, resId, options);

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
        }
        else {
            reqqHeight = (int) (reqqHeight * ratio);
        }

        bitmap = Bitmap.createScaledBitmap(bitmap, reqqWidth, reqqHeight, false);

        return bitmap;

    }

    private static int calculateInSampleSizeStrict(BitmapFactory.Options options,
                                                   int reqWidth,
                                                   int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;

        float scaleHeight = ((float) height) / reqHeight;
        float scaleWidth = ((float) width) / reqWidth;

        if (scaleWidth > scaleHeight) {
            return (int) scaleWidth;
        }
        else {
            return (int) scaleHeight;
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth,
                                            int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static int calculateInSampleSizeRaw(BitmapFactory.Options options,
                                                int reqWidth,
                                                int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 16;
            }
        }

        return inSampleSize;
    }

    private static int getDimensionCode(int reqWidth, int reqHeight) {
        return reqWidth * 1000 + reqHeight;
    }
}

