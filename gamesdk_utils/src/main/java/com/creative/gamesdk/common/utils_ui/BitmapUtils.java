package com.creative.gamesdk.common.utils_ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.Base64;

import com.creative.gamesdk.common.utils_base.cache.ApplicationCache;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2019/2/25.
 * 处理图片bitmap
 */

public class BitmapUtils {

    /**
     * 获取网络图片
     *
     * @param imageUrl
     * @return
     */
    public static Bitmap getImageBitmap(String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            return null;
        }

        Bitmap bitmap = null;

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * 保存图片到私有内存
     *
     * @param bm
     * @param fileName
     * @return
     */
    public static String savePicture(Bitmap bm, String fileName) {

        try {
            File file = ApplicationCache.getInstance().getApplicationContext().getExternalCacheDir();
            File pictureFile = new File(file.getPath(), fileName);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(pictureFile));
            if (fileName.endsWith(".png")) {
                bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
            } else if (fileName.endsWith(".jpg")) {
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            }
            bos.flush();
            bos.close();
            return pictureFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取私有内存指定文件
     *
     * @param fileName
     * @return
     */
    public static Bitmap getPicture(String fileName) {
        String filePath = ApplicationCache.getInstance().getApplicationContext().getExternalCacheDir().getPath();
        File file = new File(filePath, fileName);
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            Bitmap bm = BitmapFactory.decodeStream(in);
            in.close();
            return bm;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 压缩bitmap方法
     *
     * @param bitmap
     * @param maxSize 单位kb
     * @return
     */
    public static Bitmap getZoomedBitmap(Bitmap bitmap, double maxSize) {

        if (null == bitmap || bitmap.isRecycled()) {
            return null;
        }

        double currentSize = bitmapToByte(bitmap, false).length / 1024;
        while (currentSize > maxSize) {
            double multiple = currentSize / maxSize;
            bitmap = getZoomedBitmap(bitmap, bitmap.getWidth() / Math.sqrt(multiple), bitmap.getHeight() / Math.sqrt(multiple));
            currentSize = bitmapToByte(bitmap, false).length / 1024;
        }

        return bitmap;
    }

    private static Bitmap getZoomedBitmap(Bitmap orgBitmap, double newWidth, double newHeight) {
        if (orgBitmap == null || orgBitmap.isRecycled()) {
            return null;
        }

        if (newWidth <= 0 || newHeight <= 0) {
            return null;
        }

        float width = orgBitmap.getWidth();
        float height = orgBitmap.getHeight();
        Matrix matrix = new Matrix();

        float scaleWidth = (float) (width / newWidth);
        float scaleHeight = (float) (height / newHeight);

        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(orgBitmap, 0, 0, (int) width, (int) height, matrix, true);

        return bitmap;
    }


    /**
     * 将BitMap转换为byte[]
     *
     * @param bitmap
     * @param needRecycle
     * @return
     */
    public static byte[] bitmapToByte(Bitmap bitmap, boolean needRecycle) {
        if (null == bitmap || bitmap.isRecycled()) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        if (needRecycle) {
            bitmap.recycle();
        }
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    /**
     * 将bitmap转化成String
     * @param bitmap
     * @return
     */
    public static String bitmapToString(Bitmap bitmap){
        if (bitmap==null){
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
        byte[] bytes = bos.toByteArray();
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
        return result;
    }

    /**
     * 将String转化为bitmap
     * @param str
     * @return
     */
    public static Bitmap stringToBitmap(String str){
        Bitmap bitmap = null;
        try {
            byte[] bytes = Base64.decode(str,Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        } catch (Exception e) {
            return null;
        }
        return bitmap;
    }

    /**
     * 图片的大小
     * @param size
     * @return
     */
    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

}
