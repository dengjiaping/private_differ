# 把Rx相关放到主dex，否则热修复会报错
-keep class io.reactivex.**{*;}
-keep class io.reactivex.android.**{*;}
-keep class com.trello.rxlifecycle2.**{*;}