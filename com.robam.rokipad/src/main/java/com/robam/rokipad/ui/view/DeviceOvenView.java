package com.robam.rokipad.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
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
import com.legent.plat.pojos.device.AbsDevice;
import com.legent.ui.UIService;
import com.legent.ui.ext.popoups.PopoupHelper;
import com.legent.utils.EventUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.common.Utils;
import com.robam.common.events.DeviceOvenSwitchViewEvent;
import com.robam.common.events.DeviceSteamSwitchViewEvent;
import com.robam.common.events.OvenStatusChangedEvent;
import com.robam.common.pojos.device.Oven.AbsOven;
import com.robam.common.pojos.device.Oven.OvenStatus;
import com.robam.common.ui.UiHelper;
import com.robam.rokipad.R;
import com.robam.rokipad.model.DeviceWorkMsg;
import com.robam.rokipad.model.NormalModeItemMsg;
import com.robam.rokipad.ui.Helper;
import com.robam.rokipad.ui.PageArgumentKey;
import com.robam.rokipad.ui.PageKey;
import com.robam.rokipad.ui.dialog.OvenSelfCleaningDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by linxiaobin on 16/1/2.
 */
public class DeviceOvenView extends FrameLayout {
    @InjectView(R.id.llChickenWing)
    LinearLayout llChickenWing;
    @InjectView(R.id.llCake)
    LinearLayout llCake;
    @InjectView(R.id.llBread)
    LinearLayout llBread;
    @InjectView(R.id.llStreakyPork)
    LinearLayout llStreakyPork;
    @InjectView(R.id.llSteak)
    LinearLayout llSteak;
    @InjectView(R.id.llPisa)
    LinearLayout llPisa;
    @InjectView(R.id.llSeafood)
    LinearLayout llSeafood;
    @InjectView(R.id.llCookie)
    LinearLayout llCookie;
    @InjectView(R.id.llVegetable)
    LinearLayout llVegetable;
    @InjectView(R.id.llProMode)
    LinearLayout llProMode;

    @InjectView(R.id.imgChickenWing)
    ImageView imgChickenWing;
    @InjectView(R.id.imgCake)
    ImageView imgCake;
    @InjectView(R.id.imgBread)
    ImageView imgBread;
    @InjectView(R.id.imgStreakyPork)
    ImageView imgStreakyPork;
    @InjectView(R.id.imgSteak)
    ImageView imgSteak;
    @InjectView(R.id.imgPisa)
    ImageView imgPisa;
    @InjectView(R.id.imgSeafood)
    ImageView imgSeafood;
    @InjectView(R.id.imgCookie)
    ImageView imgCookie;
    @InjectView(R.id.imgVegetable)
    ImageView imgVegetable;
    @InjectView(R.id.imgProMode)
    ImageView imgProMode;

    @InjectView(R.id.txtChickenWing)
    TextView txtChickenWing;
    @InjectView(R.id.txtCake)
    TextView txtCake;
    @InjectView(R.id.txtBread)
    TextView txtBread;
    @InjectView(R.id.txtStreakyPork)
    TextView txtStreakyPork;
    @InjectView(R.id.txtSteak)
    TextView txtSteak;
    @InjectView(R.id.txtPisa)
    TextView txtPisa;
    @InjectView(R.id.txtSeafood)
    TextView txtSeafood;
    @InjectView(R.id.txtCookie)
    TextView txtCookie;
    @InjectView(R.id.txtVegetable)
    TextView txtVegetable;
    @InjectView(R.id.txtProMode)
    TextView txtProMode;


    @InjectView(R.id.rlSelfClean)
    RelativeLayout rlSelfClean;
    @InjectView(R.id.txtSelfClean)
    TextView txtSelfClean;

    @InjectView(R.id.relSwitch)
    RelativeLayout relSwitch;
    @InjectView(R.id.imgSwitch)
    ImageView imgSwitch;
    @InjectView(R.id.txtSwitch)
    TextView txtSwitch;
    @InjectView(R.id.imgSwitchLine)
    ImageView imgSwitchLine;
    AbsOven oven;

    @InjectView(R.id.rlNoticeStart)
    RelativeLayout rlNoticeStart;

    short status = 1;
    protected View view;

    public DeviceOvenView(Context context) {
        super(context);
        init(context, null);
    }

