import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {
	static Logger log;
    public Handler fileHandler;
    Formatter plainText;

    private Log() throws IOException{
        
    	String logLocation = System.getProperty("user.home") + File.separator + "WebMining" + File.separator + "Log.txt";
    	
        log = Logger.getLogger(Log.class.getName());
        fileHandler = new FileHandler(logLocation,true);
        plainText = new SimpleFormatter();
        fileHandler.setFormatter(plainText);
        log.addHandler(fileHandler);
    }
    
    private static Logger getLogger(){
        if(log == null){
            try {
                new Log();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return log;
    }
    public static void log(Level level, String msg){
        getLogger().log(level, msg);
        System.out.println(msg);
    }
}
