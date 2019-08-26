import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class FIRST_TIME_GUI {

    public static void first_time_config() throws IOException{

        UIManager.put("OptionPane.yesButtonText", "ΝΑΙ");
        UIManager.put("OptionPane.noButtonText", "ΟΧΙ");
        UIManager.put("OptionPane.cancelButtonText", "ΑΚΥΡΟ");
        
        int reply = JOptionPane.showConfirmDialog(Main.DJF.getDummyJFrame(), "ΤΟ ΠΡΟΓΡΑΜΜΑ ΤΡΕΧΕΙ ΓΙΑ ΠΡΩΤΗ ΦΟΡΑ ΣΤΟΝ ΥΠΟΛΟΓΙΣΤΗ ΣΑΣ\n"
                + "ΘΕΛΕΤΕ ΝΑ ΟΡΙΣΤΕ ΤΙΣ ΡΥΘΜΙΣΕΙΣ ΤΟΥ BPM AGENT;", "ΠΡΩΤΗ ΕΚΚΙΝΗΣΗ", JOptionPane.YES_NO_OPTION);
        Main.DJF.getDummyJFrame().dispose();
        
        if (reply == JOptionPane.YES_OPTION) {
            Object[][] settings = new Object[2][2];
            
            settings[0][0] = new JLabel("PROXY ADDRESS: ");
            settings[0][1] = new JTextField();
            
            settings[1][0] = new JLabel("PROXY PORT: ");
            settings[1][1] = new JTextField();

            int option = JOptionPane.showConfirmDialog(Main.DJF.getDummyJFrame(),settings,"ΡΥΘΜΙΣΕΙΣ",JOptionPane.OK_CANCEL_OPTION);
            if(option == JOptionPane.OK_OPTION)
            {
                Main.DJF.getDummyJFrame().dispose();
                FILE_OPERATIONS.set_PROXY_ADDRESS(((JTextField)settings[0][1]).getText());
                FILE_OPERATIONS.set_PROXY_PORT(((JTextField)settings[1][1]).getText());
                FILE_OPERATIONS.set_FIRST_RUN_FALSE();
            }
            else
            {
                FILE_OPERATIONS.set_FIRST_RUN_FALSE();
            }
            
        } else {
            FILE_OPERATIONS.set_FIRST_RUN_FALSE();
        }   
    }
    
    public static void install_naps()
    {

        int reply = JOptionPane.showConfirmDialog(Main.DJF.getDummyJFrame(), "ΘΕΛΕΤΕ ΝΑ ΕΓΚΑΤΑΣΤΗΣΕΤΕ ΤΟ ΠΡΟΓΡΑΜΜΑ ΣΚΑΝΑΡΙΣΜΑΤΟΣ NAPS2 ?", "ΠΡΩΤΗ ΕΚΚΙΝΗΣΗ", JOptionPane.YES_NO_OPTION);
        Main.DJF.getDummyJFrame().dispose();
        if (reply == JOptionPane.NO_OPTION) {
            Main.INSTALL_NAPS=false;
        }
    }
}
