package com.robam.roki.ui.page;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Objects;
import com.google.common.eventbus.Subscribe;
import com.legent.Callback2;
import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.events.DeviceConnectionChangedEvent;
import com.legent.plat.events.RecipeShowEvent;
import com.legent.ui.UIService;
import com.legent.ui.ext.HeadPage;
import com.legent.ui.ext.popoups.PopoupHelper;
import com.legent.utils.api.ToastUtils;
import com.robam.common.Utils;
import com.robam.common.events.OvenStatusChangedEvent;
import com.robam.common.pojos.device.Oven.AbsOven;
import com.robam.common.pojos.device.Oven.OvenStatus;
import com.robam.common.ui.UiHelper;
import com.robam.roki.R;
import com.robam.roki.model.NormalModeItemMsg;
import com.robam.roki.ui.Helper;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.PageKey;
import com.robam.roki.ui.dialog.OvenSelfCleaningDialog;


import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by linxiaobin on 2015/12/21.
 */
public class DeviceOvenPage extends HeadPage {

    AbsOven oven;
    //
    @InjectView(R.id.chickenWingItem)//9个常用
    LinearLayout chickenWingItem;
    @InjectView(R.id.cakeItem)
    LinearLayout cakeItem;
    @InjectView(R.id.breadItem)
    LinearLayout breadItem;
    @InjectView(R.id.streakyPorkItem)
    LinearLayout streakyPorkItem;
    @InjectView(R.id.steakItem)
    LinearLayout steakItem;
    @InjectView(R.id.pisaItem)
    LinearLayout pisaItem;
    @InjectView(R.id.cookieItem)
    LinearLayout cookieItem;
    @InjectView(R.id.vegetableItem)
    LinearLayout vegetableItem;
    @InjectView(R.id.seafoodItem)
    LinearLayout seafoodItem;
    @InjectView(R.id.llProMode)
    LinearLayout llProMode;

    @InjectView(R.id.llOvenPage)
    LinearLayout llOvenPage;

   static final int PollStatus = 10;

    @InjectView(R.id.imgChickenWing)//
            ImageView imgChickenWing;
    @InjectView(R.id.imgCake)
    ImageView imgCake;
    @InjectView(R.id.imgBread)
    ImageView imgBread;
    @InjectView(R.id.imgStreakPork)
    ImageView imgStreakPork;
    @InjectView(R.id.imgPisa)
    ImageView imgPisa;
    @InjectView(R.id.imgCookie)
    ImageView imgCookie;
    @InjectView(R.id.imgVegetable)
    ImageView imgVegetable;
    @InjectView(R.id.imgProMode)
    ImageView imgNormalMode;
    @InjectView(R.id.imgSteak)
    ImageView imgSteak;
    @InjectView(R.id.imgSeafood)
    ImageView imgSeafood;


    @InjectView(R.id.titleChickenWing)//9个常用
            TextView titleChickenWing;
    @InjectView(R.id.titleCake)//9个常用
            TextView titleCake;
    @InjectView(R.id.titleBread)//9个常用
            TextView titleBread;
    @InjectView(R.id.titleStreakPork)//9个常用
            TextView titleStreakPork;
    @InjectView(R.id.titleSteak)//9个常用
            TextView titleSteak;
    @InjectView(R.id.titlePisa)//9个常用
            TextView titlePisa;
    @InjectView(R.id.titleSeafood)//9个常用
            TextView titleSeafood;
    @InjectView(R.id.titleCookie)//9个常用
            TextView titleCookie;
    @InjectView(R.id.titleVegetable)//9个常用
            TextView titleVegetable;
    @InjectView(R.id.txtProMode)//9个常用
            TextView txtNormalMode;

    @InjectView(R.id.selfCleaningButton)
    RelativeLayout selfCleaningButton;
    @InjectView(R.id.txtSelfCleaning)
    TextView txtSelfCleaning;

    @InjectView(R.id.disconnectHintView)
    LinearLayout disconnectHintView;

    @InjectView(R.id.relSwitch)
    RelativeLayout relSwitch;
    @InjectView(R.id.imgSwitchLine)
    ImageView imgSwitchLine;
    @InjectView(R.id.txtSwitch)
    TextView txtSwitch;
    @InjectView(R.id.imgSwitch)
    ImageView imgSwitch;
    @InjectView(R.id.llNoticeStart)
    LinearLayout llNoticeStart;
    @InjectView(R.id.txtRecipe)
            TextView txtRecipe;

