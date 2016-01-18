package com.robam.common.io.cloud;

import com.legent.plat.pojos.RCReponse;
import com.robam.common.io.cloud.Requests.*;
import com.robam.common.io.cloud.Reponses.*;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface IRokiRestService {

    String Url_UserProfile = "/rest/api/view-register-agreement";

    // -------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------

    String getStoreVersion = "/rest/api/store/version/get";
    String getStoreCategory = "/rest/api/cookbook/group-tag/get";
    String getCookbookProviders = "/rest/api/cookbook/source/get";
    String getCookbooksByTag = "/rest/api/cookbook/by-tag/get";
    String getCookbooksByName = "/rest/api/cookbook/by-name/get";
    String getSeasonCookbooks = "/rest/api/cookbook/season/get";
    String getRecommendCookbooksForMob = "/rest/api/cookbook/recommend/app/get";
    String getRecommendCookbooksForPad = "/rest/api/cookbook/recommend/pad/get";
    String getHotKeysForCookbook = "/rest/api/cookbook/hot/get";
    String getCookbookById = "/rest/api/cookbook/by-id/get";
    String getAccessoryFrequencyForMob = "/rest/api/cookbook/recommend/app/accessory/get";

    // -------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------

    String getTodayCookbooks = "/rest/api/cookbook/today/get";
    String addTodayCookbook = "/rest/api/cookbook/today/add";
    String deleteTodayCookbook = "/rest/api/cookbook/today/delete";
    String deleteAllTodayCookbook = "/rest/api/cookbook/today/all/delete";
    String exportMaterialsFromToday = "/rest/api/cookbook/today/material/export";
    String addMaterialsToToday = "/rest/api/cookbook/today/material/add";
    String deleteMaterialsFromToday = "/rest/api/cookbook/today/material/delete";
    String getFavorityCookbooks = "/rest/api/cookbook/collect/get";
    String addFavorityCookbooks = "/rest/api/cookbook/collect/add";
    String delteFavorityCookbooks = "/rest/api/cookbook/collect/delete";
    String delteAllFavorityCookbooks = "/rest/api/cookbook/collect/all/delete";
    String getGroundingRecipes = "/rest/api/cookbook/grounding/get";

    // -------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------
    String addCookingLog = "/rest/api/cooking/record/add";


    // -------------------------------------------------------------------------------
    //album
    // -------------------------------------------------------------------------------
    String getMyCookAlbumByCookbook = "/rest/api/cooking/album/my/get";
    String getOtherCookAlbumsByCookbook = "/rest/api/cooking/album/others/get";
    String submitCookAlbum = "/rest/api/cooking/album/add";
    String removeCookAlbum = "/rest/api/cooking/album/delete";
    String praiseCookAlbum = "/rest/api/cooking/album/point-praise";
    String unpraiseCookAlbum = "/rest/api/cooking/album/cancel-point-praise";
    String getMyCookAlbums = "/rest/api/cooking/album/my/get-all";
    String clearMyCookAlbums = "/rest/api/cooking/album/delete-all";

    // -------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------
    String getHomeAdvertsForMob = "/rest/api/app/image/advert/get";
    String getHomeTitleForMob="/rest/api/app/image/title/get";
    String getHomeAdvertsForPad = "/rest/api/pad/image/advert/get";
    String getFavorityImagesForPad = "/rest/api/pad/image/collect/get";
    String getRecommendImagesForPad = "/rest/api/pad/image/recommend/get";
    String getAllBookImagesForPad = "/rest/api/pad/image/cookbook/get";

    // -------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------

    String applyAfterSale = "/rest/api/customer-service/apply";
    String getSmartParams = "/rest/api/device/intelligence/get";
    String setSmartParamsByDaily = "/rest/api/device/intelligence/by-day";
    String setSmartParamsByWeekly = "/rest/api/device/intelligence/weekly";
    String getSmartParams360 = "/rest/api/device/360/get";
    String setSmartParams360 = "/rest/api/device/360/set";

    // -------------------------------------------------------------------------------
    //免费配送
    // -------------------------------------------------------------------------------
    String getCustomerInfo = "/rest/api/shopping/customer-info/get";
    String saveCustomerInfo = "/rest/api/shopping/customer-info/save";
    String submitOrder = "/rest/api/shopping/order/save";
    String getOrder = "/rest/api/shopping/order/get-by-id";
    String updateOrderContacter = "/rest/api/shopping/order/customer-info/update";
    String queryOrder = "/rest/api/shopping/order/get";
    String cancelOrder = "/rest/api/shopping/order/delete";
    String orderIfOpen = "/rest/api/shopping/if-open";
    String deiverIfAllow = "/rest/api/shopping/if-allow-free-send";
    String getEventStatus="/rest/api/event/status/get";


    // -------------------------------------------------------------------------------
    // 清洁维保
    // -------------------------------------------------------------------------------

    String getCrmCustomerRequest = "/rest/api/maintain/customer/get";
    String submitMaintainRequest = "/rest/api/maintain/save";
    String queryMaintainRequest = "/rest/api/maintain/get";


    // -------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------


    /**
     * 获取库版本号
     */
    @POST(getStoreVersion)
    void getStoreVersion(@Body StoreRequest reqBody,
                         Callback<StoreVersionResponse> callback);

    /**
     * 获取分类
     */
    @POST(getStoreCategory)
    void getStoreCategory(Callback<StoreCategoryResponse> callback);

    /**
     * 获取所有菜谱供应商
     */
    @POST(getCookbookProviders)
    void getCookbookProviders(Callback<CookbookProviderResponse> callback);

    /**
     * 根据标签获取菜谱列表，包含自有菜谱与第三方菜谱
     */
    @POST(getCookbooksByTag)
    void getCookbooksByTag(@Body GetCookbooksByTagRequest reqBody,
                           Callback<CookbooksResponse> callback);

    /**
     * 根据名称获取菜谱列表，包含自有菜谱与第三方菜谱
     */
    @POST(getCookbooksByName)
    void getCookbooksByName(@Body GetCookbooksByNameRequest reqBody,
                            Callback<CookbooksResponse> callback);

    /**
     * 获取时令菜谱
     */
    @POST(getSeasonCookbooks)
    void getSeasonCookbooks(Callback<CookbooksResponse> callback);

    /**
     * 获取推荐菜谱 for 手机
     */
    @POST(getRecommendCookbooksForMob)
    void getRecommendCookbooksForMob(@Body UserRequest reqBody,
                                     Callback<ThumbCookbookResponse> callback);

    /**
     * 获取推荐菜谱 for Pad
     */
    @POST(getRecommendCookbooksForPad)
    void getRecommendCookbooksForPad(@Body UserRequest reqBody,
                                     Callback<ThumbCookbookResponse> callback);

    /**
     * 获取热门搜索关键字
     */
    @POST(getHotKeysForCookbook)
    void getHotKeysForCookbook(Callback<HotKeysForCookbookResponse> callback);

    /**
     * 获取菜谱详情
     */
    @POST(getCookbookById)
    void getCookbookById(@Body UserBookRequest reqBody,
                         Callback<CookbookResponse> callback);

    /**
     * 获取mob端首页调味料排行榜
     */
    @POST(getAccessoryFrequencyForMob)
    void getAccessoryFrequencyForMob(@Body UserRequest reqBody, Callback<MaterialFrequencyResponse> callback);

    // -------------------------------------------------------------------------------
    // 今日菜单
    // -------------------------------------------------------------------------------

    /**
     * 获取今日菜谱
     */
    @POST(getTodayCookbooks)
    void getTodayCookbooks(@Body UserRequest reqBody,
                           Callback<CookbooksResponse> callback);

    /**
     * 添加菜谱到今日菜谱
     */
    @POST(addTodayCookbook)
    void addTodayCookbook(@Body UserBookRequest reqBody,
                          Callback<RCReponse> callback);

    /**
     * 从今日菜谱中删除菜谱
     */
    @POST(deleteTodayCookbook)
    void deleteTodayCookbook(@Body UserBookRequest reqBody,
                             Callback<RCReponse> callback);

    /**
     * 从今日菜谱中删除全部菜谱
     */
    @POST(deleteAllTodayCookbook)
    void deleteAllTodayCookbook(@Body UserRequest reqBody,
                                Callback<RCReponse> callback);

    /**
     * 从今日菜谱中导出食材
     */
    @POST(exportMaterialsFromToday)
    void exportMaterialsFromToday(@Body UserRequest reqBody,
                                  Callback<MaterialsResponse> callback);

    /**
     * 添加食材到今日菜谱的食材清单
     */
    @POST(addMaterialsToToday)
    void addMaterialsToToday(@Body UserMaterialRequest reqBody,
                             Callback<RCReponse> callback);

    /**
     * 添加食材到今日菜谱的食材清单
     */
    @POST(deleteMaterialsFromToday)
    void deleteMaterialsFromToday(@Body UserMaterialRequest reqBody,
                                  Callback<RCReponse> callback);

    // -------------------------------------------------------------------------------
    // 我的收藏
    // -------------------------------------------------------------------------------

    /**
     * 获取菜谱-我的收藏
     */
    @POST(getFavorityCookbooks)
    void getFavorityCookbooks(@Body UserRequest reqBody,
                              Callback<CookbooksResponse> callback);

    /**
     * 增加菜谱到我的收藏
     */
    @POST(addFavorityCookbooks)
    void addFavorityCookbooks(@Body UserBookRequest reqBody,
                              Callback<RCReponse> callback);

    /**
     * 从我的收藏中删除菜谱
     */
    @POST(delteFavorityCookbooks)
    void deleteFavorityCookbooks(@Body UserBookRequest reqBody,
                                 Callback<RCReponse> callback);

    /**
     * 清空我的收藏
     */
    @POST(delteAllFavorityCookbooks)
    void delteAllFavorityCookbooks(@Body UserRequest reqBody,
                                   Callback<RCReponse> callback);

    @POST(getGroundingRecipes)
    void getGroundingRecipes(@Body GetGroudingRecipesRequest reqBody,
                             Callback<ThumbCookbookResponse> callback);


    // -------------------------------------------------------------------------------
    // 烧菜分享
    // -------------------------------------------------------------------------------

    @POST(addCookingLog)
    void addCookingLog(@Body CookingLogRequest reqBody,
                       Callback<RCReponse> callback);

    /**
     */
    @POST(getMyCookAlbumByCookbook)
    void getMyCookAlbumByCookbook(@Body UserBookRequest reqBody,
                                  Callback<AlbumResponse> callback);

    /**
     * 获取烧菜分享列表
     */
    @POST(getOtherCookAlbumsByCookbook)
    void getOtherCookAlbumsByCookbook(@Body GetCookAlbumsRequest reqBody,
                                      Callback<AlbumsResponse> callback);

    /**
     * 提交烧菜分享
     */
    @POST(submitCookAlbum)
    void submitCookAlbum(@Body SubmitCookAlbumRequest reqBody,
                         Callback<RCReponse> callback);

    /**
     * 删除已经烧菜分享
     */
    @POST(removeCookAlbum)
    void removeCookAlbum(@Body CookAlbumRequest reqBody,
                         Callback<RCReponse> callback);

    /**
     * 点赞
     */
    @POST(praiseCookAlbum)
    void praiseCookAlbum(@Body CookAlbumRequest reqBody,
                         Callback<RCReponse> callback);

    /**
     * 取消点赞
     */
    @POST(unpraiseCookAlbum)
    void unpraiseCookAlbum(@Body CookAlbumRequest reqBody,
                           Callback<RCReponse> callback);

    /**
     * 获取厨艺秀列表
     */
    @POST(getMyCookAlbums)
    void getMyCookAlbums(@Body UserRequest reqBody,
                         Callback<AlbumsResponse> callback);

    /**
     * 清空厨艺秀列表
     */
    @POST(clearMyCookAlbums)
    void clearMyCookAlbums(@Body UserRequest reqBody,
                           Callback<RCReponse> callback);

    // -------------------------------------------------------------------------------
    // 推送广告
    // -------------------------------------------------------------------------------

    /**
     * 获取app首页推广图片
     */
    @POST(getHomeAdvertsForMob)
    void getHomeAdvertsForMob(Callback<HomeAdvertsForMobResponse> callback);

    /**
     * 获取mob端首页推广的title的文字和图片
     */
    @POST(getHomeTitleForMob)
    void getHomeTitleForMob(Callback<HomeTitleForMobResponse> callback);

    /**
     * 获取pad首页推广图片
     */
    @POST(getHomeAdvertsForPad)
    void getHomeAdvertsForPad(Callback<HomeAdvertsForPadResponse> callback);

    /**
     * 获取pad我的收藏菜谱入口图片
     */
    @POST(getFavorityImagesForPad)
    void getFavorityImagesForPad(Callback<CookbookImageReponse> callback);

    /**
     * 获取pad为您推荐菜谱入口图片
     */
    @POST(getRecommendImagesForPad)
    void getRecommendImagesForPad(Callback<CookbookImageReponse> callback);

    /**
     * 获取pad所有菜谱入口图片
     */
    @POST(getAllBookImagesForPad)
    void getAllBookImagesForPad(Callback<CookbookImageReponse> callback);

    /**
     * 申请售后服务
     */
    @POST(applyAfterSale)
    void applyAfterSale(@Body ApplyAfterSaleRequest reqBody,
                        Callback<RCReponse> callback);

    // -------------------------------------------------------------------------------
    // 智能设定
    // -------------------------------------------------------------------------------

    /**
     * 获取智能设定配置（通风换气）
     */
    @POST(getSmartParams)
    void getSmartParams(@Body GetSmartParamsRequest reqBody,
                        Callback<SmartParamsReponse> callback);

    @POST(setSmartParamsByDaily)
    void setSmartParamsByDaily(@Body SetSmartParamsByDailyRequest reqBody,
                               Callback<RCReponse> callback);

    @POST(setSmartParamsByWeekly)
    void setSmartParamsByWeekly(@Body SetSmartParamsByWeeklyRequest reqBody,
                                Callback<RCReponse> callback);

    @POST(getSmartParams360)
    void getSmartParams360(@Body GetSmartParams360Request reqBody,
                           Callback<GetSmartParams360Reponse> callback);

    @POST(setSmartParams360)
    void setSmartParams360(@Body SetSmartParams360Request reqBody,
                           Callback<RCReponse> callback);


    // -------------------------------------------------------------------------------
    // 订单配送
    // -------------------------------------------------------------------------------
    @POST(getCustomerInfo)
    void getCustomerInfo(@Body UserRequest reqBody,
                         Callback<GetCustomerInfoReponse> callback);


    @POST(saveCustomerInfo)
    void saveCustomerInfo(@Body SaveCustomerInfoRequest reqBody,
                          Callback<RCReponse> callback);

    @POST(submitOrder)
    void submitOrder(@Body SubmitOrderRequest reqBody,
                     Callback<SubmitOrderReponse> callback);


    @POST(getOrder)
    void getOrder(@Body GetOrderRequest reqBody,
                  Callback<GetOrderReponse> callback);

    @POST(queryOrder)
    void queryOrder(@Body QueryOrderRequest reqBody,
                    Callback<QueryOrderReponse> callback);

    @POST(cancelOrder)
    void cancelOrder(@Body UserOrderRequest reqBody,
                     Callback<RCReponse> callback);

    @POST(updateOrderContacter)
    void updateOrderContacter(@Body UpdateOrderContacterRequest reqBody, Callback<RCReponse> callback);

    @POST(orderIfOpen)
    void orderIfOpen(Callback<OrderIfOpenReponse> callback);

    @POST(getEventStatus)
    void getEventStatus(Callback<EventStatusReponse> callback);

    @POST(deiverIfAllow)
    void deiverIfAllow(@Body UserRequest reqBody, Callback<DeiverIfAllowReponse> callback);

    // -------------------------------------------------------------------------------
    // 清洁维保
    // -------------------------------------------------------------------------------
    @POST(getCrmCustomerRequest)
    void getCrmCustomer(@Body GetCrmCustomerRequest reqBody,
                        Callback<GetCrmCustomerReponse> callback);

    @POST(submitMaintainRequest)
    void submitMaintain(@Body SubmitMaintainRequest reqBody,
                        Callback<RCReponse> callback);

    @POST(queryMaintainRequest)
    void queryMaintain(@Body QueryMaintainRequest reqBody,
                       Callback<QueryMaintainReponse> callback);


    // -------------------------------------------------------------------------------
    // other
    // -------------------------------------------------------------------------------
}
