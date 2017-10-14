package com.appgame.differ.bean.game;

import org.json.JSONObject;

/**
 * Created by yukunlin on 17/4/25.
 */

public class AppraisePoints {
    private int count;
    private ListBean list;

    public AppraisePoints resolveAppraisePoints(JSONObject object) {
        AppraisePoints points = new AppraisePoints();
        ListBean listBean = new ListBean();
        points.setCount(object.optInt("count"));
        JSONObject listOb = object.optJSONObject("list");
        listBean.setStar1(listOb.optInt("star1"));
        listBean.setStar2(listOb.optInt("star2"));
        listBean.setStar3(listOb.optInt("star3"));
        listBean.setStar4(listOb.optInt("star4"));
        listBean.setStar5(listOb.optInt("star5"));
        points.setList(listBean);
        return points;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ListBean getList() {
        return list;
    }

    public void setList(ListBean list) {
        this.list = list;
    }

    public class ListBean {
        private int star1;
        private int star2;
        private int star3;
        private int star4;
        private int star5;

        public int getStar1() {
            return star1;
        }

        public void setStar1(int star1) {
            this.star1 = star1;
        }

        public int getStar2() {
            return star2;
        }

        public void setStar2(int star2) {
            this.star2 = star2;
        }

        public int getStar3() {
            return star3;
        }

        public void setStar3(int star3) {
            this.star3 = star3;
        }

        public int getStar4() {
            return star4;
        }

        public void setStar4(int star4) {
            this.star4 = star4;
        }

        public int getStar5() {
            return star5;
        }

        public void setStar5(int star5) {
            this.star5 = star5;
        }
    }
}
