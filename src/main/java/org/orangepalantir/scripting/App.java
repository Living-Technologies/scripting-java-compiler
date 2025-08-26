package org.orangepalantir.scripting;

import org.scijava.Context;
import org.scijava.script.DefaultScriptService;
import org.scijava.script.ScriptLanguage;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.lang.reflect.InvocationTargetException;

/**
 * Hello world!
 */
public class App {
    static String s = "package dog;\n" +
                      "import org.scijava.Context;\n" +
                      "import org.scijava.plugin.Parameter;\n" +
                      "import org.scijava.command.Command;\n" +
                      "import org.scijava.plugin.Plugin;\n" +
                      "//@Plugin(type = Command.class, name=\"Start DM3D\", menuPath=\"Plugins > DM3D>  Start DM3D \")\n" +
                      "public class Rex implements Command{\n" +
                      "    @Parameter\n" +
                      "    Context context;\n" +
                      "    public void run(){\n" +
                      "        System.out.println(\"Running! \" + context);\n" +
                      "    }\n" +
                      "}";
    static String s2 = "package dog;\n" +
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
    public static void main(String[] args) throws ScriptException, InvocationTargetException {
        try(Context c = new Context()){

            DefaultScriptService service = (DefaultScriptService) c.getService("org.scijava.script.DefaultScriptService");
            ScriptLanguage lang = service.getLanguageByName("JavaCompiler");
            ScriptEngine engine = lang.getScriptEngine();
            engine.eval(s);
            engine.eval(s2);

        }
    }
}
