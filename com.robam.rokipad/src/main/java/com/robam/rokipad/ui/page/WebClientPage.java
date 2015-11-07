package com.robam.rokipad.ui.page;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.common.base.Strings;
import com.legent.ui.ext.HeadPage;
import com.legent.utils.api.ToastUtils;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.PageArgumentKey;

import java.util.Locale;

public class WebClientPage extends HeadPage {

	WebView webView;

	@Override
	public View onCreateContentView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		String url = getArguments().getString(PageArgumentKey.Url);

		View view = inflater.inflate(R.layout.frame_web_client, container,
				false);
		webView = (WebView) view.findViewById(R.id.webView);
		setWebView(webView);
		loadUrl(url);
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		webView.destroy();
		webView = null;
	}

	private void loadUrl(String url) {
		if (Strings.isNullOrEmpty(url))
			return;

		if (!url.toLowerCase(Locale.getDefault()).startsWith("http:")) {
			url = "http://" + url;
		}
		webView.loadUrl(url);
	}

	@SuppressLint("SetJavaScriptEnabled")
	void setWebView(WebView webview) {
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setGeolocationEnabled(true);
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);

		// 启用数据库
		webSettings.setDatabaseEnabled(true);
		// 使用localStorage则必须打开
		webSettings.setDomStorageEnabled(true);

		webView.setHorizontalScrollBarEnabled(false);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.setHorizontalScrollbarOverlay(true);

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				titleBar.setTitle(title);
			}
		});

		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				webView.loadUrl(url);
				return true;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				ToastUtils.showShort(description);
			}

		});
	}

}
