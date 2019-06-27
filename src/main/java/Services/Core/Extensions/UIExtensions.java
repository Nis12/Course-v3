package Services.Core.Extensions;

import java.awt.*;

public class UIExtensions {

    public static Point centerPosition(Dimension dialogSize) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Point point = new Point();
        point.x = screenSize.width / 2 - dialogSize.width / 2;
        point.y = screenSize.height / 2 - dialogSize.height / 2;
        return  point;
    }
}
