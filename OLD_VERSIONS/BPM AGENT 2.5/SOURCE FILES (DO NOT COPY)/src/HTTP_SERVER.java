

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HTTP_SERVER {
    
    private int port;
    private HttpServer HTTP_SERV;
    
    public void Start(int Port)
    {
        this.port=Port;
        try {
            HTTP_SERV=HttpServer.create(new InetSocketAddress(port),0);
            System.out.println("Server started at port: "+port+"\n");
            
            
            HTTP_SERV.createContext("/test",new HTTP_HANDLERS.testHandler());
            HTTP_SERV.createContext("/edit",new HTTP_HANDLERS.EditHandler());
            HTTP_SERV.createContext("/scan",new HTTP_HANDLERS.scanHandler());
            HTTP_SERV.createContext("/view",new HTTP_HANDLERS.ViewHandler());
            HTTP_SERV.createContext("/sign",new HTTP_HANDLERS.SignHandler());
            HTTP_SERV.setExecutor(Executors.newCachedThreadPool());
            HTTP_SERV.start();
            
            
            
        } catch (IOException ex) {
            Logger.getLogger(HTTP_SERVER.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void Stop()
    {
        HTTP_SERV.stop(0);
        System.out.println("SERVER STOPPED ITS OPERATIONS \n");
    }
    
    protected int get_Port()
    {
        return port;
    }
    
}
