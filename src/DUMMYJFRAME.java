import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;

public class DUMMYJFRAME {

    private JFrame dummyJFrame;
    
    public DUMMYJFRAME() {
        dummyJFrame = new JFrame();
        dummyJFrame.setVisible(false);
        dummyJFrame.setAlwaysOnTop(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        dummyJFrame.setLocation(dim.width / 2 - dummyJFrame.getSize().width / 2, dim.height / 2 - dummyJFrame.getSize().height / 2);
    }
    
    public JFrame getDummyJFrame()
    {
        return dummyJFrame;
    }
}