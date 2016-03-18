package com.robam.rokipad.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.legent.utils.api.DisplayUtils;
import com.robam.rokipad.R;

/**
 * Created by WZTCM on 2016/1/16.
 */
public class RecipeTypePopupWindow extends PopupWindow {

    final static public int Stove_Recipe = 0;
    final static public int Oven_Recipe = 1;
    final static public int Steam_Recipe = 2;
    final static public int Wave_Recipe = 3;

    public interface PickListener {
        void onConfirm(int type);
    }

    PickListener listener;
    ListView list;

    public RecipeTypePopupWindow(Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.view_recipe_type_popup, null);
        list = (ListView) view.findViewById(R.id.list);
        list.setAdapter(new Adapter(context));
        list.setOnItemClickListener(itemClickListener);
        this.setContentView(view);
        this.setWidth(180);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
    }

    public void setListener(PickListener listener) {
        this.listener = listener;
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            dismiss();
            listener.onConfirm(i);
        }
    };

    class Adapter extends BaseAdapter {
        Context context;

        public Adapter(Context context) {
            this.context = context;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(context).inflate(R.layout.recipe_type_popup_item, null);
            ImageView img = (ImageView) view.findViewById(R.id.img);
            TextView txt = (TextView) view.findViewById(R.id.txt);
            switch (i) {
                case 0:
                    img.setImageResource(R.mipmap.img_recipe_popup_stove);
                    txt.setText("灶具菜谱");
                    break;
                case 1:
                    img.setImageResource(R.mipmap.img_recipe_popup_oven);
                    txt.setText("烤箱菜谱");
                    break;
                case 2:
                    img.setImageResource(R.mipmap.img_recipe_popup_steam);
                    txt.setText("蒸汽炉菜谱");
                    break;
                case 3:
                    img.setImageResource(R.mipmap.img_recipe_popup_wave);
                    txt.setText("微波炉菜谱");
                    break;
            }
            return view;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