    short status = 1;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PollStatus:
                    if(oven.status == OvenStatus.Working){
                        Bundle bundle = new Bundle();
                        bundle.putString(PageArgumentKey.Guid, oven.getID());
                        UIService.getInstance().postPage(PageKey.DeviceOvenWorking, bundle);}
                    break;
                default:break;
            }
        }
    };

    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bd = getArguments();
        String guid = bd == null ? null : bd.getString(PageArgumentKey.Guid);
        oven = Plat.deviceService.lookupChild(guid);
        contentView = inflater.inflate(R.layout.page_device_oven_normal,
                container, false);
        ButterKnife.inject(this, contentView);
        initView();
        setPageTitle("");
        return contentView;
    }

    // -----------------------------------0----- 开关按钮 ------------------------------------------
    @OnClick(R.id.relSwitch)
    public void onClickSwitch() {
        llNoticeStart.setVisibility(View.GONE);
        short status = (oven.status == OvenStatus.Off) ? OvenStatus.On : OvenStatus.Off;
        setStatus(status);
    }

    private void setStatus(short status) {
        if (!checkConnection()) {
            return;
        }

        oven.setOvenStatus(status, new VoidCallback() {
            @Override
            public void onSuccess() {
                onRefresh();
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });
    }

    void initView() {
        disconnectHintView.setVisibility(View.INVISIBLE);
        onRefresh();
    }

    private void onRefresh() {
        disconnectHintView.setVisibility(oven != null && !oven.isConnected()
                ? View.VISIBLE
                : View.INVISIBLE);

        if (oven == null)
            return;

        boolean isOn = oven.isConnected() && oven.status != OvenStatus.Off;
        setSwitch(isOn);
    }
    @OnClick(R.id.txtRecipe)
    public void onClickRecipe() {
        postEvent(new RecipeShowEvent());
        UIService.getInstance().returnHome();
    }
    private void setSwitch(boolean isOn) {
        imgChickenWing.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.ic_device_oven_chicken_wing_working : R.mipmap.ic_device_oven_chicken_wing_unworking));
        imgCake.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.ic_device_oven_cake_working : R.mipmap.ic_device_oven_cake_unworking));
        imgBread.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.ic_device_oven_bread_working : R.mipmap.ic_device_oven_bread_unworking));
        imgStreakPork.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.ic_device_oven_streaky_pork_working : R.mipmap.ic_device_oven_streaky_pork_unworking));
        imgSteak.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.ic_device_oven_steak_working : R.mipmap.ic_device_oven_steak_unworking));
        imgSeafood.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.ic_device_oven_seafood_working : R.mipmap.ic_device_oven_seafood_unworking));
        imgCookie.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.ic_device_oven_cookie_working : R.mipmap.ic_device_oven_cookie_unworking));
        imgVegetable.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.ic_device_oven_vegetable_working : R.mipmap.ic_device_oven_vegetable_unworking));
        imgNormalMode.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.ic_device_oven_normal_working : R.mipmap.ic_device_oven_normal_unworking));
        imgPisa.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.ic_device_oven_pisa_working : R.mipmap.ic_device_oven_pisa_unworking));

        titleChickenWing.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));
        titleCake.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));
        titleBread.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));
        titleStreakPork.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));
        titleSteak.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));
        titlePisa.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));
        titleSeafood.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));
        titleCookie.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));
        titleVegetable.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));
        txtNormalMode.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));

        txtSelfCleaning.setBackground(getResources().getDrawable(isOn ? R.mipmap.ic_device_oven_white_circle : R.mipmap.ic_device_oven_gray_circle));
        txtSelfCleaning.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));

        imgSwitchLine.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.ic_device_oven_leanline_yellow : R.mipmap.ic_device_oven_leanline_white));
        imgSwitch.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.ic_device_oven_started : R.mipmap.ic_device_oven_start_white));
        txtSwitch.setText(isOn ? "已开启" : "已关闭");


    }

    //------------------------------------------- 专业模式按钮 --------------------------------------
    @OnClick(R.id.llProMode)
    public void onClickContext() {
        if (oven.status == OvenStatus.Off) {
            llNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            if (UiHelper.checkAuthWithDialog(getContext(), PageKey.UserLogin)) {
                Bundle bundle = new Bundle();
                bundle.putString(PageArgumentKey.Guid, oven.getID());
                UIService.getInstance().postPage(PageKey.DeviceOvenProfessionalSetting, bundle);
//                UIService.getInstance().postPage(PageKey.DeviceOvenProfessionalSetting, null);
            }
        }
    }

    //------------------------------------------- 自洁模式按钮 --------------------------------------
