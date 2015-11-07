package com.robam.common.io.cloud;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legent.plat.pojos.AbsPostRequest;
import com.robam.common.pojos.CrmProduct;

import java.util.List;

/**
 * Created by sylar on 15/7/31.
 */
public interface Requests {

    class StoreRequest extends AbsPostRequest {
        @JsonProperty("storeId")
        public String storeId;

        public StoreRequest(String storeId) {
            this.storeId = storeId;
        }
    }

    class UserRequest extends AbsPostRequest {
        @JsonProperty("userId")
        public long userId;

        public UserRequest(long userId) {
            this.userId = userId;
        }
    }

    class UserBookRequest extends UserRequest {

        @JsonProperty("cookbookId")
        public long cookbookId;

        public UserBookRequest(long userId, long cookbookId) {
            super(userId);
            this.cookbookId = cookbookId;
        }
    }

    class GetCookbooksByTagRequest extends AbsPostRequest {
        @JsonProperty("cookbookTagId")
        public long cookbookTagId;

        public GetCookbooksByTagRequest(long cookbookTagId) {
            this.cookbookTagId = cookbookTagId;
        }
    }

    class GetCookbooksByNameRequest extends AbsPostRequest {
        @JsonProperty("name")
        public String name;

        public GetCookbooksByNameRequest(String name) {
            this.name = name;
        }
    }

    class UserMaterialRequest extends UserRequest {

        @JsonProperty("materialId")
        public long materialId;

        public UserMaterialRequest(long userId, long materialId) {
            super(userId);
            this.materialId = materialId;
        }
    }

    class GetCookAlbumsRequest extends UserBookRequest {

        @JsonProperty("start")
        public int start;

        @JsonProperty("limit")
        public int limit;

        public GetCookAlbumsRequest(long userId, long cookbookId, int start,
                                    int limit) {
            super(userId, cookbookId);
            this.start = start;
            this.limit = limit;
        }

    }

    class SubmitCookAlbumRequest extends UserBookRequest {

        @JsonProperty("image")
        public String image;

        @JsonProperty("desc")
        public String desc;

        public SubmitCookAlbumRequest(long userId, long cookbookId,
                                      String image, String desc) {
            super(userId, cookbookId);
            this.image = image;
            this.desc = desc;
        }

    }

    class CookAlbumRequest extends UserRequest {
        @JsonProperty("albumId")
        public long albumId;

        public CookAlbumRequest(long userId, long albumId) {
            super(userId);
            this.albumId = albumId;
        }
    }

    class ApplyAfterSaleRequest extends UserRequest {

        @JsonProperty("deviceGuid")
        public String guid;

        public ApplyAfterSaleRequest(long userId, String deviceGuid) {
            super(userId);
            this.guid = deviceGuid;
        }
    }

    class GetSmartParamsRequest extends UserRequest {

        @JsonProperty("deviceGuid")
        public String guid;

        public GetSmartParamsRequest(long userId, String deviceGuid) {
            super(userId);
            this.guid = deviceGuid;
        }
    }

    class SetSmartParamsByDailyRequest extends UserRequest {

        @JsonProperty("deviceGuid")
        public String guid;

        @JsonProperty("on")
        public boolean enable;

        @JsonProperty("day")
        public int day;

        public SetSmartParamsByDailyRequest(long userId, String guid,
                                            boolean enable, int day) {
            super(userId);
            this.guid = guid;
            this.enable = enable;
            this.day = day;
        }
    }

    class SetSmartParamsByWeeklyRequest extends
            SetSmartParamsByDailyRequest {

        @JsonProperty("time")
        public String time;

        public SetSmartParamsByWeeklyRequest(long userId, String guid,
                                             boolean enable, int day, String time) {
            super(userId, guid, enable, day);
            this.time = time;
        }

    }


    class GetSmartParams360Request extends UserRequest {

        @JsonProperty("deviceGuid")
        public String guid;

        public GetSmartParams360Request(long userId, String deviceGuid) {
            super(userId);
            this.guid = deviceGuid;
        }
    }


    class SetSmartParams360Request extends GetSmartParams360Request {

        @JsonProperty("switch")
        public boolean switchStatus;

