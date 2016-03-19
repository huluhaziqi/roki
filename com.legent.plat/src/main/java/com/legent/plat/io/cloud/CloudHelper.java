package com.legent.plat.io.cloud;

import com.legent.Helper;
import com.legent.Callback;
import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.io.RCRetrofitCallback;
import com.legent.plat.io.RCRetrofitCallbackWithVoid;
import com.legent.plat.io.cloud.Reponses.*;
import com.legent.plat.io.cloud.Requests.*;
import com.legent.plat.pojos.AppVersionInfo;
import com.legent.plat.pojos.ChatMsg;
import com.legent.plat.pojos.RCReponse;
import com.legent.plat.pojos.User;
import com.legent.plat.pojos.device.DeviceGroupInfo;
import com.legent.plat.pojos.device.DeviceInfo;
import com.legent.services.RestfulService;
import com.legent.utils.api.PackageUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by sylar on 15/7/23.
 */
public class CloudHelper {

    static ICloudService svr = getRestfulApi(ICloudService.class);

    static public <T> T getRestfulApi(Class<T> apiClazz) {
        return RestfulService.getInstance().createApi(apiClazz);
    }

    // ==========================================================Common Start==========================================================

    static public String getAppGuid(String appType, String token) {
        GetAppIdReponse res = svr.getAppId(new GetAppIdRequest(appType, token));
        return res != null ? res.appGuid : null;
    }

    static public void getAppGuid(String appType, String token,
                                  final Callback<String> callback) {
        svr.getAppId(new GetAppIdRequest(appType, token),
                new RCRetrofitCallback<GetAppIdReponse>(callback) {
                    @Override
                    protected void afterSuccess(GetAppIdReponse result) {
                        callback.onSuccess(result.appGuid);
                    }
                });
    }

