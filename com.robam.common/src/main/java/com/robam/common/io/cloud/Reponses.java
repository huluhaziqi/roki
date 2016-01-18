package com.robam.common.io.cloud;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.legent.plat.pojos.RCReponse;
import com.legent.pojos.IJsonPojo;
import com.robam.common.pojos.Advert;
import com.robam.common.pojos.AdvertImage;
import com.robam.common.pojos.CookAlbum;
import com.robam.common.pojos.CrmCustomer;
import com.robam.common.pojos.Group;
import com.robam.common.pojos.MaintainInfo;
import com.robam.common.pojos.MaterialFrequency;
import com.robam.common.pojos.Materials;
import com.robam.common.pojos.OrderContacter;
import com.robam.common.pojos.OrderInfo;
import com.robam.common.pojos.Recipe;
import com.robam.common.pojos.Recipe3rd;
import com.robam.common.pojos.RecipeProvider;

import java.util.List;

/**
 * Created by sylar on 15/7/31.
 */
public interface Reponses {

    class StoreVersionResponse extends RCReponse {
        @JsonProperty("version")
        public int version;
    }

    class StoreCategoryResponse extends RCReponse {
        @JsonProperty("cookbookTagGroups")
        public List<Group> groups;
    }

    class CookbookResponse extends RCReponse {
        @JsonProperty("cookbook")
        public Recipe cookbook;
    }

    class Cookbook3rdResponse extends RCReponse {
        @JsonProperty("cookbook")
        public Recipe3rd cookbook;
    }

    class ThumbCookbookResponse extends RCReponse {
        @JsonProperty("cookbooks")
        public List<Recipe> cookbooks;
    }

    class CookbooksResponse extends RCReponse {

        @JsonProperty("cookbooks")
        public List<Recipe> cookbooks;

        @JsonProperty("cookbook_3rds")
        public List<Recipe3rd> cookbooks3rd;

        public int count() {
            int res = 0;
            if (cookbooks != null)
                res += cookbooks.size();
            if (cookbooks3rd != null)
                res += cookbooks3rd.size();

            return res;
        }
    }

    class MaterialFrequencyResponse extends RCReponse {

        @JsonProperty("accessorys")
        public List<MaterialFrequency> list;

    }

    class MaterialsResponse extends RCReponse {

        @JsonProperty("materials")
        public Materials materials;

    }

    class CookbookProviderResponse extends RCReponse {

        @JsonProperty("sources")
        public List<RecipeProvider> providers;

    }

    class HotKeysForCookbookResponse extends RCReponse {

        @JsonProperty("cookbooks")
        public List<String> hotKeys;

    }

    class AlbumResponse extends RCReponse {

        @JsonProperty("album")
        public CookAlbum album;

    }

    class AlbumsResponse extends RCReponse {

        @JsonProperty("albums")
        public List<CookAlbum> cookAlbums;

    }

    class HomeAdvertsForMobResponse extends RCReponse {

        @JsonProperty("images")
        public List<Advert.MobAdvert> adverts;

    }

    class HomeTitleForMobResponse extends RCReponse {

        @JsonProperty("images")
        public List<Advert.MobAdvert> titles;

    }

    class HomeAdvertsForPadResponse extends RCReponse {

        @JsonProperty("left")
        public List<Advert.PadAdvert> left;

        @JsonProperty("middle")
        public List<Advert.PadAdvert> middle;

    }

    class CookbookImageReponse extends RCReponse {

        @JsonProperty("images")
        public List<AdvertImage> images;

    }

    class SmartParamsReponse extends RCReponse {

        @JsonProperty("daily")
        public Daily daily;

        @JsonProperty("weekly")
        public Weekly weekly;

        class Daily implements IJsonPojo {

            @JsonProperty("on")
            public boolean enable;

            @JsonProperty("day")
            public int day;
        }

        class Weekly extends Daily {

            @JsonProperty("time")
            public String time;
        }
    }

    class GetSmartParams360Reponse extends RCReponse {

        @JsonProperty("switch")
        public boolean switchStatus;

    }

    class GetCustomerInfoReponse extends RCReponse {

        @JsonProperty("customer")
        public OrderContacter customer;

    }

    class QueryOrderReponse extends RCReponse {

        @JsonProperty()
        public List<OrderInfo> orders;

    }

    class OrderIfOpenReponse extends RCReponse {

        @JsonProperty()
        public boolean open;
    }

    class EventStatusReponse extends RCReponse {

        @JsonProperty()
        public String image;
        @JsonProperty()
        public int status;
    }

    class SubmitOrderReponse extends RCReponse {
        @JsonProperty()
        public long orderId;

    }

    class GetOrderReponse extends RCReponse {
        @JsonProperty()
        public OrderInfo order;
    }

    class DeiverIfAllowReponse extends RCReponse {

        @JsonProperty()
        public boolean allow;

    }

    class GetCrmCustomerReponse extends RCReponse {
        @JsonProperty()
        public CrmCustomer customerInfo;
    }

    class QueryMaintainReponse extends RCReponse {
        @JsonProperty()
        public MaintainInfo maintainInfo;
    }
}
