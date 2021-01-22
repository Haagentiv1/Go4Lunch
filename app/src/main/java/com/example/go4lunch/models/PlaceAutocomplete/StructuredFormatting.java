
package com.example.go4lunch.models.PlaceAutocomplete;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StructuredFormatting {

    @SerializedName("main_text")
    @Expose
    private String mainText;
    @SerializedName("main_text_matched_substrings")
    @Expose
    private List<MainTextMatchedSubstring> mainTextMatchedSubstrings = null;
    @SerializedName("secondary_text")
    @Expose
    private String secondaryText;
    @SerializedName("secondary_text_matched_substrings")
    @Expose
    private List<SecondaryTextMatchedSubstring> secondaryTextMatchedSubstrings = null;

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public List<MainTextMatchedSubstring> getMainTextMatchedSubstrings() {
        return mainTextMatchedSubstrings;
    }

    public void setMainTextMatchedSubstrings(List<MainTextMatchedSubstring> mainTextMatchedSubstrings) {
        this.mainTextMatchedSubstrings = mainTextMatchedSubstrings;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }

    public List<SecondaryTextMatchedSubstring> getSecondaryTextMatchedSubstrings() {
        return secondaryTextMatchedSubstrings;
    }

    public void setSecondaryTextMatchedSubstrings(List<SecondaryTextMatchedSubstring> secondaryTextMatchedSubstrings) {
        this.secondaryTextMatchedSubstrings = secondaryTextMatchedSubstrings;
    }

}