    public DeviceOvenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DeviceOvenView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_device_oven, this, true);
        if (!view.isInEditMode()) {
            ButterKnife.inject(this, view);
        }
        EventUtils.regist(this);
        initData();
//        AbsDevice device = Utils.getDefaultOven();
//        if (device != null) {
//            String guid = device.getID();
//            oven = Plat.deviceService.lookupChild(guid);
//        }
    }

    private void initData() {
        oven = Utils.getDefaultOven();
//        if (oven != null) {
//            String guid = oven.getID();
//            oven = Plat.deviceService.lookupChild(guid);
//        }
    }

    // -----------------------------------0----- 开关按钮 ------------------------------------------
    @OnClick(R.id.relSwitch)
    public void onClickSwitch() {
        rlNoticeStart.setVisibility(View.GONE);
        short status = (oven.status == OvenStatus.Off) ? OvenStatus.On : OvenStatus.Off;
        setStatus(status);
//        status = (short) (status == 1 ? 0 : 1);
//        setSwitch(status == 0);
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

    private void onRefresh() {
        if (oven == null)
            return;
        boolean isOn = oven.isConnected() && oven.status != OvenStatus.Off;
        setSwitch(isOn);
    }

    public void setSwitch(boolean isOn) {
        imgChickenWing.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.img_oven_chicken_wing_working : R.mipmap.img_oven_chicken_wing_unworking));
        imgCake.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.img_oven_cake_working : R.mipmap.img_oven_cake_unworking));
        imgBread.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.img_oven_bread_working : R.mipmap.img_oven_bread_unworking));
        imgStreakyPork.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.img_oven_streaky_pork_working : R.mipmap.img_oven_streaky_pork_unworking));
        imgSteak.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.img_oven_steak_working : R.mipmap.img_oven_steak_unworking));
        imgSeafood.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.img_oven_seafood_working : R.mipmap.img_oven_seafood_unworking));
        imgCookie.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.img_oven_cookie_working : R.mipmap.img_oven_cookie_unworking));
        imgVegetable.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.img_oven_vegetable_working : R.mipmap.img_oven_vegetable_unworking));
        imgProMode.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.img_oven_pro_mode_working : R.mipmap.img_oven_pro_mode_unworking));
        imgPisa.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.img_oven_pisa_working : R.mipmap.img_oven_pisa_unworking));

        txtChickenWing.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));
        txtCake.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));
        txtBread.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));
        txtStreakyPork.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));
        txtSteak.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));
        txtPisa.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));
        txtSeafood.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));
        txtCookie.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));
        txtVegetable.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));
        txtProMode.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));

        rlSelfClean.setBackground(getResources().getDrawable(isOn ? R.mipmap.img_oven_circle_working : R.mipmap.img_oven_circle_unworking));
        txtSelfClean.setTextColor(getResources().getColor(isOn ? R.color.c14 : R.color.Gray_57));

        imgSwitchLine.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.ic_device_oven_leanline_yellow : R.mipmap.ic_device_oven_leanline_white));
        imgSwitch.setImageDrawable(getResources().getDrawable(isOn ? R.mipmap.ic_device_oven_start_button_yellow : R.mipmap.ic_device_oven_start_button_white));
        txtSwitch.setText(isOn ? "已开启" : "已关闭");
    }

    //------------------------------------------- 专业模式按钮 --------------------------------------
    @OnClick(R.id.llProMode)
    public void onClickContext() {
        if (oven.status == OvenStatus.Off) {
            rlNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            if (UiHelper.checkAuthWithDialog(getContext(), PageKey.UserLogin)) {
                PopupWindow pop = Helper.newOvenThreePickerPAD(this.getContext(), new Callback2<NormalModeItemMsg>() {
                    @Override
                    public void onCompleted(final NormalModeItemMsg message) {
//                        Bundle bd = new Bundle();
//                        bd.putSerializable("msg", message);
//                        EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
                        startWork(message.getType(), message);
                    }
                });//待修改

                PopoupHelper.show(DeviceOvenView.this, pop, Gravity.CENTER);
            }
        }
    }

    //------------------------------------------- 自洁模式按钮 --------------------------------------
    @OnClick(R.id.rlSelfClean)
    public void onClickClean() {
        if (oven.status == OvenStatus.Off) {
            rlNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            NormalModeItemMsg msg = new NormalModeItemMsg();
            msg.setType("自洁");
            msg.setTime(String.valueOf(90));
            msg.setTemperature(String.valueOf(120));
//            Bundle bundle = new Bundle();
//            bundle.putString(PageArgumentKey.Guid, oven.getID());
//            OvenSelfCleaningDialog.show(getContext(), msg);
            Bundle bd = new Bundle();
            bd.putSerializable("msg", msg);
            EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
        }

    }

    //------------------------------------------- 鸡翅按钮 --------------------------------------
    @OnClick({R.id.llChickenWing})
    public void onClickChicken() {
        if (oven.status == OvenStatus.Off) {
            rlNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            NormalModeItemMsg msg = new NormalModeItemMsg();
            msg.setType("鸡翅");
            msg.setTime(String.valueOf(16));
            msg.setTemperature(String.valueOf(180));
            msg.setDrawable(getResources().getDrawable(R.mipmap.img_oven_chicken_wing_unworking));
            PopupWindow pop = Helper.newOvenTwoSettingPickerPad(this.getContext(), new Callback2<NormalModeItemMsg>() {
                @Override
                public void onCompleted(final NormalModeItemMsg message) {
                    oven.setOvenStrongBarbecue(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            ToastUtils.show(message.toString(), Toast.LENGTH_SHORT);
                            short time = Short.valueOf(message.getTime());
                            short temp = Short.valueOf(message.getTemperature());
                            NormalModeItemMsg msg = new NormalModeItemMsg();
                            msg.setTemperature(String.valueOf(temp));
                            msg.setTime(String.valueOf(time));
                            msg.setType("鸡翅");
                            Bundle bd = new Bundle();
                            bd.putString(PageArgumentKey.Guid,oven.getID());
                            bd.putSerializable("msg", msg);
                            EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });
                }
            }, msg);//待修改

            PopoupHelper.show(DeviceOvenView.this, pop, Gravity.CENTER);
        }
    }

    @OnClick({R.id.llCake})
    public void onClickCake() {
        if (oven.status == OvenStatus.Off) {
            rlNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {

            NormalModeItemMsg msg = new NormalModeItemMsg();
            msg.setType("蛋糕");
            msg.setTime(String.valueOf(25));
            msg.setTemperature(String.valueOf(160));
            msg.setDrawable(getResources().getDrawable(R.mipmap.img_oven_chicken_wing_unworking));
            PopupWindow pop = Helper.newOvenTwoSettingPickerPad(this.getContext(), new Callback2<NormalModeItemMsg>() {
                @Override
                public void onCompleted(final NormalModeItemMsg message) {
                    oven.setOvenQuickHeating(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            ToastUtils.show(message.toString(), Toast.LENGTH_SHORT);
                            short time = Short.valueOf(message.getTime());
                            short temp = Short.valueOf(message.getTemperature());
                            NormalModeItemMsg msg = new NormalModeItemMsg();
                            msg.setTemperature(String.valueOf(temp));
                            msg.setTime(String.valueOf(time));
                            msg.setType("蛋糕");
                            Bundle bd = new Bundle();
                            bd.putSerializable("msg", msg);
                            EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });

                }
            }, msg);//待修改

            PopoupHelper.show(DeviceOvenView.this, pop, Gravity.CENTER);
        }
    }

    @OnClick({R.id.llBread})
    public void onClickBread() {
        if (oven.status == OvenStatus.Off) {
            rlNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            NormalModeItemMsg msg = new NormalModeItemMsg();
            msg.setType("面包");
            msg.setTime(String.valueOf(18));
            msg.setTemperature(String.valueOf(165));
            msg.setDrawable(getResources().getDrawable(R.mipmap.img_oven_chicken_wing_unworking));
            PopupWindow pop = Helper.newOvenTwoSettingPickerPad(this.getContext(), new Callback2<NormalModeItemMsg>() {
                @Override
                public void onCompleted(final NormalModeItemMsg message) {
                    oven.setOvenAirBarbecue(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            ToastUtils.show(message.toString(), Toast.LENGTH_SHORT);
                            short time = Short.valueOf(message.getTime());
                            short temp = Short.valueOf(message.getTemperature());
                            NormalModeItemMsg msg = new NormalModeItemMsg();
                            msg.setTemperature(String.valueOf(temp));
                            msg.setTime(String.valueOf(time));
                            msg.setType("面包");
                            Bundle bd = new Bundle();
                            bd.putSerializable("msg", msg);
                            EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
                            ;
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });

                }
            }, msg);//待修改

            PopoupHelper.show(DeviceOvenView.this, pop, Gravity.CENTER);
        }
    }

    @OnClick({R.id.llStreakyPork})
    public void onClickStreakPork() {
        if (oven.status == OvenStatus.Off) {
            rlNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            NormalModeItemMsg msg = new NormalModeItemMsg();
            msg.setType("五花肉");
            msg.setTime(String.valueOf(45));
            msg.setTemperature(String.valueOf(215));
            msg.setDrawable(getResources().getDrawable(R.mipmap.img_oven_chicken_wing_unworking));
            PopupWindow pop = Helper.newOvenTwoSettingPickerPad(this.getContext(), new Callback2<NormalModeItemMsg>() {
                @Override
                public void onCompleted(final NormalModeItemMsg message) {
                    oven.setOvenAirBarbecue(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            ToastUtils.show(message.toString(), Toast.LENGTH_SHORT);
                            short time = Short.valueOf(message.getTime());
                            short temp = Short.valueOf(message.getTemperature());
                            NormalModeItemMsg msg = new NormalModeItemMsg();
                            msg.setTemperature(String.valueOf(temp));
                            msg.setTime(String.valueOf(time));
                            msg.setType("五花肉");
                            Bundle bd = new Bundle();
                            bd.putSerializable("msg", msg);
                            EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });

                }
            }, msg);//待修改

            PopoupHelper.show(DeviceOvenView.this, pop, Gravity.CENTER);
        }
    }

    @OnClick({R.id.llSteak})
    public void onClickSteak() {
        if (oven.status == OvenStatus.Off) {
            rlNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            NormalModeItemMsg msg = new NormalModeItemMsg();
            msg.setType("牛排");
            msg.setTime(String.valueOf(15));
            msg.setTemperature(String.valueOf(180));
            msg.setDrawable(getResources().getDrawable(R.mipmap.img_oven_chicken_wing_unworking));
            PopupWindow pop = Helper.newOvenTwoSettingPickerPad(this.getContext(), new Callback2<NormalModeItemMsg>() {
                @Override
                public void onCompleted(final NormalModeItemMsg message) {
                    oven.setOvenToast(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            ToastUtils.show(message.toString(), Toast.LENGTH_SHORT);
                            short time = Short.valueOf(message.getTime());
                            short temp = Short.valueOf(message.getTemperature());
                            NormalModeItemMsg msg = new NormalModeItemMsg();
                            msg.setTemperature(String.valueOf(temp));
                            msg.setTime(String.valueOf(time));
                            msg.setType("牛排");
                            Bundle bd = new Bundle();
                            bd.putSerializable("msg", msg);
                            EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });

                }
            }, msg);//待修改

            PopoupHelper.show(DeviceOvenView.this, pop, Gravity.CENTER);
        }
    }

    @OnClick({R.id.llPisa})
    public void onClickPisa() {
        if (oven.status == OvenStatus.Off) {
            rlNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            NormalModeItemMsg msg = new NormalModeItemMsg();
            msg.setType("披萨");
            msg.setTime(String.valueOf(20));
            msg.setTemperature(String.valueOf(200));
            msg.setDrawable(getResources().getDrawable(R.mipmap.img_oven_chicken_wing_unworking));
            PopupWindow pop = Helper.newOvenTwoSettingPickerPad(this.getContext(), new Callback2<NormalModeItemMsg>() {
                @Override
                public void onCompleted(final NormalModeItemMsg message) {
                    oven.setOvenToast(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            ToastUtils.show(message.toString(), Toast.LENGTH_SHORT);
                            short time = Short.valueOf(message.getTime());
                            short temp = Short.valueOf(message.getTemperature());
                            NormalModeItemMsg msg = new NormalModeItemMsg();
                            msg.setTemperature(String.valueOf(temp));
                            msg.setTime(String.valueOf(time));
                            msg.setType("披萨");
                            Bundle bd = new Bundle();
                            bd.putSerializable("msg", msg);
                            EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });

                }
            }, msg);//待修改

            PopoupHelper.show(DeviceOvenView.this, pop, Gravity.CENTER);
        }
    }

    @OnClick({R.id.llSeafood})
    public void onClickSeaFood() {
        if (oven.status == OvenStatus.Off) {
            rlNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            NormalModeItemMsg msg = new NormalModeItemMsg();
            msg.setType("海鲜");
            msg.setTime(String.valueOf(23));
            msg.setTemperature(String.valueOf(200));
            msg.setDrawable(getResources().getDrawable(R.mipmap.img_oven_chicken_wing_unworking));
            PopupWindow pop = Helper.newOvenTwoSettingPickerPad(this.getContext(), new Callback2<NormalModeItemMsg>() {
                @Override
                public void onCompleted(final NormalModeItemMsg message) {
                    oven.setOvenStrongBarbecue(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            ToastUtils.show(message.toString(), Toast.LENGTH_SHORT);
                            short time = Short.valueOf(message.getTime());
                            short temp = Short.valueOf(message.getTemperature());
                            NormalModeItemMsg msg = new NormalModeItemMsg();
                            msg.setTemperature(String.valueOf(temp));
                            msg.setTime(String.valueOf(time));
                            msg.setType("海鲜");
                            Bundle bd = new Bundle();
                            bd.putSerializable("msg", msg);
                            EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });

                }
            }, msg);//待修改

            PopoupHelper.show(DeviceOvenView.this, pop, Gravity.CENTER);
        }
    }

    @OnClick({R.id.llCookie})
    public void onClickCookie() {
        if (oven.status == OvenStatus.Off) {
            rlNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            NormalModeItemMsg msg = new NormalModeItemMsg();
            msg.setType("饼干");
            msg.setTime(String.valueOf(16));
            msg.setTemperature(String.valueOf(170));
            msg.setDrawable(getResources().getDrawable(R.mipmap.img_oven_chicken_wing_unworking));
            PopupWindow pop = Helper.newOvenTwoSettingPickerPad(this.getContext(), new Callback2<NormalModeItemMsg>() {
                @Override
                public void onCompleted(final NormalModeItemMsg message) {
                    oven.setOvenAirBarbecue(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            ToastUtils.show(message.toString(), Toast.LENGTH_SHORT);
                            short time = Short.valueOf(message.getTime());
                            short temp = Short.valueOf(message.getTemperature());
                            NormalModeItemMsg msg = new NormalModeItemMsg();
                            msg.setTemperature(String.valueOf(temp));
                            msg.setTime(String.valueOf(time));
                            msg.setType("饼干");
                            Bundle bd = new Bundle();
                            bd.putSerializable("msg", msg);
                            EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });

                }
            }, msg);//待修改

            PopoupHelper.show(DeviceOvenView.this, pop, Gravity.CENTER);
        }
    }

    @OnClick({R.id.llVegetable})
    public void onClickVegetable() {
        if (oven.status == OvenStatus.Off) {
            rlNoticeStart.setVisibility(View.VISIBLE);
        } else if (oven.status != OvenStatus.Off) {
            NormalModeItemMsg msg = new NormalModeItemMsg();
            msg.setType("蔬菜");
            msg.setTime(String.valueOf(15));
            msg.setTemperature(String.valueOf(200));
            msg.setDrawable(getResources().getDrawable(R.mipmap.img_oven_chicken_wing_unworking));
            PopupWindow pop = Helper.newOvenTwoSettingPickerPad(getContext(), new Callback2<NormalModeItemMsg>() {
                @Override
                public void onCompleted(final NormalModeItemMsg message) {
                    oven.setOvenQuickHeating(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            ToastUtils.show(message.toString(), Toast.LENGTH_SHORT);
                            short time = Short.valueOf(message.getTime());
                            short temp = Short.valueOf(message.getTemperature());
                            NormalModeItemMsg msg = new NormalModeItemMsg();
                            msg.setTemperature(String.valueOf(temp));
                            msg.setTime(String.valueOf(time));
                            msg.setType("蔬菜");
                            Bundle bd = new Bundle();
                            bd.putSerializable("msg", msg);
                            EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });

                }
            }, msg);//待修改

            PopoupHelper.show(DeviceOvenView.this, pop, Gravity.CENTER);
        }
    }

    private void startWork(String type, NormalModeItemMsg msg) {
        short preTemp = Short.valueOf(msg.getTemperature());
        short preTime = Short.valueOf(msg.getTime());
        final Bundle bd = new Bundle();
        bd.putSerializable("msg", msg);
        if (type.equals("快热")||type.equals("蛋糕")||type.equals("蔬菜")) {
            oven.setOvenQuickHeating(preTime, preTemp, (short) 0, new VoidCallback() {
                @Override
                public void onSuccess() {
                    oven.setOvenStatusControl(OvenStatus.Working, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                        }
                    });
                }

                @Override
                public void onFailure(Throwable t) {
                    ToastUtils.showThrowable(t);
                }
            });
        } else if (type.equals("风焙烤")) {
            oven.setOvenAirBaking(preTime, preTemp, (short) 0, new VoidCallback() {
                @Override
                public void onSuccess() {
                    oven.setOvenStatusControl(OvenStatus.Working, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                        }
                    });
                }

                @Override
                public void onFailure(Throwable t) {
                    ToastUtils.showThrowable(t);
                }
            });
        } else if (type.equals("焙烤")||type.equals("牛排")||type.equals("披萨")) {
            oven.setOvenToast(preTime, preTemp, (short) 0, new VoidCallback() {
                @Override
                public void onSuccess() {
                    oven.setOvenStatusControl(OvenStatus.Working, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                        }
                    });
                }

                @Override
                public void onFailure(Throwable t) {
                    ToastUtils.showThrowable(t);
                }
            });
        } else if (type.equals("底加热")) {
            oven.setOvenBottomHeating(preTime, preTemp, (short) 0, new VoidCallback() {
                @Override
                public void onSuccess() {
                    oven.setOvenStatusControl(OvenStatus.Working, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                        }
                    });
                }

                @Override
                public void onFailure(Throwable t) {
                    ToastUtils.showThrowable(t);
                }
            });
        } else if (type.equals("解冻")) {
            oven.setOvenUnfreeze(preTime, preTemp, (short) 0, new VoidCallback() {
                @Override
                public void onSuccess() {
                    oven.setOvenStatusControl(OvenStatus.Working, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                        }
                    });
                }

                @Override
                public void onFailure(Throwable t) {
                    ToastUtils.showThrowable(t);
                }
            });
        } else if (type.equals("风扇烤")||type.equals("饼干")||type.equals("面包")||type.equals("五花肉")) {
            oven.setOvenAirBarbecue(preTime, preTemp, (short) 0, new VoidCallback() {
                @Override
                public void onSuccess() {
                    oven.setOvenStatusControl(OvenStatus.Working, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                        }
                    });

                }

                @Override
                public void onFailure(Throwable t) {
                    ToastUtils.showThrowable(t);
                }
            });
        } else if (type.equals("烧烤")) {
            oven.setOvenBarbecue(preTime, preTemp, (short) 0, new VoidCallback() {
                @Override
                public void onSuccess() {
                    oven.setOvenStatusControl(OvenStatus.Working, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
                        }

                        @Override
                        public void onFailure(Throwable t) {
                        }
                    });

                }

                @Override
                public void onFailure(Throwable t) {
                    ToastUtils.showThrowable(t);
                }
            });
        } else if (type.equals("强烧烤")||type.equals("海鲜")||type.equals("鸡翅")) {
            oven.setOvenStrongBarbecue(preTime, preTemp, (short) 0, new VoidCallback() {
                @Override
                public void onSuccess() {
                    oven.setOvenStatusControl(OvenStatus.Working, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
                        }
                        @Override
                        public void onFailure(Throwable t) {
                        }
                    });

                }

                @Override
                public void onFailure(Throwable t) {
                    ToastUtils.showThrowable(t);
                }
            });
        } else if (type.equals("自洁")) {
            oven.setOvenAirBarbecue(preTime,preTemp,(short)0, new VoidCallback() {
                @Override
                public void onSuccess() {
                    EventUtils.postEvent(new DeviceOvenSwitchViewEvent(3, bd));
                }

                @Override
                public void onFailure(Throwable t) {
                    ToastUtils.showThrowable(t);
                }
            });
        }
    }

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

//        disconnectHintView.setVisibility(event.isConnected ? View.INVISIBLE : View.VISIBLE);
    }

    @Subscribe
    public void onEvent(OvenStatusChangedEvent event) {
        if (oven == null || !Objects.equal(oven.getID(), event.pojo.getID()))
            return;
        onRefresh();
    }
}
