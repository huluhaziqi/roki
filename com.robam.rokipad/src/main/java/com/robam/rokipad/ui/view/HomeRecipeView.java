package com.robam.rokipad.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.legent.Callback;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.utils.api.ToastUtils;
import com.robam.common.io.cloud.Reponses;
import com.robam.common.pojos.Recipe;
import com.robam.common.pojos.Tag;
import com.robam.common.services.CookbookManager;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.adapter.RecipeAdapter;
import com.robam.rokipad.ui.popup.PopupHelper;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by WZTCM on 2016/1/16.
 */
public class HomeRecipeView extends FrameLayout {

    @InjectView(R.id.titleView)
    HomeTitleView titleView;
    @InjectView(R.id.gridView)
    RecipeGridView gridView;

    Context cx;
    List<Recipe> list;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ProgressDialogHelper.setRunning(cx, true);
                    CookbookManager.getInstance().getCookbooks(new com.legent.Callback<List<Recipe>>() {
                        @Override
                        public void onSuccess(List<Recipe> recipes) {
                            list.clear();
                            list = recipes;
                            gridView.loadData(recipes, RecipeAdapter.Model_Normal);
                            ProgressDialogHelper.setRunning(cx, false);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                            ProgressDialogHelper.setRunning(cx, false);
                        }
                    });
                    break;
                case 1:
                    list.clear();
                    gridView.loadData(list, RecipeAdapter.Model_Normal);
                    break;
                case 2:
                    list.clear();
                    gridView.loadData(list, RecipeAdapter.Model_Normal);
                    break;
                case 3:
                    list.clear();
                    gridView.loadData(list, RecipeAdapter.Model_Normal);
                    break;
            }
        }
    };

    public HomeRecipeView(Context context) {
        super(context);
        init(context, null);
    }

    public HomeRecipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HomeRecipeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        cx = context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_recipe_home, this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
        initViews();
    }

    private void initViews() {
        titleView.setPnlLeft(true);
        titleView.setRightIcon1(false);
        titleView.setRightIcon2(false);
        titleView.setRightIcon3(true);
        titleView.findViewById(R.id.img_left).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupWindow pop = PopupHelper.newRecipeTypePopupWindow(getContext(), new RecipeTypePopupWindow.PickListener() {
                    @Override
                    public void onConfirm(int type) {
                        switch (type) {
                            case RecipeTypePopupWindow.Stove_Recipe:
                                handler.sendEmptyMessage(0);
                                break;
                            case RecipeTypePopupWindow.Oven_Recipe:
                                handler.sendEmptyMessage(1);
                                break;
                            case RecipeTypePopupWindow.Steam_Recipe:
                                handler.sendEmptyMessage(2);
                                break;
                            case RecipeTypePopupWindow.Wave_Recipe:
                                handler.sendEmptyMessage(3);
                                break;
                        }
                    }
                });
                pop.showAsDropDown(view);
            }
        });
        ProgressDialogHelper.setRunning(cx, true);
        if (list == null) {
            CookbookManager.getInstance().getCookbooks(new com.legent.Callback<List<Recipe>>() {
                @Override
                public void onSuccess(List<Recipe> recipes) {
                    list = recipes;
                    gridView.loadData(recipes, RecipeAdapter.Model_Normal);
                    ProgressDialogHelper.setRunning(cx, false);
                }

                @Override
                public void onFailure(Throwable t) {
                    ToastUtils.showThrowable(t);
                    ProgressDialogHelper.setRunning(cx, false);
                }
            });
        }
    }

    private void refreshContent(Tag tag) {
        if (tag == null) {
            return;
        }

        ProgressDialogHelper.setRunning(cx, true);
        CookbookManager.getInstance().getCookbooksByTag(tag,
                new Callback<Reponses.CookbooksResponse>() {

                    @Override
                    public void onSuccess(Reponses.CookbooksResponse result) {
                        gridView.loadData(result.cookbooks,
                                RecipeAdapter.Model_Normal);
                        ProgressDialogHelper.setRunning(cx, false);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        ProgressDialogHelper.setRunning(cx, false);
                        ToastUtils.showShort(t.getMessage());
                    }
                });

    }

}
