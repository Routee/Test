package com.routee.game.module.game;

import android.Manifest;
import android.content.Intent;
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

    private final int SELECT_PIC = 100;

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
    }

    @OnClick({R.id.bt_select_pic})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_select_pic:
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
                break;
            default:
                break;
        }
    }

    private void selectPic() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
