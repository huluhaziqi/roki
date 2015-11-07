package com.legent.plat.pojos.device;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legent.pojos.IJsonPojo;
import com.legent.pojos.AbsKeyPojo;
import com.legent.utils.JsonUtils;

public class SubDeviceInfo extends AbsKeyPojo<String> implements IJsonPojo, Parcelable {

    /**
     * 唯一编码
     */
    @JsonProperty("guid")
    public String guid;

    /**
     * 设备名称
     */
    @JsonProperty("name")
    public String name;

    /**
     * 业务编码（供应商定制ID）
     */
    @JsonProperty("bid")
    public String bid;

    /**
     * 设备固件版本
     */
    @JsonProperty("ver")
    public int ver;

    public int mcuType;
    public boolean isConnected;

    public SubDeviceInfo() {
    }

    public SubDeviceInfo(String guid, String bid) {
        this();
        this.guid = guid;
        this.bid = bid;
    }

    @Override
    public String getID() {
        return guid;
    }

    @Override
    public String getName() {
        return name;
    }

    public DeviceGuid getDeviceGuid() {
        return DeviceGuid.newGuid(guid);
    }

    @Override
    public String toString() {
        try {
            return JsonUtils.pojo2Json(this);
        } catch (Exception e) {
            e.printStackTrace();
            return guid;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.guid);
        dest.writeString(this.name);
        dest.writeString(this.bid);
        dest.writeInt(this.ver);
        dest.writeInt(this.mcuType);
        dest.writeByte(isConnected ? (byte) 1 : (byte) 0);
    }

    private SubDeviceInfo(Parcel in) {
        this.guid = in.readString();
        this.name = in.readString();
        this.bid = in.readString();
        this.ver = in.readInt();
        this.mcuType = in.readInt();
        this.isConnected = in.readByte() != 0;
    }

    public static final Creator<SubDeviceInfo> CREATOR = new Creator<SubDeviceInfo>() {
        public SubDeviceInfo createFromParcel(Parcel source) {
            return new SubDeviceInfo(source);
        }

        public SubDeviceInfo[] newArray(int size) {
            return new SubDeviceInfo[size];
        }
    };
}
