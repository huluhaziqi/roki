package com.legent.plat.io.cloud;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legent.plat.pojos.AppVersionInfo;
import com.legent.plat.pojos.ChatMsg;
import com.legent.plat.pojos.RCReponse;
import com.legent.plat.pojos.User;
import com.legent.plat.pojos.device.DeviceGroupInfo;
import com.legent.plat.pojos.device.DeviceInfo;

import java.util.List;

/**
 * Created by sylar on 15/7/23.
 */
public interface Reponses {

    // ==========================================================Common Start==========================================================


    class GetAppIdReponse extends RCReponse {

        @JsonProperty("appGuid")
        public String appGuid;

    }

    class CheckAppVerReponse extends RCReponse {

        @JsonProperty("ver")
        public AppVersionInfo verInfo;

    }


    class GetStartImagesResponse extends RCReponse {

        @JsonProperty("images")
        public List<String> images;

    }

    class ChatGetReponse extends RCReponse {

        @JsonProperty("msgs")
        public List<ChatMsg> msgList;

    }

    class ChatSendReponse extends RCReponse {

        @JsonProperty("id")
        public long id;

        @JsonProperty("time")
        public long time;

    }


    class ChatisExistResponse extends RCReponse {

        @JsonProperty("existed")
        public boolean existed;

    }
    // ==========================================================Common End==========================================================
    // ==========================================================User Start==========================================================

    class IsExistedResponse extends RCReponse {

        @JsonProperty("existed")
        public boolean existed;
    }

    class LoginReponse extends RCReponse {

        @JsonProperty("tgt")
        public String tgt;

        @JsonProperty("user")
        public User user;
    }

    class GetUserReponse extends RCReponse {

        @JsonProperty("user")
        public User user;
    }

    class UpdateFigureReponse extends RCReponse {

        @JsonProperty("figureUrl")
        public String figureUrl;
    }

    class GetVerifyCodeReponse extends RCReponse {

        @JsonProperty("verifyCode")
        public String verifyCode;

    }

    class GetDynamicPwdRequestReponse extends RCReponse {
        @JsonProperty
        public String dynamicPwd;
    }


    // ==========================================================User End==========================================================
    // ==========================================================Device Start==========================================================

    class AddDeviceGroupResponse extends RCReponse {

        @JsonProperty("groupId")
        public long groupId;
    }

    class GetDeviceGroupsResponse extends RCReponse {

        @JsonProperty("deviceGroups")
        public List<DeviceGroupInfo> deviceGroups;
    }

    class GetDevicesResponse extends RCReponse {

        @JsonProperty("devices")
        public List<DeviceInfo> devices;
    }

    class GetDevicePesponse extends RCReponse {

        @JsonProperty("device")
        public DeviceInfo device;
    }

    class GetDeviceUsersResponse extends RCReponse {

        @JsonProperty("users")
        public List<User> users;

    }

    class GetSnForDeviceResponse extends RCReponse {

        @JsonProperty("sn")
        public String sn;

    }
    // ==========================================================Device End==========================================================

}
