package com.robam.rokipad.ui.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.legent.Callback;
import com.legent.ui.UIService;
import com.legent.ui.ext.BasePage;
import com.legent.ui.ext.views.DelayEditText;
import com.legent.utils.api.ToastUtils;
import com.robam.common.pojos.Recipe;
import com.robam.common.services.CookbookManager;
import com.robam.common.io.cloud.Reponses.*;
import com.robam.rokipad.R;
import com.robam.rokipad.ui.adapter.RecipeAdapter;
import com.robam.rokipad.ui.view.RecipeGridView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RecipeSearchPage extends BasePage {

    @InjectView(R.id.edtSearch)
    DelayEditText edtSearch;
    @InjectView(R.id.recipeView)
    RecipeGridView recipeView;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_recipe_search, container, false);
        ButterKnife.inject(this, view);
        edtSearch.setOnTextChangedCallback(new DelayEditText.OnTextChangedCallback() {

            @Override
            public void onTextChanged(String value) {
                onSearch(value);
            }

            @Override
            public void onTextChangedWithoutDelay(String s) {

            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    @OnClick(R.id.tvCancel)
    public void onClickCancel() {
        UIService.getInstance().popBack();
    }

    void onSearch(String word) {

        CookbookManager.getInstance().getCookbooksByName(word,
                new Callback<CookbooksResponse>() {

                    @Override
                    public void onSuccess(CookbooksResponse result) {
                        loadBooks(result.cookbooks, RecipeAdapter.Model_Normal);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        ToastUtils.showThrowable(t);
                    }
                });
    }

    protected void loadBooks(List<Recipe> books, int modelType) {
        recipeView.loadData(books, modelType);
    }

}
