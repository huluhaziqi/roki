package com.robam.rokipad.ui.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.legent.Callback;
import com.legent.ui.UIService;
import com.legent.ui.ext.HeadPage;
import com.legent.ui.ext.dialogs.ProgressDialogHelper;
import com.legent.ui.ext.views.TitleBar;
import com.legent.utils.api.DisplayUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.common.pojos.Group;
import com.robam.common.pojos.Recipe;
import com.robam.common.pojos.Tag;
import com.robam.common.services.CookbookManager;
import com.robam.common.io.cloud.Reponses.*;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.PageKey;
import com.robam.rokipad.ui.adapter.RecipeAdapter;
import com.robam.rokipad.ui.view.RecipeGridView;
import com.robam.rokipad.ui.view.RecipeMiddleTitleView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class RecipeCategoryPage extends HeadPage {

    @InjectView(R.id.tagList)
    ListView tagList;

    @InjectView(R.id.recipeView)
    RecipeGridView recipeView;

    @InjectView(R.id.imgTop)
    protected ImageView imgTop;

    Adapter adapter;

    @Override
    public View onCreateContentView(LayoutInflater inflater,
                                    ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_recipe_category, container,
                false);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);

            adapter = new Adapter();
            tagList.setAdapter(adapter);

            registMiddleTitleView();
            registRightTitleView();

            int screenWidth = DisplayUtils.getScreenWidthPixels(cx);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tagList
                    .getLayoutParams();
            lp.width = (int) (screenWidth * 1F / 6F);

            tagList.setLayoutParams(lp);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.imgTop)
    public void onClickToTop() {

        recipeView.smoothScrollToPosition(0);
    }

    @OnItemClick(R.id.tagList)
    public void onTagItemClick(AdapterView<?> adapterView, View view,
                               int position, long id) {
        adapter.select(position);
        Tag tag = (Tag) adapter.getItem(position);
        refreshContent(tag);
    }

    protected void loadBooks(List<Recipe> books, int modelType) {
        recipeView.loadData(books, modelType);
    }

    private void registMiddleTitleView() {

        RecipeMiddleTitleView middleTitle = new RecipeMiddleTitleView(cx,
                new RecipeMiddleTitleView.TitleClickCallback() {

                    @Override
                    public void onCategorySelected(Group group) {
                        refreshContent(group);
                    }
                });

        titleBar.replaceMiddle(middleTitle);
    }

    private void registRightTitleView() {
        View icon = TitleBar.newTitleIconView(cx, R.mipmap.ic_search, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIService.getInstance().postPage(PageKey.RecipeSearch);
            }
        });

        titleBar.replaceRight(icon);
    }

    private void refreshContent(Group group) {
        adapter.loadData(group.getTags());

        if (adapter.getCount() > 0) {
            tagList.performItemClick(tagList.getChildAt(0), 0,
                    tagList.getItemIdAtPosition(0));
        }
    }

    private void refreshContent(Tag tag) {
        if (tag == null) {
            return;
        }

        ProgressDialogHelper.setRunning(cx, true);
        CookbookManager.getInstance().getCookbooksByTag(tag,
                new Callback<CookbooksResponse>() {

                    @Override
                    public void onSuccess(CookbooksResponse result) {
                        recipeView.loadData(result.cookbooks,
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

    // -----------------------------------------------------------------------------------------------------

    class Adapter extends BaseAdapter {
        List<Tag> list = Lists.newArrayList();
        int selectedIndex;

        public void loadData(List<Tag> tags) {
            list.clear();
            if (tags != null) {
                list.addAll(tags);
            }
            notifyDataSetChanged();
        }

        public void select(int index) {
            this.selectedIndex = index;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (convertView != null) {
                vh = (ViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(cx).inflate(
                        R.layout.view_recipe_category_tag_item, parent, false);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            }

            Tag tag = list.get(position);
            vh.show(tag, position == selectedIndex);
            return convertView;
        }

        class ViewHolder {

            @InjectView(R.id.tv_category)
            TextView txtTitle;

            @InjectView(R.id.img_right)
            ImageView imgFlag;

            public ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }

            public void show(Tag tag, boolean isSelected) {
                txtTitle.setText(tag.name);

                int colorResid = isSelected ? R.color.setting_text_pressed
                        : R.color.setting_text_normal;
                txtTitle.setTextColor(cx.getResources().getColor(colorResid));

                imgFlag.setVisibility(isSelected ? View.VISIBLE
                        : View.INVISIBLE);
            }

        }
    }

}
