package com.routee.game.module.game;

import android.Manifest;
import android.view.View;
import android.widget.Button;

import com.routee.game.R;
import com.routee.game.base.BaseActivity;
import com.routee.game.utils.PermissionDialogHelper;
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
                    selectPic();
                } else {
                    new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe(granted -> {
                                if (granted) {
                                    selectPic();
                                } else {
                                    new PermissionDialogHelper(GameActivity.this).setMsg(getString(R.string.permission_write_external_storage)).show();
                                }
                            });
                }
                break;
            case R.id.bt_take_pic:
                if (new RxPermissions(this).isGranted(Manifest.permission.CAMERA)) {
                    selectPic();
                } else {
                    new RxPermissions(this).request(Manifest.permission.CAMERA)
                            .subscribe(granted -> {
                                if (granted) {
                                    takePic();
                                } else {
                                    new PermissionDialogHelper(GameActivity.this).setMsg(getString(R.string.permission_camera)).show();
                                }
                            });
                }
                break;
            default:
                break;
        }
    }

    private void takePic() {

    }

    private void selectPic() {

    }
}
