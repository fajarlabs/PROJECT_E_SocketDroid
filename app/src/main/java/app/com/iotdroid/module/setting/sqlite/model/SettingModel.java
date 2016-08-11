package app.com.iotdroid.module.setting.sqlite.model;

/**
 * Created by masfajar on 6/22/2016.
 */
public class SettingModel {
    private String optionName;
    private String optionValue;

    public SettingModel() {}

    public SettingModel(String optionName, String optionValue) {
        this.optionName = optionName;
        this.optionValue = optionValue;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }
}
