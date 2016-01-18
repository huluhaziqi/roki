package com.robam.common.io.cloud;

import android.graphics.Bitmap;

import com.legent.Callback;
import com.legent.Helper;
import com.legent.VoidCallback;
import com.legent.plat.Plat;
import com.legent.plat.exceptions.ExceptionHelper;
import com.legent.plat.io.RCRetrofitCallback;
import com.legent.plat.io.RCRetrofitCallbackWithVoid;
import com.legent.plat.pojos.RCReponse;
import com.legent.utils.graphic.BitmapUtils;
import com.robam.common.io.cloud.Reponses.AlbumResponse;
import com.robam.common.io.cloud.Reponses.AlbumsResponse;
import com.robam.common.io.cloud.Reponses.CookbookImageReponse;
import com.robam.common.io.cloud.Reponses.CookbookProviderResponse;
import com.robam.common.io.cloud.Reponses.CookbookResponse;
import com.robam.common.io.cloud.Reponses.CookbooksResponse;
import com.robam.common.io.cloud.Reponses.DeiverIfAllowReponse;
import com.robam.common.io.cloud.Reponses.GetCrmCustomerReponse;
import com.robam.common.io.cloud.Reponses.GetCustomerInfoReponse;
import com.robam.common.io.cloud.Reponses.GetOrderReponse;
import com.robam.common.io.cloud.Reponses.GetSmartParams360Reponse;
import com.robam.common.io.cloud.Reponses.HomeAdvertsForMobResponse;
import com.robam.common.io.cloud.Reponses.HomeAdvertsForPadResponse;
import com.robam.common.io.cloud.Reponses.HotKeysForCookbookResponse;
import com.robam.common.io.cloud.Reponses.MaterialFrequencyResponse;
import com.robam.common.io.cloud.Reponses.MaterialsResponse;
import com.robam.common.io.cloud.Reponses.OrderIfOpenReponse;
import com.robam.common.io.cloud.Reponses.EventStatusReponse;
import com.robam.common.io.cloud.Reponses.QueryMaintainReponse;
import com.robam.common.io.cloud.Reponses.QueryOrderReponse;
import com.robam.common.io.cloud.Reponses.SmartParamsReponse;
import com.robam.common.io.cloud.Reponses.StoreCategoryResponse;
import com.robam.common.io.cloud.Reponses.StoreVersionResponse;
import com.robam.common.io.cloud.Reponses.SubmitOrderReponse;
import com.robam.common.io.cloud.Reponses.ThumbCookbookResponse;
import com.robam.common.io.cloud.Requests.ApplyAfterSaleRequest;
import com.robam.common.io.cloud.Requests.CookAlbumRequest;
import com.robam.common.io.cloud.Requests.CookingLogRequest;
import com.robam.common.io.cloud.Requests.GetCookAlbumsRequest;
import com.robam.common.io.cloud.Requests.GetCookbooksByNameRequest;
import com.robam.common.io.cloud.Requests.GetCookbooksByTagRequest;
import com.robam.common.io.cloud.Requests.GetCrmCustomerRequest;
import com.robam.common.io.cloud.Requests.GetGroudingRecipesRequest;
import com.robam.common.io.cloud.Requests.GetOrderRequest;
import com.robam.common.io.cloud.Requests.GetSmartParams360Request;
import com.robam.common.io.cloud.Requests.GetSmartParamsRequest;
import com.robam.common.io.cloud.Requests.QueryMaintainRequest;
import com.robam.common.io.cloud.Requests.QueryOrderRequest;
import com.robam.common.io.cloud.Requests.SaveCustomerInfoRequest;
import com.robam.common.io.cloud.Requests.SetSmartParams360Request;
import com.robam.common.io.cloud.Requests.SetSmartParamsByDailyRequest;
import com.robam.common.io.cloud.Requests.SetSmartParamsByWeeklyRequest;
import com.robam.common.io.cloud.Requests.StoreRequest;
import com.robam.common.io.cloud.Requests.SubmitCookAlbumRequest;
import com.robam.common.io.cloud.Requests.SubmitMaintainRequest;
import com.robam.common.io.cloud.Requests.SubmitOrderRequest;
import com.robam.common.io.cloud.Requests.UserBookRequest;
import com.robam.common.io.cloud.Requests.UserMaterialRequest;
import com.robam.common.io.cloud.Requests.UserOrderRequest;
import com.robam.common.io.cloud.Requests.UserRequest;
import com.robam.common.pojos.Advert.MobAdvert;
import com.robam.common.pojos.AdvertImage;
import com.robam.common.pojos.CookAlbum;
import com.robam.common.pojos.CrmCustomer;
import com.robam.common.pojos.CrmProduct;
import com.robam.common.pojos.Group;
import com.robam.common.pojos.MaintainInfo;
import com.robam.common.pojos.MaterialFrequency;
import com.robam.common.pojos.Materials;
import com.robam.common.pojos.OrderContacter;
import com.robam.common.pojos.OrderInfo;
import com.robam.common.pojos.Recipe;
import com.robam.common.pojos.RecipeProvider;

