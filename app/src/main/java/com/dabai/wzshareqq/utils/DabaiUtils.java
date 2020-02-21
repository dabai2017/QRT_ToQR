package com.dabai.wzshareqq.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class DabaiUtils {

    /**
     开发者 DABAI2017
     创建日期 2019.5.9
     创建类型 工具类
     **/

    /**
     * 打开app
     */

    public void openApp(Context context, String Packname) {
        Intent resolveIntent = context.getPackageManager().getLaunchIntentForPackage(Packname);
        context.startActivity(resolveIntent);
    }

    /**
     * 打开链接
     *
     * @param link
     */
    public void openLink(Context context, String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));
        context.startActivity(intent);
    }

    //发送文本
    public void sendText(Context context, String p0) {

        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            // 指定发送内容的类型
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, p0);
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(Intent.createChooser(sendIntent, "分享文本"));
        } catch (Exception e) {
            Toast.makeText(context, "调用分享组件失败!" + e, Toast.LENGTH_SHORT).show();
            Log.d("dabaizzz", e + "");
        }

    }


    //分享文件
    void shareFile(Context c, String path) {
        Intent imageIntent = new Intent(Intent.ACTION_SEND);
        imageIntent.setType("*/*");
        imageIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(String.format("file://%s", path)));
        c.startActivity(Intent.createChooser(imageIntent, "分享"));
    }

}