package com.robam.roki.ui.page;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.google.common.eventbus.Subscribe;
import com.legent.Callback2;
import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.events.DeviceConnectionChangedEvent;
import com.legent.ui.UIService;
import com.legent.ui.ext.BasePage;
import com.legent.ui.ext.popoups.PopoupHelper;
import com.legent.ui.ext.views.TitleBar;
import com.legent.utils.TimeUtils;
import com.legent.utils.api.ToastUtils;
import com.robam.common.Utils;
import com.robam.common.events.OvenAlarmEvent;
import com.robam.common.events.OvenLightResetEvent;
import com.robam.common.events.OvenSpitRotateResetEvent;
import com.robam.common.events.OvenStatusChangedEvent;
import com.robam.common.pojos.device.Oven.AbsOven;
import com.robam.common.pojos.device.Oven.OvenStatus;
import com.robam.roki.R;
import com.robam.roki.model.NormalModeItemMsg;
import com.robam.roki.ui.Helper;
import com.robam.roki.ui.PageArgumentKey;
import com.robam.roki.ui.PageKey;
import com.robam.roki.ui.dialog.OvenBrokenDialog;
import com.robam.roki.ui.dialog.OvenCancelWorkDialog;
import com.robam.roki.ui.dialog.OvenCountDownDialog;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by linxiaobin on 2015/12/25.
 */
public class DeviceOvenWorkingPage extends BasePage {

    AbsOven oven;
    private int remainTime;
    private short preTime;
    private short preTemp;
    private String type;
    private short rotate;
    private short light;

    private short testStatus = 0;

    static final int Start = 4;
    static final int Pause = 0;
    static final int Working = 1;
    static final int Done = 2;
    static final int CountDown = 3;
    static final int Reset = 5;
    static final int ReturnHome = 6;
    private int lastTime;
    private boolean canCountDown = false;
    int preStatus;
    int currentStatus;
    boolean tagAlarmShow = false;
    static final int Light = 7;
    static final int Rotate = 8;

    static final int PollStatus = 9;
    static final int RefreshTime = 10;

    static boolean fromSetting = false;

    private OvenBrokenDialog dlg = null;//报警

    private Animation circleRotate = null;//动画设置
    private Animation spitRotate = null;//动画设置

    View contentView;
    @InjectView(R.id.txtCurrentTem)
    TextView txtCurrentTem;
    @InjectView(R.id.txtCurrentTime)
    TextView txtCurrentTime;
    @InjectView(R.id.imgSpinCircle)
    ImageView imgSpinCircle;
    @InjectView(R.id.imgContent)
    ImageView imgContent;
    @InjectView(R.id.imgPause)
    ImageView imgPause;
    @InjectView(R.id.imgDone)
    ImageView imgDone;
    @InjectView(R.id.workType1)
    TextView workType1;
    @InjectView(R.id.workType2)
    TextView workType2;
    @InjectView(R.id.imgLight)
    ImageView imgLight;
    @InjectView(R.id.rlLight)
    RelativeLayout rlLight;
    @InjectView(R.id.imgRotate)
    ImageView imgRotate;
    @InjectView(R.id.imgReturn)
    ImageView imgReturn;
    @InjectView(R.id.txtRecipe)
    TextView txtRecipe;
    @InjectView(R.id.imgTempReset)
    ImageView imgTempReset;
    @InjectView(R.id.imgTimeReset)
    ImageView imgTimeReset;

    @InjectView(R.id.txtTemSet)
    TextView txtTemSet;
    @InjectView(R.id.txtTimeSet)
    TextView txtTimeSet;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Start:

