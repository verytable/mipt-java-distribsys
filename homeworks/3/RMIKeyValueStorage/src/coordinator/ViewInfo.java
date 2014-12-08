package coordinator;

import java.io.Serializable;

/**
 * Created by arseny on 16.11.14.
 */
public class ViewInfo implements Serializable {

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

    @Override
    public String toString() {
        return "ViewInfo{" +
                "view=" + view +
                ", primary='" + primary + '\'' +
                ", backup='" + backup + '\'' +
                '}';
    }
}
