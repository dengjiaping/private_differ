package com.appgame.differ.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static com.liulishuo.filedownloader.util.FileDownloadUtils.generateFileName;
import static com.liulishuo.filedownloader.util.FileDownloadUtils.generateFilePath;

/**
 * Created by lzx on 2017/3/2.
 * 386707112@qq.com
 */

public class FileUtil {

    /**
     * 将Bitmap写入SD卡中的一个文件中,并返回写入文件的Uri
     */
    public static Uri saveBitmap(Bitmap bm, String dirPath) {
        //新建文件夹用于存放裁剪后的图片
        File tmpDir = new File(Environment.getExternalStorageDirectory() + "/" + dirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        //新建文件存储裁剪后的图片
        File img = new File(tmpDir.getAbsolutePath() + "/" + System.currentTimeMillis() + ".png");
        try {
            //打开文件输出流
            FileOutputStream fos = new FileOutputStream(img);
            //将bitmap压缩后写入输出流(参数依次为图片格式、图片质量和输出流)
            bm.compress(Bitmap.CompressFormat.PNG, 90, fos);
            //刷新输出流
            fos.flush();
            //关闭输出流
            fos.close();
            //返回File类型的Uri
            return Uri.fromFile(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将content类型的Uri转化为文件类型的Uri
     */
    public static Uri convertUri(Context context, Uri uri) {
        InputStream is;
        try {
            is = context.getContentResolver().openInputStream(uri);
            Bitmap bm = BitmapFactory.decodeStream(is);
            //关闭流
            is.close();
            return saveBitmap(bm, "temp");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 把Bitmap转Byte
     */
    public static String bitmapToBytes(Bitmap bm) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 90, baos);
            return Base64.encodeToString(baos.toByteArray(), 0, baos.toByteArray().length, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getImagePath() {
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/differ/image";
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }

    public static boolean deleteImage(String filePath) {
        boolean isSuccess = false;
        if (isExistsFile(filePath)) {
            File file = new File(filePath);
            isSuccess = file.delete();
        }
        return isSuccess;
    }


    /**
     * 查找是否存在文件
     */
    public static boolean isExistsFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建下载文件夹路径
     */
    public static String getDownloadPath() {
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/differ/download";
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }

    /**
     * 创建图片文件
     */
    public static File createImageFile(String fileName) {
        return createFile(fileName, "/differ/image");
    }

    /**
     * 创建日志文件
     */
    public static File createLogFile(String fileName) {
        return createFile(fileName, "/differ/log");
    }

    /**
     * 向文件中写入内容
     */
    public static void writeFile(String filePathAndName, String fileContent) {
        try {
            OutputStream outputStream = new FileOutputStream(filePathAndName);
            OutputStreamWriter out = new OutputStreamWriter(outputStream);
            out.write(fileContent);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File createFile(String fileName, String path) {
        String filePath = Environment.getExternalStorageDirectory().getPath() + path;
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File imageFile = new File(file, fileName);
        if (imageFile.exists()) {
            boolean isDelete = imageFile.delete();
            LogUtil.i("isDelete = " + isDelete);
        }
        try {
            boolean isCreate = imageFile.createNewFile();
            LogUtil.i("isCreate = " + isCreate);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    /**
     * 创建下载文件夹路径
     */
    public static String getDefaultSaveFilePath(final String url) {
        return generateFilePath(getDownloadPath(), generateFileName(url) + ".apk");
    }

    /**
     * 创建下载文件夹路径
     */
    public static String createPath(final String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        return getDefaultSaveFilePath(url);
    }

    /**
     * 查找是否存在安装包
     */
    public static boolean isExistsInstallationPackage(String url) {
        try {
            String filePath = getDownloadPath() + "/" + generateFileName(url) + ".apk";
            File file = new File(filePath);
            if (file.exists()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isExistsInstallationPackageTemp(String url) {
        try {
            String filePath = getDownloadPath() + "/" + generateFileName(url) + ".apk.temp";
            File file = new File(filePath);
            if (file.exists()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 得到安装包file
     */
    public static File getInstallationPackageFile(String url) {
        String filePath = getDownloadPath() + "/" + generateFileName(url) + ".apk";
        return new File(filePath);
    }

    public static String getTempDownloadPath(String url) {
        return getDownloadPath() + "/" + generateFileName(url) + ".apk.temp";
    }

    /**
     * 删除安装包
     */
    public static boolean deleteGamePkgFile(String url) {
        boolean isSuccess = false;
        if (isExistsInstallationPackage(url)) {
            File file = getInstallationPackageFile(url);
            isSuccess = file.delete();
        }
        return isSuccess;
    }


    /**
     * 拍照
     */
    public static void takePicFromCamera(Activity activity, int requestCode) {
        File imgFile = new File(generateImgePath());
        Uri imgUri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            //如果是7.0或以上，使用getUriForFile()获取文件的Uri
            imgUri = FileProvider.getUriForFile(activity, "com.appgame.differ" + ".provider", imgFile);
        } else {
            imgUri = Uri.fromFile(imgFile);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 相册
     */
    public static void takePicFromLocal(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 发起剪裁图片的请求
     *
     * @param activity    上下文
     * @param srcFile     原文件的File
     * @param output      输出文件的File
     * @param requestCode 请求码
     */
    public static void startPhotoZoom(Activity activity, File srcFile, File output, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= 24) {
            //兼容7.0
            intent.setDataAndType(getImageContentUri(activity, srcFile), "image/*");
        } else {
            intent.setDataAndType(Uri.fromFile(srcFile), "image/*");
        }
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("return-data", false);// true:不返回uri，false：返回uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 安卓7.0裁剪根据文件路径获取uri
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 将Uri转换为Bitmap对象
     *
     * @param context
     * @param uri
     * @return
     */
    public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            // 先通过getContentResolver方法获得一个ContentResolver实例，
            // 调用openInputStream(Uri)方法获得uri关联的数据流stream
            // 把上一步获得的数据流解析成为bitmap
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));

            // bitmap = compressImage(bitmap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 按质量压缩bm
     *
     * @param bm
     * @param quality 压缩保存率
     * @return
     */
    public static String saveBitmapByQuality(Bitmap bm, int quality) {
        String croppath = "";
        try {
            File f = new File(generateImgePath());
            //得到相机图片存到本地的图片
            croppath = f.getPath();
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.flush();
            out.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmapToBytes(bm);
    }

    public static String generateImgePath() {
        return getImagePath() + "/" + System.currentTimeMillis() + ".jpeg";
    }


    /**
     * 根据路径获得图片信息并按比例压缩，返回bitmap
     */
    public static Bitmap getBitmapFromPath(String filePath) {

        return BitmapFactory.decodeFile(filePath);
    }


    /**
     * 根据路径获得图片信息并按比例压缩，返回bitmap
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//只解析图片边沿，获取宽高
        BitmapFactory.decodeFile(filePath, options);
        // 计算缩放比
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // 完整解析图片返回bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


}
