package java;

/**
 * Created by arseny on 16.11.14.
 */
public class ViewInfo {

    public int view;
    public String primary;
    public String backup;

    public ViewInfo(ViewInfo other) {
        view = other.view;
        primary = other.primary;
        backup = other.backup;
    }

    public ViewInfo() {
        view = 0;
        primary = "";
        backup = "";
    }
}
