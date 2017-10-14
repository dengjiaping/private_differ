package com.appgame.differ.base.app;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created by lzx on 2017/6/19.
 */

public class BaseTinkerApplication extends TinkerApplication {
    public BaseTinkerApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.appgame.differ.base.app.DifferApplication",
                "com.tencent.tinker.loader.TinkerLoader", false);
    }
}
