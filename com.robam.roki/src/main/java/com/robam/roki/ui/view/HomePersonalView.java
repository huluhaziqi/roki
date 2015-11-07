package com.robam.roki.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.legent.Callback;
import com.legent.plat.Plat;
import com.legent.plat.events.UserUpdatedEvent;
import com.legent.plat.pojos.User;
import com.legent.ui.UIService;
import com.legent.ui.ext.adapters.ExtBaseAdapter;
import com.legent.utils.EventUtils;
import com.legent.utils.graphic.ImageUtils;
import com.robam.common.events.CookMomentsRefreshEvent;
import com.robam.common.events.FavorityBookCleanEvent;
import com.robam.common.events.FavorityBookRefreshEvent;
import com.robam.common.io.cloud.Reponses.CookbooksResponse;
import com.robam.common.io.cloud.RokiRestHelper;
import com.robam.common.pojos.CookAlbum;
import com.robam.common.pojos.MaintainInfo;
import com.robam.common.services.CookbookManager;
import com.robam.common.ui.UiHelper;
import com.robam.roki.R;
import com.robam.roki.ui.Helper;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.PageKey;
import com.robam.roki.ui.UIListeners;
import com.robam.roki.ui.dialog.MaintainHomeDialog;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class HomePersonalView extends FrameLayout implements UIListeners.IRefresh {

    final int ID_Favority = 0;
    final int ID_CookMoments = 1;
    final int ID_SmartParams = 2;
    final int ID_DeviceManager = 3;
    final int ID_SaleService = 4;
    final int ID_Maintain = 5;
    final int ID_About = 6;

    @InjectView(R.id.imgFigure)
    ImageView imgUser;
    @InjectView(R.id.txtFigure)
    TextView txtUser;
    @InjectView(R.id.listview)
    ListView listview;

    Adapter adapter;

    public HomePersonalView(Context context) {
        super(context);
        init(context, null);
    }

    public HomePersonalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HomePersonalView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventUtils.regist(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventUtils.unregist(this);
    }

    @Subscribe
    public void onEvent(UserUpdatedEvent event) {
        onRefresh();
    }

    @Subscribe
    public void onEvent(CookMomentsRefreshEvent event) {
        onRefresh();
    }

    @Subscribe
    public void onEvent(FavorityBookRefreshEvent event) {
        onRefresh();
    }

    @Subscribe
    public void onEvent(FavorityBookCleanEvent event) {
        onRefresh();
    }


    void init(Context cx, AttributeSet attrs) {

        View view = LayoutInflater.from(cx).inflate(R.layout.view_home_personal,
                this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
            adapter = new Adapter();
            listview.setAdapter(adapter);
            adapter.loadData(buildItems());
            onRefresh();
        }
    }

    @OnItemClick(R.id.listview)
    public void onItemClicked(AdapterView<?> parent, View view, int position, long id) {
        ListItem item = (ListItem) adapter.getItem(position);
        UIService ui = UIService.getInstance();
        Context cx = getContext();
        switch (item.id) {
            case ID_About:
                ui.postPage(PageKey.About);
                break;
            case ID_DeviceManager:
                if (UiHelper.checkAuthWithDialog(cx, PageKey.UserLogin))
                    ui.postPage(PageKey.DeviceManger);
                break;
            case ID_Favority:
                if (UiHelper.checkAuthWithDialog(cx, PageKey.UserLogin))
                    ui.postPage(PageKey.RecipeFavority);
                break;
            case ID_CookMoments:
                if (UiHelper.checkAuthWithDialog(cx, PageKey.UserLogin))
                    ui.postPage(PageKey.RecipeCookMoments);
                break;
            case ID_SaleService:
                ui.postPage(PageKey.SaleService);
                break;
            case ID_SmartParams:
                if (UiHelper.checkAuthWithDialog(cx, PageKey.UserLogin))
                    ui.postPage(PageKey.SmartParams);
                break;
            case ID_Maintain:
                onMaintain();
                break;
        }
    }

    @OnClick(R.id.imgFigure)
    public void onClickFigure() {
        boolean isLogin = Plat.accountService.isLogon();
        if (isLogin) {
            UIService.getInstance().postPage(PageKey.UserInfo);
        } else {
            UIService.getInstance().postPage(PageKey.UserLogin);
        }
    }

    @Override
    public void onRefresh() {

        boolean isLogin = Plat.accountService.isLogon();
        if (isLogin) {
            User user = Plat.accountService.getCurrentUser();

            txtUser.setText(Strings.isNullOrEmpty(user.name) ? user.phone : user.name);
            if (!Strings.isNullOrEmpty(user.figureUrl)) {
                ImageUtils.displayImage(user.figureUrl, imgUser, Helper.DisplayImageOptions_UserFace);
            }
        } else {
            txtUser.setText("立即登录");
            imgUser.setImageResource(R.mipmap.ic_user_default_figure);
        }

        adapter.loadData(buildItems());

    }

    void onMaintain() {
        if (UiHelper.checkAuthWithDialog(getContext(), PageKey.UserLogin)) {
            RokiRestHelper.queryMaintain(new Callback<MaintainInfo>() {
                @Override
                public void onSuccess(MaintainInfo mi) {
                    if (mi == null || mi.postTime == 0) {
                        MaintainHomeDialog.show(getContext());
                    } else {
                        Bundle bd = new Bundle();
                        bd.putParcelable(PageArgumentKey.MaintainInfo, mi);
                        UIService.getInstance().postPage(PageKey.MaintainInfo, bd);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    MaintainHomeDialog.show(getContext());
                }
            });
        }

    }

    List<ListItem> buildItems() {
        List<ListItem> list = Lists.newArrayList();
        list.add(new ListItem(ID_Favority, "我的收藏", R.mipmap.ic_my_item_favority));
        list.add(new ListItem(ID_CookMoments, "晒过的厨艺", R.mipmap.ic_my_item_cook_moments));
        list.add(new ListItem(ID_SmartParams, "智能设定", R.mipmap.ic_my_item_smart_params));
        list.add(new ListItem(ID_DeviceManager, "厨电管理", R.mipmap.ic_my_item_device));
        list.add(new ListItem(ID_SaleService, "售后服务", R.mipmap.ic_my_item_sale_service));
        list.add(new ListItem(ID_Maintain, "保养服务", R.mipmap.ic_my_item_maintain));
        list.add(new ListItem(ID_About, "关于ROKI", R.mipmap.ic_my_item_about));
        return list;
    }

    class ListItem {

        int id;
        int imgResid;
        String title;

        public ListItem(int id, String title, int imgResid) {
            this.id = id;
            this.title = title;
            this.imgResid = imgResid;
        }
    }

    class Adapter extends ExtBaseAdapter<ListItem> {


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_personal_item, parent, false);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            ListItem item = (ListItem) getItem(position);
            vh.showData(item);

            return convertView;
        }

        class ViewHolder {
            @InjectView(R.id.imgIcon)
            ImageView imgIcon;
            @InjectView(R.id.txtTitle)
            TextView txtTitle;
            @InjectView(R.id.txtTip)
            TextView txtTip;

            View view;

            ViewHolder(View view) {
                this.view = view;
                ButterKnife.inject(this, view);
            }

            void showData(final ListItem item) {
                imgIcon.setImageResource(item.imgResid);
                txtTitle.setText(item.title);

                showTip(0);
                if (Plat.accountService.isLogon()) {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateTip(item.id);
                        }
                    }, 50);
                } else {
                    showTip(0);
                }
            }

            void updateTip(int id) {
                switch (id) {
                    case ID_Favority:
                        CookbookManager.getInstance().getFavorityCookbooks(new Callback<CookbooksResponse>() {
                            @Override
                            public void onSuccess(CookbooksResponse res) {

                                int tip = 0;
                                if (res != null) {
                                    if (res.cookbooks != null) {
                                        tip += res.cookbooks.size();
                                    }
                                    if (res.cookbooks3rd != null) {
                                        tip += res.cookbooks3rd.size();
                                    }
                                }
                                showTip(tip);
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                showTip(0);
                            }
                        });
                        break;
                    case ID_CookMoments:
                        CookbookManager.getInstance().getMyCookAlbums(new Callback<List<CookAlbum>>() {
                            @Override
                            public void onSuccess(List<CookAlbum> res) {
                                int tip = 0;
                                if (res != null) {
                                    tip += res.size();
                                }
                                showTip(tip);
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                showTip(0);
                            }
                        });
                        break;
                }
            }

            void showTip(int tip) {
                txtTip.setText(String.format("(%s)", tip));
                txtTip.setVisibility(tip > 0 ? VISIBLE : GONE);
            }
        }

    }
}
