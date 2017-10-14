package com.appgame.differ.utils.network;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * appGameAPI
 * Created by yukunlin on 17/2/24.
 */

public interface AppGameService {

    /**
     * 获取收藏列表
     */
    @GET("api/user/collections")
    Observable<ResponseBody> requestMineCollections(@QueryMap Map<String, Object> map);

    /**
     * 我的评价列表
     */
    @GET("/api/user/appraises")
    Observable<ResponseBody> requestAppraises(@QueryMap Map<String, Object> map);

    /**
     * 上传头像
     */
    @FormUrlEncoded
    @POST("/api/user/avatar")
    Observable<ResponseBody> uploadUserHeader(@FieldMap Map<String, Object> map);

    /**
     * 收藏
     */
    @FormUrlEncoded
    @POST("/api/user/collection")
    Observable<ResponseBody> collectionGame(@FieldMap Map<String, String> map);

    /**
     * 更改用户信息
     */
    @FormUrlEncoded
    @POST("/api/user")
    Observable<ResponseBody> updateUserInfo(@FieldMap Map<String, Object> map);

    /**
     * 通过accessToken获取用户信息
     */
    @GET("api/user")
    Observable<ResponseBody> requestUserInfoByToken(@Query("access_token") String access_token, @Query("extra") String extra);

    /**
     * 通过uid获取用户信息
     */
    @GET("api/user")
    Observable<ResponseBody> requestOtherUserInfo(@Query("access_token") String access_token, @Query("user_id") String userId, @Query("extra") String extra);

    @GET("api/user")
    Observable<ResponseBody> requestUserInfoById(@Query("user_id") String userId, @Query("extra") String extra);

    /**
     * 游戏评价列表
     */
    @GET("/api/appraises/comments")
    Observable<ResponseBody> requestGameCommList(@QueryMap Map<String, Object> map);

    /**
     * 用户回复评价
     */
    @FormUrlEncoded
    @POST("/api/appraises/comment")
    Observable<ResponseBody> submitGameCommList(@FieldMap Map<String, String> map);

    /**
     * 用户点赞/踩
     */
    @FormUrlEncoded
    @POST("/api/appraises/thumb")
    Observable<ResponseBody> submitThumb(@FieldMap Map<String, Object> map);

    /**
     * 发现页游戏分类
     */
    @GET("/api/game/categories")
    Observable<ResponseBody> getGamesClassList();

    /**
     * 发现页轮播图
     */
    @GET("api/columns")
    Observable<ResponseBody> getDiscoverSlider(@QueryMap Map<String, String> options);

    /**
     * 游戏分类页
     */
    @GET("/api/games/nature")
    Observable<ResponseBody> getGamesClassifyList(@QueryMap Map<String, String> options);

    /**
     * 获取游戏置顶列表
     */
    @GET("/api/games/manual")
    Observable<ResponseBody> getGamesClassifyManual(@QueryMap Map<String, String> options);

    /**
     * 获取游戏详情
     */
    @GET("/api/game")
    Observable<ResponseBody> getGameDetail(@QueryMap Map<String, String> options);

//    /***
//     * 游戏搜索
//     */
//    @GET("/api/games/search")
//    Observable<ResponseBody> searchGame(@QueryMap Map<String, String> options);

    /**
     * 游戏评价列表
     */
    @GET("/api/appraises")
    Observable<ResponseBody> getGameComment(@QueryMap Map<String, String> options);

    /**
     * 用户评价游戏
     */
    @FormUrlEncoded
    @POST("/api/appraises")
    Observable<ResponseBody> submitGameComment(@FieldMap Map<String, String> options);

    /**
     * 获取攻略关键词
     */
    @GET("/api/tips/keywords")
    Observable<ResponseBody> getKeywords(@QueryMap Map<String, String> options);

    /**
     * 获取攻略列表
     */
    @GET("/api/tips")
    Observable<ResponseBody> getTipsList(@QueryMap Map<String, String> options);

    /**
     * 获取栏目内容列表
     */
    @GET("/api/columns/content")
    Observable<ResponseBody> getColumnsList(@QueryMap Map<String, String> options);

    /**
     * 专题详情
     */
    @GET("/api/columns/topic")
    Observable<ResponseBody> getTopicDetail(@Query("id") String topic_id, @Query("screen_size") String screen_size, @Query("extra") String extra);

    /**
     * 版本查询
     */
    @GET("/api/version")
    Observable<ResponseBody> requestVersion(@Query("platform") String platform, @Query("provider_code") String provider_code, @Query("extra") String extra);

