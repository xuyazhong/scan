package com.xuyazhong.scan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.xuyazhong.scan.R;

import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class ScanQRActivity extends AppCompatActivity implements QRCodeView.Delegate {
    private static final String TAG = "###";
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;
    private ZXingView mZXingView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan_qr);

        mZXingView = findViewById(R.id.zxingview);
        mZXingView.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
        mZXingView.getScanBoxView().setOnlyDecodeScanBoxArea(true); // 仅识别扫描框中的码
        mZXingView.changeToScanQRCodeStyle(); // 切换成扫描二维码样式
        mZXingView.setType(BarcodeType.ONLY_QR_CODE, null); // 只识别 QR_CODE
        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别

    }

    public void clickBack(View v) {
        close();
        finish();
    }

    @Override
    protected void onStop() {
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {

        Log.i(TAG, "result:" + result);
        setTitle("扫描结果为：" + result);
//        vibrate();
        mZXingView.stopSpot(); // 停止识别
        /// 关闭页面，传值
        Intent intent1 = new Intent();
        intent1.putExtra("QRCODE",result);
        setResult(99, intent1);
        finish();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        String tipText = mZXingView.getScanBoxView().getTipText();
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZXingView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZXingView.getScanBoxView().setTipText(tipText);
            }
        }
    }

    private void close() {
        mZXingView.stopSpot(); // 停止识别
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        setResult(99);
    }

    @Override
    public void onBackPressed() {
        close();
        super.onBackPressed();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
        Toast.makeText(ScanQRActivity.this, "打开相机出错", Toast.LENGTH_LONG).show();
        finish();
    }
}
