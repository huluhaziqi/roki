package com.legent.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public interface ILayoutLoader {

    void layout(FragmentActivity main);

    boolean toggleMenu();

    IPage switchContent(String pageKey, Bundle args);

    void onPageInActivated(String pageKey);

    void onPageActivated(String pageKey);
}
