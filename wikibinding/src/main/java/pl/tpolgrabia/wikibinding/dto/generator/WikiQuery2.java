package pl.tpolgrabia.wikibinding.dto.generator;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by tpolgrabia on 01.11.16.
 */
public class WikiQuery2 {
    @SerializedName("pages")
    private Map<Long, WikiPage> pages;

    public Map<Long, WikiPage> getPages() {
        return pages;
    }

    public void setPages(Map<Long, WikiPage> pages) {
        this.pages = pages;
    }

    @Override
    public String toString() {
        return "WikiQuery2{" +
            "pages=" + pages +
            '}';
    }
}
