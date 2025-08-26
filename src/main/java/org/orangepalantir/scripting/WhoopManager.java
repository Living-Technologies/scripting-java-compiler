package org.orangepalantir.scripting;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class WhoopManager<T extends JavaFileManager> extends ForwardingJavaFileManager<T> {
    Map<String, JavaFileObject> objects = new HashMap<>();
    Location location;
    JavaFileManager parent;

    WhoopManager(T sjfm) {
        super(sjfm);
        parent = sjfm;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, final String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        this.location = location;
        JavaFileObject f = new JavaClassFileObject(className, kind);
        objects.put(className, f);

        return f;
    }

    public Map<String, JavaFileObject> getCompiledObjects() {
        return objects;
    }
}
