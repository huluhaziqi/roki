package com.robam.roki.ui.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.common.base.Strings;
import com.legent.ui.ext.HeadPage;
import com.legent.ui.ext.views.ExtWebView;
import com.legent.utils.api.ToastUtils;
import com.robam.roki.R;
import com.robam.roki.ui.PageArgumentKey;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sylar on 15/6/4.
 */
public class WebClientPage extends HeadPage {
    @InjectView(R.id.webView)
    protected ExtWebView webView;

    @Override
    protected View onCreateContentView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {

        View view = layoutInflater.inflate(R.layout.page_webclient, viewGroup, false);
        ButterKnife.inject(this, view);
        initPage(getArguments());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        webView.destroy();
        ButterKnife.reset(this);
    }

    protected void initPage(Bundle bd) {
        String url = bd == null ? null : bd.getString(PageArgumentKey.Url);
        final String webTitle = bd == null ? null : bd.getString(PageArgumentKey.WebTitle);
        titleBar.setTitle(webTitle);

        webView.setCallback(new ExtWebView.Callback() {
            @Override
            public void onReceivedTitle(WebView webView, String title) {
                if (Strings.isNullOrEmpty(webTitle)) {
                    titleBar.setTitle(title);
                } else {
                    titleBar.setTitle(webTitle);
                }
            }

            @Override
            public void onReceivedError(WebView webView, int errorCode,
                                        String description, String failingUrl) {
                ToastUtils.showShort(description);
            }
        });

        webView.loadUrl(url);
    }

}
