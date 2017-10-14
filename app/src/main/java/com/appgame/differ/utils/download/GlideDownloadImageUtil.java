package com.appgame.differ.utils.download;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.appgame.differ.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * 使用Glide下载图片到本地工具类
 */
public class GlideDownloadImageUtil {

    public static Observable<Uri> saveImageToLocal(final Context context, final String url) {
        return Observable.create((ObservableOnSubscribe<File>) e -> {
            File file;
            try {
                LogUtil.all("download" + url);
                file = Glide.with(context).load(url).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                e.onNext(file);
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }
        }).flatMap(new Function<File, ObservableSource<Uri>>() {

            @Override
            public ObservableSource<Uri> apply(@NonNull File file) throws Exception {
                File mFile = null;
                try {
                    String path = Environment.getExternalStorageDirectory() + File.separator + "differ";
                    File dir = new File(path);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    String fileName = System.currentTimeMillis() + ".jpg";
                    mFile = new File(dir, fileName);
                    FileInputStream fis = new FileInputStream(file.getAbsolutePath());
                    int byteread;
                    byte[] buf = new byte[1444];
                    FileOutputStream fos = new FileOutputStream(mFile.getAbsolutePath());
                    while ((byteread = fis.read(buf)) != -1) {
                        fos.write(buf, 0, byteread);
                    }
                    fos.close();
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.all("图片下载失败");
                }
                //更新本地图库
                Uri uri = Uri.fromFile(mFile);
                Intent mIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                context.sendBroadcast(mIntent);
                return Observable.just(uri);
            }
        }).subscribeOn(Schedulers.io());
    }
}
