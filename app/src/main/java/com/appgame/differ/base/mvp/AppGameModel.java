package com.appgame.differ.base.mvp;

import android.text.TextUtils;

import com.appgame.differ.bean.daily.CommentReplies;
import com.appgame.differ.bean.daily.DailyComment;
import com.appgame.differ.bean.daily.DailyInfo;
import com.appgame.differ.bean.daily.DailyListInfo;
import com.appgame.differ.bean.dynamic.DynamicInfo;
import com.appgame.differ.bean.evaluation.EvaluationInfo;
import com.appgame.differ.bean.evaluation.UserAppraise;
import com.appgame.differ.bean.find.DiscoverNavInfo;
import com.appgame.differ.bean.game.GameInfo;
import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.bean.recommend.TopicInfo;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.network.RetrofitHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

import static com.appgame.differ.data.constants.AppConstants.page_size;
import static com.appgame.differ.utils.network.RetrofitHelper.getAppGameAPI;

/**
 * Created by lzx on 2017/8/17.
 */

public class AppGameModel {


    public Observable<DailyInfo> getArticleDetail(String id) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        return getAppGameAPI().getArticleDetail(id, token, CommonUtil.getExtraParam())
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    DailyInfo dailyInfo = new DailyInfo();
                    JSONObject object = jsonObject.getJSONObject("data");
                    dailyInfo.resolveJson(object);
                    return dailyInfo;
                });
    }

    /**
     * 获取动态列表
     */
    public Observable<List<DynamicInfo>> getDynamic(String userId, String position) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", token);
        if (!TextUtils.isEmpty(userId)) {
            map.put("user_id", userId);
        }
        map.put("page_size", !CommonUtil.isLogin() ? "3" : String.valueOf(page_size));
        map.put("position", position);
        map.put("extra", CommonUtil.getExtraParam());
        return getAppGameAPI().getDynamic(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    return makeDynamicInfoList(jsonObject);
                });
    }

    private List<DynamicInfo> makeDynamicInfoList(JSONObject dynamicObject) {
        JSONArray list = dynamicObject.optJSONObject("data").optJSONArray("list");
        List<DynamicInfo> dynamicList = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            DynamicInfo info = new DynamicInfo();
            info.resolveJson(list.optJSONObject(i));
            info.postion = dynamicObject.optJSONObject("meta").optString("position");
            dynamicList.add(info);
        }
        return dynamicList;
    }

    /**
     * 删除动态
     */
    public Observable<ResponseBody> deleteDynamic(String dynamic_id) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", token);
        map.put("dynamic_id", dynamic_id);
        map.put("extra", CommonUtil.getExtraParam());
        return getAppGameAPI().deleteDynamic(map);
    }

    /**
     * 动态点赞
     */
    public Observable<Boolean> thumbDynamic(String dynamic_id, int type) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", token);
        map.put("dynamic_id", dynamic_id);
        map.put("type", type);
        map.put("extra", CommonUtil.getExtraParam());
        return getAppGameAPI().thumbDynamic(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    int status = jsonObject.getJSONObject("meta").optInt("status", -1);
                    return status == 200;
                });
    }


    /**
     * 获取动态详情
     */
    public Observable<DynamicInfo> requestDynamicDetail(String dynamic_id) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", token);
        map.put("dynamic_id", dynamic_id);
        map.put("extra", CommonUtil.getExtraParam());
        return getAppGameAPI().getDynamicDetail(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONObject data = jsonObject.getJSONObject("data");
                    DynamicInfo info = new DynamicInfo();
                    info.resolveJson(data);
                    return info;
                });
    }

    /**
     * 获取动态评论（动态，评价）
     */
    public Observable<List<DailyComment>> requestDynamicComment(String target, String target_id, String position, String page_size) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", token);
        map.put("target", target);
        map.put("target_id", target_id);
        map.put("position", position);
        map.put("page_size", page_size);
        map.put("extra", CommonUtil.getExtraParam());
        return getAppGameAPI().getCommentList(map)
                .map(responseBody -> {
                    List<DailyComment> list = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONArray listArray = jsonObject.getJSONObject("data").getJSONArray("list");
                    for (int i = 0; i < listArray.length(); i++) {
                        DailyComment comment = new DailyComment();
                        comment.position = jsonObject.getJSONObject("meta").getString("position");
                        JSONObject comOb = listArray.getJSONObject(i);
                        comment.setComId(comOb.optString("id"));

                        JSONObject attr = comOb.getJSONObject("attributes");
                        comment.setContent(attr.optString("content"));
                        comment.setReplied(attr.optString("replied"));
                        comment.setThumbsUp(attr.optString("thumbs_up"));
                        comment.setCreatedAt(attr.optString("created_at"));
                        comment.setIs_thumb(attr.getInt("is_thumb"));

                        List<CommentReplies> repliesList = new ArrayList<CommentReplies>();
                        JSONArray repliesArray = attr.getJSONArray("replies");
                        for (int j = 0; j < repliesArray.length(); j++) {
                            CommentReplies replies = new CommentReplies();
                            replies.setType(repliesArray.getJSONObject(j).optString("type"));
                            replies.setId(repliesArray.getJSONObject(j).optString("id"));
                            JSONObject repliesOb = repliesArray.getJSONObject(j).getJSONObject("attributes");
                            replies.setContent(repliesOb.optString("content"));
                            replies.setIsReplied(repliesOb.optString("is_replied"));
                            replies.setReplyId(repliesOb.optString("reply_id"));
                            replies.setReplyUserId(repliesOb.optString("reply_user_id"));
                            replies.setCreatedAt(repliesOb.optString("created_at"));
                            JSONObject relationOb = repliesArray.getJSONObject(j).getJSONObject("relationships");
                            UserInfo relation = UserInfoManager.getImpl().resolveUserInfo(relationOb.getJSONObject("author"));
                            replies.setRelationships(relation);
                            repliesList.add(replies);
                        }
                        comment.setReplies(repliesList);

                        JSONObject author = listArray.getJSONObject(i).getJSONObject("relationships").getJSONObject("author");
                        UserInfo relation = UserInfoManager.getImpl().resolveUserInfo(author);
                        comment.setRelationships(relation);

                        list.add(comment);
                    }
                    return list;
                });
    }

    /**
     * 发布评论
     */
    public Observable<Boolean> postComment(String target, String target_id, String content) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", token);
        map.put("target", target);
        map.put("target_id", target_id);
        map.put("content", content);
        map.put("extra", CommonUtil.getExtraParam());
        return getAppGameAPI().submitComment(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    int status = jsonObject.getJSONObject("meta").getInt("status");
                    String msg = jsonObject.getJSONObject("meta").optString("message");
                    return status == 200 && msg.equals("success");
                });
    }

    /**
     * 回复评论
     */
    public Observable<Boolean> repliesComment(String comment_id, String content, String is_replied, String reply_id, String reply_user_id) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", token);
        map.put("comment_id", comment_id);
        map.put("content", content);
        map.put("is_replied", is_replied); //是 0 否 1
        map.put("reply_id", reply_id);
        map.put("reply_user_id", reply_user_id);
        map.put("extra", CommonUtil.getExtraParam());
        return getAppGameAPI().repliesComment(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    int status = jsonObject.getJSONObject("meta").getInt("status");
                    String msg = jsonObject.getJSONObject("meta").optString("message");
                    return status == 200 && msg.equals("success");
                });
    }

    /**
     * 评论点赞
     */
    public Observable<Boolean> postDiscussThumb(String comment_id, int type) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", token);
        map.put("comment_id", comment_id);
        map.put("type", type);
        map.put("extra", CommonUtil.getExtraParam());
        return getAppGameAPI().commentsThumb(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    int status = jsonObject.getJSONObject("meta").getInt("status");
                    String msg = jsonObject.getJSONObject("meta").optString("message");
                    return status == 200 && msg.equals("success");
                });
    }


    public Observable<List<TagsInfo>> getTagsList(String target, String target_id, int page_size) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        Map<String, Object> map = new HashMap<>();
        map.put("target", target);
        map.put("target_id", target_id);
        if (CommonUtil.isLogin()) {
            map.put("access_token", token);
        }
        map.put("page_size", page_size);
        map.put("extra", CommonUtil.getExtraParam());
        return RetrofitHelper.getAppGameAPI().getTagsList(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("list");
                    List<TagsInfo> list = new ArrayList<TagsInfo>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject attr = jsonArray.getJSONObject(i).getJSONObject("attributes");
                        TagsInfo tagsInfo = new TagsInfo();
                        tagsInfo.setId(attr.getString("id"));
                        tagsInfo.setName(attr.getString("name"));
                        tagsInfo.setThumbsUp(attr.getString("thumbs_up"));
                        tagsInfo.setIsThumb(attr.getInt("is_thumb"));
                        list.add(tagsInfo);
                    }
                    return list;
                });
    }

    public Observable<Boolean> putTags(String target, String target_id, String name) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", token);
        map.put("target", target);
        map.put("target_id", target_id);
        map.put("name", name);
        map.put("extra", CommonUtil.getExtraParam());
        return RetrofitHelper.getAppGameAPI().putTags(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    int status = jsonObject.getJSONObject("meta").getInt("status");
                    String msg = jsonObject.getJSONObject("meta").getString("message");
                    return status == 200 && msg.equals("success");
                });
    }

    public Observable<Boolean> putTagsThumb(String tag_id, final int type) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", token);
        map.put("tag_id", tag_id);
        map.put("type", type); //1:顶， 0: 取消顶
        map.put("extra", CommonUtil.getExtraParam());
        return getAppGameAPI().putTagsThumb(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    int status = jsonObject.getJSONObject("meta").getInt("status");
                    String msg = jsonObject.getJSONObject("meta").getString("message");
                    return status == 200 && msg.equals("success");
                });
    }

    public Observable<Boolean> followUser(String follow_id, final String action) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        Map<String, String> map = new HashMap<>();
        map.put("access_token", token);
        map.put("follow_id", follow_id);
        if (action.equals("cancel")) {
            map.put("action", action);
        }
        map.put("extra", CommonUtil.getExtraParam());
        return RetrofitHelper.getAppGameAPI().followUser(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    int status = jsonObject.getJSONObject("meta").getInt("status");
                    String msg = jsonObject.getJSONObject("meta").getString("message");
                    return status == 200 && msg.equals("success");
                });
    }

    public Observable<DailyListInfo> getDailyList(String target, int position) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        return RetrofitHelper.getAppGameAPI().getDailyList(target, String.valueOf(position), token, CommonUtil.getExtraParam())
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    DailyListInfo info = new DailyListInfo();
                    info.resolveJson(jsonObject.optJSONObject("data"));
                    info.position = jsonObject.getJSONObject("meta").getString("position");
                    return info;
                });
    }

    public Observable<List<RecommedInfo>> getRecommendList(String ids, String search_ids) {
        final String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        return getAppGameAPI().getRecommendList(token, ids, search_ids, CommonUtil.getExtraParam())
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    List<RecommedInfo> list = new ArrayList<RecommedInfo>();
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject info = dataArray.getJSONObject(i);
                        RecommedInfo recommedInfo = new RecommedInfo();
                        recommedInfo.resolveJson(info);
                        list.add(recommedInfo);
                    }
                    return list;
                });
    }

    public Observable<Boolean> collectionGame(String gameId, String type) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, String> map = new HashMap<>();
        map.put("access_token", token);
        map.put("game_id", gameId);
        map.put("type", type);
        map.put("extra", CommonUtil.getExtraParam());
        return RetrofitHelper.getAppGameAPI().collectionGame(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    int status = jsonObject.getJSONObject("meta").getInt("status");
                    return status == 200;
                });
    }

    public Observable<GameInfo> getGameDetail(String game_id) {
        Map<String, String> options = new HashMap<>();
        options.put("game_id", game_id);
        options.put("access_token", SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN));
        options.put("extra", CommonUtil.getExtraParam());
        return RetrofitHelper.getAppGameAPI().getGameDetail(options)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    GameInfo gameInfo = new GameInfo();
                    gameInfo.resolveGameInfo(jsonObject.getJSONObject("data"));
                    return gameInfo;
                });
    }

    public Observable<List<UserAppraise>> getEvaluationList(String game_id, String order, String position, int page_size) {
        String access_token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, String> map = new HashMap<>();
        map.put("game_id", game_id);
        map.put("access_token", access_token);
        map.put("position", position);
        map.put("page_size", String.valueOf(page_size));
        map.put("order", order);
        map.put("extra", CommonUtil.getExtraParam());
        return RetrofitHelper.getAppGameAPI().getGameComment(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONObject data = jsonObject.getJSONObject("data");
                    List<UserAppraise> list = new ArrayList<UserAppraise>();
                    JSONArray listArray = data.optJSONArray("list");
                    if (listArray != null) {
                        for (int i = 0; i < listArray.length(); i++) {
                            JSONObject object = listArray.optJSONObject(i);
                            UserAppraise appraise = new UserAppraise();
                            appraise.resolveJson(object);
                            appraise.position = jsonObject.getJSONObject("meta").getString("position");
                            appraise.count = jsonObject.getJSONObject("meta").getString("count");
                            list.add(appraise);
                        }
                    }
                    return list;
                });
    }

    public Observable<UserAppraise> getUserAppraise(String game_id, String order, String position) {
        String access_token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, String> map = new HashMap<>();
        map.put("game_id", game_id);
        map.put("access_token", access_token);
        map.put("position", position);
        map.put("page_size", String.valueOf(page_size));
        map.put("order", order);
        map.put("extra", CommonUtil.getExtraParam());
        return RetrofitHelper.getAppGameAPI().getGameComment(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONObject data = jsonObject.getJSONObject("data");
                    UserAppraise appraise = new UserAppraise();
                    try {
                        appraise.resolveJson(data.getJSONObject("user_appraise"));
                        return appraise;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new UserAppraise();
                    }
                });
    }

    public Observable<Boolean> thumbEvaluation(String comment_id, int type) {
        String access_token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("appraise_id", comment_id);
        map.put("type", type);
        map.put("extra", CommonUtil.getExtraParam());
        return RetrofitHelper.getAppGameAPI().submitThumb(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    int status = jsonObject.getJSONObject("meta").optInt("status", -1);
                    return status == 200;
                });
    }

    public Observable<EvaluationInfo> getDiscussList(String target, String target_id, String position, int page_size) {
        Map<String, Object> map = new HashMap<>();
        map.put("target", target);
        map.put("target_id", target_id);
        map.put("position", position);
        map.put("page_size", page_size);
        map.put("access_token", SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, ""));
        map.put("extra", CommonUtil.getExtraParam());
        return RetrofitHelper.getAppGameAPI().getCommentList(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONObject data = jsonObject.getJSONObject("data");

                    EvaluationInfo evaluationInfo = new EvaluationInfo();

                    evaluationInfo.position = jsonObject.getJSONObject("meta").getString("position");

                    JSONObject targetData = data.optJSONObject("target_data");
                    UserAppraise userAppraise = new UserAppraise();
                    userAppraise.resolveJson(targetData);
                    evaluationInfo.setUserAppraise(userAppraise);

                    List<DailyComment> list = new ArrayList<DailyComment>();
                    JSONArray listArray = data.optJSONArray("list");
                    if (listArray != null) {
                        for (int i = 0; i < listArray.length(); i++) {
                            JSONObject object = listArray.optJSONObject(i);
                            DailyComment comment = new DailyComment();
                            comment.setComId(object.getString("id"));
                            JSONObject attr = object.getJSONObject("attributes");
                            comment.setContent(attr.getString("content"));
                            comment.setThumbsUp(attr.getString("thumbs_up"));
                            comment.setCreatedAt(attr.getString("created_at"));
                            comment.setIs_thumb(attr.getInt("is_thumb"));
                            List<CommentReplies> repliesList = new ArrayList<CommentReplies>();
                            JSONArray repliesArray = attr.getJSONArray("replies");
                            for (int j = 0; j < repliesArray.length(); j++) {
                                CommentReplies replies = new CommentReplies();
                                replies.setType(repliesArray.getJSONObject(j).getString("type"));
                                replies.setId(repliesArray.getJSONObject(j).getString("id"));
                                JSONObject repliesOb = repliesArray.getJSONObject(j).getJSONObject("attributes");
                                replies.setContent(repliesOb.getString("content"));
                                replies.setIsReplied(repliesOb.getString("is_replied"));
                                replies.setReplyId(repliesOb.getString("reply_id"));
                                replies.setReplyUserId(repliesOb.getString("reply_user_id"));
                                replies.setCreatedAt(repliesOb.getString("created_at"));
                                JSONObject relationOb = repliesArray.getJSONObject(j).getJSONObject("relationships");
                                UserInfo relation = UserInfoManager.getImpl().resolveUserInfo(relationOb.getJSONObject("author"));
                                replies.setRelationships(relation);
                                repliesList.add(replies);
                            }
                            comment.setReplies(repliesList);
                            JSONObject author = object.getJSONObject("relationships").getJSONObject("author");
                            UserInfo relation = UserInfoManager.getImpl().resolveUserInfo(author);
                            comment.setRelationships(relation);
                            list.add(comment);
                        }
                    }
                    evaluationInfo.setCommentList(list);
                    return evaluationInfo;
                });
    }

    public Observable<DiscoverNavInfo> requestDiscoverNavInfo(String id, String position) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, String> map = new HashMap<>();
        map.put("access_token", token);
        map.put("id", id);
        map.put("position", position);
        map.put("page_size", String.valueOf(page_size));
        map.put("extra", CommonUtil.getExtraParam());
        return RetrofitHelper.getAppGameAPI().getDiscoverNavigates(map)
                .map(responseBody -> {
                    DiscoverNavInfo navInfo = new DiscoverNavInfo();
                    List<RecommedInfo> recommedInfos = new ArrayList<RecommedInfo>();
                    List<DailyInfo> dailyInfos = new ArrayList<DailyInfo>();
                    List<TopicInfo> topicInfos = new ArrayList<TopicInfo>();

                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    navInfo.position = jsonObject.getJSONObject("meta").getString("position");
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String type = object.getString("type");
                        if (type.equals("game")) {
                            RecommedInfo recom = new RecommedInfo();
                            recom.resolveJson(object);
                            recommedInfos.add(recom);
                        } else if (type.equals("articles")) {
                            DailyInfo dailyInfo = new DailyInfo();
                            dailyInfo.resolveJson(object);
                            dailyInfos.add(dailyInfo);
                        } else if (type.equals("topics")) {
                            TopicInfo topicInfo = new TopicInfo();
                            topicInfo.resolveJson(object);
                            topicInfos.add(topicInfo);
                        }
                    }
                    navInfo.setRecommedInfos(recommedInfos);
                    navInfo.setDailyInfos(dailyInfos);
                    navInfo.setTopicInfos(topicInfos);
                    return navInfo;
                });

    }

}
