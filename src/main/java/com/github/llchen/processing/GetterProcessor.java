package com.github.llchen.processing;

import com.github.llchen.annotation.Getter;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author llchen12
 * @date 2018/6/22
 */
public class GetterProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Messager messager;
    private Filer filer;
    private Types typeUtils;
    private JavacTrees javacTrees;
    private TreeMaker treeMaker;
    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.elementUtils = processingEnv.getElementUtils();
        this.messager = processingEnv.getMessager();
        this.filer = processingEnv.getFiler();
        this.typeUtils = processingEnv.getTypeUtils();
        this.javacTrees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Getter.class);
        messager.printMessage(Diagnostic.Kind.NOTE,"开始处理...");
        messager.printMessage(Diagnostic.Kind.NOTE,annotations.toString());

        for (Element ele : elements) {
            if (ele.getKind() == ElementKind.CLASS) {
                JCTree tree = javacTrees.getTree(ele);
                tree.accept(new TreeTranslator() {
                    @Override
                    public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                        //List<JCTree.JCVariableDecl> jcVariableDeclList = List.nil();

                        for (JCTree t : jcClassDecl.defs) {
                            if (t.getKind() == Tree.Kind.VARIABLE) {
                                JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) t;
                                //jcVariableDeclList.add(jcVariableDecl);
                                messager.printMessage(Diagnostic.Kind.NOTE, jcVariableDecl.getName() + " has been processed");
                                jcClassDecl.defs = jcClassDecl.defs.prepend(makeGetterMethodDecl(jcVariableDecl));
                            }
                        }
                        super.visitClassDef(jcClassDecl);


                    }

                    private JCTree.JCMethodDecl makeGetterMethodDecl(JCTree.JCVariableDecl jcVariableDecl) {
                        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
                        statements.append(treeMaker.Return(treeMaker.Select(treeMaker.Ident(names.fromString("this")), jcVariableDecl.getName())));
                        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
                        return treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), getNewMethodName(jcVariableDecl.getName()), jcVariableDecl.vartype, List.nil(), List.nil(),
                                List.nil(), body, null);
                    }

                    private Name getNewMethodName(Name name) {
                        String s = name.toString();
                        return names.fromString("get" + s.substring(0, 1).toUpperCase() + s.substring(1));
                    }
                });


            } else {
                messager.printMessage(Diagnostic.Kind.ERROR, "@Getter只能修饰类", ele);
            }
        }
        return true;
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>(2);
        types.add(Getter.class.getCanonicalName());
        return Collections.unmodifiableSet(types);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
