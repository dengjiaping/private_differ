package com.appgame.differ.bean.game;

import java.util.List;

/**
 * Created by yukunlin on 17/4/27.
 */

public class Attributes {
    private String game_name_cn;
    private String avg_appraise_star;
    private List<Category> category;
    private String cover;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getGame_name_cn() {
        return game_name_cn;
    }

    public void setGame_name_cn(String game_name_cn) {
        this.game_name_cn = game_name_cn;
    }

    public String getAvg_appraise_star() {
        return avg_appraise_star;
    }

    public void setAvg_appraise_star(String avg_appraise_star) {
        this.avg_appraise_star = avg_appraise_star;
    }

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }

    public static class Category {
        private String id;
        private String name;
        private String icon;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}
