package com.dabai.wzshareqq.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.dabai.wzshareqq.MainActivity;

public class TextSend extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();


        try {
            if (intent.getClipData().getItemAt(0).getText() == null) {
                Toast.makeText(this, "没有获取到数据，无法生成二维码", Toast.LENGTH_SHORT).show();
                gohome();
                finish();
                return;
            }
        } catch (Exception e) {
            finish();
            return;
        }


        try {
            String link = "" + intent.getClipData().getItemAt(0).getText();
            ToRes(link);

        } catch (Exception e) {
            Toast.makeText(this, "没有获取到数据，无法生成二维码", Toast.LENGTH_SHORT).show();
            gohome();
            finish();
            return;
        }

    }

    private void gohome() {
        Intent intent2 = new Intent();
        // 为Intent设置Action、Category属性
        intent2.setAction(Intent.ACTION_MAIN);// "android.intent.action.MAIN"
        intent2.addCategory(Intent.CATEGORY_HOME); //"android.intent.category.HOME"
        startActivity(intent2);
    }

    /**
     * 传值给 生成二维码界面
     *
     * @param text
     */
    void ToRes(String text) {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.setData(Uri.parse(text));
        startActivity(resultIntent);
        finish();
    }


}
