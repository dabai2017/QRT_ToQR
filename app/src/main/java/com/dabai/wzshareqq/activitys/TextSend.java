package com.dabai.wzshareqq.activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dabai.wzshareqq.MainActivity;

public class TextSend extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        try {

            if (intent == null)
                return;
            Bundle extras = intent.getExtras();

            if (extras == null)
                return;

            switch (intent.getType()) {
                case "text/plain"://分享的内容类型，如果png图片：image/png
                    ToRes(extras.get(Intent.EXTRA_TEXT) + "");
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            Toast.makeText(this, "异常:"+e, Toast.LENGTH_SHORT).show();
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
