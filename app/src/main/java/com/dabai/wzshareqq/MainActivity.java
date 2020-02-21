package com.dabai.wzshareqq;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dabai.wzshareqq.utils.Base64;
import com.dabai.wzshareqq.utils.DabaiUtils;
import com.dabai.wzshareqq.utils.Patterns;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.regex.Matcher;

public class MainActivity extends AppCompatActivity {

    private static File photoFile;
    ImageView img1;
    TextView te1, tip1;
    private String link;
    Button bu1;

    Switch sw1;


    String telink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setBackgroundDrawable(new ColorDrawable(0));

        getSupportActionBar().setElevation(0);

        //dark
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setTitle("转二维码");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        te1 = findViewById(R.id.textView1);
        tip1 = findViewById(R.id.tip1);
        img1 = findViewById(R.id.imageView1);
        bu1 = findViewById(R.id.bu1);

        sw1 = findViewById(R.id.sw1);


        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    /**
                     * 提取文本中的链接
                     */
                    String data = link;

                    Matcher matcher = Patterns.WEB_URL.matcher(data);
                    if (matcher.find()) {
                        link = matcher.group();
                    }
                    te1.setText(link);

                    Bitmap bit = createQRCodeBitmap(link, 700, 700, "UTF-8", "H", "1", R.color.colorAccent, Color.WHITE);
                    img1.setImageBitmap(bit);

                } else {
                    link = telink;
                    te1.setText(link);

                    Bitmap bit = createQRCodeBitmap(link, 700, 700, "UTF-8", "H", "1", R.color.colorAccent, Color.WHITE);
                    img1.setImageBitmap(bit);
                }
            }
        });


        try {
            Intent intent = getIntent();

            String getdata = "" + intent.getData();
            telink = getdata;

            if (!getdata.contains("546L6ICF6I2j6ICA")) {

                link = getdata;
                Bitmap bit = createQRCodeBitmap(link, 700, 700, "UTF-8", "H", "1", R.color.colorAccent, Color.WHITE);

                if (bit == null) {
                    Toast.makeText(this, "生成失败:最多可容纳1850个大写字母或2710个数字或1108个字节，或500多个汉字!", Toast.LENGTH_LONG).show();
                    //gohome();
                    return;
                }

                img1.setImageBitmap(bit);
                te1.setText(link);

                tip1.setText("扫码识别文本或链接");


            } else {
                String base64_link = getdata.substring(getdata.indexOf("&url") + 5, getdata.indexOf("&app_name"));
                Base64 base64 = new Base64();
                link = base64.decode(base64_link);

                Bitmap bit = createQRCodeBitmap(link, 700, 700, "UTF-8", "H", "1", R.color.colorAccent, Color.WHITE);

                img1.setImageBitmap(bit);
                te1.setText(link);

                tip1.setText("扫码进入王者荣耀房间");

            }
        } catch (Exception e) {
            Toast.makeText(this, "转二维码异常:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private void gohome() {
        Intent intent2 = new Intent();
        // 为Intent设置Action、Category属性
        intent2.setAction(Intent.ACTION_MAIN);// "android.intent.action.MAIN"
        intent2.addCategory(Intent.CATEGORY_HOME); //"android.intent.category.HOME"
        startActivity(intent2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                // 处理返回逻辑
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //根据view获取bitmap
    public static Bitmap getBitmapByView(View view) {
        int h = 0;
        Bitmap bitmap = null;
        bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    //检查sd
    public static boolean checkSDCardAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static void savePhotoToSDCard(Bitmap photoBitmap, String path, String photoName) {
        if (checkSDCardAvailable()) {

            photoFile = new File(path, photoName + ".png");
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap != null) {
                    if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
                        fileOutputStream.flush();
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 生成简单二维码
     *
     * @param content                字符串内容
     * @param width                  二维码宽度
     * @param height                 二维码高度
     * @param character_set          编码方式（一般使用UTF-8）
     * @param error_correction_level 容错率 L：7% M：15% Q：25% H：35%
     * @param margin                 空白边距（二维码与边框的空白区域）
     * @param color_black            黑色色块
     * @param color_white            白色色块
     * @return BitMap
     */
    public static Bitmap createQRCodeBitmap(String content, int width, int height,
                                            String character_set, String error_correction_level,
                                            String margin, int color_black, int color_white) {
        // 字符串内容判空
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        // 宽和高>=0
        if (width < 0 || height < 0) {
            return null;
        }
        try {
            /** 1.设置二维码相关配置 */
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            // 字符转码格式设置
            if (!TextUtils.isEmpty(character_set)) {
                hints.put(EncodeHintType.CHARACTER_SET, character_set);
            }
            // 容错率设置
            if (!TextUtils.isEmpty(error_correction_level)) {
                hints.put(EncodeHintType.ERROR_CORRECTION, error_correction_level);
            }
            // 空白边距设置
            if (!TextUtils.isEmpty(margin)) {
                hints.put(EncodeHintType.MARGIN, margin);
            }
            /** 2.将配置参数传入到QRCodeWriter的encode方法生成BitMatrix(位矩阵)对象 */
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值 */
            int[] pixels = new int[width * height];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    //bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = color_black;//黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white;// 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象 */
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            return null;
        }
    }


    public void share_qr(View view) {
        bu1.setText("处理中");
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.line2).setVisibility(View.GONE);
                        sw1.setVisibility(View.GONE);
                    }
                });

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bu1.setText("分享二维码");
                        savePhotoToSDCard(getBitmapByView(findViewById(R.id.line1)), getApplicationContext().getExternalFilesDir("imgtmp").getAbsolutePath(), "QRtmp");
                        shareImageToQQ(photoFile.getAbsolutePath());
                        findViewById(R.id.line2).setVisibility(View.VISIBLE);
                        sw1.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();


    }


    public void share_link(View view) {
        new DabaiUtils().sendText(this, "来自" + Build.MODEL + "分享的王者荣耀链接:\n" + link);
    }


    /**
     * 分享图片给QQ好友
     */
    public void shareImageToQQ(String path) {

        try {
            Uri uriToImage = Uri.parse(path);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.setType("image/*");
            // 遍历所有支持发送图片的应用。找到需要的应用
            //ComponentName componentName = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");

            //shareIntent.setComponent(componentName);
            // mContext.startActivity(shareIntent);
            startActivity(Intent.createChooser(shareIntent, "分享图片"));
        } catch (Exception e) {
            Toast.makeText(this, "分享失败！", Toast.LENGTH_SHORT).show();
        }

    }

    public void seeall(View view) {

        BottomSheetDialog bsd = new BottomSheetDialog(this);

        bsd.setContentView(R.layout.img_sheet);
        ImageView iv4 = bsd.findViewById(R.id.imageView4);

        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width2 = outMetrics.widthPixels;

        Bitmap bit = createQRCodeBitmap(link, width2, width2, "UTF-8", "H", "1", Color.BLACK, Color.WHITE);
        iv4.setImageBitmap(bit);
        bsd.show();

    }
}
