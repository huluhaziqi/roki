package com.robam.common.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.legent.Callback;
import com.legent.Helper;
import com.legent.VoidCallback;
import com.legent.dao.DaoHelper;
import com.legent.plat.Plat;
import com.legent.plat.events.UserLoginEvent;
import com.legent.plat.events.UserLogoutEvent;
import com.legent.services.AbsService;
import com.legent.utils.EventUtils;
import com.legent.utils.api.PreferenceUtils;
import com.robam.common.PrefsKey;
import com.robam.common.Utils;
import com.robam.common.events.CookMomentsRefreshEvent;
import com.robam.common.events.FavorityBookCleanEvent;
import com.robam.common.events.FavorityBookRefreshEvent;
import com.robam.common.events.OrderRefreshEvent;
import com.robam.common.events.TodayBookCleanEvent;
import com.robam.common.events.TodayBookRefreshEvent;
import com.robam.common.io.cloud.Reponses;
import com.robam.common.io.cloud.Reponses.CookbooksResponse;
import com.robam.common.io.cloud.Reponses.HomeAdvertsForPadResponse;
import com.robam.common.io.cloud.Reponses.EventStatusReponse;
import com.robam.common.io.cloud.RokiRestHelper;
import com.robam.common.pojos.AbsRecipe;
import com.robam.common.pojos.Advert;
import com.robam.common.pojos.Advert.MobAdvert;
import com.robam.common.pojos.AdvertImage;
import com.robam.common.pojos.CookAlbum;
import com.robam.common.pojos.Group;
import com.robam.common.pojos.MaterialFrequency;
import com.robam.common.pojos.Materials;
import com.robam.common.pojos.OrderContacter;
import com.robam.common.pojos.OrderInfo;
import com.robam.common.pojos.Recipe;
import com.robam.common.pojos.Recipe3rd;
import com.robam.common.pojos.RecipeProvider;
import com.robam.common.pojos.Tag;

import java.util.List;
import java.util.Set;

public class StoreService extends AbsService {

    static private StoreService instance = new StoreService();

    synchronized static public StoreService getInstance() {
        return instance;
    }

    int cloudVersion;
    DaoService daoService = DaoService.getInstance();

    private StoreService() {

    }

    @Override
    public void init(Context cx, Object... params) {
        super.init(cx, params);
        daoService.init(cx, params);
        initSync();
    }

    @Subscribe
    public void onEvent(UserLoginEvent event) {
        daoService.switchUser();
        initSync();
    }

    @Subscribe
    public void onEvent(UserLogoutEvent event) {
        daoService.switchUser();
        initSync();
    }

    // -------------------------------------------------------------------------------
    // IStoreService
    // -------------------------------------------------------------------------------


    public void isNewest(final Callback<Boolean> callback) {
        RokiRestHelper.getStoreVersion(new Callback<Integer>() {

            @Override
            public void onSuccess(Integer version) {
                cloudVersion = version;
                int localVer = getLocalVersion();
                boolean isNewest = localVer >= cloudVersion;
                Helper.onSuccess(callback, isNewest);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onSuccess(callback, true);
            }
        });
    }

