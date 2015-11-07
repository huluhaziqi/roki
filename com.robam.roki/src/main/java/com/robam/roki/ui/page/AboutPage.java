package com.robam.roki.ui.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.legent.Callback;
import com.legent.plat.Plat;
import com.legent.plat.pojos.AppVersionInfo;
import com.legent.ui.UIService;
import com.legent.ui.ext.HeadPage;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.utils.api.PackageUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.common.io.cloud.IRokiRestService;
import com.robam.common.services.AppUpdateService;
import com.robam.roki.R;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.PageKey;

import java.util.concurrent.ScheduledFuture;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by sylar on 15/6/14.
 */
public class AboutPage extends HeadPage {

    ScheduledFuture<?> future;
    @InjectView(R.id.txtVersion)
    TextView txtVersion;
    @InjectView(R.id.txtNewestVer)
    TextView txtNewestVer;
    @InjectView(R.id.txtWebSite)
    TextView txtWebSite;
    @InjectView(R.id.txtTerm)
    TextView txtTerm;

    AppVersionInfo verInfo;

    @Override
    protected View onCreateContentView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(R.layout.page_about, viewGroup, false);
        ButterKnife.inject(this, view);

        txtVersion.setText("V " + PackageUtils.getVersionName(cx));
        checkVer();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.txtNewestVer)
    public void onClickNewestVer() {
        if (verInfo == null) {
            checkVer();
        } else {
            AppUpdateService.getInstance().start(cx);
        }
    }

    @OnClick(R.id.txtWebSite)
    public void onClickWebSite() {
        Bundle bd = new Bundle();
        bd.putString(PageArgumentKey.Url, txtWebSite.getText().toString());
        UIService.getInstance().postPage(PageKey.WebClient, bd);
    }

    @OnClick(R.id.txtTerm)
    public void onClickTerm() {
        String url = String.format("%s/%s",
                Plat.serverOpt.getRestfulBaseUrl(),
                IRokiRestService.Url_UserProfile);

        Bundle bd = new Bundle();
        bd.putString(PageArgumentKey.Url, url);
        UIService.getInstance().postPage(PageKey.WebClient, bd);
    }

    void checkVer() {
        ProgressDialogHelper.setRunning(cx, true);
        AppUpdateService.getInstance().checkVersion(new Callback<AppVersionInfo>() {
            @Override
            public void onSuccess(AppVersionInfo info) {
                verInfo = info;
                ProgressDialogHelper.setRunning(cx, false);
                txtNewestVer.setText(info != null ? info.name : txtVersion.getText());
            }

            @Override
            public void onFailure(Throwable t) {
                ProgressDialogHelper.setRunning(cx, false);
                txtNewestVer.setText("点此检查");
                ToastUtils.showThrowable(t);
            }
        });
    }


}
