package com.routee.game.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

/**
 * @author: Routee
 * @date 2018/4/23
 * @mail wangc4@qianbaocard.com
 * ------------1.本类由Routee开发,阅读、修改时请勿随意修改代码排版格式后提交到git。
 * ------------2.阅读本类时，发现不合理请及时指正.
 * ------------3.如需在本类内部进行修改,请先联系Routee,若未经同意修改此类后造成损失本人概不负责。
 */

public class PermissionDialogHelper {

    private final Context mContext;

    public PermissionDialogHelper(Context context) {
        mContext = context;
    }

    public AlertDialog setMsg(String msg) {
        AlertDialog dialog = new AlertDialog.Builder(mContext).setMessage(msg)
                                     .setCancelable(true)
                                     .setPositiveButton("去设置", (dialogInterface, i) -> {
                                         gotoSetting();
                                     }).create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private void gotoSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
        intent.setData(uri);
        mContext.startActivity(intent);
    }
}
