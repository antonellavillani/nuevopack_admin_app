package com.nuevopack.admin.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageUtils {
    // Comprime y convierte a Base64
    public static String compressImageToBase64(Context context, Uri imageUri, int maxWidth, int maxHeight, int quality) {
        try {
            InputStream is = context.getContentResolver().openInputStream(imageUri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);
            is.close();

            int srcWidth = options.outWidth;
            int srcHeight = options.outHeight;

            float ratio = Math.min((float) maxWidth / srcWidth, (float) maxHeight / srcHeight);
            int newWidth = Math.round(srcWidth * ratio);
            int newHeight = Math.round(srcHeight * ratio);

            is = context.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            byte[] byteArray = baos.toByteArray();
            return android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