    /**
     * 提交游戏反馈需要的信息
     */
    @GET("/h5/feedback")
    Observable<ResponseBody> summitFeebackOptions(@QueryMap Map<String, String> options);

    /**
     * 首页列表
     */
    @GET("/api/columns")
    Observable<ResponseBody> getIndexList(@Query("game_id") String game_id);

    /**
     * 今日推荐列表
     */
    @GET("/api/columns/recommend")
    Observable<ResponseBody> getRecommendList(@Query("access_token") String token, @Query("ids") String ids, @Query("search_ids") String search_ids, @Query("extra") String extra);

    /**
     * differ日报
     */
    @GET("/api/columns/daily")
    Observable<ResponseBody> getDailyList(@Query("remark") String remark, @Query("position") String position, @Query("access_token") String token, @Query("extra") String extra);

    /**
     * 评论墙
     */
    @GET("/api/columns/appraise")
    Observable<ResponseBody> getEvaluationWall(@QueryMap Map<String, String> map);

    /**
     * 用户评论
     */
    @POST("/api/comments")
    Observable<ResponseBody> submitComment(@QueryMap Map<String, Object> map);

    /**
     * 获取用户评论
     */
    @GET("/api/comments")
    Observable<ResponseBody> getCommentList(@QueryMap Map<String, Object> map);

    /**
     * 探索游戏
     */
    @GET("/api/games/search")
    Observable<ResponseBody> gameSearch(@QueryMap Map<String, Object> map);

    /**
     * 标签列表
     */
    @GET("/api/tags")
    Observable<ResponseBody> getTagsList(@QueryMap Map<String, Object> map);

    /**
     * 标签列表
     */
    @POST("/api/tags")
    Observable<ResponseBody> putTags(@QueryMap Map<String, Object> map);

    /**
     * 用户顶/取消顶标签
     */
    @POST("/api/tags/thumb")
    Observable<ResponseBody> putTagsThumb(@QueryMap Map<String, Object> map);

    /**
     * 获取关注列表
     */
    @GET("/api/user/following")
    Observable<ResponseBody> getFollowingList(@QueryMap Map<String, Object> map);

    /**
     * 获取粉丝列表
     */
    @GET("/api/user/follower")
    Observable<ResponseBody> getFollowerList(@QueryMap Map<String, Object> map);

    /**
     * 用户评论回复
     */
    @POST("/api/replies")
    Observable<ResponseBody> repliesComment(@QueryMap Map<String, Object> map);

    /**
     * 用户评论点赞
     */
    @POST("/api/comments/thumb")
    Observable<ResponseBody> commentsThumb(@QueryMap Map<String, Object> map);

    /**
     * 导入本地游戏列表
     */
    @POST("/api/user/collection/import")
    Observable<ResponseBody> collectionImport(@QueryMap Map<String, String> map);

    /**
     * 下载完成回调接口
     */
    @POST("/api/game/downloaded")
    Observable<ResponseBody> afterDownloadSuccess(@QueryMap Map<String, String> map);

    /**
     * 关注、取消关注
     */
    @POST("/api/user/follow")
    Observable<ResponseBody> followUser(@QueryMap Map<String, String> map);

    /**
     * 获取用户留言列表
     */
    @GET("/api/user/guest")
    Observable<ResponseBody> getUserGuest(@QueryMap Map<String, String> map);

    /**
     * 提交留言
     */
    @POST("/api/user/guest")
    Observable<ResponseBody> sumbitUserGuest(@QueryMap Map<String, String> map);

    /**
     * 上传动态的图片
     */
    @FormUrlEncoded
    @POST("/api/dynamics/images")
    Observable<ResponseBody> uploadPicture(@FieldMap Map<String, Object> map);

    /**
     * 上传动态
     */
    @FormUrlEncoded
    @POST("/api/dynamics")
    Observable<ResponseBody> uploadDynamic(@FieldMap Map<String, Object> map);

    /**
     * 游戏搜索
     */
    @GET("api/games/search/name")
    Observable<ResponseBody> searchGame(@QueryMap Map<String, Object> map);

    /**
     * 动态举报
     */
    @FormUrlEncoded
    @POST("/api/report")
    Observable<ResponseBody> dynamicReport(@FieldMap Map<String, Object> map);

    /**
     * 删除动态
     */
    @FormUrlEncoded
    @POST("/api/dynamics/delete")
    Observable<ResponseBody> deleteDynamic(@FieldMap Map<String, Object> map);

    /**
     * 动态点赞
     */
    @FormUrlEncoded
    @POST("/api/dynamics/thumb")
    Observable<ResponseBody> thumbDynamic(@FieldMap Map<String, Object> map);

