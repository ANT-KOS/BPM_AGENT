
import java.io.File;
import java.util.ArrayList;


public class MASS_DELETE_FILES {
    
    private ArrayList<File> listFiles;

    public MASS_DELETE_FILES() {
        this.listFiles=new ArrayList<>();
    }
    
    public void deleteFilesOlderThanNdays(long daysBack, String dirWay)
     {
    System.out.println(dirWay);
    System.out.println(daysBack);

    final File directory = new File(dirWay);
    if(directory.exists()){
        System.out.println("Directory Exists");
        
        for(File F : directory.listFiles())
        {
            if(!F.isDirectory())
            {
                System.out.println(F);
                listFiles.add(F);
            }
        }
                 
        long purgeTime = System.currentTimeMillis() - (daysBack * 10L);

        System.out.println("System.currentTimeMillis " + System.currentTimeMillis());

        System.out.println("purgeTime " + purgeTime);

        for(File listFile : listFiles) {
            System.out.println("Length : "+ listFiles.size());
            System.out.println("listFile.getName() : " +listFile.getName());
            System.out.println("listFile.lastModified() :"+listFile.lastModified());

            if(listFile.lastModified() < purgeTime) {
                listFile.delete();
                System.out.println("Inside File Delete");
            }
        }
    } 
    else 
    {
    }
    
}
     }
    