//    @OnClick(R.id.selfCleaningButton)
//    public void onClickClean() {
//        if (oven.status == OvenStatus.Off) {
//            llNoticeStart.setVisibility(View.VISIBLE);
//        } else if (oven.status != OvenStatus.Off) {
//            NormalModeItemMsg msg = new NormalModeItemMsg();
//            msg.setType("自洁");
//            msg.setTime(String.valueOf(90));
//            msg.setTemperature(String.valueOf(230));
//            Bundle bundle = new Bundle();
//            bundle.putString(PageArgumentKey.Guid, oven.getID());
//            OvenSelfCleaningDialog.show(getContext(), msg, bundle);
//        }
//    }

    @OnClick({R.id.chickenWingItem})
    public void onClickChicken() {
        if (oven.status == OvenStatus.Off) {
            llNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            NormalModeItemMsg msg = new NormalModeItemMsg();
            msg.setType("鸡翅");
            msg.setTime(String.valueOf(16));
            msg.setTemperature(String.valueOf(180));
            msg.setDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_chicken_wing_unworking));
//        = steam.mode;
            Bundle bundle = new Bundle();
            bundle.putString(PageArgumentKey.Guid, oven.getID());
            PopupWindow pop = Helper.newOvenTwoSettingPicker(cx, new Callback2<NormalModeItemMsg>() {
                @Override
                public void onCompleted(NormalModeItemMsg message) {
                    oven.setOvenStrongBarbecue(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
//                            Bundle bundle = new Bundle();
//                            bundle.putString(PageArgumentKey.Guid, oven.getID());
//                            UIService.getInstance().postPage(PageKey.DeviceOvenWorking, bundle);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });

                    ToastUtils.show(message.toString(), Toast.LENGTH_SHORT);
                }
            }, msg, oven.getID());//待修改

            PopoupHelper.show(contentView, pop, Gravity.BOTTOM);
        }
    }

    @OnClick({R.id.cakeItem})
    public void onClickCake() {
        if (oven.status == OvenStatus.Off) {
            llNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            NormalModeItemMsg msg = new NormalModeItemMsg();
            msg.setType("蛋糕");
            msg.setTime(String.valueOf(25));
            msg.setTemperature(String.valueOf(160));
            msg.setDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_cake_unworking));
//        = steam.mode;
            Bundle bundle = new Bundle();
            bundle.putString(PageArgumentKey.Guid, oven.getID());
            PopupWindow pop = Helper.newOvenTwoSettingPicker(cx, new Callback2<NormalModeItemMsg>() {
                @Override
                public void onCompleted(NormalModeItemMsg message) {
                    oven.setOvenQuickHeating(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
//                            Bundle bundle = new Bundle();
//                            bundle.putString(PageArgumentKey.Guid, oven.getID());
//                            UIService.getInstance().postPage(PageKey.DeviceOvenWorking, bundle);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });

                    ToastUtils.show(message.toString(), Toast.LENGTH_SHORT);
                }
            }, msg, oven.getID());//待修改

            PopoupHelper.show(contentView, pop, Gravity.BOTTOM);
        }
    }

    @OnClick({R.id.breadItem})
    public void onClickBread() {
        if (oven.status == OvenStatus.Off) {
            llNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            NormalModeItemMsg msg = new NormalModeItemMsg();
            msg.setType("面包");
            msg.setTime(String.valueOf(18));
            msg.setTemperature(String.valueOf(165));
            msg.setDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_bread_unworking));
            PopupWindow pop = Helper.newOvenTwoSettingPicker(cx, new Callback2<NormalModeItemMsg>() {
                @Override
                public void onCompleted(NormalModeItemMsg message) {
                    oven.setOvenAirBarbecue(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
//                            Bundle bundle = new Bundle();
//                            bundle.putString(PageArgumentKey.Guid, oven.getID());
//                            UIService.getInstance().postPage(PageKey.DeviceOvenWorking, bundle);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });

                    ToastUtils.show(message.toString(), Toast.LENGTH_SHORT);
                }
            }, msg, oven.getID());//待修改

            PopoupHelper.show(contentView, pop, Gravity.BOTTOM);
        }
    }

    @OnClick({R.id.streakyPorkItem})
    public void onClickStreakPork() {
        if (oven.status == OvenStatus.Off) {
            llNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            NormalModeItemMsg msg = new NormalModeItemMsg();
            msg.setType("五花肉");
            msg.setTime(String.valueOf(45));
            msg.setTemperature(String.valueOf(215));
            msg.setDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_streaky_pork_unworking));