                    if (type.equals("快热") || type.equals("蛋糕") || type.equals("蔬菜")) {

                        oven.setOvenQuickHeating(preTime, preTemp, (short) 0, new VoidCallback() {
                            @Override
                            public void onSuccess() {
                                oven.setOvenStatusControl(OvenStatus.Working, new VoidCallback() {
                                    @Override
                                    public void onSuccess() {
                                        txtCurrentTem.setClickable(false);
                                        txtCurrentTime.setClickable(false);
                                        txtTemSet.setText(String.valueOf(preTemp));
                                        txtTimeSet.setText(String.valueOf(preTime));
                                        if (circleRotate == null) {
                                            circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_circle_rotate);
                                            LinearInterpolator lin = new LinearInterpolator();
                                            circleRotate.setInterpolator(lin);
                                        }
                                        imgContent.setImageDrawable(getResources().getDrawable(R.mipmap.img_oven_quick_heating_working));
                                        workType1.setText("快热模式");
                                        imgSpinCircle.startAnimation(circleRotate);
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
                                        txtTemSet.setText(String.valueOf(preTemp));
                                        txtTimeSet.setText(String.valueOf(preTime));
                                        txtCurrentTem.setClickable(false);
                                        txtCurrentTime.setClickable(false);
                                        if (circleRotate == null) {
                                            circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_circle_rotate);
                                            LinearInterpolator lin = new LinearInterpolator();
                                            circleRotate.setInterpolator(lin);
                                        }
                                        imgContent.setImageDrawable(getResources().getDrawable(R.mipmap.img_oven_air_barking_working));
                                        workType1.setText("风焙烤模式");
                                        imgSpinCircle.startAnimation(circleRotate);
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
                    } else if (type.equals("焙烤") || type.equals("牛排") || type.equals("披萨")) {
                        oven.setOvenToast(preTime, preTemp, (short) 0, new VoidCallback() {
                            @Override
                            public void onSuccess() {
                                oven.setOvenStatusControl(OvenStatus.Working, new VoidCallback() {
                                    @Override
                                    public void onSuccess() {
                                        txtTemSet.setText(String.valueOf(preTemp));
                                        txtTimeSet.setText(String.valueOf(preTime));
                                        txtCurrentTem.setClickable(false);
                                        txtCurrentTime.setClickable(false);
                                        if (circleRotate == null) {
                                            circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_circle_rotate);
                                            LinearInterpolator lin = new LinearInterpolator();
                                            circleRotate.setInterpolator(lin);
                                        }
                                        imgContent.setImageDrawable(getResources().getDrawable(R.mipmap.img_oven_toast_working));
                                        workType1.setText("焙烤模式");
                                        imgSpinCircle.startAnimation(circleRotate);
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
                                        txtTemSet.setText(String.valueOf(preTemp));
                                        txtTimeSet.setText(String.valueOf(preTime));
                                        txtCurrentTem.setClickable(false);
                                        txtCurrentTime.setClickable(false);
                                        if (circleRotate == null) {
                                            circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_circle_rotate);
                                            LinearInterpolator lin = new LinearInterpolator();
                                            circleRotate.setInterpolator(lin);
                                        }
                                        imgContent.setImageDrawable(getResources().getDrawable(R.mipmap.img_oven_bottom_heating_working));
                                        workType1.setText("底加热模式");
                                        imgSpinCircle.startAnimation(circleRotate);
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
                                        txtTemSet.setText(String.valueOf(preTemp));
                                        txtTimeSet.setText(String.valueOf(preTime));
                                        txtCurrentTem.setClickable(false);
                                        txtCurrentTime.setClickable(false);
                                        if (circleRotate == null) {
                                            circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_circle_rotate);
                                            LinearInterpolator lin = new LinearInterpolator();
                                            circleRotate.setInterpolator(lin);
                                        }
                                        imgContent.setImageDrawable(getResources().getDrawable(R.mipmap.img_oven_unfreezing_working));
                                        workType1.setText("解冻模式");
                                        imgSpinCircle.startAnimation(circleRotate);
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
                    } else if (type.equals("风扇烤") || type.equals("饼干") || type.equals("面包") || type.equals("五花肉")) {
                        oven.setOvenAirBarbecue(preTime, preTemp, (short) 0, new VoidCallback() {
                            @Override
                            public void onSuccess() {
                                oven.setOvenStatusControl(OvenStatus.Working, new VoidCallback() {
                                    @Override
                                    public void onSuccess() {
                                        txtTemSet.setText(String.valueOf(preTemp));
                                        txtTimeSet.setText(String.valueOf(preTime));
                                        txtCurrentTem.setClickable(false);
                                        txtCurrentTime.setClickable(false);
                                        if (circleRotate == null) {
                                            circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_circle_rotate);
                                            LinearInterpolator lin = new LinearInterpolator();
                                            circleRotate.setInterpolator(lin);
                                        }
                                        imgContent.setImageDrawable(getResources().getDrawable(R.mipmap.img_oven_air_barbecue_working));
                                        workType1.setText("风扇烤模式");
                                        imgSpinCircle.startAnimation(circleRotate);
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
                                        txtTemSet.setText(String.valueOf(preTemp));
                                        txtTimeSet.setText(String.valueOf(preTime));
                                        txtCurrentTem.setClickable(false);
                                        txtCurrentTime.setClickable(false);
                                        if (circleRotate == null) {
                                            circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_circle_rotate);
                                            LinearInterpolator lin = new LinearInterpolator();
                                            circleRotate.setInterpolator(lin);
                                        }
                                        imgContent.setImageDrawable(getResources().getDrawable(R.mipmap.img_oven_barbecue_working));
                                        workType1.setText("烧烤");
                                        imgSpinCircle.startAnimation(circleRotate);
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
                    } else if (type.equals("强烧烤") || type.equals("海鲜") || type.equals("鸡翅")) {
                        oven.setOvenStrongBarbecue(preTime, preTemp, (short) 0, new VoidCallback() {
                            @Override
                            public void onSuccess() {
                                oven.setOvenStatusControl(OvenStatus.Working, new VoidCallback() {
                                    @Override
                                    public void onSuccess() {
                                        txtTemSet.setText(String.valueOf(preTemp));
                                        txtTimeSet.setText(String.valueOf(preTime));
                                        txtCurrentTem.setClickable(false);
                                        txtCurrentTime.setClickable(false);
                                        if (circleRotate == null) {
                                            circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_circle_rotate);
                                            LinearInterpolator lin = new LinearInterpolator();
                                            circleRotate.setInterpolator(lin);
                                        }
                                        imgContent.setImageDrawable(getResources().getDrawable(R.mipmap.img_oven_strong_barbecue_working));
                                        workType1.setText("强烧烤");
                                        imgSpinCircle.startAnimation(circleRotate);
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
                        oven.setOvenAirBarbecue(preTime, preTemp, (short) 0, new VoidCallback() {
                            @Override
                            public void onSuccess() {
                                txtTemSet.setText(String.valueOf(preTemp));
                                txtTimeSet.setText(String.valueOf(preTime));
                                txtCurrentTem.setClickable(false);
                                txtCurrentTime.setClickable(false);
                                imgContent.setClickable(false);
                                imgRotate.setVisibility(View.GONE);
                                rlLight.setClickable(false);
                                if (circleRotate == null) {
                                    circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_circle_rotate);
                                    LinearInterpolator lin = new LinearInterpolator();
                                    circleRotate.setInterpolator(lin);
                                }
                                imgContent.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_oven_self_clean));
                                workType1.setText("自洁");
                                imgSpinCircle.startAnimation(circleRotate);

                            }

                            @Override
                            public void onFailure(Throwable t) {
                                ToastUtils.showThrowable(t);
                            }
                        });
                    }

                    break;
                case Pause:
                    oven.setOvenStatusControl(OvenStatus.Pause, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            imgPause.setVisibility(View.VISIBLE);
                            txtCurrentTem.setClickable(true);
                            txtCurrentTime.setClickable(true);
                            imgTempReset.setVisibility(View.VISIBLE);
                            imgTimeReset.setVisibility(View.VISIBLE);
                            imgSpinCircle.clearAnimation();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.showThrowable(t);
                        }
                    });
                    break;
                case Working:
                    oven.setOvenStatusControl(OvenStatus.Working, new VoidCallback() {
                        @Override
                        public void onSuccess() {
                            imgPause.setVisibility(View.GONE);
                            imgTempReset.setVisibility(View.GONE);
                            imgTimeReset.setVisibility(View.GONE);
                            txtCurrentTem.setClickable(false);
                            if (circleRotate == null) {
                                circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_circle_rotate);
                                LinearInterpolator lin = new LinearInterpolator();
                                circleRotate.setInterpolator(lin);
                            }
                            imgSpinCircle.startAnimation(circleRotate);
                            handler.sendEmptyMessage(CountDown);
                            canCountDown = true;
                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }
                    });
                    break;
                case Done:
                    imgSpinCircle.clearAnimation();
                    imgSpinCircle.setVisibility(View.GONE);
                    imgDone.setVisibility(View.VISIBLE);
                    imgContent.setVisibility(View.GONE);
                    workType1.setVisibility(View.GONE);
                    workType2.setVisibility(View.VISIBLE);
                    // 10min后自动关机
                    handler.sendEmptyMessageDelayed(ReturnHome, 3 * 1000);
                    break;
                case CountDown:
                    if (oven.status == OvenStatus.Working) {
                        int min = (int) (remainTime / 60.0);
                        int leftMin = oven.time - min * 60;
                        if (leftMin > 0)
                            min++;
                        txtCurrentTime.setText(String.valueOf(min));
                        remainTime--;
                        txtCurrentTem.setText(String.valueOf(oven.temp));
                        handler.sendEmptyMessageDelayed(CountDown, 1000);
                    }
                    break;
                case Light:
                    Log.e("light", String.valueOf(oven.light));
                    Log.e("revolve", String.valueOf(oven.revolve));
                    if (oven.light == 0) {
                        oven.setOvenSpitRotateLightControl(oven.revolve, (short) 1, (short) 0, new VoidCallback() {
                            @Override
                            public void onSuccess() {
                                imgLight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_fan_light_selected));
                                rlLight.setBackground(getResources().getDrawable(R.mipmap.img_oven_working_circle_yellow));
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                ToastUtils.showThrowable(t);
                            }
                        });
                    } else if (oven.light == 1) {
                        oven.setOvenSpitRotateLightControl(oven.revolve, (short) 0, (short) 0, new VoidCallback() {
                            @Override
                            public void onSuccess() {
                                imgLight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_fan_light_normal));
                                rlLight.setBackground(getResources().getDrawable(R.mipmap.ic_device_oven_gray_circle));
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                ToastUtils.showThrowable(t);
                            }
                        });
                    }
                    break;
                case Rotate:
                    if (oven.revolve == 0) {
                        oven.setOvenSpitRotateLightControl((short) 1, oven.light, (short) 0, new VoidCallback() {
                            @Override
                            public void onSuccess() {
                                if (spitRotate == null) {
                                    spitRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_spit_rotate);
                                    LinearInterpolator lin = new LinearInterpolator();
                                    spitRotate.setInterpolator(lin);
                                }
                                imgRotate.setImageDrawable(getResources().getDrawable(R.mipmap.img_device_oven_rotate_open));
//                                imgRotate.startAnimation(spitRotate);
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                ToastUtils.showThrowable(t);
                            }
                        });
                    } else if (oven.revolve == 1) {
                        oven.setOvenSpitRotateLightControl((short) 0, oven.light, (short) 0, new VoidCallback() {
                            @Override
                            public void onSuccess() {
                                imgRotate.clearAnimation();
                                imgRotate.setImageDrawable(getResources().getDrawable(R.mipmap.img_device_oven_rotate_close));
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                ToastUtils.showThrowable(t);
                            }
                        });
//                        imgRotate.clearAnimation();
                    }
                    break;
                case Reset:
                    final NormalModeItemMsg message = (NormalModeItemMsg) msg.obj;
                    imgTempReset.setVisibility(View.GONE);
                    imgTimeReset.setVisibility(View.GONE);


                    if (oven.runP == 1)
                        oven.setOvenQuickHeating(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                            @Override
                            public void onSuccess() {
                                txtTemSet.setText(message.getTemperature());
                                txtTimeSet.setText(message.getTime());
                                remainTime = Short.valueOf(message.getTime()) * 60;

                                if (circleRotate == null) {
                                    circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_circle_rotate);
                                    LinearInterpolator lin = new LinearInterpolator();
                                    circleRotate.setInterpolator(lin);
                                    imgSpinCircle.startAnimation(circleRotate);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                ToastUtils.showThrowable(t);
                            }
                        });
                    if (oven.runP == 2)
                        oven.setOvenAirBaking(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                            @Override
                            public void onSuccess() {
                                txtTemSet.setText(message.getTemperature());
                                txtTimeSet.setText(message.getTime());
                                remainTime = Short.valueOf(message.getTime()) * 60;
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                ToastUtils.showThrowable(t);
                            }
                        });
                    if (oven.runP == 3)
                        oven.setOvenToast(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                            @Override
                            public void onSuccess() {
                                txtTemSet.setText(message.getTemperature());
                                txtTimeSet.setText(message.getTime());
                                remainTime = Short.valueOf(message.getTime()) * 60;
                                if (circleRotate == null) {
                                    circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_circle_rotate);
                                    LinearInterpolator lin = new LinearInterpolator();
                                    circleRotate.setInterpolator(lin);
                                    imgSpinCircle.startAnimation(circleRotate);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                ToastUtils.showThrowable(t);
                            }
                        });
                    if (oven.runP == 4)
                        oven.setOvenBottomHeating(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                            @Override
                            public void onSuccess() {
                                txtTemSet.setText(message.getTemperature());
                                txtTimeSet.setText(message.getTime());
                                remainTime = Short.valueOf(message.getTime()) * 60;
                                if (circleRotate == null) {
                                    circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_circle_rotate);
                                    LinearInterpolator lin = new LinearInterpolator();
                                    circleRotate.setInterpolator(lin);
                                    imgSpinCircle.startAnimation(circleRotate);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                ToastUtils.showThrowable(t);
                            }
                        });
                    if (oven.runP == 5)
                        oven.setOvenUnfreeze(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                            @Override
                            public void onSuccess() {
                                txtTemSet.setText(message.getTemperature());
                                txtTimeSet.setText(message.getTime());
                                remainTime = Short.valueOf(message.getTime()) * 60;
                                if (circleRotate == null) {
                                    circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_circle_rotate);
                                    LinearInterpolator lin = new LinearInterpolator();
                                    circleRotate.setInterpolator(lin);
                                    imgSpinCircle.startAnimation(circleRotate);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                ToastUtils.showThrowable(t);
                            }
                        });
                    if (oven.runP == 6)
                        oven.setOvenAirBarbecue(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                            @Override
                            public void onSuccess() {
                                txtTemSet.setText(message.getTemperature());
                                txtTimeSet.setText(message.getTime());
                                remainTime = Short.valueOf(message.getTime()) * 60;
                                if (circleRotate == null) {
                                    circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_circle_rotate);
                                    LinearInterpolator lin = new LinearInterpolator();
                                    circleRotate.setInterpolator(lin);
                                    imgSpinCircle.startAnimation(circleRotate);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                ToastUtils.showThrowable(t);
                            }
                        });
                    if (oven.runP == 7)
                        oven.setOvenBarbecue(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                            @Override
                            public void onSuccess() {
                                txtTemSet.setText(message.getTemperature());
                                txtTimeSet.setText(message.getTime());
                                remainTime = Short.valueOf(message.getTime()) * 60;
                                if (circleRotate == null) {
                                    circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_circle_rotate);
                                    LinearInterpolator lin = new LinearInterpolator();
                                    circleRotate.setInterpolator(lin);
                                    imgSpinCircle.startAnimation(circleRotate);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                ToastUtils.showThrowable(t);
                            }
                        });
                    if (oven.runP == 8)
                        oven.setOvenStrongBarbecue(Short.valueOf(message.getTime()), Short.valueOf(message.getTemperature()), (short) 0, new VoidCallback() {
                            @Override
                            public void onSuccess() {
                                txtTemSet.setText(message.getTemperature());
                                txtTimeSet.setText(message.getTime());
                                remainTime = Short.valueOf(message.getTime()) * 60;
                                if (circleRotate == null) {
                                    circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_circle_rotate);
                                    LinearInterpolator lin = new LinearInterpolator();
                                    circleRotate.setInterpolator(lin);
                                    imgSpinCircle.startAnimation(circleRotate);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                ToastUtils.showThrowable(t);
                            }
                        });

                    break;

                case PollStatus:
                    Log.e("temp", String.valueOf(oven.setTemp));
                    //判断完成后关机
                    int min = oven.time / 60;
                    int leftMin = oven.time - min * 60;
                    if (leftMin > 0)
                        min++;
                    preStatus = currentStatus;
                    currentStatus = oven.status;
                    txtTemSet.setText(String.valueOf(oven.setTemp));
                    txtTimeSet.setText(String.valueOf(oven.setTime));
                    txtCurrentTem.setText(String.valueOf(oven.temp));
                    txtCurrentTime.setText(String.valueOf(min));
                    Log.e("preStatus", String.valueOf(preStatus));
                    Log.e("currentStatus", String.valueOf(currentStatus));
                    if (tagAlarmShow == false||oven.alarm == AbsOven.Event_Oven_Alarm_Senor_Open || oven.alarm == AbsOven.Event_Oven_Alarm_Senor_Short) {
                        checkBroken();
                        tagAlarmShow = true;
                        break;
                    }
                    if (preStatus == OvenStatus.Working && currentStatus != preStatus && currentStatus != OvenStatus.Off
                            && currentStatus != OvenStatus.Pause && currentStatus != OvenStatus.Working && currentStatus != 18 && preStatus != 18)
                        handler.sendEmptyMessage(Done);
                    if (oven.status == OvenStatus.Off)
                        UIService.getInstance().returnHome();
                    if (oven.status == OvenStatus.Pause) {
                        imgPause.setVisibility(View.VISIBLE);
                        imgTempReset.setVisibility(View.VISIBLE);
                        imgTimeReset.setVisibility(View.VISIBLE);
                        txtCurrentTem.setClickable(true);
                        txtCurrentTime.setClickable(true);

                        imgSpinCircle.clearAnimation();
                    } else if (oven.status == OvenStatus.Working) {
                        if (dlg != null && dlg.isShowing()) {
                            dlg.dismiss();
                        }
                        imgPause.setVisibility(View.GONE);
                        imgTempReset.setVisibility(View.GONE);
                        imgTimeReset.setVisibility(View.GONE);
                        txtCurrentTem.setClickable(false);
                        txtCurrentTime.setClickable(false);
                        if (circleRotate == null) {
                            circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_circle_rotate);
                            LinearInterpolator lin = new LinearInterpolator();
                            circleRotate.setInterpolator(lin);
                            imgSpinCircle.startAnimation(circleRotate);
                        }
