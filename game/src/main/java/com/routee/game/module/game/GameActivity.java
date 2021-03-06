package com.routee.game.module.game;

import android.Manifest;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.routee.game.R;
import com.routee.game.base.BaseActivity;
import com.routee.game.utils.PermissionDialogHelper;
import com.routee.game.view.NinePicGameView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;

/**
 * @author Routee
 */
public class GameActivity extends BaseActivity implements View.OnTouchListener, NinePicGameView.FinishListener, View.OnClickListener {

    private static final int IMAGE_PICKER = 100;
    Button          mBtSelectPic;
    NinePicGameView mIv;
    Button          mBtShowPic;
    Button          mBtEasy;
    Button          mBtHard;
    TextView        mTvTime;

    private ImageItem mImageItem;

    @Override
    public void setView() {
        mBtShowPic.setOnTouchListener(this);
        mIv.addFinishListener(this);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
        mBtSelectPic = (Button) findViewById(R.id.bt_select_pic);
        mBtSelectPic.setOnClickListener(this);
        mIv = (NinePicGameView) findViewById(R.id.game_view);
        mBtShowPic = (Button) findViewById(R.id.bt_show_pic);
        mBtEasy = (Button) findViewById(R.id.bt_easy);
        mBtEasy.setOnClickListener(this);
        mBtHard = (Button) findViewById(R.id.bt_hard);
        mBtHard.setOnClickListener(this);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());     //设置图片加载器
        imagePicker.setShowCamera(true);                        //显示拍照按钮
        imagePicker.setCrop(true);                              //允许裁剪（单选才有效）
        imagePicker.setSelectLimit(1);                          //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);    //裁剪框的形状
    }

    private void selectPic() {
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, IMAGE_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                mImageItem = images.get(0);
                mIv.setImage(mImageItem.path);
            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.bt_show_pic) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mIv.showResult();
                    break;
                case MotionEvent.ACTION_UP:
                    mIv.hideResult();
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    @Override
    public void finish(long mills) {
        long seconds = mills / 1000;
        Toast.makeText(this, "Congratulations!!! It cost you " + seconds + "s", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_easy) {
            mIv.setEasier();
        } else if (i == R.id.bt_hard) {
            mIv.setHarder();
        } else if (i == R.id.bt_select_pic) {
            RxPermissions rxPermissions = new RxPermissions(this);
            if (rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE) && rxPermissions.isGranted(Manifest.permission.CAMERA)) {
                selectPic();
            } else {
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .subscribe(granted -> {
                            if (granted) {
                                selectPic();
                            } else {
                                new PermissionDialogHelper(this).setMsg("没有文件存储或相机权限，请去设置页添加相应权限").show();
                            }
                        });
            }
        }
    }
}
