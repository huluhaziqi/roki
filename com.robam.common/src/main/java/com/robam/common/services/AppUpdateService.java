package com.robam.common.services;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.legent.Callback;
import com.legent.plat.Plat;
import com.legent.plat.pojos.AppVersionInfo;
import com.legent.services.AbsUpdateService;
import com.legent.services.DownloadService;
import com.legent.ui.UI;
import com.legent.utils.api.PackageUtils;
import com.legent.utils.api.ToastUtils;
import com.legent.utils.api.ViewUtils;
import com.robam.common.R;

@SuppressLint("InflateParams")
public class AppUpdateService extends AbsUpdateService {

    private static AppUpdateService instance = new AppUpdateService();

    synchronized public static AppUpdateService getInstance() {
        return instance;
    }

    Context cx;
    Resources r;
    String apkName;

    private AppUpdateService() {
        apkName = String.format("%s.apk", Plat.app.getPackageName());
    }

    @Override
    public void checkVersion(final Context cx, final CheckVersionListener listener) {
        this.cx = cx;
        r = cx.getResources();

        checkVersion(new Callback<AppVersionInfo>() {
            @Override
            public void onSuccess(AppVersionInfo verInfo) {
                if (listener == null)
                    return;

                if (verInfo == null) {
                    listener.onWithoutNewest();
                } else {
                    int currentVercode = PackageUtils.getVersionCode(cx);

                    if (verInfo.code > currentVercode) {
                        if (Strings.isNullOrEmpty(verInfo.desc))
                            listener.onWithNewest(verInfo.url);
                        else
                            listener.onWithNewest(verInfo.url, verInfo.desc);
                    } else {
                        listener.onWithoutNewest();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (listener != null) {
                    listener.onCheckFailure(new Exception(t));
                }
            }
        });
    }

    public void checkVersion(Callback<AppVersionInfo> callback) {
        Plat.commonService.checkAppVersion(callback);
    }

    @Override
    protected void download(String downUrl) {
        ToastUtils.showLong(R.string.update_downloading);

        String title = r.getString(R.string.update_title);
        String description = r.getString(R.string.update_description);

        try {
            DownloadService.newAppDownloadTask(cx, getClass().getSimpleName(),
                    downUrl).download(apkName, title, description);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onNewest(final String downUrl, Object... params) {
        String title = r.getString(R.string.update_title);
        String desc = null;
        if (params != null && params.length > 0) {
            if (params[0] != null)
                desc = params[0].toString();
        }

        LayoutInflater inflater = (LayoutInflater) cx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_upgrade_newest, null);
        final TextView txtDesc = (TextView) view.findViewById(R.id.txtVerDesc);
        txtDesc.setText(desc);

        OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ViewUtils.setDialogShowField(dialog, true);

                if (which == DialogInterface.BUTTON_POSITIVE) {
                    download(downUrl);
                }
            }
        };

        AlertDialog.Builder builder = new Builder(cx);
        builder.setTitle(title);
        builder.setView(view);
        builder.setNegativeButton(UI.getStr_Cancel(cx), clickListener);
        builder.setPositiveButton(UI.getStr_Ok(cx), clickListener);

        AlertDialog dlg = builder.create();
        dlg.show();
    }

    @Override
    protected void onWithout() {
        String msg = r.getString(R.string.update_newest);
        ToastUtils.showShort(msg);
    }

    @Override
    protected void onFailure(Exception ex) {
        String msg = r.getString(R.string.update_check_failure);
        ToastUtils.showShort(msg);
    }

}