//                        if (oven.time < lastTime) {
                        handler.sendEmptyMessage(15);

                        if (oven.time < lastTime) {
                            if (!canCountDown) {
                                handler.sendEmptyMessage(CountDown);
                                canCountDown = true;
                            }
                        } else {
                            lastTime = oven.time;
                            canCountDown = false;
                        }
                    } else if (oven.status == OvenStatus.Wait) {
                        if (dlg != null && dlg.isShowing()) {
                            dlg = null;
                        }
                    }
                    break;
                case ReturnHome:
                    imgSpinCircle.clearAnimation();
                    imgRotate.clearAnimation();
                    UIService.getInstance().returnHome();
                    break;
                case RefreshTime:
                    int minute = oven.time / 60;
                    int minuteLeft = oven.time - minute * 60;
                    if (minuteLeft > 0)
                        minute++;
                    txtCurrentTime.setText(String.valueOf(minute));
//                    txtCurrentTime.setText(TimeUtils.sec2clock(50));

                    break;
                case 15:
                    Log.e("time", String.valueOf(oven.time));
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        Bundle bd = getArguments();
        String guid = bd == null ? null : bd.getString(PageArgumentKey.Guid);
        NormalModeItemMsg msg = bd == null ? null : (NormalModeItemMsg) bd.getSerializable("msg");
        if (msg != null) {
            preTime = Short.valueOf(msg.getTime());

            preTemp = Short.valueOf(msg.getTemperature());

            type = msg.getType();
        }
        oven = Plat.deviceService.lookupChild(guid);
