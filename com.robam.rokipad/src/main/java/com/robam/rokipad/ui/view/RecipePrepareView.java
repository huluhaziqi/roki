package com.robam.rokipad.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.legent.utils.api.ToastUtils;
import com.robam.common.Utils;
import com.robam.common.pojos.Material;
import com.robam.common.pojos.Materials;
import com.robam.common.pojos.Recipe;
import com.robam.common.pojos.device.Stove.Stove;
import com.robam.common.pojos.device.Stove.Stove.StoveHead;
import com.robam.common.pojos.device.fan.AbsFan;
import com.robam.rokipad.R;
import com.robam.rokipad.service.CookTaskService;
import com.robam.rokipad.ui.dialog.ChooseStoveDialog;
import com.robam.rokipad.ui.dialog.ChooseStoveDialog.StoveSelectedCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RecipePrepareView extends FrameLayout {

    @InjectView(R.id.btnCook)
    Button btnCook;

    @InjectView(R.id.tv_main_content)
    TextView txtMain;

    @InjectView(R.id.tv_accessory_content)
    TextView txtAccessory;

    Recipe book;

    public RecipePrepareView(Context context, Recipe book) {
        super(context);
        this.book = book;
        init(context, null);
    }

    void init(Context cx, AttributeSet attrs) {

        View view = LayoutInflater.from(cx).inflate(
                R.layout.view_recipe_prepare, this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
            loadData();
        }
    }

    @OnClick(R.id.btnCook)
    public void onCook() {

        ChooseStoveDialog.show(getContext(), new StoveSelectedCallback() {

            @Override
            public void onStoveSelected(int stoveHeadId) {
                try {
                    startCook(stoveHeadId);
                } catch (Exception e) {
                    ToastUtils.showException(e);
                }
            }
        });
    }

    void loadData() {
        if (book.materials == null)
            return;

        String mainStr = getMaterialsString(book.materials, true);
        String accessoryStr = getMaterialsString(book.materials, false);

        txtMain.setText(mainStr);
        txtAccessory.setText(accessoryStr);
    }

    private void startCook(int headId) {
        if (CookTaskService.getInstance().isRunning()) {
            ToastUtils.showShort("正在烧菜中,不可重复烧菜!");
            return;
        }

        AbsFan device = Utils.getDefaultFan();
        Stove stove = Utils.getDefaultStove();
        StoveHead head = null;

        if (stove != null) {
            head = stove.getHeadById(headId);
            Context cx = getContext();
            Preconditions.checkState(stove.getValid(),
                    cx.getString(R.string.dev_invalid_error));
            Preconditions.checkState(stove.isConnected(),
                    cx.getString(R.string.stove_invalid_error));
        }

        CookTaskService.getInstance().start(head, book);
    }


    // 换成每项换行的形式
    static String getMaterialsString(Materials materials, boolean isMain) {
        List<Material> list = Material.getMaterialList(materials.getID(), isMain);
        StringBuilder sb = new StringBuilder();
        if (list == null || list.size() == 0) {
            return sb.toString();
        }

        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).toString()).append("\n");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();

    }
}