    static public void bindAppGuidAndUser(String appGuid, long userId,
                                          final VoidCallback callback) {
        svr.bindAppGuidAndUser(new AppUserGuidRequest(appGuid, userId),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void unbindAppGuidAndUser(String appGuid, long userId,
                                            final VoidCallback callback) {
        svr.unbindAppGuidAndUser(new AppUserGuidRequest(appGuid, userId),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void checkAppVersion(String appType,
                                       final Callback<AppVersionInfo> callback) {
        svr.checkAppVersion(new AppTypeRequest(appType),
                new RCRetrofitCallback<CheckAppVerReponse>(callback) {
                    @Override
                    protected void afterSuccess(CheckAppVerReponse result) {
                        callback.onSuccess(result.verInfo);
                    }
                });
    }

    static public void reportLog(String appGuid, int logType, String log,
                                 VoidCallback callback) {
        int verCode = PackageUtils.getAppVersionCode(Plat.app);
        svr.reportLog(new ReportLogRequest(appGuid, verCode, logType, log),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void getStartImages(String appType,
                                      final Callback<List<String>> callback) {
        svr.getStartImages(new AppTypeRequest(appType),
                new RCRetrofitCallback<GetStartImagesResponse>(
                        callback) {
                    @Override
                    protected void afterSuccess(GetStartImagesResponse result) {
                        callback.onSuccess(result.images);
                    }
                });
    }

    static public void sendChatMsg(long userId, String msg,
                                   final Callback<ChatSendReponse> callback) {
        svr.sendChatMsg(new ChatSendRequest(userId, msg), new RCRetrofitCallback<ChatSendReponse>(callback) {
            @Override
            protected void afterSuccess(ChatSendReponse result) {
                Helper.onSuccess(callback, result);
            }
        });
    }

    static public void getChatBefore(long userId, Date date, int count,
                                     final Callback<List<ChatMsg>> callback) {
        svr.getChatBefore(new ChatGetRequest(userId, date, count),
                new RCRetrofitCallback<ChatGetReponse>(callback) {
                    @Override
                    protected void afterSuccess(ChatGetReponse result) {
                        callback.onSuccess(result.msgList);
                    }
                });
    }

    static public void getChatAfter(long userId, Date date, int count,
                                    final Callback<List<ChatMsg>> callback) {
        svr.getChatAfter(new ChatGetRequest(userId, date, count),
                new RCRetrofitCallback<ChatGetReponse>(callback) {
                    @Override
                    protected void afterSuccess(ChatGetReponse result) {
                        callback.onSuccess(result.msgList);
                    }
                });
    }

    static public void isExistChatMsg(long userId, Date date,
                                      final Callback<Boolean> callback) {
        svr.isExistChatMsg(new ChatisExistRequest(userId, date),
                new RCRetrofitCallback<ChatisExistResponse>(callback) {
                    @Override
                    protected void afterSuccess(ChatisExistResponse result) {
                        callback.onSuccess(result.existed);
                    }
                });
    }

    // ==========================================================User Start==========================================================
    static public void isExisted(String account,
                                 final Callback<Boolean> callback) {
        svr.isExisted(new AccountRequest(account),
                new RCRetrofitCallback<IsExistedResponse>(callback) {
                    @Override
                    protected void afterSuccess(IsExistedResponse result) {
                        callback.onSuccess(result.existed);
                    }
                });

    }

    static public void registByPhone(String phone, String nickname,
                                     String password, String figure, boolean gender, String verifyCode,
                                     VoidCallback callback) {
        svr.registByPhone(new RegistByPhoneRequest(phone, nickname, password,
                        figure, gender, verifyCode),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void registByEmail(String email, String nickname,
                                     String password, String figure, boolean gender,
                                     VoidCallback callback) {
        svr.registByEmail(new RegistByEmailRequest(email, nickname, password,
                figure, gender), new RCRetrofitCallbackWithVoid<RCReponse>(
                callback));

    }

    static public void login(String account, String password,
                             final Callback<User> callback) {

        svr.login(new LoginRequest(account, password),
                new RCRetrofitCallback<LoginReponse>(callback) {
                    @Override
                    protected void afterSuccess(LoginReponse result) {
                        result.user.TGT = result.tgt;
                        callback.onSuccess(result.user);
                    }
                });

    }

    static public void expressLogin(String phone, String verifyCode,
                                    final Callback<User> callback) {

        svr.expressLogin(new ExpressLoginRequest(phone, verifyCode),
                new RCRetrofitCallback<LoginReponse>(callback) {
                    @Override
                    protected void afterSuccess(LoginReponse result) {
                        result.user.TGT = result.tgt;
                        callback.onSuccess(result.user);
                    }
                });

    }

    static public void logout(String tgt) {
        svr.logout(new LogoutRequest(tgt),
                new RCRetrofitCallbackWithVoid<RCReponse>(null));
    }

    static public void getUser(long userId, final Callback<User> callback) {
        svr.getUser(new UserRequest(userId),
                new RCRetrofitCallback<GetUserReponse>(callback) {
                    @Override
                    protected void afterSuccess(GetUserReponse result) {
                        callback.onSuccess(result.user);
                    }
                });
    }

    static public void updateUser(long id, String name, String phone,
                                  String email, boolean gender, VoidCallback callback) {
        svr.updateUser(new UpdateUserRequest(id, name, phone, email, gender),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void updatePassword(long userId, String oldPwd,
                                      String newPwd, VoidCallback callback) {
        svr.updatePassword(new UpdatePasswordRequest(userId, oldPwd, newPwd),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void updateFigure(long userId, String figure,
                                    final Callback<String> callback) {
        svr.updateFigure(
                new UpdateFigureRequest(userId, figure),
                new RCRetrofitCallback<UpdateFigureReponse>(callback) {
                    @Override
                    protected void afterSuccess(UpdateFigureReponse result) {
                        callback.onSuccess(result.figureUrl);
                    }
                });
    }

    static public void getVerifyCode(String phone,
                                     final Callback<String> callback) {
        svr.getVerifyCode(
                new GetVerifyCodeRequest(phone),
                new RCRetrofitCallback<GetVerifyCodeReponse>(callback) {
                    @Override
                    protected void afterSuccess(GetVerifyCodeReponse result) {
                        callback.onSuccess(result.verifyCode);
                    }
                });
    }

    static public void getDynamicPwd(String phone, final Callback<String> callback) {
        svr.getDynamicPwd(new GetVerifyCodeRequest(phone),
                new RCRetrofitCallback<GetDynamicPwdRequestReponse>(callback) {
                    @Override
                    protected void afterSuccess(GetDynamicPwdRequestReponse result) {
                        callback.onSuccess(result.dynamicPwd);
                    }
                });
    }

    static public void resetPasswordByPhone(String phone, String newPwd,
                                            String verifyCode, VoidCallback callback) {
        svr.resetPasswordByPhone(new ResetPwdByPhoneRequest(phone, newPwd,
                verifyCode), new RCRetrofitCallbackWithVoid<RCReponse>(
                callback));
    }

    static public void resetPasswordByEmail(String email, VoidCallback callback) {
        svr.resetPasswordByEmail(new ResetPwdByEmailRequest(email),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void loginFrom3rd(int platId, String userId3rd,
                                    String nickname, String figureUrl, String token,
                                    final Callback<User> callback) {
        svr.loginFrom3rd(new LoginFrom3rdRequest(platId, userId3rd, nickname,
                        figureUrl, token),
                new RCRetrofitCallback<LoginReponse>(callback) {
                    @Override
                    protected void afterSuccess(LoginReponse result) {
                        callback.onSuccess(result.user);
                    }
                });
    }

    static public void bind3rd(long userId, int platType, String thirdId,
                               String nickname, VoidCallback callback) {
        svr.bind3rd(new Bind3rdRequest(userId, platType, thirdId, nickname),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void unbind3rd(long userId, int platType,
                                 VoidCallback callback) {
        svr.unbind3rd(new Unbind3rdRequest(userId, platType),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));

    }
    // ==========================================================Device Start==========================================================


    static public void getDeviceGroups(long userId,
                                       final Callback<List<DeviceGroupInfo>> callback) {
        svr.getDeviceGroups(new UserRequest(userId),
                new RCRetrofitCallback<GetDeviceGroupsResponse>(
                        callback) {
                    @Override
                    protected void afterSuccess(GetDeviceGroupsResponse result) {
                        callback.onSuccess(result.deviceGroups);
                    }
                });
    }

    static public void addDeviceGroup(long userId, String groupName,
                                      final Callback<Long> callback) {
        svr.addDeviceGroup(new AddDeviceGroupRequest(userId, groupName),
                new RCRetrofitCallback<AddDeviceGroupResponse>(
                        callback) {
                    @Override
                    protected void afterSuccess(AddDeviceGroupResponse result) {
                        callback.onSuccess(result.groupId);
                    }
                });

    }

    static public void deleteDeviceGroup(long userId, long groupId,
                                         final VoidCallback callback) {
        svr.deleteDeviceGroup(new UserGroupRequest(userId, groupId),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));

    }

    static public void updateDeviceGroupName(long userId, long groupId,
                                             String groupName, VoidCallback callback) {
        svr.updateDeviceGroupName(new UpdateGroupNameRequest(userId, groupId,
                groupName), new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void addDeviceToGroup(long userId, long groupId, String guid,
                                        VoidCallback callback) {
        svr.addDeviceToGroup(new UserGroupGuidRequest(userId, groupId, guid),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void deleteDeviceFromGroup(long userId, long groupId,
                                             String guid, VoidCallback callback) {
        svr.deleteDeviceFromGroup(new UserGroupGuidRequest(userId, groupId,
                guid), new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void clearDeviceByGroup(long userId, long groupId,
                                          VoidCallback callback) {
        svr.clearDeviceByGroup(new UserGroupRequest(userId, groupId),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    // ----------------------------------------------------------------

    static public void getDevices(long userId,
                                  final Callback<List<DeviceInfo>> callback) {

        svr.getDevices(new UserRequest(userId),
                new RCRetrofitCallback<GetDevicesResponse>(callback) {
                    protected void afterSuccess(GetDevicesResponse result) {
                        callback.onSuccess(result.devices);
                    }
                });

    }

    static public void getDeviceById(String guid,
                                     final Callback<DeviceInfo> callback) {
        svr.getDeviceById(new GuidRequest(guid),
                new RCRetrofitCallback<GetDevicePesponse>(callback) {
                    @Override
                    protected void afterSuccess(GetDevicePesponse result) {
                        callback.onSuccess(result.device);
                    }
                });
    }

    static public void getDeviceBySn(String sn, final Callback<DeviceInfo> callback) {
        svr.getDeviceBySn(new GetDeviceBySnRequest(sn),
                new RCRetrofitCallback<GetDevicePesponse>(callback) {
                    @Override
                    protected void afterSuccess(GetDevicePesponse result) {
                        callback.onSuccess(result.device);
                    }
                });
    }

    static public void updateDeviceName(long userId, String guid, String name,
                                        VoidCallback callback) {
        svr.updateDeviceName(new UpdateDeviceNameRequest(userId, guid, name),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void bindDevice(long userId, String guid, String name,
                                  boolean isOwner, VoidCallback callback) {
        svr.bindDevice(new BindDeviceRequest(userId, guid, name, isOwner),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void unbindDevice(long userId, String guid,
                                    VoidCallback callback) {
        svr.unbindDevice(new UnbindDeviceRequest(userId, guid),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void getSnForDevice(long userId, String guid,
                                      final Callback<String> callback) {
        svr.getSnForDevice(new UserGuidRequest(userId, guid),
                new RCRetrofitCallback<GetSnForDeviceResponse>(
                        callback) {
                    @Override
                    protected void afterSuccess(GetSnForDeviceResponse result) {
                        callback.onSuccess(result.sn);
                    }
                });
    }

    static public void getDeviceUsers(long userId, String guid,
                                      final Callback<List<User>> callback) {
        svr.getDeviceUsers(new UserGuidRequest(userId, guid),
                new RCRetrofitCallback<GetDeviceUsersResponse>(
                        callback) {
                    @Override
                    protected void afterSuccess(GetDeviceUsersResponse result) {
                        callback.onSuccess(result.users);
                    }
                });
    }

    static public void deleteDeviceUsers(long userId, String guid,
                                         List<Long> userIds, VoidCallback callback) {
        svr.deleteDeviceUsers(new DeleteDeviceUsersRequest(userId, guid,
                userIds), new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }
}