//        oven.setTime = preTime;
//        oven.setTemp = preTemp;
        contentView = inflater.inflate(R.layout.page_device_oven_working,
                container, false);
        ButterKnife.inject(this, contentView);
//        Log.e("temp", String.valueOf(oven.setTemp));

        if (tagAlarmShow == false||oven.alarm == AbsOven.Event_Oven_Alarm_Senor_Open || oven.alarm == AbsOven.Event_Oven_Alarm_Senor_Short) {
            checkBroken();
            tagAlarmShow = true;
        }

        if (oven.status == OvenStatus.Pause || oven.status == OvenStatus.Working) {//烤箱处于暂停或者工作状态
            initView();
        }
        if (oven.status == OvenStatus.On) {//烤箱开机但是未开始工作
            fromSetting = true;
            restoreView();
        }
        initState();
        startTimer();
        return contentView;
    }

    private void initState() {
        // --------设置titleBar--------

        TextView cookbook = TitleBar.newTitleTextView(getContext(), "菜谱", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 跳转菜谱
            }
        });

//        txtTemSet.setText(oven.temp);
//        txtTimeSet.setText(oven.time);
        readyToWork();
    }

    private void initView() {
        Log.e("temp", String.valueOf(oven.setTemp));
        txtCurrentTem.setText(String.valueOf(oven.temp));
        int min = oven.time / 60;
        int leftMin = oven.time - min * 60;
        if (leftMin > 0)
            min++;
        Log.e("min", String.valueOf(min));
        txtCurrentTime.setText(String.valueOf(min));
        txtTemSet.setText(String.valueOf(oven.setTemp));
        txtTimeSet.setText(String.valueOf(oven.setTime));
//        txtCurrentTime.setText(String.valueOf(preTime + ":00"));
        remainTime = oven.time;
        //更新图标以及模式
        if (oven.runP == 1) {
            workType1.setText("快热模式");
            imgContent.setImageResource(R.mipmap.img_oven_quick_heating_working);
        } else if (oven.runP == 2) {
            workType1.setText("风焙烤模式");
            imgContent.setImageResource(R.mipmap.img_oven_air_barking_working);
        } else if (oven.runP == 3) {
            workType1.setText("焙烤模式");
            imgContent.setImageResource(R.mipmap.img_oven_toast_working);
        } else if (oven.runP == 4) {
            workType1.setText("底加热模式");
            imgContent.setImageResource(R.mipmap.img_oven_bottom_heating_working);
        } else if (oven.runP == 5) {
            workType1.setText("解冻模式");
            imgContent.setImageResource(R.mipmap.img_oven_unfreezing_working);
        } else if (oven.runP == 6) {
            workType1.setText("风扇烤模式");
            imgContent.setImageResource(R.mipmap.img_oven_air_barbecue_working);
        } else if (oven.runP == 7) {
            workType1.setText("烧烤模式");
            imgContent.setImageResource(R.mipmap.img_oven_barbecue_working);
        } else if (oven.runP == 8) {
            workType1.setText("强烧烤模式");
            imgContent.setImageResource(R.mipmap.img_oven_strong_barbecue_working);
        }
        if (oven.status == OvenStatus.Pause) {//如果是暂停状态
            imgPause.setVisibility(View.VISIBLE);
            imgTempReset.setVisibility(View.VISIBLE);
            imgTimeReset.setVisibility(View.VISIBLE);
            txtCurrentTem.setClickable(true);
            txtCurrentTime.setClickable(true);
            imgSpinCircle.clearAnimation();
        } else {
            imgPause.setVisibility(View.GONE);//工作状态
            imgTempReset.setVisibility(View.GONE);
            imgTimeReset.setVisibility(View.GONE);
            txtCurrentTem.setClickable(false);
            txtCurrentTime.setClickable(false);
            if (circleRotate == null) {
                circleRotate = AnimationUtils.loadAnimation(getContext(), R.anim.device_oven_circle_rotate);
                LinearInterpolator lin = new LinearInterpolator();
                circleRotate.setInterpolator(lin);
            }
            imgSpinCircle.startAnimation(circleRotate);
            if (canCountDown) {
                handler.sendEmptyMessage(CountDown);
                canCountDown = true;
            }
        }
    }

    private void restoreView() {
        txtCurrentTime.setText(String.valueOf(preTime));
        remainTime = preTime;
        handler.sendEmptyMessage(Start);
    }

    private void checkBroken() {
        Log.e("alarm", String.valueOf(oven.alarm));
        if (oven.alarm == AbsOven.Event_Oven_Alarm_Senor_Short) {
            showDialog("错误：E01", AbsOven.Event_Oven_Alarm_Senor_Short);
        } else if (oven.alarm == AbsOven.Event_Oven_Alarm_Senor_Open) {
            showDialog("错误：E02", AbsOven.Event_Oven_Alarm_Senor_Open);
        }
    }

    private void readyToWork() {
        lastTime = 0;
        if (oven.status == OvenStatus.On || oven.status == OvenStatus.Wait) {
            OvenCountDownDialog.show(getContext(), 4);
        }
    }

    @Subscribe
    public void onEvent(OvenAlarmEvent event) {
        switch (event.alarmId) {
            case AbsOven.Event_Oven_Alarm_ok:
                if (dlg != null) {
                    dlg.dismiss();
                }
                handler.sendEmptyMessage(Working);
                break;
            case AbsOven.Event_Oven_Alarm_Senor_Short:
//                handler.sendEmptyMessage(Pause);
                showDialog("错误：E01", AbsOven.Event_Oven_Alarm_Senor_Short);
                break;
            case AbsOven.Event_Oven_Alarm_Senor_Open:
//                handler.sendEmptyMessage(Pause);
                showDialog("错误：E02", AbsOven.Event_Oven_Alarm_Senor_Open);
                break;
        }
    }

    @Subscribe
    public void onEvent(OvenStatusChangedEvent event) {
        if (oven == null || !Objects.equal(oven.getID(), event.pojo.getID())) {
            return;
        }

        handler.sendEmptyMessage(PollStatus);
    }

    @Subscribe
    public void onEvent(OvenSpitRotateResetEvent event) {
        if (event.rotate == 1)
            imgRotate.setImageDrawable(getResources().getDrawable(R.mipmap.img_device_oven_rotate_open));
        else
            imgRotate.setImageDrawable(getResources().getDrawable(R.mipmap.img_device_oven_rotate_close));
    }

    @Subscribe
    public void onEvent(OvenLightResetEvent event) {
        if (event.light == 1)
            imgLight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_fan_light_selected));
        else
            imgLight.setImageDrawable(getResources().getDrawable(R.mipmap.ic_device_fan_light_normal));
    }


    @Subscribe
    public void onEvent(DeviceConnectionChangedEvent event) {
        if (oven == null || !Objects.equal(oven.getID(), event.device.getID()))
            return;

        UIService.getInstance().popBack();
    }

    @OnClick({R.id.txtCurrentTem, R.id.txtCurrentTime})
    public void onClickCurTem() {
        NormalModeItemMsg msg = new NormalModeItemMsg();
        String s = "预热";
        short type = oven.runP;
        if (type == 1) {
            s = "快热";
        } else if (type == 2) {
            s = "风焙烤";
        } else if (type == 3) {
            s = "焙烤";
        } else if (type == 4) {
            s = "底加热";
        } else if (type == 5) {
            s = "解冻";
        } else if (type == 6) {
            s = "风扇烤";
        } else if (type == 7) {
            s = "烧烤";
        } else if (type == 8) {
            s = "强烧烤";
        }
        msg.setType(s);
        PopupWindow pop = Helper.newOvenResetTwoSettingPicker(cx, new Callback2<NormalModeItemMsg>() {
            @Override
            public void onCompleted(final NormalModeItemMsg message) {
                Message msg = Message.obtain();
                msg.what = Reset;
                msg.obj = message;
                handler.sendMessage(msg);

            }
        }, msg);

        PopoupHelper.show(contentView, pop, Gravity.BOTTOM);
    }

    @OnClick(R.id.linSwitch)
    public void onClickSwitch() {
        String s = "结束烤箱工作";
        String s1 = " ";
        OvenCancelWorkDialog.Listener listener = new OvenCancelWorkDialog.Listener() {
            @Override
            public void onClick() {
                oven.setOvenStatus(OvenStatus.Off, new VoidCallback() {
                    @Override
                    public void onSuccess() {
//                        handler.sendEmptyMessage(Done);
//                        UIService.getInstance().popBack();
//                        if (fromSetting ) {
//                            UIService.getInstance().popBack();
//                        } else {
//                            UIService.getInstance().removePage(PageKey.DeviceOvenWorking);
//                        }DeviceOvenWorking
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        ToastUtils.showThrowable(t);
                    }
                });
            }
        };
        OvenCancelWorkDialog.show(getContext(), s, s1, listener);
    }

    @OnClick(R.id.imgReturn)
    public void onClickReturn() {
        UIService.getInstance().returnHome();
    }

    @OnClick(R.id.txtRecipe)
    public void onClickRecipe() {
        // TODO 跳转菜谱
    }


    @OnClick(R.id.imgContent)
    public void onClickContent() {
        if (oven.status == OvenStatus.Working) {
            handler.sendEmptyMessage(Pause);
        } else if (oven.status == OvenStatus.Pause) {
            handler.sendEmptyMessage(Working);
        }
    }

    @OnClick(R.id.rlLight)
    public void onClickLight() {
        light = oven.light;
        if (oven.status == OvenStatus.Working) {
            handler.sendEmptyMessage(Light);
        }
    }

    @OnClick(R.id.imgRotate)
    public void onClickRotate() {
        rotate = oven.revolve;
        if (oven.status == OvenStatus.Working) {
            handler.sendEmptyMessage(Rotate);
        }
    }

    private String getCurTem() {
        // 从硬件获取当前温度
        return String.valueOf(oven.temp);
    }

    private short getCurTime() {
        // 从硬件获取当前剩余时间
        return oven.time;
    }


