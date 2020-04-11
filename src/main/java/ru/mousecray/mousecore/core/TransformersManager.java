package ru.mousecray.mousecore.core;

import org.apache.logging.log4j.Level;
import ru.mousecray.mousecore.api.asm.MousecoreHook;
import ru.mousecray.mousecore.api.asm.transformers.AbstractMousecoreTransformer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.mousecray.mousecore.core.Mousecore.LOGGER;

public class TransformersManager {
    private List<Class<?>> transformerCandidates = new ArrayList<>();
    private List<AbstractMousecoreTransformer> transformers = new ArrayList<>();
    private boolean closed = false;

    public TransformersManager() {
        TransformerBytesManager bytesManager = null;
        try { bytesManager = new TransformerBytesManager(); } catch (IOException e) {e.printStackTrace(); }
        if (bytesManager != null) {
            Map<String, byte[]> bytes = bytesManager.getTransformerBytes();
            ByteClassLoader loader = new ByteClassLoader(getClass().getClassLoader(), bytes);
            bytes.keySet().forEach(str -> {
                Class<?> clazz = loader.findClass(str);
                if (clazz != null) transformerCandidates.add(clazz);
            });

            resolveCandidates();
            removeSelfHookAndOtherHookingHook();
        }

    }

    public boolean isClosed() {
        return closed;
    }

    @Nonnull
    public final List<Class<?>> resolveCandidates() {
        if (closed) {
            LOGGER.log(Level.WARN,
                    "Candidates of " + getClass().getCanonicalName() + " is resolved and " + TransformersManager.class.getCanonicalName() +
                            " is closed!");
            return Collections.emptyList();
        }

        if (transformerCandidates.isEmpty()) {
            LOGGER.log(Level.WARN, "List of candidates of " + getClass() + " is empty!");
            return Collections.emptyList();
        }

        List<Class<?>> removingClasses = new ArrayList<>();
        transformerCandidates.forEach(candidate -> {
            boolean isAssignable = false;

            if (AbstractMousecoreTransformer.class.isAssignableFrom(candidate)) isAssignable = true;

            if (isAssignable) try {
                transformers.add((AbstractMousecoreTransformer) candidate.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {e.printStackTrace();}
            else {
                LOGGER.log(Level.ERROR,
                        "Class " + candidate.getCanonicalName() + " that indicated with " + MousecoreHook.class.getCanonicalName() +
                                " is not assignable from " +
                                AbstractMousecoreTransformer.class.getCanonicalName() + "! It will be skipped.");
                removingClasses.add(candidate);
            }
        });
        transformerCandidates.clear();
        closed = true;
        return removingClasses;
    }

    private void removeSelfHookAndOtherHookingHook() {
        for (AbstractMousecoreTransformer transformer : transformers)
            if (AbstractMousecoreTransformer.class.isAssignableFrom(transformer.getTransformClass())) {
                LOGGER.log(Level.ERROR, "Removing invalid transformer " + transformer.getClass().getCanonicalName());
                transformers.remove(transformer);
            }
    }

    public List<AbstractMousecoreTransformer> getTransformers() {
        if (!closed) {
            LOGGER.log(Level.ERROR,
                    "Resolve candidates of " + getClass().getCanonicalName() + " before getting transformers! Returns empty list.");
            return Collections.emptyList();
        }
        return transformers;
    }

    public byte[] apply(String name, String transformedName, byte[] basicClass) {
        if (!closed) {
            LOGGER.log(Level.FATAL, "Candidates is Unresolved! Resolve candidates before call this method! Return default value.");
            return basicClass;
        }
        byte[] clazz = basicClass;
        for (AbstractMousecoreTransformer transformer : transformers) clazz = transformer.transform(name, transformedName, basicClass);
        return clazz;
    }

    static class ByteClassLoader extends ClassLoader {
        private final Map<String, byte[]> extraClassDefs;

        public ByteClassLoader(ClassLoader parent, Map<String, byte[]> extraClassDefs) {
            super(parent);
            this.extraClassDefs = new HashMap<>(extraClassDefs);
        }

        @Nullable
        @Override
        protected Class<?> findClass(String name) {
            byte[] classBytes = extraClassDefs.remove(name);
            if (classBytes == null) {
                LOGGER.log(Level.FATAL, "Class " + name + " is not found!");
                return null;
            }
            return defineClass(name, classBytes, 0, classBytes.length);
        }
    }
}