        public SetSmartParams360Request(long userId, String deviceGuid, boolean status) {
            super(userId, deviceGuid);
            this.switchStatus = status;
        }
    }

    class CookingLogRequest extends UserBookRequest {

        @JsonProperty("deviceGuid")
        public String deviceId;

        @JsonProperty("startTime")
        public long start;

        @JsonProperty("endTime")
        public long end;

        @JsonProperty("finishType")
        public int finishType;

        public CookingLogRequest(long userId, long cookbookId, String deviceId,
                                 long start, long end, boolean isBroken) {
            super(userId, cookbookId);
            this.deviceId = deviceId;
            this.start = start;
            this.end = end;
            this.finishType = isBroken ? 2 : 1;
        }
    }

    class UserOrderRequest extends UserRequest {
        @JsonProperty
        public long orderId;

        public UserOrderRequest(long userId, long orderId) {
            super(userId);
            this.orderId = orderId;
        }
    }

    class SaveCustomerInfoRequest extends UserRequest {

        @JsonProperty()
        public String name;
        @JsonProperty()
        public String phone;
        @JsonProperty()
        public String city;
        @JsonProperty()
        public String address;


        public SaveCustomerInfoRequest(long userId, String name, String phone, String city, String address) {
            super(userId);
            this.name = name;
            this.phone = phone;
            this.city = city;
            this.address = address;
        }
    }

    class SubmitOrderRequest extends UserRequest {
        @JsonProperty("cookbooks")
        public List<Long> recipeIds;

        public SubmitOrderRequest(long userId, List<Long> ids) {
            super(userId);
            this.recipeIds = ids;
        }
    }

    class QueryOrderRequest extends UserRequest {
        @JsonProperty()
        public long time;
        @JsonProperty()
        public int limit;

        public QueryOrderRequest(long userId, long time, int limit) {
            super(userId);
            this.time = time;
            this.limit = limit;
        }
    }

    class UpdateOrderContacterRequest extends SaveCustomerInfoRequest {

        @JsonProperty()
        public long orderId;

        public UpdateOrderContacterRequest(long userId, long orderId, String name, String phone, String city, String address) {
            super(userId, name, phone, city, address);
            this.orderId = orderId;
        }
    }

    class GetOrderRequest extends AbsPostRequest {
        @JsonProperty()
        public long orderId;

        public GetOrderRequest(long orderId) {
            this.orderId = orderId;
        }
    }

    class GetGroudingRecipesRequest extends AbsPostRequest {

        @JsonProperty()
        public int start;
        @JsonProperty()
        public int limit;

        public GetGroudingRecipesRequest(int start, int limit) {
            this.start = start;
            this.limit = limit;
        }
    }

    class GetCrmCustomerRequest extends AbsPostRequest {
        @JsonProperty()
        public String phone;

        public GetCrmCustomerRequest(String phone) {
            this.phone = phone;
        }
    }

    class SubmitMaintainRequest extends UserRequest {
        @JsonProperty()
        public String customerId;

        @JsonProperty()
        public String customerName;

        @JsonProperty()
        public String phone;

        @JsonProperty()
        public String address;

        @JsonProperty()
        public String category;

        @JsonProperty()
        public String productType;

        @JsonProperty()
        public String productId;

        @JsonProperty()
        public String buyTime;

        @JsonProperty()
        public long bookTime;

        @JsonProperty()
        public String province;

        @JsonProperty()
        public String city;

        @JsonProperty()
        public String county;


        public SubmitMaintainRequest(long userId, CrmProduct product, long bookTime, String customerId, String customerName, String phone, String province, String city, String county, String address) {
            super(userId);

            this.productId = product.id;
            this.category = product.category;
            this.productType = product.type;
            this.productId = product.id;
            this.buyTime = product.buyTime;
            //
            this.customerId = customerId;
            this.customerName = customerName;
            this.bookTime = bookTime;
            this.phone = phone;
            this.province = province;
            this.city = city;
            this.county = county;
            this.address = address;
        }
    }

    class QueryMaintainRequest extends UserRequest {
        public QueryMaintainRequest(long userId) {
            super(userId);
        }
    }
}