// ---------------------------------------- 计时器代码段 -----------------------------------------

    /**
     * 倒计时单位时间
     */
    private Timer timer;

    private MyTimerTask timerTask;

    private void startTimer() {
        timer = new Timer();
        timerTask = new MyTimerTask();
        timer.scheduleAtFixedRate(timerTask, 1000 * 4, 30 * 1000);
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
//            txtCurrentTime.setText(TimeUtils.sec2clock(oven.time * 60));
//            txtCurrentTime.setText(TimeUtils.sec2clock(50 * 60));
            remainTime = oven.time;
            handler.sendEmptyMessage(RefreshTime);

        }

    }

    // ------------------------------------------  弹出对话框 ------------------------------

    private void showDialog(String s, short type) {
        dlg = new OvenBrokenDialog(cx);
        Window win = dlg.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.alpha = 0.6f;
        win.setAttributes(lp);
        dlg.setCanceledOnTouchOutside(false);
        imgSpinCircle.clearAnimation();
        imgPause.setVisibility(View.VISIBLE);
        dlg.setText(s);
        dlg.changeErrorText(type);
        dlg.show();
    }

    private void showAfterBuyDialog() {
        OvenBrokenDialog dlg = new OvenBrokenDialog(cx);
        Window win = dlg.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.alpha = 0.6f;
        win.setAttributes(lp);
        dlg.show();
    }

    // ------------------------------------------  监控传感器代码段-----------------------------------

    // TODO 创建广播，接收传感器信息，包括蒸汽炉未关、缺水以及传感器错误


    // SteamOvenWarningDialog.show(getContext, string, type);

    //----------------------------------------- 手动退出当前页面 ------------------------------------


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            UIService.getInstance().returnHome();
        }
        return true;
    }

}

