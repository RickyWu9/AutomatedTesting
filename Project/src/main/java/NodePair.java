import com.ibm.wala.ipa.callgraph.CGNode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class NodePair {
    Type type;
    String name;
    private ArrayList<String> callerSet=new ArrayList<String>(); //调用者
    private ArrayList<String> calleeSet=new ArrayList<String>(); //被调用者
    private int size=0;



    public NodePair(String name, Type type){
        this.name=name;
        this.type=type;
    }
    //封装
    public int getSize() {
        return size;
    }
    public ArrayList<String> getCallerSet(){
        return callerSet;
    }
    public ArrayList<String> getCalleeSet(){
        return calleeSet;
    }
    //增加边
    public void addPair(String caller,String callee){

        for(int i=0;i<size;i++){
            //已存在关系对
            if(callerSet.get(i).equals(caller) && calleeSet.get(i).equals(callee)) return;
        }
        callerSet.add(caller);
        calleeSet.add(callee);
        this.size++;
    }
    //生成dot文件
    public void makeDotFile() throws IOException {
        BufferedWriter out;
        if(this.type.equals(Type.CLASS)){
            out = new BufferedWriter(new FileWriter("src\\main\\File\\class.dot"));
            out.write("digraph dependence_class{\n");

        }else{
            out = new BufferedWriter(new FileWriter("src\\main\\File\\method.dot"));
            out.write("digraph dependence_method{\n");
        }
        for(int i=0;i<this.size;i++){
            out.write("\t\""+calleeSet.get(i)+"\" -> \""+callerSet.get(i)+"\";\n");
        }
        out.write("}");
        out.close();
    }
    //用于输出测试
    public void pairPrint(){
        System.out.println(this.name);
        for(int i=0;i<size;i++){
            System.out.println(callerSet.get(i)+"-->"+calleeSet.get(i));
        }
    }

}
