package com.legent.plat.services;

import android.content.Context;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.legent.Callback;
import com.legent.Helper;
import com.legent.VoidCallback;
import com.legent.events.ScreenPowerChangedEvent;
import com.legent.plat.Plat;
import com.legent.plat.R;
import com.legent.plat.events.DeviceAddedEvent;
import com.legent.plat.events.DeviceCollectionChangedEvent;
import com.legent.plat.events.DeviceConnectedNoticEvent;
import com.legent.plat.events.DeviceDeletedEvent;
import com.legent.plat.events.DeviceSelectedEvent;
import com.legent.plat.events.DeviceUpdatedEvent;
import com.legent.plat.events.UserLoginEvent;
import com.legent.plat.events.UserLogoutEvent;
import com.legent.plat.pojos.device.DeviceGroupInfo;
import com.legent.plat.pojos.device.DeviceInfo;
import com.legent.plat.pojos.device.IDevice;
import com.legent.plat.pojos.device.IDeviceHub;
import com.legent.services.ScreenPowerService;
import com.legent.utils.LogUtils;
import com.legent.utils.api.AlarmUtils;

import java.util.List;
import java.util.Map;

public class DeviceService extends AbsDeviceCloudService {

    private static DeviceService instance = new DeviceService();

    synchronized public static DeviceService getInstance() {
        return instance;
    }

    private DeviceService() {
    }

    protected long userId;
    protected long pollingPeriodInBack = 1000 * 30;
    protected long pollingPeriodInFront = 1000 * 2;
    protected long pollingPeriod = pollingPeriodInFront;
    protected IDevice defPojo;
    protected Map<Long, DeviceGroupInfo> mapGroups = Maps.newHashMap();
    protected Map<String, IDevice> mapDevice = Maps.newHashMap();
    protected Map<Long, List<IDevice>> mapTree = Maps.newHashMap();

    @Override
    public void init(Context cx, Object... params) {
        super.init(cx, params);
        startPolling(false);
    }

    @Override
    public void dispose() {
        super.dispose();
        stopPolling();
    }

    // -------------------------------------------------------------------------------
    // onEvent
    // -------------------------------------------------------------------------------

    @Subscribe
    public void onEvent(UserLoginEvent event) {
        userId = event.pojo.id;
    }

    @Subscribe
    public void onEvent(UserLogoutEvent event) {
        userId = 0;
    }

    @Subscribe
    public void onEvent(ScreenPowerChangedEvent event) {
        boolean isInBack = event.powerStatus == ScreenPowerService.OFF;

        LogUtils.logFIleWithTime("\n\n");
        LogUtils.logFIleWithTime("---------------------------------------------");
        LogUtils.logFIleWithTime(String.format("App 前后台切换:%s", isInBack ? "后台" : "前台"));
        LogUtils.logFIleWithTime("---------------------------------------------\n\n");

        startPolling(isInBack);
    }

    @Subscribe
    public void onEvent(DeviceConnectedNoticEvent event) {

        IDevice dev = queryById(event.deviceInfo.guid);
        if (dev == null)
            return;

        if (dev instanceof IDeviceHub) {
            IDeviceHub hub = (IDeviceHub) dev;
            hub.onChildrenChanged(event.deviceInfo.subDevices);
        }
    }

    // -------------------------------------------------------------------------------
    // device cache manager start
    // -------------------------------------------------------------------------------

    /**
     * 查询设备(含子设备)
     */
    public <T extends IDevice> T lookupChild(String guid) {
        for (IDevice dev : mapDevice.values()) {
            if (Objects.equal(dev.getID(), guid)) {
                return (T) dev;
            } else if (dev instanceof IDeviceHub) {
                IDeviceHub hub = (IDeviceHub) dev;
                IDevice child = hub.getChild(guid);
                if (child != null) {
                    return (T) child;
                }
            }
        }
        return null;
    }

    public List<DeviceGroupInfo> getGroups() {
        return Lists.newArrayList(mapGroups.values());
    }

    public List<IDevice> getDevicesByGroup(long groupId) {
        if (!mapTree.containsKey(groupId)) {
            List<IDevice> list = Lists.newArrayList();
            mapTree.put(groupId, list);
        }

        return mapTree.get(groupId);
    }

    public void batchAddGroup(List<DeviceGroupInfo> list) {
        if (list == null || list.size() == 0)
            return;

        for (DeviceGroupInfo g : list) {
            mapGroups.put(g.id, g);
        }
    }

    public void batchAdd(List<IDevice> list) {
        if (list == null || list.size() == 0)
            return;

        for (IDevice dev : list) {
            add(dev);
        }

        mappingGroup();
    }

    protected void mappingGroup() {
        mapTree.clear();
        List<IDevice> devices = queryAll();

        if (devices == null)
            return;

        for (IDevice device : devices) {
            if (device instanceof IDeviceHub) {
                IDeviceHub hub = (IDeviceHub) device;
                List<IDevice> list = getDevicesByGroup(hub.getGroupId());
                if (!list.contains(device)) {
                    list.add(device);
                }
            }
        }
    }