//        = steam.mode;
            Bundle bundle = new Bundle();
            bundle.putString(PageArgumentKey.Guid, oven.getID());
            PopupWindow pop = Helper.newOvenTwoSettingPicker(cx, new Callback2<NormalModeItemMsg>() {
                @Override
                public void onCompleted(NormalModeItemMsg message) {
                    oven.setOvenAirBarbecue(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
//                            Bundle bundle = new Bundle();
//                            bundle.putString(PageArgumentKey.Guid, oven.getID());
//                            UIService.getInstance().postPage(PageKey.DeviceOvenWorking, bundle);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });

                    ToastUtils.show(message.toString(), Toast.LENGTH_SHORT);
                }
            }, msg, oven.getID());//待修改

            PopoupHelper.show(contentView, pop, Gravity.BOTTOM);
        }
    }

    @OnClick({R.id.steakItem})
    public void onClickSteak() {
        if (oven.status == OvenStatus.Off) {
            llNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            NormalModeItemMsg msg = new NormalModeItemMsg();
            msg.setType("牛排");
            msg.setTime(String.valueOf(15));
            msg.setTemperature(String.valueOf(180));
            msg.setDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_steak_unworking));
//        = steam.mode;
            Bundle bundle = new Bundle();
            bundle.putString(PageArgumentKey.Guid, oven.getID());
            PopupWindow pop = Helper.newOvenTwoSettingPicker(cx, new Callback2<NormalModeItemMsg>() {
                @Override
                public void onCompleted(NormalModeItemMsg message) {
                    oven.setOvenToast(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
//                            Bundle bundle = new Bundle();
//                            bundle.putString(PageArgumentKey.Guid, oven.getID());
//                            UIService.getInstance().postPage(PageKey.DeviceOvenWorking, bundle);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });

                    ToastUtils.show(message.toString(), Toast.LENGTH_SHORT);
                }
            }, msg, oven.getID());//待修改
            PopoupHelper.show(contentView, pop, Gravity.BOTTOM);
        }
    }

    @OnClick({R.id.pisaItem})
    public void onClickPisa() {
        if (oven.status == OvenStatus.Off) {
            llNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            NormalModeItemMsg msg = new NormalModeItemMsg();
            msg.setType("披萨");
            msg.setTime(String.valueOf(20));
            msg.setTemperature(String.valueOf(200));
            msg.setDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_pisa_unworking));
//        = steam.mode;
            Bundle bundle = new Bundle();
            bundle.putString(PageArgumentKey.Guid, oven.getID());
            PopupWindow pop = Helper.newOvenTwoSettingPicker(cx, new Callback2<NormalModeItemMsg>() {
                @Override
                public void onCompleted(NormalModeItemMsg message) {
                    oven.setOvenToast(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
//                            Bundle bundle = new Bundle();
//                            bundle.putString(PageArgumentKey.Guid, oven.getID());
//                            UIService.getInstance().postPage(PageKey.DeviceOvenWorking, bundle);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });

                    ToastUtils.show(message.toString(), Toast.LENGTH_SHORT);
                }
            }, msg, oven.getID());//待修改

            PopoupHelper.show(contentView, pop, Gravity.BOTTOM);
        }
    }

    @OnClick({R.id.seafoodItem})
    public void onClickSeaFood() {
        if (oven.status == OvenStatus.Off) {
            llNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            NormalModeItemMsg msg = new NormalModeItemMsg();
            msg.setType("海鲜");
            msg.setTime(String.valueOf(23));
            msg.setTemperature(String.valueOf(200));
            msg.setDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_seafood_unworking));
