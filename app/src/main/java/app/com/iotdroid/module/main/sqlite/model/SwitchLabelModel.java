package app.com.iotdroid.module.main.sqlite.model;

/**
 * Created by masfajar on 6/23/2016.
 */
public class SwitchLabelModel {
    private Integer id;
    private int stack;
    private String title;
    private String description;

    public SwitchLabelModel() {}

    public SwitchLabelModel(Integer id, int stack, String title, String description) {
        this.id = id;
        this.stack = stack;
        this.title = title;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getStack() {
        return stack;
    }

    public void setStack(int stack) {
        this.stack = stack;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