    public void addWithBind(final String guid, String name, boolean isOwner,
                            final VoidCallback callback) {

        bindDevice(userId, guid, name, isOwner, new VoidCallback() {

            @Override
            public void onSuccess() {
                getDeviceById(userId, guid, new Callback<DeviceInfo>() {

                    @Override
                    public void onSuccess(DeviceInfo deviceInfo) {

                        if (deviceInfo == null || Strings.isNullOrEmpty(deviceInfo.guid)) {
                            Helper.onFailure(callback, new Throwable("deviceInfo'guid is invalid"));
                            return;
                        }

                        if (Plat.deviceFactory != null) {
                            IDevice device = Plat.deviceFactory.generate(deviceInfo);
                            add(device);
                        }

                        Helper.onSuccess(callback);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Helper.onFailure(callback, t);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }

    public void deleteWithUnbind(final String guid, final VoidCallback callback) {

        unbindDevice(userId, guid, new VoidCallback() {

            @Override
            public void onSuccess() {
                IDevice device = queryById(guid);
                if (device != null) {
                    delete(device);
                }

                Helper.onSuccess(callback);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }
    // -------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------

    public boolean isEmpty() {
        return count() == 0;
    }

    public long count() {
        return mapDevice.size();
    }

    public boolean containsId(String id) {
        return mapDevice.containsKey(id);
    }

    public <T extends IDevice> boolean containsPojo(T t) {

        return containsId(t.getID());
    }

    public <T extends IDevice> T queryById(String id) {
        return (T) mapDevice.get(id);
    }

    public <T extends IDevice> T queryByIndex(int index) {
        List<IDevice> list = queryAll();
        if (list.size() > index)
            return (T) list.get(index);
        else
            return null;
    }

    public <T extends IDevice> List<T> queryDevices() {
        List<T> list = Lists.newArrayList();
        List<IDevice> all = queryAll();
        for (IDevice device : all) {
            list.add((T) device);
        }
        return list;
    }

    public List<IDevice> queryAll() {
        return Lists.newArrayList(mapDevice.values());
    }

    public <T extends IDevice> boolean add(T pojo) {
        if (containsPojo(pojo))
            return false;

        mapDevice.put(pojo.getID(), pojo);
        pojo.init(cx);

        onPojoAdded(pojo);
        return true;
    }

    public <T extends IDevice> boolean delete(T pojo) {
        if (!containsPojo(pojo))
            return false;

        mapDevice.remove(pojo.getID());
        pojo.dispose();

        onPojoDeleted(pojo);
        return true;
    }

    public <T extends IDevice> boolean update(T pojo) {
        if (!containsPojo(pojo))
            return false;

        mapDevice.put(pojo.getID(), pojo);
        onPojoUpdated(pojo);
        return true;
    }

    public void clear() {
        mapGroups.clear();
        mapTree.clear();

        for (IDevice dev : mapDevice.values()) {
            dev.dispose();
        }

        mapDevice.clear();


        onCollectionChanged();
    }


    public <T extends IDevice> T getDefault() {
        return (T) defPojo;
    }

    public <T extends IDevice> void setDefault(T t) {
        defPojo = t;
        onPojoSelected(defPojo);
    }

    protected <T extends IDevice> void onPojoAdded(T pojo) {
        onCollectionChanged();
        postEvent(new DeviceAddedEvent(pojo));
    }

    protected <T extends IDevice> void onPojoDeleted(T pojo) {
        onCollectionChanged();
        postEvent(new DeviceDeletedEvent(pojo));
    }

    protected <T extends IDevice> void onPojoUpdated(T pojo) {
        postEvent(new DeviceUpdatedEvent(pojo));
    }

    protected <T extends IDevice> void onPojoSelected(T pojo) {
        postEvent(new DeviceSelectedEvent(pojo));
    }

    protected void onCollectionChanged() {
        if (defPojo == null || !containsId(defPojo.getID())) {
            setInternalDefault();
        }

        postEvent(new DeviceCollectionChangedEvent(queryAll()));
    }

    protected void setInternalDefault() {
        if (count() > 0) {
            setDefault(queryByIndex(0));
        } else {
            setDefault(null);
        }
    }

    // -------------------------------------------------------------------------------
    // device cache manager end
    // -------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------
    // 设备轮询 start
    // -------------------------------------------------------------------------------

    public long getPollingPeriod() {
        return pollingPeriod;
    }

    public int getPollingTaskId() {
        return R.id.device_polling_task_id;
    }

    public void setPolltingPeriod(long periodInFront, long periodInBack) {
        this.pollingPeriodInFront = periodInFront < 1000 ? 1000 : periodInFront;
        this.pollingPeriodInBack = periodInBack < 1000 ? 1000 : periodInBack;
    }

    private void startPolling(boolean isInBack) {
        pollingPeriod = getPollingInterval(isInBack);

        AlarmUtils.startPollingWithBroadcast(cx,
                DevicePollingReceiver.getIntent(cx),
                pollingPeriod,
                getPollingTaskId());
    }

    private void stopPolling() {
        AlarmUtils.stopPollingWithBroadcast(cx,
                DevicePollingReceiver.getIntent(cx),
                getPollingTaskId());
    }

    protected long getPollingInterval(boolean isInBack) {
        return isInBack ? pollingPeriodInBack : pollingPeriodInFront;
    }

    // -------------------------------------------------------------------------------
    // 设备轮询 end
    // -------------------------------------------------------------------------------

}
