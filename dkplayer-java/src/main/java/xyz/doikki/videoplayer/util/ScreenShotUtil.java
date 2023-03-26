package xyz.doikki.videoplayer.util;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenShotUtil {
    public static void ScreenShot(Context context, Bitmap bitmap) {
        String str = Environment.getExternalStorageDirectory().getPath() + File.separator + "VideoPlayScreenImg/image/" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())) + ".jpg";
        ContentValues contentValues = new ContentValues();
        contentValues.put("_display_name", str);
        contentValues.put("description", str);
        contentValues.put("mime_type", "image/jpeg");
        try {
            OutputStream openOutputStream = context.getContentResolver().openOutputStream(context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, openOutputStream);
            openOutputStream.close();
//            ToastUtils.show((CharSequence) "截图已保存");
            Toast.makeText(context, "截图已保存", Toast.LENGTH_SHORT).show();
        } catch (Exception unused) {
            Toast.makeText(context, "截图保存失败", Toast.LENGTH_SHORT).show();
//            ToastUtils.show((CharSequence) "截图保存失败");
        }
    }
}
