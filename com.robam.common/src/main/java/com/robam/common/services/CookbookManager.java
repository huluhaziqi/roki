package com.robam.common.services;

import android.graphics.Bitmap;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.legent.Callback;
import com.legent.Helper;
import com.legent.VoidCallback;
import com.legent.dao.DaoHelper;
import com.legent.plat.Plat;
import com.legent.services.AbsService;
import com.legent.utils.api.PreferenceUtils;
import com.robam.common.PrefsKey;
import com.robam.common.io.cloud.Reponses.CookbooksResponse;
import com.robam.common.pojos.CookAlbum;
import com.robam.common.pojos.Group;
import com.robam.common.pojos.MaterialFrequency;
import com.robam.common.pojos.Materials;
import com.robam.common.pojos.Recipe;
import com.robam.common.pojos.Recipe3rd;
import com.robam.common.pojos.RecipeProvider;
import com.robam.common.pojos.Tag;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class CookbookManager extends AbsService {

    //6小时的更新间隔
    public static final long UpdatePeriod = 1000 * 60 * 60 * 6;
    private static CookbookManager instance = new CookbookManager();

    synchronized static public CookbookManager getInstance() {
        return instance;
    }

    StoreService ss = StoreService.getInstance();

    long lastUpdateTime_recommend;
    long lastUpdateTime_favority;

    private CookbookManager() {
    }

    public Recipe getRecipeById(long id) {
        return DaoHelper.getById(Recipe.class, id);
    }

    public Recipe3rd getRecipe3rdById(long id) {
        return DaoHelper.getById(Recipe3rd.class, id);
    }


    // ------------------------------------------------------------------------------------------------------------------

    public void saveHistoryKeysForCookbook(String word) {
        List<String> list = getHistoryKeysForCookbook();
        if (list.contains(word)) {
            list.remove(word);
        }

        list.add(0, word);
        list = list.subList(0, Math.min(3, list.size()));

        Set<String> keys = Sets.newHashSet(list);
        PreferenceUtils.setStrings(PrefsKey.HistoryKeys + Plat.accountService.getCurrentUserId(), keys);
    }

    public List<String> getHistoryKeysForCookbook() {

        Set<String> keys = PreferenceUtils.getStrings(PrefsKey.HistoryKeys + Plat.accountService.getCurrentUserId(),
                null);

        List<String> result = Lists.newArrayList();
        if (keys != null) {
            result.addAll(keys);
        }
        return result;
    }

    public void saveCookingHistory(Recipe recipe) {
        List<String> list = getCookingHistory();
        if (list.contains(recipe.name)) {
            list.remove(recipe.name);
        }

        list.add(0, recipe.name);
        list = list.subList(0, Math.min(3, list.size()));

        Set<String> keys = Sets.newHashSet(list);
        PreferenceUtils.setStrings(PrefsKey.HistoryCooking + Plat.accountService.getCurrentUserId(), keys);
    }

    public List<String> getCookingHistory() {

        Set<String> keys = PreferenceUtils.getStrings(PrefsKey.HistoryCooking + Plat.accountService.getCurrentUserId(),
                null);

        List<String> result = Lists.newArrayList();
        if (keys != null) {
            result.addAll(keys);
        }
        return result;
    }


    // ------------------------------------------------------------------------------------------------------------------

    public void getProviders(final Callback<List<RecipeProvider>> callback) {

        boolean isNewest = SysCfgManager.getInstance().isNewest();
        if (isNewest) {
            List<RecipeProvider> list = DaoHelper.getAll(RecipeProvider.class);
            if (list != null)
                Helper.onSuccess(callback, list);
            else
                ss.getCookbookProviders(callback);
        } else {
            ss.getCookbookProviders(callback);
        }

    }

    public void getGroups(final Callback<List<Group>> callback) {
        boolean isNewest = SysCfgManager.getInstance().isNewest();
        if (isNewest) {
            List<Group> list = DaoHelper.getAll(Group.class);
            if (list != null)
                Helper.onSuccess(callback, list);
            else
                ss.getStoreCategory(callback);
        } else {
            ss.getStoreCategory(callback);
        }

    }

    public void getGroupsWithoutHome(final Callback<List<Group>> callback) {

        getGroups(new Callback<List<Group>>() {

            @Override
            public void onSuccess(List<Group> result) {
                Helper.onSuccess(callback, Group.getGroupsWithoutHome());
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });

    }

    public void getHomeTags(final int count, final Callback<List<Tag>> callback) {

        getGroups(new Callback<List<Group>>() {

            @Override
            public void onSuccess(List<Group> result) {
                Group homeGroup = Group.getHomeGroup();
                List<Tag> res = Lists.newArrayList();

                if (homeGroup != null) {
                    List<Tag> tags = homeGroup.getTags();
                    if (tags != null && tags.size() > 0) {
                        res = tags.subList(0, Math.min(count, tags.size()));
                        Helper.onSuccess(callback, res);
                    } else {
                        Helper.onSuccess(callback, res);
                    }
                } else {
                    Helper.onSuccess(callback, res);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Helper.onFailure(callback, t);
            }
        });

    }

    public void getCookbooksByTag(Tag tag,
                                  final Callback<CookbooksResponse> callback) {

        if (tag.isNewest()) {
            // TinyBook
            List<Recipe> books = StoreHelper.getCookooksByTag(tag);

            // Recipe3rd
            List<Recipe3rd> books2 = StoreHelper.getThirdBooksByTag(tag);

            // CookbooksResponse
            CookbooksResponse res = new CookbooksResponse();
            res.cookbooks = books;
            res.cookbooks3rd = books2;

            Helper.onSuccess(callback, res);
        } else {
            ss.getCookbooksByTag(tag.id, callback);
        }
    }

    public void getCookbooksByName(final String name,
                                   final Callback<CookbooksResponse> callback) {
        ss.getCookbooksByName(name, new Callback<CookbooksResponse>() {

            @Override
            public void onSuccess(CookbooksResponse result) {
                Helper.onSuccess(callback, result);
            }

            @Override
            public void onFailure(Throwable t) {
                CookbooksResponse result = StoreHelper.searchByName(name);
                Helper.onSuccess(callback, result);
            }
        });
    }

    public void getHotKeysForCookbook(final Callback<List<String>> callback) {
        ss.getHotKeysForCookbook(new Callback<List<String>>() {

            @Override
            public void onSuccess(List<String> result) {
                Helper.onSuccess(callback, result);
            }

            @Override
            public void onFailure(Throwable t) {
                Set<String> keys = PreferenceUtils.getStrings(
                        PrefsKey.HotKeys, null);
                List<String> result = Lists.newArrayList();
                if (keys != null) {
                    result.addAll(keys);
                }
                Helper.onSuccess(callback, result);
            }
        });
    }

    public void getCookbookById(final long bookId, final Callback<Recipe> callback) {
        Recipe recipe = DaoHelper.getById(Recipe.class, bookId);
        if (recipe != null && recipe.isNewest()) {
            Helper.onSuccess(callback, recipe);
        } else {
            ss.getCookbookById(bookId, new Callback<Recipe>() {
                @Override
                public void onSuccess(Recipe recipe) {
                    Helper.onSuccess(callback, recipe);
                }

                @Override
                public void onFailure(Throwable t) {
                    Helper.onFailure(callback, t);
                }
            });
        }
    }

    public void getTodayCookbooks(final Callback<CookbooksResponse> callback) {
        ss.getTodayCookbooks(new Callback<CookbooksResponse>() {

            @Override
            public void onSuccess(CookbooksResponse result) {
                Helper.onSuccess(callback, result);
            }

            @Override
            public void onFailure(Throwable t) {
                CookbooksResponse result = StoreHelper.getTodayList();
                Helper.onSuccess(callback, result);
            }
        });
    }

    public void getCookbooks(final Callback<List<Recipe>> callback) {
        List<Recipe> list = DaoHelper.getAll(Recipe.class);
        if (list != null && list.size() > 0) {
            Helper.onSuccess(callback, list);
        } else {
        }
    }

    public void getFavorityCookbooks(final Callback<CookbooksResponse> callback) {
        //是否10分钟内更新
        boolean isNearest = Calendar.getInstance().getTimeInMillis() - lastUpdateTime_favority <= UpdatePeriod;
        if (isNearest) {
            CookbooksResponse result = StoreHelper.getFavorityList();
            Helper.onSuccess(callback, result);
        } else {
            ss.getFavorityCookbooks(new Callback<CookbooksResponse>() {

                @Override
                public void onSuccess(CookbooksResponse result) {
                    lastUpdateTime_favority = Calendar.getInstance().getTimeInMillis();
                    Helper.onSuccess(callback, result);
                }

                @Override
                public void onFailure(Throwable t) {
                    CookbooksResponse result = StoreHelper.getFavorityList();
                    Helper.onSuccess(callback, result);
                }
            });
        }
    }

    public void getRecommendCookbooks(final Callback<List<Recipe>> callback) {

        //是否10分钟内更新
        boolean isNearest = Calendar.getInstance().getTimeInMillis() - lastUpdateTime_recommend <= UpdatePeriod;
//        if (isNearest) {
//            CookbooksResponse result = StoreHelper.getRecommendList();
//            Helper.onSuccess(callback, result.cookbooks);
//        } else {
            ss.getRecommendCookbooks(new Callback<List<Recipe>>() {

                @Override
                public void onSuccess(List<Recipe> result) {
                    lastUpdateTime_recommend = Calendar.getInstance().getTimeInMillis();
                    Helper.onSuccess(callback, result);
                }

                @Override
                public void onFailure(Throwable t) {
                    CookbooksResponse result = StoreHelper.getRecommendList();
                    Helper.onSuccess(callback, result.cookbooks);
                }
            });
//        }
    }

    public void getGroundingRecipes(int start, int limit, final Callback<List<Recipe>> callback) {
        ss.getGroundingRecipes(start, limit, callback);
    }


    // ------------------------------------------------------------------------------------------------------------------

    public void addTodayCookbook(final long bookId, final VoidCallback callback) {
        ss.addTodayCookbook(bookId, callback);
    }

    public void deleteTodayCookbook(final long bookId,
                                    final VoidCallback callback) {
        ss.deleteTodayCookbook(bookId, callback);
    }

    public void deleteAllTodayCookbook(final VoidCallback callback) {
        ss.deleteAllTodayCookbook(callback);
    }

    public void exportMaterialsFromToday(Callback<Materials> callback) {
        // TODO
        ss.exportMaterialsFromToday(callback);
    }

    public void addMaterialsToToday(long materialId, VoidCallback callback) {
        // TODO
        ss.addMaterialsToToday(materialId, callback);
    }

    public void deleteMaterialsFromToday(long materialId, VoidCallback callback) {
        // TODO
        ss.deleteMaterialsFromToday(materialId, callback);
    }

    // ------------------------------------------------------------------------------------------------------------------

    public void addFavorityCookbooks(final long bookId,
                                     final VoidCallback callback) {
        ss.addFavorityCookbooks(bookId, callback);
    }

    public void deleteFavorityCookbooks(final long bookId,
                                        final VoidCallback callback) {
        ss.deleteFavorityCookbooks(bookId, callback);
    }

    public void delteAllFavorityCookbooks(final VoidCallback callback) {
        ss.delteAllFavorityCookbooks(callback);
    }

    // ------------------------------------------------------------------------------------------------------------------


    public void addCookingLog(String deviceGuid, Recipe recipe, long startTime, long endTime, boolean isBroken, VoidCallback callback) {
        if (recipe == null) return;
        saveCookingHistory(recipe);
        ss.addCookingLog(deviceGuid, recipe.id, startTime, endTime, isBroken, callback);
    }


    public void getMyCookAlbumByCookbook(long bookId, Callback<CookAlbum> callback) {
        // TODO
        ss.getMyCookAlbumByCookbook(bookId, callback);
    }

    public void getOtherCookAlbumsByCookbook(long bookId, int start, int limit,
                                             Callback<List<CookAlbum>> callback) {
        // TODO
        ss.getOtherCookAlbumsByCookbook(bookId, start, limit, callback);
    }

    public void submitCookAlbum(long bookId, Bitmap image, String desc,
                                final VoidCallback callback) {
        ss.submitCookAlbum(bookId, image, desc, callback);
    }

    public void removeCookAlbum(final long albumId, final VoidCallback callback) {
        ss.removeCookAlbum(albumId, callback);
    }

    public void praiseCookAlbum(long albumId, final VoidCallback callback) {
        ss.praiseCookAlbum(albumId, callback);
    }

    public void unpraiseCookAlbum(long albumId, final VoidCallback callback) {
        ss.unpraiseCookAlbum(albumId, callback);
    }

    public void getAccessoryFrequencyForMob(final Callback<List<MaterialFrequency>> callback) {
        ss.getAccessoryFrequencyForMob(callback);
    }

    public void getMyCookAlbums(final Callback<List<CookAlbum>> callback) {
        ss.getMyCookAlbums(new Callback<List<CookAlbum>>() {
            @Override
            public void onSuccess(List<CookAlbum> cookAlbums) {
                Helper.onSuccess(callback, cookAlbums);
            }

            @Override
            public void onFailure(Throwable t) {
                List<CookAlbum> cookAlbums = DaoHelper.getAll(CookAlbum.class);
                Helper.onSuccess(callback, cookAlbums);
            }
        });
    }

    public void clearMyCookAlbums(final VoidCallback callback) {
        ss.clearMyCookAlbums(callback);
    }

    // ------------------------------------------------------------------------------------------------------------------


}
