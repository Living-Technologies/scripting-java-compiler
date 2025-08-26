package org.orangepalantir.scripting;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.scijava.Context;
import org.scijava.script.DefaultScriptService;
import org.scijava.script.ScriptLanguage;

import javax.script.ScriptException;

/**
 * Unit test for simple App.
 */
public class AppTest {
    final static String main= "    package dog;\n" +
                                 "        public class Rex{\n" +
                                 "            public static void main(String[] args){\n" +
                                 "                System.out.println(\"Main class\");\n" +
                                 "            }\n" +
                                 "        }\n";
    /**
     * Compiles a class with a main method that scijava will run.
     */
    @Test
    public void mainCheck() throws ScriptException {
        try(Context c = new Context()){
            DefaultScriptService service = (DefaultScriptService) c.getService("org.scijava.script.DefaultScriptService");
            ScriptLanguage lang = service.getLanguageByName("JavaCompiler");
            JavaCompilerEngine engine = (JavaCompilerEngine)lang.getScriptEngine();
            engine.eval(main);
        }
    }

    static String command = "package dog;\n" +
                            "import org.scijava.Context;\n" +
                            "import org.scijava.plugin.Parameter;\n" +
                            "import org.scijava.command.Command;\n" +
                            "import org.scijava.plugin.Plugin;\n" +
                            "//@Plugin(type = Command.class, name=\"Start DM3D\", menuPath=\"Plugins > DM3D>  Start DM3D \")\n" +
                            "public class Rex implements Command{\n" +
                            "    @Parameter\n" +
                            "    Context context;\n" +
                            "    public void run(){\n" +
                            "        System.out.println(\"Command: \" + context);\n" +
                            "    }\n" +
                            "}";

    /**
     * Creates a command that scijava runs. Demonstrates capturing a
     * Context.
     * @throws ScriptException
     */
    @Test
    public void commandCheck() throws ScriptException{
        try(Context c = new Context()){
            DefaultScriptService service = (DefaultScriptService) c.getService("org.scijava.script.DefaultScriptService");
            ScriptLanguage lang = service.getLanguageByName("JavaCompiler");
            JavaCompilerEngine engine = (JavaCompilerEngine)lang.getScriptEngine();
            //engine.setFileName("Rex");
            engine.eval(command);
        }
    }

    /**
     * Compiles the same package.ClassName twice to show that a new class
     * is created.
     *
     * @throws ScriptException
     */
    @Test
    public void reCompileClassCheck() throws ScriptException{
        try(Context c = new Context()){
            DefaultScriptService service = (DefaultScriptService) c.getService("org.scijava.script.DefaultScriptService");
            ScriptLanguage lang = service.getLanguageByName("JavaCompiler");
            JavaCompilerEngine engine = (JavaCompilerEngine)lang.getScriptEngine();
            //engine.setFileName("Rex");
            engine.eval(command);
            engine.eval(main);
        }
    }

    static String multi = "package dog;\n" +
                       "import org.scijava.Context;\n" +
                       "import org.scijava.plugin.Parameter;\n" +
                       "import org.scijava.command.Command;\n" +
                       "import org.scijava.plugin.Plugin;\n" +
                       "public class Rex{\n" +
                       "    static class Collar{\n" +
                       "      int size;\n" +
                       "      boolean spikes;\n" +
                       "    }\n" +
                       "    public static void main(String[] args){\n" +
                       "        Collar c = new Collar();\n" +
                       "        House h = new House();\n" +
                       "        System.out.println(\"why not? \" + c.size + \", \" + c.spikes);\n" +
                       "        System.out.println(h.windows + \" windows and a \" + h.doors);\n" +
                       "    }\n" +
                       "}\n" +
                       "\n" +
                       "class House{\n" +
                       "  int windows;\n" +
                       "  int doors;\n" +
                       "}\n" +
                       "\n";

    /**
     *  Demonstrates creating a single java file with multiple classes and
     *  one of them that gets run.
     *
     *
     * @throws ScriptException
     */
    @Test
    public void multipleClassesCheck() throws ScriptException{
        try(Context c = new Context()){
            DefaultScriptService service = (DefaultScriptService) c.getService("org.scijava.script.DefaultScriptService");
            ScriptLanguage lang = service.getLanguageByName("JavaCompiler");
            JavaCompilerEngine engine = (JavaCompilerEngine)lang.getScriptEngine();
            engine.setFileName("Rex");
            engine.eval(multi);
        }
    }


}