//        = steam.mode;
            Bundle bundle = new Bundle();
            bundle.putString(PageArgumentKey.Guid, oven.getID());
            PopupWindow pop = Helper.newOvenTwoSettingPicker(cx, new Callback2<NormalModeItemMsg>() {
                @Override
                public void onCompleted(NormalModeItemMsg message) {
                    oven.setOvenStrongBarbecue(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
//                            Bundle bundle = new Bundle();
//                            bundle.putString(PageArgumentKey.Guid, oven.getID());
//                            UIService.getInstance().postPage(PageKey.DeviceOvenWorking, bundle);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });

                    ToastUtils.show(message.toString(), Toast.LENGTH_SHORT);
                }
            }, msg, oven.getID());//待修改

            PopoupHelper.show(contentView, pop, Gravity.BOTTOM);
        }
    }

    @OnClick({R.id.cookieItem})
    public void onClickCookie() {
        if (oven.status == OvenStatus.Off) {
            llNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            NormalModeItemMsg msg = new NormalModeItemMsg();
            msg.setType("饼干");
            msg.setTime(String.valueOf(16));
            msg.setTemperature(String.valueOf(170));
            msg.setDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_cookie_unworking));
//        = steam.mode;
            Bundle bundle = new Bundle();
            bundle.putString(PageArgumentKey.Guid, oven.getID());
            PopupWindow pop = Helper.newOvenTwoSettingPicker(cx, new Callback2<NormalModeItemMsg>() {
                @Override
                public void onCompleted(NormalModeItemMsg message) {
                    oven.setOvenAirBarbecue(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
//                            Bundle bundle = new Bundle();
//                            bundle.putString(PageArgumentKey.Guid, oven.getID());
//                            UIService.getInstance().postPage(PageKey.DeviceOvenWorking, bundle);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });

                    ToastUtils.show(message.toString(), Toast.LENGTH_SHORT);
                }
            }, msg, oven.getID());//待修改

            PopoupHelper.show(contentView, pop, Gravity.BOTTOM);
        }
    }

    @OnClick({R.id.vegetableItem})
    public void onClickVegetable() {
        if (oven.status == OvenStatus.Off) {
            llNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            NormalModeItemMsg msg = new NormalModeItemMsg();
            msg.setType("蔬菜");
            msg.setTime(String.valueOf(15));
            msg.setTemperature(String.valueOf(200));
            msg.setDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_vegetable_unworking));
//        = steam.mode;
            Bundle bundle = new Bundle();
            bundle.putString(PageArgumentKey.Guid, oven.getID());
            PopupWindow pop = Helper.newOvenTwoSettingPicker(cx, new Callback2<NormalModeItemMsg>() {
                @Override
                public void onCompleted(NormalModeItemMsg message) {
                    oven.setOvenQuickHeating(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
//                            Bundle bundle = new Bundle();
//                            bundle.putString(PageArgumentKey.Guid, oven.getID());
//                            UIService.getInstance().postPage(PageKey.DeviceOvenWorking, bundle);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });

                    ToastUtils.show(message.toString(), Toast.LENGTH_SHORT);
                }
            }, msg, oven.getID());//待修改

            PopoupHelper.show(contentView, pop, Gravity.BOTTOM);
        }
    }
//    private void refreshUI() {
//        txtTemSet.setText(oven.temp);
//        txtTimeSet.setText(oven.time);
//        remainTime = steam.time * 60;
//        startTimer();
//    }

    private boolean checkConnection() {
        if (!oven.isConnected()) {
            ToastUtils.showShort(R.string.oven_invalid_error);
            return false;
        } else {
            return true;
        }
    }

    @Subscribe
    public void onEvent(DeviceConnectionChangedEvent event) {
        if (oven == null || !Objects.equal(oven.getID(), event.device.getID()))
            return;

        disconnectHintView.setVisibility(event.isConnected ? View.INVISIBLE : View.VISIBLE);
    }

    @Subscribe
    public void onEvent(OvenStatusChangedEvent event) {
        if (oven == null || !Objects.equal(oven.getID(), event.pojo.getID()))
            return;
        onRefresh();
        handler.sendEmptyMessage(PollStatus);
    }

}
