import java.io.*;
import java.util.ArrayList;

public class Selection {
    NodePair classPair;
    NodePair methodPair;
    NodePair classTestMap;
    Type type;
    String changeInfoDir;
    ArrayList<String> methodChanged;
    ArrayList<String> classChanged;

    public Selection(NodePair classPair,NodePair methodPair,NodePair classTestMap,Type type,String changeInfoDir) throws IOException {
        this.classPair=classPair;
        this.methodPair=methodPair;
        this.type=type;
        this.changeInfoDir=changeInfoDir;
        methodChanged=new ArrayList<String>();
        classChanged=new ArrayList<String>();
        this.classTestMap=classTestMap;
        this.changeAnalysis();

    }
    //防止添加重复元素
    public void uniqueAdd(ArrayList<String> changeList,String changeEntry){
        if(!changeList.contains(changeEntry)) changeList.add(changeEntry);
    }
    //读取更改的方法信息
    public void changeAnalysis() throws IOException {
        FileInputStream inputStream = new FileInputStream(this.changeInfoDir);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String method=bufferedReader.readLine();
        while(method!=null){
            String[] changeInfo=method.split(" ");
            this.uniqueAdd(classChanged,changeInfo[0]);
            this.uniqueAdd(methodChanged,changeInfo[1]);
            method=bufferedReader.readLine();
        }
    }

    //选择器，根据类型选择方法
    public void select() throws IOException {
        ArrayList<String> selection=new ArrayList<String>();
        if(this.type.equals(Type.CLASS)){
            selection=this.classSelect();
        }else{
            selection=this.methodSelect();
        }
        this.makeTxtFile(selection);
    }
    //创建最终结果文件
    public void makeTxtFile(ArrayList<String> selection) throws IOException {
        BufferedWriter out;
        if(this.type.equals(Type.CLASS)){
            out=new BufferedWriter(new FileWriter("selection-class.txt"));
        }else{
            out=new BufferedWriter(new FileWriter("selection-method.txt"));
        }
        for(String line:selection){
            out.write(line+"\n");
        }
        out.close();
    }
    //method选择
    public ArrayList<String> methodSelect(){
        ArrayList<String> methodSelected=new ArrayList<String>();
        ArrayList<String> selection=new ArrayList<String>();
        findMethod(methodSelected,this.methodChanged);
        for(int i=0;i<classTestMap.getSize();i++){
            if(methodSelected.contains(classTestMap.getCalleeSet().get(i)))
                selection.add(classTestMap.getCallerSet().get(i)+" "+classTestMap.getCalleeSet().get(i));
        }
        return selection;
    }
    //递归查找，由于可能存在循环依赖，需要判断是否有新加的方法，若没有则返回
    public void findMethod(ArrayList<String> methodSelected, ArrayList<String> lastMethodChanged){
        int add=0;
        ArrayList<String> nextMethodChanged=new ArrayList<String>();
        for(int i=0;i<methodPair.getSize();i++){//每次要判断，当前加入的方法是否已经存在，是否在改变的方法当中
            if(lastMethodChanged.contains(methodPair.getCalleeSet().get(i))
                    &&(!this.methodChanged.contains(methodPair.getCallerSet().get(i)))
                    &&(!methodSelected.contains(methodPair.getCallerSet().get(i)))){
                methodSelected.add(methodPair.getCallerSet().get(i));
                nextMethodChanged.add(methodPair.getCallerSet().get(i));
                add+=1;
            }
        }
        if(add==0)return;else{findMethod(methodSelected,nextMethodChanged);}
    }
    //class选择
    public ArrayList<String> classSelect(){
        ArrayList<String> classSelected=new ArrayList<String>();
        ArrayList<String> selection=new ArrayList<String>();
        findClass(classSelected,this.classChanged);
        for(int i=0;i<classTestMap.getSize();i++){
            if(classSelected.contains(classTestMap.getCallerSet().get(i)))
                selection.add(classTestMap.getCallerSet().get(i)+" "+classTestMap.getCalleeSet().get(i));
        }
        return selection;
    }
    //递归查找，由于可能存在循环依赖，需要判断是否有新加的方法，若没有则返回
    public void findClass(ArrayList<String> classSelected,ArrayList<String> lastClassChanged){
        int add=0;
        ArrayList<String> nextClassChanged=new ArrayList<String>();
        for(int i=0;i<classPair.getSize();i++){//每次要判断，当前加入的类是否已经存在，是否在改变的类当中
            if(lastClassChanged.contains(classPair.getCalleeSet().get(i))
                    &&(!this.classChanged.contains(classPair.getCallerSet().get(i)))
                    &&(!classSelected.contains(classPair.getCallerSet().get(i)))){
                classSelected.add(classPair.getCallerSet().get(i));
                nextClassChanged.add(classPair.getCallerSet().get(i));
                add+=1;
            }
        }
        if(add==0)return;else{findClass(classSelected,nextClassChanged);}

    }

}
