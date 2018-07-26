package com.github.snailycy.hybridlib.util;

import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 缓存相关操作
 */

public class HybridCacheUtils {

    private static String DIR = ".app";

    public static final String DIR_H5 = "h5";

    /**
     * 设置缓存路径
     * dafault '.app'
     *
     * @param rootDir
     */
    public static void setCacheRootDir(String rootDir) {
        DIR = rootDir;
    }

    public static String convertUriToFilePath(Uri uri, boolean needQueryParams) {
        if (null == uri || TextUtils.isEmpty(uri.toString())) {
            return null;
        }

        // 获取SD卡主缓存目录
        String cacheDir = getCacheDir();
        if (TextUtils.isEmpty(cacheDir)) {
            return null;
        }
        File file = new File(cacheDir, DIR_H5);
        if (!file.exists()) {
            file.mkdirs();
        }
        cacheDir = file.getAbsolutePath();

        // 取出host + path信息
        StringBuilder filePathSB = new StringBuilder(cacheDir);
        filePathSB.append(File.separator);
        String host = uri.getHost();
        if (!TextUtils.isEmpty(host)) {
            filePathSB.append(host);
        }

        String path = uri.getPath();
        if (!TextUtils.isEmpty(path)) {
            filePathSB.append(path);
        }
        if (needQueryParams) {
            // Query信息
            String query = uri.getQuery();
            if (!TextUtils.isEmpty(query)) {
                filePathSB.append(query);
            }
        }
        // host + path不为空
        if (filePathSB.length() > 0) {
            return filePathSB.toString();
        }
        return null;
    }

    /**
     * 网络路径转换成本地路径，默认带query参数
     *
     * @param uri
     * @return
     */
    public static String convertUriToFilePath(Uri uri) {
        return convertUriToFilePath(uri, true);
    }

    public static void saveResource(Uri uri, String cacheFilePath, OnDownloadListener onDownloadListener) {
        DownloadFileUtils.download(uri, cacheFilePath, onDownloadListener);
    }

    /**
     * 根据路径后缀判断文本类型
     *
     * @param path
     * @return
     */
    public static String getResourceType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        } else if (path.endsWith(".js")) {
            return "application/x-javascript";
        } else if (path.endsWith(".png")) {
            return "image/png";
        } else if (path.endsWith(".jpg")) {
            return "image/jpeg";
        } else if (path.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        return "TEXT/HTML";//默认
    }

    /**
     * 返回HTML页面的MimeType（有些HttpURLConnection获取的contentType为“text/html;charset=UTF-8”时，页面显示为代码。设置为text/html时能显示网页）
     *
     * @param contentType
     * @return
     */
    public static String getHtmlMimeType(String contentType) {
        String mimeType = "text/html";
        if (!TextUtils.isEmpty(contentType)) {
            if (contentType.contains(";")) {//如果contentType为“text/html;charset=UTF-8”这种形式
                String[] args = contentType.split(";");
                mimeType = args[0];
            } else {//contentType为“text/html”这种形式
                mimeType = contentType;
            }
        }
        return mimeType;
    }

    /**
     * 返回HTML页面的encoding
     *
     * @param contentType
     * @return
     */
    public static String getHtmlEncoding(String contentType) {
        String encoding = "UTF-8";
        if (!TextUtils.isEmpty(contentType)) {
            if (contentType.contains(";")) {//如果contentType为“text/html;charset=UTF-8”这种形式
                String[] args = contentType.split(";");
                if (args.length > 1 && args[1].trim().contains("charset=")) {
                    encoding = args[1].substring(args[1].indexOf("=") + 1);//截取charset=后的encoding
                }
            }
        }
        return encoding;
    }

    /**
     * 将InputStream中的字节保存到ByteArrayOutputStream中。
     *
     * @param inputStream
     * @return
     */
    public static ByteArrayOutputStream inputStreamCache(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        if (inputStream == null) {
            return null;
        }
        byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;

        try {
            while ((len = inputStream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != byteArrayOutputStream) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return byteArrayOutputStream;

    }

    /**
     * 将缓存ByteArrayOutputStream中的内容转为InputStream
     *
     * @param byteArrayOutputStream
     * @return
     */
    public static InputStream getInputStream(ByteArrayOutputStream byteArrayOutputStream) {
        if (byteArrayOutputStream == null) {
            return null;
        }
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    /**
     * 将缓存ByteArrayOutputStream中的内容转为JsonObject
     *
     * @param byteArrayOutputStream
     * @return
     */
    public static JsonObject getJsonObjectFromOutputStream(ByteArrayOutputStream byteArrayOutputStream) {
        if (byteArrayOutputStream == null) {
            return null;
        }
        String content = byteArrayOutputStream.toString();
        JsonObject returnData = null;
        if (content.startsWith("{") && content.endsWith("}")) {
            returnData = new JsonParser().parse(content).getAsJsonObject();
        }
        return returnData;
    }

    /**
     * 判断资源是否需要保存
     *
     * @param uri
     * @return
     */
    public static boolean needCache(Uri uri) {
        if (null == uri || TextUtils.isEmpty(uri.getPath())) {
            return false;
        }

        String path = uri.getPath();

        // 这些资源类型需要缓存
        if (path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png")
                || path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".gif")) {

            // "hm.gif","0.gif","v.gif" 不保存
            if (path.endsWith("0.gif") || path.endsWith("v.gif") || path.endsWith("hm.gif")) {
                return false;
            }

            if (!TextUtils.isEmpty(uri.getQuery())) {
                return false;
            }

            return true;
        }
        return false;
    }

    /**
     * 判断路径是否存在
     *
     * @param path
     * @return
     */
    public static boolean checkPathExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        File file = new File(path);
        if (null == file) {
            return false;
        }
        if (file.exists()) {
            // 如果文件存在，且缓存的文件大小为0，说明之前下载失败，删除文件
            if (file.length() <= 0) {
                file.delete();
                return false;
            }
            // 文件存在，且大小大于0
            return true;
        }
        return false;
    }

    /**
     * 获取缓存文件，返回字符串文本
     *
     * @param path
     * @return
     */
    public static String getCacheFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File file = new File(path);
        if (null == file) {
            return null;
        }
        if (file.exists()) {
            // 如果文件存在，且缓存的文件大小为0，说明之前下载失败，删除文件
            if (file.length() <= 0) {
                file.delete();
                return null;
            }
            // 读文件
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                byte[] buff = new byte[fis.available()];
                fis.read(buff);
                return new String(buff, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取SD卡下的缓存目录
     *
     * @return
     */
    public static String getCacheDir() {
        // 获取SD卡根目录
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        if (null == externalStorageDirectory || !externalStorageDirectory.exists()) {
            return null;
        }

        // 根目录下的缓存目录
        File cacheDir = new File(externalStorageDirectory, DIR);
        if (null == cacheDir || !cacheDir.exists()) {
            // 若主缓存目录不存在，创建
            if (null != cacheDir) {
                cacheDir.mkdirs();
            }
            return null;
        }
        return cacheDir.getAbsolutePath();
    }

    /**
     * 判断SDCard是否可用
     */
    public static boolean mountedSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


    /**
     * 根据系统时间、前缀、后缀产生一个文件
     */
    public static File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }
}
