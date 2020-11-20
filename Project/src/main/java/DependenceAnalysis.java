import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.config.AnalysisScopeReader;


import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class DependenceAnalysis {
    private CHACallGraph cg;
    public CHACallGraph getCg(){
        return this.cg;
    }
    ArrayList<NodePair> makeGraph(String projectDir) throws IOException, InvalidClassFileException, ClassHierarchyException, CancelException {
        ArrayList<NodePair> graphs=new ArrayList<NodePair>();
        ArrayList<String> classFiles=new FileGet().getFiles(projectDir);
        //for(String i:classFiles)System.out.println(i);
        AnalysisScope scope= AnalysisScopeReader.readJavaScope("scope.txt"
                ,new File("exclusion.txt"),ClassLoader.getSystemClassLoader());

        for(String i:classFiles){
            File file=new File(i);
            scope.addClassFileToScope(ClassLoaderReference.Application,file);
        }
        ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);
        Iterable<Entrypoint> eps = new AllApplicationEntrypoints(scope, cha);
        CHACallGraph cg = new CHACallGraph(cha);
        cg.init(eps);
        this.cg=cg;
        graphs.add(makeClassGraph(cg));
        graphs.add(makeMethodGraph(cg));
        return graphs;
    }
    NodePair makeClassGraph(CHACallGraph cg){
        NodePair pairs=new NodePair("CLASSPAIR",Type.CLASS);
        pairLink(cg,pairs,Type.CLASS);
        return pairs;
    }
    NodePair makeMethodGraph(CHACallGraph cg){
        NodePair pairs=new NodePair("METHODPAIR",Type.METHOD);
        pairLink(cg,pairs,Type.METHOD);
        return pairs;
    }
    NodePair makeClassTestMap(CHACallGraph cg){
        NodePair pairs=new NodePair("MAP",Type.MAP);
        pairLink(cg,pairs,Type.MAP);
        return pairs;
    }

    void pairLink(CHACallGraph cg,NodePair pairs,Type type){
        for(CGNode i: cg) {
            if(i.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) i.getMethod();
                if("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
                    String classInnerName =method.getDeclaringClass().getName().toString();
                    String signature = method.getSignature();
                    if(type.equals(Type.MAP)){
                        if(i.getMethod().getAnnotations().toString().contains("Annotation type <Application,Lorg/junit/Test>")){
                            pairs.addPair(classInnerName,signature);
                        }
                        continue;
                    }
                    Iterator<CGNode> iterator = cg.getPredNodes(i);
                    while (iterator.hasNext()) {
                        CGNode caller = iterator.next();
                        String callerClassInnerName = caller.getMethod().getDeclaringClass().getName().toString();
                        String callerSignature = caller.getMethod().getSignature();
                        if(callerClassInnerName.startsWith("Ljava")||callerClassInnerName.startsWith("Lorg"))continue;
                        if(type.equals(Type.CLASS)){
                            pairs.addPair(callerClassInnerName,classInnerName);
                        }else if(type.equals(Type.METHOD)){
                            pairs.addPair(callerSignature,signature);
                        }
                        /*
                        if (!(node1classInnerName.startsWith("Ljava")||node1classInnerName.startsWith("Ljavax"))){
                            // 排除掉MyExclusion没有排除的JAVA原生类
                        }*/
                    }
                }
            }
        }
        //return pairs;
    }


}
