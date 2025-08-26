package org.orangepalantir.scripting;

import org.scijava.Context;
import org.scijava.plugin.Parameter;
import org.scijava.run.RunService;
import org.scijava.script.ScriptService;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaCompilerEngine extends AbstractScriptEngine {
    @Parameter
    private RunService runService;
    @Parameter
    private ScriptService scriptService;
    @Parameter
    private String fileName = "";
    public String getFileName(){
        return fileName;
    }

    JavaCompiler compiler;
    DiagnosticCollector<JavaFileObject> diagnostics;
    WhoopManager<? extends JavaFileManager> fileManager;
    public JavaCompilerEngine(){
        compiler = ToolProvider.getSystemJavaCompiler();
        diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null, null, null);

        fileManager = new WhoopManager<>(stdFileManager);
    }

    Map<String, Class<?>> loadedClasses = new HashMap<>();
    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(baos);

        List<String> classes = new ArrayList<>();

        List<SimpleJavaFileObject> objects = new ArrayList<>();
        String usableName = getName(script);
        objects.add(new JavaSourceFromString(usableName, script));
        JavaCompiler.CompilationTask task = compiler.getTask(
                writer,
                fileManager,
                diagnostics,
                new ArrayList<String>(),
                classes,
                objects
        );
        try {
            Boolean b = task.call();
            if (b) {
                Map<String, JavaFileObject> objs = fileManager.getCompiledObjects();
                List<Class<?>> classless = new ArrayList<>();

                ClassLoader cl0 = Context.getClassLoader();
                //Each time you compile and run a new classloader is created.
                ClassLoader cl = new ClassLoader(cl0){};

                for (String item : objs.keySet()) {
                    JavaClassFileObject obj = (JavaClassFileObject) objs.get(item);
                    Class<? extends ClassLoader> cls = ClassLoader.class;

                    Method meth = cls.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
                    meth.setAccessible(true);
                    String name = obj.getName().replace("/", ".").replace(".class", "").substring(1);

                    byte[] data = obj.getBytes();
                    int start = 0;
                    int end = data.length;

                    Class<?> created = (Class<?>) meth.invoke(cl, name, data, start, end);
                    classless.add(created);
                }
                runService.run(getClosestClass(classless, usableName));
                return null;

            } else {
                StringBuilder builder = new StringBuilder();
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    String message = String.format("Error on line %s in %s%n",
                            diagnostic.getCode(),
                            diagnostic.getMessage(Locale.US));
                    builder.append(message);
                }
                throw new ScriptException(builder.toString());

            }
        } catch(Exception e){
            throw new ScriptException(e);
        }
    }

    Class<?> getClosestClass(List<Class<?>> classes, String usedName){
        for(Class<?> cls : classes){
            String name = cls.getName();
            if(name.contains("$")){
                continue;
            }
            String[] elements = name.split("\\.");
            if(usedName.equals(elements[elements.length - 1])){
                return cls;
            }
        }
        return classes.get(0);

    }
    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        return eval(reader.toString(), context);
    }

    @Override
    public Bindings createBindings() {
        return null;
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return null;
    }

    /**
     * Should be the name of the file without the extension, eg Rex.
     * TODO How to set this value?
     * @param name
     */
    public void setFileName(String name){
        this.fileName = name;
    }

    private String getName( String source ){
        if(fileName.isEmpty()){
            Pattern pattern = Pattern.compile("public\\s+class\\s+(\\w+)[^{]*");
            Matcher matcher = pattern.matcher(source);
            if(matcher.find()){
                return matcher.group(1);
            };
        }

        return fileName;
    }


}