    /**
     * 获取动态
     */
    @GET("/api/dynamics")
    Observable<ResponseBody> getDynamic(@QueryMap Map<String, Object> map);

    /**
     * 获取动态详情
     */
    @GET("/api/dynamic")
    Observable<ResponseBody> getDynamicDetail(@QueryMap Map<String, Object> map);

    /**
     * 设置用户徽章显示/隐藏
     */
    @POST("/api/user/achieve/status")
    Observable<ResponseBody> changeAchieveStatus(@QueryMap Map<String, String> map);

    /**
     * 上传用户封面
     */
    @FormUrlEncoded
    @POST("/api/user/cover")
    Observable<ResponseBody> uploadUserCover(@FieldMap Map<String, String> map);

    /**
     * 推荐页banner
     */
    @GET("/api/discover/banners")
    Observable<ResponseBody> getDiscoverBanner(@Query("access_token") String access_token, @Query("extra") String extra);

    /**
     * 导航分类列表
     */
    @GET("/api/discover/navigation")
    Observable<ResponseBody> getDiscoverNavigation(@Query("access_token") String access_token, @Query("extra") String extra);

    /**
     * 获取常规内容列表
     */
    @GET("/api/discover/conventions")
    Observable<ResponseBody> getDiscoverConventions(@Query("access_token") String access_token,
                                                    @Query("page_size") String page_size,
                                                    @Query("position") String position,
                                                    @Query("target") String target,
                                                    @Query("extra") String extra);

    /**
     * 热门搜索
     */
    @GET("/api/games/hot")
    Observable<ResponseBody> getSearchHot();

    /**
     * 搜索游戏接口
     */
    @GET("/api/games/search/name")
    Observable<ResponseBody> searchGameByKeywords(@QueryMap Map<String, String> map);

    /**
     * 搜索文章
     */
    @GET("/api/article/search/name")
    Observable<ResponseBody> searchArticleByKeywords(@QueryMap Map<String, String> map);

    /**
     * 搜索用户
     */
    @GET("/api/user/search/name")
    Observable<ResponseBody> searchUserByKeywords(@QueryMap Map<String, String> map);

    /**
     * 文章详情
     */
    @GET("/api/columns/article")
    Observable<ResponseBody> getArticleDetail(@Query("id") String id, @Query("access_token") String access_token, @Query("extra") String extra);

    /**
     * app按分类找游戏接口(后台配置的是标签)
     */
    @GET("/api/games/tags/hot")
    Observable<ResponseBody> getTagsHot(@Query("access_token") String access_token, @Query("extra") String extra);

    /**
     * 对应分类的游戏列表(后台配置的标签)
     */
    @GET("/api/games/tags/games")
    Observable<ResponseBody> getGameTagsGames(@QueryMap Map<String, String> map);

    /**
     * 意见反馈
     */
    @GET("/api/feedbacks/types")
    Observable<ResponseBody> getFeedbacksTypes(@Query("access_token") String access_token, @Query("extra") String extra);

    /**
     * 提交反馈
     */
    @POST("/api/feedbacks")
    Observable<ResponseBody> postFeedbacks(@QueryMap Map<String, String> map);

    /**
     * 2. 导航分类列表内具体列表
     */
    @GET("/api/discover/navigates")
    Observable<ResponseBody> getDiscoverNavigates(@QueryMap Map<String, String> map);

    /**
     * 用户留言点赞
     */
    @POST("/api/guest/thumb")
    Observable<ResponseBody> sumbitGuestThumb(@Query("access_token") String access_token, @Query("guest_id") String guest_id, @Query("type") int type, @Query("extra") String extra);

    /**
     * 合并匿名用户玩过/不喜欢的游戏
     */
    @POST("/api/user/collection/merge")
    Observable<ResponseBody> collectionMerge(@Query("access_token") String access_token, @Query("anonymous_access_token") String anonymous_access_token, @Query("extra") String extra);

    /**
     * 删除评论
     */
    @POST("/api/comments/delete")
    Observable<ResponseBody> commentsDelete(@Query("access_token") String access_token, @Query("id") String id, @Query("extra") String extra);

    /**
     * 删除游戏评价接口
     */
    @POST("/api/appraises/delete")
    Observable<ResponseBody> appraisesDelete(@Query("access_token") String access_token, @Query("id") String id, @Query("extra") String extra);

    /**
     * 删除用户留言
     */
    @POST("/api/user/guest/delete")
    Observable<ResponseBody> guestDelete(@Query("access_token") String access_token, @Query("id") String id, @Query("extra") String extra);

    @GET("/api/wikis")
    Observable<ResponseBody> getWikis(@Query("position") String position);
}
