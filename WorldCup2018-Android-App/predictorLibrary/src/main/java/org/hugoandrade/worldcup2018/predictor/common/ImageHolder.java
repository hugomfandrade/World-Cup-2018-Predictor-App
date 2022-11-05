package org.hugoandrade.worldcup2018.predictor.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;

public class ImageHolder {

    private ImageView mImageView;
    private int mDefaultImageResource;
    private int mInSampleSize;

    private ImageHolder(ImageView imageView,
                        String filePath,
                        int defaultImageResource,
                        int inSampleSize) {

        mImageView = imageView;
        mDefaultImageResource = defaultImageResource;
        mInSampleSize = inSampleSize;

        displayImage(filePath);
    }

    private static HashMap<String, Bitmap> sBitmaps = new HashMap<>();

    private ImageHolder(ImageView imageView,
                        String fileUri,
                        int defaultImageResource,
                        int inSampleSize,
                        File filesDir) {

        mImageView = imageView;
        mDefaultImageResource = defaultImageResource;
        mInSampleSize = inSampleSize;

        File f = getImageUriIfExists(fileUri, filesDir);

        if (f == null) {
            mImageView.setImageResource(mDefaultImageResource);
            new DownloadImageAsyncTask(filesDir)
                    .setOnFinishedListener(new DownloadImageAsyncTask.OnFinishedListener() {
                        @Override
                        public void onFinished(String filePath) {
                            displayImage(filePath);
                        }
                    })
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, fileUri);
        } else {
            displayImage(f.getAbsolutePath());
        }
    }

    private void displayImage(String filePath) {
        if (containsStatic(filePath, mInSampleSize))
            mImageView.setImageBitmap(getStatic(filePath, mInSampleSize));
        else
            mImageView.setImageResource(mDefaultImageResource);

        new DisplayImageAsyncTask(mInSampleSize)
                .setOnFinishedListener(new DisplayImageAsyncTask.OnFinishedListener() {
                    @Override
                    public void onFinished(Bitmap result) {
                        if (mImageView == null)
                            return;

                        if (result != null)
                            mImageView.setImageBitmap(result);
                        else if (mDefaultImageResource != -1)
                            mImageView.setImageResource(mDefaultImageResource);
                    }
                })
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, filePath);
    }

    private File getImageUriIfExists(String imageUri, File filesDir) {
        if (imageUri == null)
            return null;

        String[] a = imageUri.split("/");
        String filename = a[a.length - 1];

        if (filesDir == null)
            return null;
        try {
            for (File f : filesDir.listFiles()) {
                if (f.getName().equals(filename))
                    return f;
            }
            return null;
        } catch (NullPointerException e) {
            return null;
        }
    }

    private static class DisplayImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @SuppressWarnings("unused") private String TAG = getClass().getSimpleName();

        private int mInSampleSize;
        private OnFinishedListener mListener;

        DisplayImageAsyncTask(int inSampleSize) {
            mInSampleSize = inSampleSize;
        }

        @Override
        protected Bitmap doInBackground(String... filePaths) {

            String filePath = filePaths[0];

            if (filePath == null)
                return null;

            File f = new File(filePath);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = mInSampleSize;
            // down sizing image as it throws OutOfMemory Exception for larger images

            if (f.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), options);
                putStatic(filePath, mInSampleSize, bitmap);
                return bitmap;
            }
            return null;
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

    private static void putStatic(String filePath, int inSampleSize, Bitmap bitmap) {
        sBitmaps.put(TextUtils.concat(filePath, Integer.toString(inSampleSize)).toString(), bitmap);
    }

    private static Bitmap getStatic(String filePath, int inSampleSize) {
        return sBitmaps.get(TextUtils.concat(filePath, Integer.toString(inSampleSize)).toString());
    }

    private static boolean containsStatic(String filePath, int inSampleSize) {
        return sBitmaps.containsKey(TextUtils.concat(filePath, Integer.toString(inSampleSize)).toString());
    }

    private static class DownloadImageAsyncTask extends AsyncTask<String, Void, String> {

        private String TAG = getClass().getSimpleName();

        private File mFilesDir;
        private OnFinishedListener mListener;

        DownloadImageAsyncTask(File filesDir) {
            mFilesDir = filesDir;
        }

        @Override
        protected String doInBackground(String... filePaths) {

            String imageURL = filePaths[0];

            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                String[] a = imageURL.split("/");
                String filename = a[a.length - 1];

                File pictureFile = new File(mFilesDir, filename);
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "File not found: " + e.getMessage());
                    return null;
                } catch (IOException e) {
                    Log.e(TAG, "Error accessing file: " + e.getMessage());
                    return null;
                }

                return pictureFile.getAbsolutePath();

            } catch (MalformedURLException e) {
                Log.w(TAG, "MalformedURLException: " + imageURL);
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + imageURL);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (mListener != null)
                mListener.onFinished(result);
        }

        DownloadImageAsyncTask setOnFinishedListener(OnFinishedListener listener) {
            mListener = listener;
            return this;
        }

        public interface OnFinishedListener {
            void onFinished(String filePath);
        }
    }

    public static class Builder  {

        private final ImageHolderParams P;

        public static Builder instance(ImageView imageView) {
            return new Builder(imageView);
        }

        public Builder(ImageView imageView) {
            P = new ImageHolderParams(imageView);
        }

        public Builder setFileUrl(String fileUrl) {
            P.fileUrl = fileUrl;
            P.mode = ImageHolderParams.FILE_URL;
            return this;
        }

        public Builder setFilePath(String filePath) {
            P.filePath = filePath;
            P.mode = ImageHolderParams.FILE_PATH;
            return this;
        }

        public Builder setDefaultImageResource(int defaultImageResource) {
            P.defaultImageResource = defaultImageResource;
            return this;
        }

        public Builder setInSampleSize(@SuppressWarnings("SameParameterValue") int inSampleSize) {
            P.inSampleSize = inSampleSize;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setFileDir(File filesDir) {
            P.filesDir = filesDir;
            return this;
        }

        public void execute() {

            switch (P.mode) {
                case ImageHolderParams.FILE_URL:
                    if (P.filesDir == null)
                        P.filesDir = P.imageView.getContext().getExternalFilesDir(null);
                    new ImageHolder(P.imageView,
                            P.fileUrl,
                            P.defaultImageResource,
                            P.inSampleSize,
                            P.filesDir);
                    break;
                case ImageHolderParams.FILE_PATH:
                    new ImageHolder(P.imageView,
                            P.filePath,
                            P.defaultImageResource,
                            P.inSampleSize);
                    break;
            }
        }
    }

    private static class ImageHolderParams  {

        final static int FILE_URL = 1;
        final static int FILE_PATH = 2;

        private ImageView imageView;
        private File filesDir;
        private String fileUrl;
        private String filePath;
        private int defaultImageResource = -1;
        private int inSampleSize = 8;

        private int mode;

        ImageHolderParams(ImageView imageView) {
            this.imageView = imageView;
        }
    }
}