    public void getStoreCategory(final Callback<List<Group>> callback) {

        RokiRestHelper.getStoreCategory(new Callback<List<Group>>() {

            @Override
            public void onSuccess(List<Group> groups) {
                DaoHelper.deleteAll(Group.class);
                DaoHelper.deleteAll(Tag.class);

                if (groups != null) {
                    for (Group group : groups) {
                        group.save2db();
                    }
                }
                SysCfgManager.getInstance().setLocalVersion(cloudVersion);

                Helper.onSuccess(callback, groups);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }

    public void getCookbookProviders(final Callback<List<RecipeProvider>> callback) {

        RokiRestHelper.getCookbookProviders(new Callback<List<RecipeProvider>>() {

            @Override
            public void onSuccess(List<RecipeProvider> list) {
                DaoHelper.deleteAll(RecipeProvider.class);
                if (list != null) {
                    for (RecipeProvider cp : list) {
                        cp.save2db();
                    }
                }
                Helper.onSuccess(callback, list);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });

    }

    public void getCookbooksByTag(final long tagId,
                                  final Callback<CookbooksResponse> callback) {

        RokiRestHelper.getCookbooksByTag(tagId,
                new Callback<CookbooksResponse>() {

                    @Override
                    public void onSuccess(CookbooksResponse result) {
                        Tag tag = DaoHelper.getById(Tag.class, tagId);
                        if (tag != null) {
                            tag.save2db(result.cookbooks, result.cookbooks3rd);
                        }

                        Helper.onSuccess(callback, result);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Helper.onFailure(callback, t);
                    }
                });

    }

    public void getCookbooksByName(final String name,
                                   final Callback<CookbooksResponse> callback) {
        RokiRestHelper.getCookbooksByName(name, new Callback<CookbooksResponse>() {
            @Override
            public void onSuccess(CookbooksResponse res) {
                if (res.cookbooks != null) {
                    for (Recipe r : res.cookbooks) {
                        r.save2db();
                    }
                }
                if (res.cookbooks3rd != null) {
                    for (Recipe3rd r : res.cookbooks3rd) {
                        r.save2db();
                    }
                }
                Helper.onSuccess(callback, res);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }


    public void getRecommendCookbooks(final Callback<List<Recipe>> callback) {

        if (Utils.isMobApp()) {
            RokiRestHelper
                    .getRecommendCookbooksForMob(new Callback<List<Recipe>>() {

                        @Override
                        public void onSuccess(List<Recipe> result) {
                            DaoHelper.setField(Recipe.class, AbsRecipe.COLUMN_isRecommend, false);
                            DaoHelper.setField(Recipe3rd.class, AbsRecipe.COLUMN_isRecommend, false);

                            if (result != null) {
                                for (Recipe book : result) {
                                    book.setIsRecommend(true);
                                }
                            }

                            Helper.onSuccess(callback, result);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Helper.onFailure(callback, t);
                        }
                    });
        } else {
            RokiRestHelper
                    .getRecommendCookbooksForPad(new Callback<List<Recipe>>() {

                        @Override
                        public void onSuccess(List<Recipe> result) {
                            DaoHelper.setField(Recipe.class, AbsRecipe.COLUMN_isRecommend, false);
                            DaoHelper.setField(Recipe3rd.class, AbsRecipe.COLUMN_isRecommend, false);

                            if (result != null) {
                                for (Recipe book : result) {
                                    book.setIsRecommend(true);
                                }
                            }
                            Helper.onSuccess(callback, result);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Helper.onFailure(callback, t);
                        }
                    });
        }
    }

    public void getHotKeysForCookbook(final Callback<List<String>> callback) {
        RokiRestHelper.getHotKeysForCookbook(new Callback<List<String>>() {

            @Override
            public void onSuccess(List<String> result) {
                Set<String> keys = Sets.newHashSet(result);
                PreferenceUtils.setStrings(PrefsKey.HotKeys, keys);
                Helper.onSuccess(callback, result);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }

    public void getCookbookById(long bookId, final Callback<Recipe> callback) {
        RokiRestHelper.getCookbookById(bookId, new Callback<Recipe>() {

            @Override
            public void onSuccess(Recipe result) {
                if (result != null) {
                    result.setDetailFalg(true);
                    result.save2db();
                }
                Helper.onSuccess(callback, result);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });

    }

    public void getAccessoryFrequencyForMob(final Callback<List<MaterialFrequency>> callback) {
        RokiRestHelper.getAccessoryFrequencyForMob(callback);
    }

    public void getTodayCookbooks(final Callback<CookbooksResponse> callback) {
        RokiRestHelper.getTodayCookbooks(new Callback<CookbooksResponse>() {

            @Override
            public void onSuccess(CookbooksResponse result) {
                DaoHelper.setField(Recipe.class, AbsRecipe.COLUMN_isToday, false);
                DaoHelper.setField(Recipe3rd.class, AbsRecipe.COLUMN_isToday, false);

                if (result != null) {
                    if (result.cookbooks != null) {
                        for (Recipe book : result.cookbooks) {
                            book.setIsToday(true);
                        }
                    }

                    if (result.cookbooks3rd != null) {
                        for (Recipe3rd book : result.cookbooks3rd) {
                            book.setIsToday(true);
                        }
                    }
                }
                Helper.onSuccess(callback, result);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }

    public void addTodayCookbook(final long bookId, final VoidCallback callback) {
        RokiRestHelper.addTodayCookbook(bookId, new VoidCallback() {
            @Override
            public void onSuccess() {
                AbsRecipe.setIsToday(bookId, true);
                EventUtils.postEvent(new TodayBookRefreshEvent(bookId, true));
                Helper.onSuccess(callback);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }

    public void deleteTodayCookbook(final long bookId, final VoidCallback callback) {
        RokiRestHelper.deleteTodayCookbook(bookId, new VoidCallback() {
            @Override
            public void onSuccess() {
                AbsRecipe.setIsToday(bookId, false);
                EventUtils.postEvent(new TodayBookRefreshEvent(bookId, false));
                Helper.onSuccess(callback);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }

    public void deleteAllTodayCookbook(final VoidCallback callback) {
        RokiRestHelper.deleteAllTodayCookbook(new VoidCallback() {
            @Override
            public void onSuccess() {
                DaoHelper.setField(Recipe.class, Recipe.COLUMN_isToday,
                        false);
                DaoHelper.setField(Recipe3rd.class, Recipe3rd.COLUMN_isToday,
                        false);

                EventUtils.postEvent(new TodayBookCleanEvent());
                Helper.onSuccess(callback);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }

    public void exportMaterialsFromToday(Callback<Materials> callback) {
        RokiRestHelper.exportMaterialsFromToday(callback);
    }

    public void addMaterialsToToday(long materialId, VoidCallback callback) {
        RokiRestHelper.addMaterialsToToday(materialId, callback);
    }

    public void deleteMaterialsFromToday(long materialId, VoidCallback callback) {
        RokiRestHelper.deleteMaterialsFromToday(materialId, callback);
    }

    public void getFavorityCookbooks(final Callback<CookbooksResponse> callback) {
        RokiRestHelper.getFavorityCookbooks(new Callback<CookbooksResponse>() {

            @Override
            public void onSuccess(CookbooksResponse result) {
                DaoHelper.setField(Recipe.class, AbsRecipe.COLUMN_isFavority, false);
                DaoHelper.setField(Recipe3rd.class, AbsRecipe.COLUMN_isFavority, false);

                if (result != null) {
                    if (result.cookbooks != null) {
                        for (Recipe book : result.cookbooks) {
                            book.setIsFavority(true);
                        }
                    }
                    if (result.cookbooks3rd != null) {
                        for (Recipe3rd book : result.cookbooks3rd) {
                            book.setIsFavority(true);
                        }
                    }
                }

                Helper.onSuccess(callback, result);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }

    public void addFavorityCookbooks(final long bookId, final VoidCallback callback) {
        RokiRestHelper.addFavorityCookbooks(bookId, new VoidCallback() {
            @Override
            public void onSuccess() {
                AbsRecipe.setIsFavority(bookId, true);
                EventUtils
                        .postEvent(new FavorityBookRefreshEvent(bookId, true));
                Helper.onSuccess(callback);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }

    public void deleteFavorityCookbooks(final long bookId, final VoidCallback callback) {
        RokiRestHelper.deleteFavorityCookbooks(bookId, new VoidCallback() {
            @Override
            public void onSuccess() {
                AbsRecipe.setIsFavority(bookId, false);
                EventUtils
                        .postEvent(new FavorityBookRefreshEvent(bookId, false));
                Helper.onSuccess(callback);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }

    public void delteAllFavorityCookbooks(final VoidCallback callback) {
        RokiRestHelper.delteAllFavorityCookbooks(new VoidCallback() {
            @Override
            public void onSuccess() {
                DaoHelper.setField(Recipe.class, AbsRecipe.COLUMN_isFavority, false);
                DaoHelper.setField(Recipe3rd.class, AbsRecipe.COLUMN_isFavority, false);

                EventUtils
                        .postEvent(new FavorityBookCleanEvent());
                Helper.onSuccess(callback);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }

    public void getGroundingRecipes(int start, int limit, final Callback<List<Recipe>> callback) {
        RokiRestHelper.getGroundingRecipes(start, limit, callback);
    }

    public void addCookingLog(String deviceId, long cookbookId, long start,
                              long end, boolean isBroken, VoidCallback callback) {
        RokiRestHelper.addCookingLog(deviceId, cookbookId, start, end,
                isBroken, callback);
    }

    public void getMyCookAlbumByCookbook(long cookbookId, Callback<CookAlbum> callback) {
        RokiRestHelper.getMyCookAlbumByCookbook(cookbookId, callback);
    }

    public void getOtherCookAlbumsByCookbook(long bookId, int start, int limit,
                                             Callback<List<CookAlbum>> callback) {
        RokiRestHelper.getOtherCookAlbumsByCookbook(bookId, start, limit, callback);
    }

    public void submitCookAlbum(long bookId, Bitmap image, String desc,
                                final VoidCallback callback) {
        RokiRestHelper.submitCookAlbum(bookId, image, desc, new VoidCallback() {
            @Override
            public void onSuccess() {
                EventUtils
                        .postEvent(new CookMomentsRefreshEvent());
                Helper.onSuccess(callback);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }

    public void removeCookAlbum(final long albumId, final VoidCallback callback) {
        RokiRestHelper.removeCookAlbum(albumId, new VoidCallback() {
            @Override
            public void onSuccess() {
                DaoHelper.deleteWhereEq(CookAlbum.class, CookAlbum.Col_ID, albumId);
                EventUtils
                        .postEvent(new CookMomentsRefreshEvent());
                Helper.onSuccess(callback);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }

    public void praiseCookAlbum(long albumId, VoidCallback callback) {
        RokiRestHelper.praiseCookAlbum(albumId, callback);
    }

    public void unpraiseCookAlbum(long albumId, VoidCallback callback) {
        RokiRestHelper.unpraiseCookAlbum(albumId, callback);
    }

    public void getMyCookAlbums(final Callback<List<CookAlbum>> callback) {
        RokiRestHelper.getMyCookAlbums(new Callback<List<CookAlbum>>() {
            @Override
            public void onSuccess(List<CookAlbum> albums) {
                DaoHelper.deleteAll(CookAlbum.class);
                if (albums != null) {
                    for (CookAlbum album : albums) {
                        album.save2db();
                    }
                }
                Helper.onSuccess(callback, albums);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }

    public void clearMyCookAlbums(final VoidCallback callback) {
        RokiRestHelper.clearMyCookAlbums(new VoidCallback() {
            @Override
            public void onSuccess() {
                DaoHelper.deleteAll(CookAlbum.class);
                EventUtils
                        .postEvent(new CookMomentsRefreshEvent());
                Helper.onSuccess(callback);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }


    public void getHomeAdvertsForMob(final Callback<List<MobAdvert>> callback) {
        RokiRestHelper.getHomeAdvertsForMob(new Callback<List<MobAdvert>>() {

            @Override
            public void onSuccess(List<MobAdvert> list) {
                DaoHelper.deleteAll(MobAdvert.class);
                if (list != null) {
                    for (MobAdvert advert : list) {
                        advert.save2db();
                    }
                }
                Helper.onSuccess(callback, list);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }

    public void getHomeTitleForMob(final Callback<List<MobAdvert>> callback){
        RokiRestHelper.getHomeTitleForMob(new Callback<List<MobAdvert>>() {
            @Override
            public void onSuccess(List<MobAdvert> mobAdverts) {
                DaoHelper.deleteAll(MobAdvert.class);
                if (mobAdverts != null) {
                    for (MobAdvert advert : mobAdverts) {
                        advert.save2db();
                    }
                }
                Helper.onSuccess(callback, mobAdverts);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }

    public void getHomeAdvertsForPad(
            final Callback<HomeAdvertsForPadResponse> callback) {
        RokiRestHelper
                .getHomeAdvertsForPad(new Callback<HomeAdvertsForPadResponse>() {

                    @Override
                    public void onSuccess(HomeAdvertsForPadResponse result) {
                        DaoHelper.deleteAll(Advert.PadAdvert.class);
                        if (result != null) {
                            if (result.left != null) {
                                for (Advert.PadAdvert advert : result.left) {
                                    advert.localtion = Advert.PadAdvert.LEFT;
                                    advert.save2db();
                                }
                            }
                            if (result.middle != null) {
                                for (Advert.PadAdvert advert : result.middle) {
                                    advert.localtion = Advert.PadAdvert.MIDDLE;
                                    advert.save2db();
                                }
                            }
                        }

                        Helper.onSuccess(callback, result);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Helper.onFailure(callback, t);
                    }
                });
    }

    public void getFavorityImagesForPad(
            final Callback<List<AdvertImage>> callback) {
        RokiRestHelper
                .getFavorityImagesForPad(new Callback<List<AdvertImage>>() {

                    @Override
                    public void onSuccess(List<AdvertImage> images) {
                        DaoHelper.setField(AdvertImage.class, AdvertImage.FIELD_isFavority,
                                false);
                        if (images != null) {
                            for (AdvertImage img : images) {
                                img.updateField(AdvertImage.FIELD_isFavority, true);
                            }
                        }
                        Helper.onSuccess(callback, images);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Helper.onFailure(callback, t);
                    }
                });
    }

    public void getRecommendImagesForPad(
            final Callback<List<AdvertImage>> callback) {
        RokiRestHelper
                .getRecommendImagesForPad(new Callback<List<AdvertImage>>() {

                    @Override
                    public void onSuccess(List<AdvertImage> images) {
                        DaoHelper.setField(AdvertImage.class, AdvertImage.FIELD_isRecommend,
                                false);
                        if (images != null) {
                            for (AdvertImage img : images) {
                                img.updateField(AdvertImage.FIELD_isRecommend, true);
                            }
                        }
                        Helper.onSuccess(callback, images);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Helper.onFailure(callback, t);
                    }
                });
    }

    public void getAllBookImagesForPad(
            final Callback<List<AdvertImage>> callback) {
        RokiRestHelper
                .getAllBookImagesForPad(new Callback<List<AdvertImage>>() {

                    @Override
                    public void onSuccess(List<AdvertImage> images) {
                        DaoHelper.setField(AdvertImage.class, AdvertImage.FIELD_isInAll, false);
                        if (images != null) {
                            for (AdvertImage img : images) {
                                img.updateField(AdvertImage.FIELD_isInAll, true);
                            }
                        }
                        Helper.onSuccess(callback, images);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Helper.onFailure(callback, t);
                    }
                });
    }

    public void applyAfterSale(String deviceId, VoidCallback callback) {
        RokiRestHelper.applyAfterSale(deviceId, callback);
    }

    // -------------------------------------------------------------------------------
    // 订单配送
    // -------------------------------------------------------------------------------

    public void getCustomerInfo(final Callback<OrderContacter> callback) {
        RokiRestHelper.getCustomerInfo(callback);
    }

    public void saveCustomerInfo(String name, String phone, String city, String address, final VoidCallback callback) {
        RokiRestHelper.saveCustomerInfo(name, phone, city, address, callback);
    }

    public void submitOrder(List<Long> ids, final Callback<Long> callback) {
        RokiRestHelper.submitOrder(ids, new Callback<Long>() {
            @Override
            public void onSuccess(Long result) {
                EventUtils.postEvent(new OrderRefreshEvent());
                Helper.onSuccess(callback, result);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }

    public void getOrder(long orderId, final Callback<OrderInfo> callback) {
        RokiRestHelper.getOrder(orderId, callback);
    }

    public void queryOrder(long time, int limit, final Callback<List<OrderInfo>> callback) {
        RokiRestHelper.queryOrder(time, limit, callback);
    }

    public void cancelOrder(long orderId, final VoidCallback callback) {
        RokiRestHelper.cancelOrder(orderId, new VoidCallback() {
            @Override
            public void onSuccess() {
                EventUtils.postEvent(new OrderRefreshEvent());
                Helper.onSuccess(callback);
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });
    }

    public void updateOrderContacter(long orderId, String name, String phone, String city, String address, final VoidCallback callback) {
        RokiRestHelper.updateOrderContacter(orderId, name, phone, city, address, callback);
    }

    public void orderIfOpen(final Callback<Boolean> callback) {
        RokiRestHelper.orderIfOpen(callback);
    }

    public void getEventStatus(final Callback<EventStatusReponse> callback) {
        RokiRestHelper.getEventStatus(callback);
    }

    public void deiverIfAllow(final Callback<Integer> callback) {
        RokiRestHelper.deiverIfAllow(callback);
    }


    // -------------------------------------------------------------------------------
    // 同步表态数据
    // -------------------------------------------------------------------------------

    /**
     * 初始化时同步
     */
    private void initSync() {

        isNewest(new Callback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {

                if (result) {
                    Log.d("dao", "store 已是最新版，无需更新");
                } else {
                    getCookbookProviders(null);
                    getStoreCategory(null);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                onThrowable(t);
            }
        });

        getRecommendCookbooks(null);

        long userId = getUserId();
        if (userId > 0) {
            getTodayCookbooks(null);
            getFavorityCookbooks(null);
        }

        boolean isMob = Utils.isMobApp();
        if (isMob) {
//            getHomeAdvertsForMob(null);
        } else {
            // getHomeAdvertsForPad(null);
            // getRecommendImagesForPad(null);
            // getFavorityImagesForPad(null);
            // getAllBookImagesForPad(null);
        }

    }

    // -------------------------------------------------------------------------------
    // private
    // -------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------
    // private
    // -------------------------------------------------------------------------------

    private int getLocalVersion() {
        int ver = SysCfgManager.getInstance().getLocalVersion();
        return ver;
    }

    private long getUserId() {
        return Plat.accountService.getCurrentUserId();
    }

    private void onThrowable(Throwable t) {
        t.printStackTrace();
    }

}
