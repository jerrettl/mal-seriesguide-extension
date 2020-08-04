package com.malseriesguideextension;

public class AnimeSearchResult {
    private String name;
    private String url;

    public AnimeSearchResult(String name, String url) {
        setName(name);
        setUrl(url);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }
}