import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class RokiRestHelper {

    final static String TAG = "rest";
    static IRokiRestService svr = Plat.getCustomApi(IRokiRestService.class);

    static public void getStoreVersion(final Callback<Integer> callback) {
        svr.getStoreVersion(new StoreRequest("roki"),
                new RCRetrofitCallback<StoreVersionResponse>(callback) {
                    @Override
                    protected void afterSuccess(StoreVersionResponse result) {
                        callback.onSuccess(result.version);
                    }
                });
    }

    static public void getStoreCategory(final Callback<List<Group>> callback) {
        svr.getStoreCategory(new RCRetrofitCallback<StoreCategoryResponse>(
                callback) {
            @Override
            protected void afterSuccess(StoreCategoryResponse result) {
                callback.onSuccess(result.groups);
            }
        });
    }

    static public void getCookbookProviders(
            final Callback<List<RecipeProvider>> callback) {
        svr.getCookbookProviders(new RCRetrofitCallback<CookbookProviderResponse>(
                callback) {
            @Override
            protected void afterSuccess(CookbookProviderResponse result) {
                callback.onSuccess(result.providers);
            }
        });
    }

    static public void getCookbooksByTag(long tagId,
                                         final Callback<CookbooksResponse> callback) {
        svr.getCookbooksByTag(new GetCookbooksByTagRequest(tagId),
                new RCRetrofitCallback<CookbooksResponse>(callback) {
                    @Override
                    protected void afterSuccess(CookbooksResponse result) {
                        callback.onSuccess(result);
                    }
                });
    }

    static public void getCookbooksByName(String name,
                                          final Callback<CookbooksResponse> callback) {
        svr.getCookbooksByName(new GetCookbooksByNameRequest(name),
                new RCRetrofitCallback<CookbooksResponse>(callback) {
                    @Override
                    protected void afterSuccess(CookbooksResponse result) {
                        callback.onSuccess(result);
                    }
                });
    }

    static public void getSeasonCookbooks(
            final Callback<CookbooksResponse> callback) {
        svr.getSeasonCookbooks(new RCRetrofitCallback<CookbooksResponse>(
                callback) {
            @Override
            protected void afterSuccess(CookbooksResponse result) {
                callback.onSuccess(result);
            }
        });
    }

    static public void getRecommendCookbooksForMob(
            final Callback<List<Recipe>> callback) {
        svr.getRecommendCookbooksForMob(new UserRequest(getUserId()),
                new RCRetrofitCallback<ThumbCookbookResponse>(callback) {
                    @Override
                    protected void afterSuccess(ThumbCookbookResponse result) {
                        callback.onSuccess(result.cookbooks);
                    }
                });
    }

    static public void getRecommendCookbooksForPad(
            final Callback<List<Recipe>> callback) {
        svr.getRecommendCookbooksForPad(new UserRequest(getUserId()),
                new RCRetrofitCallback<ThumbCookbookResponse>(callback) {
                    @Override
                    protected void afterSuccess(ThumbCookbookResponse result) {
                        callback.onSuccess(result.cookbooks);
                    }
                });
    }

    static public void getHotKeysForCookbook(
            final Callback<List<String>> callback) {
        svr.getHotKeysForCookbook(new RCRetrofitCallback<HotKeysForCookbookResponse>(
                callback) {
            @Override
            protected void afterSuccess(HotKeysForCookbookResponse result) {
                callback.onSuccess(result.hotKeys);
            }
        });
    }

    static public void getCookbookById(long cookbookId,
                                       final Callback<Recipe> callback) {
        svr.getCookbookById(new UserBookRequest(getUserId(), cookbookId),
                new RCRetrofitCallback<CookbookResponse>(callback) {
                    @Override
                    protected void afterSuccess(CookbookResponse result) {
                        callback.onSuccess(result.cookbook);
                    }
                });
    }

    static public void getAccessoryFrequencyForMob(final Callback<List<MaterialFrequency>> callback) {
        svr.getAccessoryFrequencyForMob(new UserRequest(0), new RCRetrofitCallback<MaterialFrequencyResponse>(callback) {
            @Override
            protected void afterSuccess(MaterialFrequencyResponse result) {
                Helper.onSuccess(callback, result.list);
            }
        });
    }

    // -------------------------------------------------------------------------------

    static public void getTodayCookbooks(
            final Callback<CookbooksResponse> callback) {
        svr.getTodayCookbooks(new UserRequest(getUserId()),
                new RCRetrofitCallback<CookbooksResponse>(callback) {
                    @Override
                    protected void afterSuccess(CookbooksResponse result) {
                        callback.onSuccess(result);
                    }
                });
    }

    static public void addTodayCookbook(long cookbookId,
                                        final VoidCallback callback) {
        svr.addTodayCookbook(new UserBookRequest(getUserId(), cookbookId),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void deleteTodayCookbook(long cookbookId,
                                           VoidCallback callback) {
        svr.deleteTodayCookbook(new UserBookRequest(getUserId(), cookbookId),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void deleteAllTodayCookbook(VoidCallback callback) {
        svr.deleteAllTodayCookbook(new UserRequest(getUserId()),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void exportMaterialsFromToday(
            final Callback<Materials> callback) {
        svr.exportMaterialsFromToday(new UserRequest(getUserId()),
                new RCRetrofitCallback<MaterialsResponse>(callback) {
                    @Override
                    protected void afterSuccess(MaterialsResponse result) {
                        callback.onSuccess(result.materials);
                    }
                });
    }

    static public void addMaterialsToToday(long materialId,
                                           VoidCallback callback) {
        svr.addMaterialsToToday(
                new UserMaterialRequest(getUserId(), materialId),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void deleteMaterialsFromToday(long materialId,
                                                VoidCallback callback) {
        svr.deleteMaterialsFromToday(new UserMaterialRequest(getUserId(),
                        materialId),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    // -------------------------------------------------------------------------------

    static public void getFavorityCookbooks(
            final Callback<CookbooksResponse> callback) {
        svr.getFavorityCookbooks(new UserRequest(getUserId()),
                new RCRetrofitCallback<CookbooksResponse>(callback) {
                    @Override
                    protected void afterSuccess(CookbooksResponse result) {
                        callback.onSuccess(result);
                    }
                });
    }

    static public void addFavorityCookbooks(long cookbookId,
                                            VoidCallback callback) {
        svr.addFavorityCookbooks(new UserBookRequest(getUserId(), cookbookId),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void deleteFavorityCookbooks(long cookbookId,
                                               VoidCallback callback) {
        svr.deleteFavorityCookbooks(
                new UserBookRequest(getUserId(), cookbookId),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void delteAllFavorityCookbooks(VoidCallback callback) {
        svr.delteAllFavorityCookbooks(new UserRequest(getUserId()),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void getGroundingRecipes(int start, int limit,
                                           final Callback<List<Recipe>> callback) {
        svr.getGroundingRecipes(new GetGroudingRecipesRequest(start, limit), new RCRetrofitCallback<ThumbCookbookResponse>(callback) {
            @Override
            protected void afterSuccess(ThumbCookbookResponse result) {
                Helper.onSuccess(callback, result.cookbooks);
            }
        });
    }

    // -------------------------------------------------------------------------------

    static public void addCookingLog(String deviceId, long cookbookId,
                                     long start, long end, boolean isBroken, final VoidCallback callback) {
        svr.addCookingLog(new CookingLogRequest(getUserId(), cookbookId,
                        deviceId, start, end, isBroken),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void getMyCookAlbumByCookbook(long cookbookId,
                                                final Callback<CookAlbum> callback) {

        svr.getMyCookAlbumByCookbook(new UserBookRequest(getUserId(), cookbookId),
                new RCRetrofitCallback<AlbumResponse>(callback) {
                    @Override
                    protected void afterSuccess(AlbumResponse result) {
                        callback.onSuccess(result.album);
                    }
                });

    }

    static public void getOtherCookAlbumsByCookbook(long cookbookId, int start, int limit,
                                                    final Callback<List<CookAlbum>> callback) {
        svr.getOtherCookAlbumsByCookbook(new GetCookAlbumsRequest(getUserId(), cookbookId,
                        start, limit),
                new RCRetrofitCallback<AlbumsResponse>(callback) {
                    @Override
                    protected void afterSuccess(AlbumsResponse result) {
                        callback.onSuccess(result.cookAlbums);
                    }
                });
    }

    static public void submitCookAlbum(long cookbookId, Bitmap image,
                                       String desc, VoidCallback callback) {
        String strImg = BitmapUtils.toBase64(image);
        svr.submitCookAlbum(new SubmitCookAlbumRequest(getUserId(), cookbookId,
                strImg, desc), new RCRetrofitCallbackWithVoid<RCReponse>(
                callback));
    }

    static public void removeCookAlbum(long albumId, VoidCallback callback) {
        svr.removeCookAlbum(new CookAlbumRequest(getUserId(), albumId),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void praiseCookAlbum(long albumId, VoidCallback callback) {
        svr.praiseCookAlbum(new CookAlbumRequest(getUserId(), albumId),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void unpraiseCookAlbum(long albumId, VoidCallback callback) {
        svr.unpraiseCookAlbum(new CookAlbumRequest(getUserId(), albumId),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void getMyCookAlbums(final Callback<List<CookAlbum>> callback) {
        svr.getMyCookAlbums(new UserRequest(getUserId()), new RCRetrofitCallback<AlbumsResponse>(callback) {
            @Override
            protected void afterSuccess(AlbumsResponse result) {
                Helper.onSuccess(callback, result.cookAlbums);
            }
        });
    }

    static public void clearMyCookAlbums(final VoidCallback callback) {
        svr.clearMyCookAlbums(new UserRequest(getUserId()), new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    // -------------------------------------------------------------------------------

    static public void getHomeAdvertsForMob(
            final Callback<List<MobAdvert>> callback) {
        svr.getHomeAdvertsForMob(new RCRetrofitCallback<HomeAdvertsForMobResponse>(
                callback) {
            @Override
            protected void afterSuccess(HomeAdvertsForMobResponse result) {
                callback.onSuccess(result.adverts);
            }
        });
    }

    static public void getHomeTitleForMob(final Callback<List<MobAdvert>> callback){
        svr.getHomeTitleForMob(new RCRetrofitCallback<Reponses.HomeTitleForMobResponse>(callback){
            @Override
            protected void afterSuccess(Reponses.HomeTitleForMobResponse result) {
                callback.onSuccess(result.titles);
            }
        });
    }

    static public void getHomeAdvertsForPad(
            final Callback<HomeAdvertsForPadResponse> callback) {
        svr.getHomeAdvertsForPad(new RCRetrofitCallback<HomeAdvertsForPadResponse>(
                callback) {
            @Override
            protected void afterSuccess(HomeAdvertsForPadResponse result) {
                callback.onSuccess(result);
            }
        });
    }

    static public void getFavorityImagesForPad(
            final Callback<List<AdvertImage>> callback) {
        svr.getFavorityImagesForPad(new RCRetrofitCallback<CookbookImageReponse>(
                callback) {
            @Override
            protected void afterSuccess(CookbookImageReponse result) {
                callback.onSuccess(result.images);
            }
        });
    }

    static public void getRecommendImagesForPad(
            final Callback<List<AdvertImage>> callback) {
        svr.getRecommendImagesForPad(new RCRetrofitCallback<CookbookImageReponse>(
                callback) {
            @Override
            protected void afterSuccess(CookbookImageReponse result) {
                callback.onSuccess(result.images);
            }
        });

    }

    static public void getAllBookImagesForPad(
            final Callback<List<AdvertImage>> callback) {
        svr.getAllBookImagesForPad(new RCRetrofitCallback<CookbookImageReponse>(
                callback) {
            @Override
            protected void afterSuccess(CookbookImageReponse result) {
                callback.onSuccess(result.images);
            }
        });
    }

    // -------------------------------------------------------------------------------

    static public void applyAfterSale(String deviceId,
                                      final VoidCallback callback) {
        svr.applyAfterSale(new ApplyAfterSaleRequest(getUserId(), deviceId),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void getSmartParams(String deviceGuid,
                                      final Callback<SmartParamsReponse> callback) {
        svr.getSmartParams(new GetSmartParamsRequest(getUserId(), deviceGuid),
                new RCRetrofitCallback<SmartParamsReponse>(callback) {
                    @Override
                    protected void afterSuccess(SmartParamsReponse result) {
                        callback.onSuccess(result);
                    }
                });
    }

    static public void setSmartParamsByDaily(String guid, boolean enable,
                                             int day, VoidCallback callback) {
        svr.setSmartParamsByDaily(new SetSmartParamsByDailyRequest(getUserId(),
                guid, enable, day), new RCRetrofitCallbackWithVoid<RCReponse>(
                callback));
    }

    static public void setSmartParamsByWeekly(String guid, boolean enable,
                                              int day, String time, VoidCallback callback) {
        svr.setSmartParamsByWeekly(new SetSmartParamsByWeeklyRequest(
                        getUserId(), guid, enable, day, time),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void getSmartParams360(String guid, final Callback<Boolean> callback) {
        svr.getSmartParams360(new GetSmartParams360Request(getUserId(), guid),
                new RCRetrofitCallback<GetSmartParams360Reponse>(callback) {
                    @Override
                    protected void afterSuccess(GetSmartParams360Reponse result) {
                        Helper.onSuccess(callback, result.switchStatus);
                    }
                });
    }

    static public void setSmartParams360(String guid, boolean switchStatus, final VoidCallback callback) {
        svr.setSmartParams360(new SetSmartParams360Request(getUserId(), guid, switchStatus),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    // -------------------------------------------------------------------------------
    // 订单配送
    // -------------------------------------------------------------------------------

    static public void getCustomerInfo(final Callback<OrderContacter> callback) {
        svr.getCustomerInfo(new UserRequest(getUserId()), new RCRetrofitCallback<GetCustomerInfoReponse>(callback) {
            @Override
            protected void afterSuccess(GetCustomerInfoReponse result) {
                Helper.onSuccess(callback, result.customer);
            }
        });
    }

    static public void saveCustomerInfo(String name, String phone, String city, String address, final VoidCallback callback) {
        svr.saveCustomerInfo(new SaveCustomerInfoRequest(getUserId(), name, phone, city, address), new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void submitOrder(List<Long> ids, final Callback<Long> callback) {
        svr.submitOrder(new SubmitOrderRequest(getUserId(), ids), new RCRetrofitCallback<SubmitOrderReponse>(callback) {
            @Override
            protected void afterSuccess(SubmitOrderReponse result) {
                Helper.onSuccess(callback, result.orderId);
            }
        });
    }

    static public void getOrder(long orderId, final Callback<OrderInfo> callback) {
        svr.getOrder(new GetOrderRequest(orderId), new RCRetrofitCallback<GetOrderReponse>(callback) {
            @Override
            protected void afterSuccess(GetOrderReponse result) {
                Helper.onSuccess(callback, result.order);
            }
        });
    }

    static public void queryOrder(long time, int limit, final Callback<List<OrderInfo>> callback) {
        svr.queryOrder(new QueryOrderRequest(getUserId(), time, limit), new RCRetrofitCallback<QueryOrderReponse>(callback) {
            @Override
            protected void afterSuccess(QueryOrderReponse result) {
                Helper.onSuccess(callback, result.orders);
            }
        });
    }

    static public void cancelOrder(long orderId, final VoidCallback callback) {
        svr.cancelOrder(new UserOrderRequest(getUserId(), orderId), new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void updateOrderContacter(long orderId, String name, String phone, String city, String address, final VoidCallback callback) {
        svr.updateOrderContacter(new Requests.UpdateOrderContacterRequest(getUserId(), orderId, name, phone, city, address),
                new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void orderIfOpen(final Callback<Boolean> callback) {
        svr.orderIfOpen(new RCRetrofitCallback<OrderIfOpenReponse>(callback) {
            @Override
            protected void afterSuccess(OrderIfOpenReponse result) {
                Helper.onSuccess(callback, result.open);
            }
        });
    }

    static public void getEventStatus(final Callback<EventStatusReponse> callback) {
        svr.getEventStatus(new RCRetrofitCallback<EventStatusReponse>(callback) {
            @Override
            protected void afterSuccess(EventStatusReponse result) {
                Helper.onSuccess(callback, result);
            }
        });
    }

    static public void deiverIfAllow(final Callback<Integer> callback) {
        svr.deiverIfAllow(new UserRequest(getUserId()), new retrofit.Callback<DeiverIfAllowReponse>() {
            @Override
            public void success(DeiverIfAllowReponse result, Response response) {
                Helper.onSuccess(callback, result.rc);
            }

            @Override
            public void failure(RetrofitError e) {
                Helper.onFailure(callback, ExceptionHelper.newRestfulException(e.getMessage()));
            }
        });
    }


    // -------------------------------------------------------------------------------
    // 清洁维保
    // -------------------------------------------------------------------------------

    static public void getCrmCustomer(String phone, final Callback<CrmCustomer> callback) {
        svr.getCrmCustomer(new GetCrmCustomerRequest(phone), new RCRetrofitCallback<GetCrmCustomerReponse>(callback) {
            @Override
            protected void afterSuccess(GetCrmCustomerReponse result) {
                Helper.onSuccess(callback, result.customerInfo);
            }
        });
    }

    static public void submitMaintain(CrmProduct product, long bookTime, String customerId, String customerName, String phone, String province, String city, String county, String address, VoidCallback callback) {
        svr.submitMaintain(new SubmitMaintainRequest(getUserId(), product, bookTime, customerId, customerName, phone, province, city, county, address), new RCRetrofitCallbackWithVoid<RCReponse>(callback));
    }

    static public void queryMaintain(final Callback<MaintainInfo> callback) {

        svr.queryMaintain(new QueryMaintainRequest(getUserId()), new RCRetrofitCallback<QueryMaintainReponse>(callback) {
            @Override
            protected void afterSuccess(QueryMaintainReponse result) {
                Helper.onSuccess(callback, result.maintainInfo);
            }
        });
    }

    // -------------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------------

    static private long getUserId() {
        return Plat.accountService.getCurrentUserId();
    }

}
