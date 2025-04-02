package ar.com.delellis.lightnvrviewer.api;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.PixelCopy;
import android.view.SurfaceView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Util {
    public static String getLocalTime(String dateTime) {
        Date utcDateTime = null;
        String dueDateAsNormal ="";
        SimpleDateFormat timeServerFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'");
        timeServerFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            utcDateTime = timeServerFormatter.parse(dateTime);
            SimpleDateFormat localFormatter = new SimpleDateFormat("MM/dd/yyyy - hh:mm:ss a");

            localFormatter.setTimeZone(TimeZone.getDefault());
            dueDateAsNormal = localFormatter.format(utcDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dueDateAsNormal;
    }

    public static File getSnapshotFile(String streamName) {
        File snapshotFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/Screenshots");
        snapshotFolder.mkdirs();

        String currentDate = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "lightNVR_" + streamName + "_" + currentDate + ".png";

        return new File(snapshotFolder.getPath() + "/" + fileName);
    }

    public static void getSurfaceBitmap(SurfaceView surfaceView, final PixelCopyListener listener) {
        Bitmap bitmap = Bitmap.createBitmap(surfaceView.getWidth(), surfaceView.getHeight(), Bitmap.Config.ARGB_8888);

        HandlerThread handlerThread = new HandlerThread(Util.class.getSimpleName());
        handlerThread.start();

        PixelCopy.request(surfaceView, bitmap, new PixelCopy.OnPixelCopyFinishedListener() {
            @Override
            public void onPixelCopyFinished(int copyResult) {
                if (copyResult == PixelCopy.SUCCESS) {
                    listener.onSurfaceBitmapReady(bitmap);
                } else {
                    listener.onSurfaceBitmapError("Couldn't create bitmap of the SurfaceView: " + String.valueOf(copyResult));
                }
                handlerThread.quitSafely();
            }
        }, new Handler(handlerThread.getLooper()));
    }

    public static void shareImage(Context context, Uri uri, String title) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/png");
        context.startActivity(Intent.createChooser(shareIntent, title));
    }

    public interface PixelCopyListener {
        void onSurfaceBitmapReady(Bitmap bitmap);

        void onSurfaceBitmapError(String errorMsg);
    }
}