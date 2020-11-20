import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.util.CancelException;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException, InvalidClassFileException, ClassHierarchyException, CancelException {

        String type=args[0];
        String projectName=args[1];
        String changeInfoName=args[2];


        //用于输入测试
        //String type="-c";
        //String projectName="E:\\Ricky\\大三\\自动化测试\\code大作业\\ClassicAutomatedTesting\\ClassicAutomatedTesting\\5-MoreTriangle\\target";
        //String changeInfoName="E:\\Ricky\\大三\\自动化测试\\code大作业\\经典大作业\\Data更正-20201116\\5-MoreTriangle\\change_info.txt";
        //E:\\Ricky\\大三\\自动化测试\\code大作业\\ClassicAutomatedTesting\\ClassicAutomatedTesting\\5-MoreTriangle\\target E:\\Ricky\\大三\\自动化测试\\code大作业\\经典大作业\\Data更正-20201116\\5-MoreTriangle\\change_info.txt
        System.out.print("Start to run...\nThis might need a few seconds.\n");
        DependenceAnalysis dependenceAnalysis=new DependenceAnalysis();
        NodePair classPair=dependenceAnalysis.makeGraph(projectName).get(0);
        System.out.println("Class dependencies handled.");
        NodePair methodPair=dependenceAnalysis.makeGraph(projectName).get(1);
        System.out.println("Method dependencies handled.");
        NodePair classTestMap=dependenceAnalysis.makeClassTestMap(dependenceAnalysis.getCg());
        System.out.println("Class to Test map handled.");
        if(type.equals("-c")){
            Selection selector=new Selection(classPair,methodPair,classTestMap,Type.CLASS,changeInfoName);
            selector.select();
        }else if(type.equals("-m")){
            Selection selector=new Selection(classPair,methodPair,classTestMap,Type.METHOD,changeInfoName);
            selector.select();
        }
        System.out.println("Finish!TXT has been made!");

        //用于测试打印
        //classTestMap.pairPrint();
        //classPair.pairPrint();
        //methodPair.pairPrint();
        //用于dot文件生成
        //classPair.makeDotFile();
        //methodPair.makeDotFile();

        /*
        用于测试选择结果
        ArrayList<String> ans=selector.classSelect();
        for(int i=0;i<ans.size();i++){
            System.out.println(ans.get(i));
        }
        System.out.print("------------");
        ans=selector.methodSelect();
        for(int i=0;i<ans.size();i++){
            System.out.println(ans.get(i));
        }

        */

    }

}
