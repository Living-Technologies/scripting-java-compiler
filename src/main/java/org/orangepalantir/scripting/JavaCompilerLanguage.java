package org.orangepalantir.scripting;

import org.scijava.plugin.Plugin;
import org.scijava.script.AbstractScriptLanguage;
import org.scijava.script.ScriptLanguage;

import javax.script.ScriptEngine;
import java.util.ArrayList;
import java.util.List;

@Plugin(type = ScriptLanguage.class, name = "JavaCompiler")
public class JavaCompilerLanguage extends AbstractScriptLanguage {
    @Override
    public boolean isCompiledLanguage() {
        return false;
    }

    @Override
    public List<String> getExtensions() {
        List<String> strings = new ArrayList<>();
        strings.add("java");
        return strings;
    }

    @Override
    public String getEngineName(){
        return "JavaCompiler";
    }

    @Override
    public ScriptEngine getScriptEngine() {
        JavaCompilerEngine engine = new JavaCompilerEngine();
        getContext().inject(engine);
        return engine;
    }


}
