import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.ExternalOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

/**
 * This class manage the JODConverter office manager.
 * If a OOo process is already running, the office manager
 * just uses it. If no LOo process exists, the the office manager
 * launches it.
 * The stop() method is executed just when the officeManager is
attached to
 * an external process in order to maintain it running.
 */
public class OfficeManagerWrapper {

        private OfficeManager officeManager;
        private boolean externalProcess;
        private int port;

        /**
         * Constructor
         * @param port
         */
        public OfficeManagerWrapper(int port) {

                this.port = port;
                try {
                        initFromExistingLOInstance();
                }  catch (org.artofsolving.jodconverter.office.OfficeException e) {
                        initFromNewLOInstance();
                }
        }

        /**
         * Try to connect to an existing instance of openoffice,
         * then create the office manager
         */
        private void initFromExistingLOInstance() {
                ExternalOfficeManagerConfiguration extConf = new
ExternalOfficeManagerConfiguration();
                extConf.setConnectOnStart(true);
                extConf.setPortNumber( this.port );
                this.officeManager = extConf.buildOfficeManager();
                this.officeManager.start();
                this.externalProcess = true;
                System.out.println("Attached to existing OpenOffice process ... ");
        }

        /**
         * Start a new openoffice instance and create the office manager
         */
        private void initFromNewLOInstance() {
                DefaultOfficeManagerConfiguration defaultConf = new
DefaultOfficeManagerConfiguration();
                defaultConf.setPortNumber( this.port );
                this.officeManager = defaultConf.setOfficeHome("C:\\Program Files\\LibreOffice 5").buildOfficeManager();
                this.officeManager.start();
                this.externalProcess = false;
                System.out.println("Created a new OpenOffice process ... ");
        }

        /**
         * @return the officeManager
         */
        public OfficeManager getOfficeManager() {
                return officeManager;
        }

        /**
         * @return the externalProcess
         */
        public boolean isExternalProcess() {
                return externalProcess;
        }

        /**
         * Get a new document converter.
         * @return
         */
        public OfficeDocumentConverter getDocumentConverter() {
                OfficeDocumentConverter docConverter = null;
                try  {
                        docConverter = new OfficeDocumentConverter ( this.officeManager );
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return docConverter;
        }

        /**
         * If is externalProcess, officeManager simply disconnects from the
process
         * else it stops the OpenOffice instance.
         */
        public void stopOfficeManager() {
                try {
                        if ( this.officeManager != null  && isExternalProcess() )
                                this.officeManager.stop();
                } catch ( IllegalStateException e) {
                        e.printStackTrace();
                }
        }
}