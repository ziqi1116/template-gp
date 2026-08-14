package com.gp.system.domain;

import java.util.List;

import lombok.Data;

@Data
public class RouterVo {

    private String name;
    private String path;
    private String component;
    private Boolean hidden = false;
    private String redirect;
    private Boolean alwaysShow = false;
    private MetaVo meta;
    private List<RouterVo> children;

    @Data
    public static class MetaVo {

        private String title;
        private String icon;
        private boolean noCache = false;
        private String link;

        public MetaVo(String title, String icon) {
            this.title = title;
            this.icon = icon;
        }

    }

}
