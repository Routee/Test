package com.routee.game.module.game;

import android.Manifest;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.routee.game.R;
import com.routee.game.base.BaseActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Routee
 */
public class GameActivity extends BaseActivity {

    @BindView(R.id.bt_select_pic)
    Button mBtSelectPic;
    @BindView(R.id.bt_take_pic)
    Button mBtTakePic;

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
    }

    @OnClick({R.id.bt_select_pic, R.id.bt_take_pic})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_select_pic:
                if (new RxPermissions(this).isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    slectPic();
                } else {
                    new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                break;
            case R.id.bt_take_pic:
                Toast.makeText(this, "拍照", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void slectPic() {

    }
}
