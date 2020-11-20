import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

public class FileGet {
    ArrayList<String> Files=new ArrayList<String>();
    ArrayList<String> getFiles(String dir){
        Vector<String> ver=new Vector<String>();
        ver.add(dir);
        while (ver.size()>0){
            File[] files=new File(ver.get(0).toString()).listFiles();
            ver.remove(0);
            int len=files.length;
            for(int i=0;i<len;i++){
                String tmp=files[i].getAbsolutePath();
                if(files[i].isDirectory())  //如果是目录，则加入队列。以便进行后续处理
                    ver.add(tmp);
                else{
                    if(tmp.substring(tmp.length()-6,tmp.length()).equals(".class")){
                        Files.add(tmp);
                        //System.out.println(tmp);    //如果是文件，则直接输出文件名到指定的文件。
                    }

                }

            }
        }
        return Files;
    }

}
