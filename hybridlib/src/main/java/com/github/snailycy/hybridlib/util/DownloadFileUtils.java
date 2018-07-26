package com.github.snailycy.hybridlib.util;

import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ycy on 2017/10/12.
 */

public class DownloadFileUtils {

    private static OkHttpClient sOkHttpClient = new OkHttpClient();

    public static void download(final Uri uri, final String saveDir, final OnDownloadListener listener) {
        if (null == uri || TextUtils.isEmpty(uri.toString()) || TextUtils.isEmpty(saveDir)) {
            // 下载失败
            if (listener != null)
                listener.onFailed();
            return;
        }
        String scheme = uri.getScheme();
        if (!TextUtils.equals(scheme, "http") && !TextUtils.equals(scheme, "https")) {
            // 下载失败
            if (listener != null)
                listener.onFailed();
            return;
        }

        Request request = new Request.Builder().url(uri.toString()).build();
        sOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                if (listener != null)
                    listener.onFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                FileOutputStream fos = null;
                File file = new File(saveDir);
                try {
                    is = response.body().byteStream();
                    File parentFile = file.getParentFile();
                    if (null != parentFile && !parentFile.exists()) {
                        parentFile.mkdirs();
                    }
                    file.createNewFile();
                    fos = new FileOutputStream(file);
                    byte[] buf = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    // 下载成功
                    if (listener != null)
                        listener.onSuccess();
                } catch (Exception e) {
                    // 下载失败
                    if (listener != null)
                        listener.onFailed();
                    if (null != file) {
                        file.delete();
                    }
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        });
    }
}
