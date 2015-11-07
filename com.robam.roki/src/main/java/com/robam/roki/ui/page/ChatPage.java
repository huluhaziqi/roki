package com.robam.roki.ui.page;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.Subscribe;
import com.legent.Callback;
import com.legent.Helper;
import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.events.ChatReceivedMsgEvent;
import com.legent.plat.pojos.ChatMsg;
import com.legent.plat.pojos.User;
import com.legent.plat.services.ChatService;
import com.legent.ui.ext.HeadPage;
import com.legent.ui.ext.adapters.ExtBaseAdapter;
import com.legent.utils.api.ToastUtils;
import com.legent.utils.graphic.ImageUtils;
import com.robam.roki.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by sylar on 15/6/14.
 */
public class ChatPage extends HeadPage {

    @InjectView(R.id.listview)
    ListView listview;
    @InjectView(R.id.edtSend)
    EditText edtSend;
    @InjectView(R.id.txtSend)
    TextView txtSend;

    Adapter adapter;
    ChatService cs = ChatService.getInstance();

    @Override
    protected View onCreateContentView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = layoutInflater.inflate(R.layout.page_chat, viewGroup, false);
        ButterKnife.inject(this, view);

        adapter = new Adapter();
        listview.setAdapter(adapter);

        final Date now = Calendar.getInstance().getTime();
        queryHistory(now, new VoidCallback() {

            @Override
            public void onSuccess() {
                cs.updateLastTime(now);
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        cs.startReceiveMsg();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        cs.stopReceiveMsg();
    }

    @OnClick(R.id.txtSend)
    public void onClickSend() {
        try {
            sendMsg();
        } catch (Exception ex) {
            ToastUtils.showShort(ex.getMessage());
        }
    }


    @Subscribe
    public void onEvent(ChatReceivedMsgEvent event) {
        List<ChatMsg> subList = event.msgList;
        adapter.addMsgList(subList);
    }

    void queryHistory(Date date, final VoidCallback callback) {

        cs.queryBefore(date, new Callback<List<ChatMsg>>() {

            @Override
            public void onSuccess(List<ChatMsg> result) {
                if (result != null) {
                    adapter.addMsgList(result);
                }

                Helper.onSuccess(callback);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });

    }

    void sendMsg() {

        final String content = edtSend.getText().toString();
        if (Strings.isNullOrEmpty(content))
            return;

        cs.sendMsg(content, new VoidCallback() {

            @Override
            public void onSuccess() {
                ChatMsg msg = ChatMsg.newSendMsg(content);
                edtSend.setText(null);
                adapter.appendMsg(msg);
            }

            @Override
            public void onFailure(Throwable t) {
                ToastUtils.showThrowable(t);
            }
        });

    }

    class Adapter extends ExtBaseAdapter<ChatMsg> {

        SimpleDateFormat SDF = new SimpleDateFormat("HH年MM月dd日 HH:mm");


        long lastTimeInMillis = 0;
        long earlyTimeInMillis;
        Ordering<ChatMsg> ordering = Ordering.natural().nullsFirst()
                .onResultOf(new Function<ChatMsg, Long>() {
                    public Long apply(ChatMsg foo) {
                        return foo.postTime;
                    }
                });

        public void addMsgList(List<ChatMsg> newList) {
            if (newList == null || newList.size() == 0)
                return;

            for (ChatMsg msg : newList) {
                if (!list.contains(msg)) {
                    list.add(msg);
                }
            }

            list = ordering.sortedCopy(list);
            notifyDataSetChanged();

            if (list.size() > 0) {
                long last = list.get(list.size() - 1).getPostTime();
                long early = list.get(0).getPostTime();

                if (last > lastTimeInMillis) {
                    listview.setSelection(getCount() - 1);
                } else if (early < earlyTimeInMillis) {
                    listview.setSelection(0);
                }

                earlyTimeInMillis = early;
                lastTimeInMillis = last;
            }
        }


        public void appendMsg(ChatMsg msg) {
            List<ChatMsg> list = Lists.newArrayList();
            list.add(msg);

            addMsgList(list);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ChatMsg msg = list.get(position);
            boolean isIncoming = msg.isIncoming();

            convertView = LayoutInflater.from(cx).inflate(isIncoming ? R.layout.view_chat_left
                    : R.layout.view_chat_right, parent, false);
            ViewHolder vh = new ViewHolder(convertView);
            vh.showData(msg, position);

            return convertView;
        }

        class ViewHolder {
            final static int ChatTimeSpan = 1000 * 60 * 3;

            @InjectView(R.id.txtSendTime)
            TextView txtSendTime;
            @InjectView(R.id.imgFigure)
            ImageView imgFigure;
            @InjectView(R.id.txtUserName)
            TextView txtUserName;
            @InjectView(R.id.txtMsg)
            TextView txtMsg;

            ViewHolder(View view) {
                ButterKnife.inject(this, view);
                view.setTag(this);
            }

            void showData(ChatMsg msg, int position) {
                boolean isJust = isJustNow(position);
                txtSendTime.setText(SDF.format(new Date(msg.postTime)));
                txtSendTime.setVisibility(isJust ? View.GONE : View.VISIBLE);
                txtMsg.setText(msg.msg);

                if (!msg.isIncoming()) {
                    User user = Plat.accountService.getCurrentUser();
                    txtUserName.setText(user.name);
                    ImageUtils.displayImage(user.figureUrl, imgFigure,
                            com.robam.roki.ui.Helper.DisplayImageOptions_UserFace);
                }

            }

            boolean isJustNow(int position) {
                if (position <= 0 || position >= list.size())
                    return false;
                ChatMsg curMsg = list.get(position);
                ChatMsg prefMsg = list.get(position - 1);

                long span = curMsg.getPostTime() - prefMsg.getPostTime();
                return span < ChatTimeSpan;
            }

        }
    }
}
