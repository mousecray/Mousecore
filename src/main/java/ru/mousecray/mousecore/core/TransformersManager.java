package ru.mousecray.mousecore.core;

import org.apache.logging.log4j.Level;
import ru.mousecray.mousecore.api.asm.MousecoreHook;
import ru.mousecray.mousecore.api.asm.transformers.AbstractMousecoreTransformer;
import ru.mousecray.mousecore.core.loaders.ByteClassLoader;
import ru.mousecray.mousecore.core.visitors.CheckHookClassVisitor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static ru.mousecray.mousecore.core.Mousecore.LOGGER;

public class TransformersManager {
    private List<Class<?>> transformerCandidates = new ArrayList<>();
    private List<AbstractMousecoreTransformer> transformers = new ArrayList<>();
    private boolean closed = false;

    public TransformersManager() {
        //Load transformers
        List<String> dependentClassCandidates = loadTransformers();
        //Load transformers dependents
        if (!dependentClassCandidates.isEmpty()) loadTransformersDependent(dependentClassCandidates);

        resolveCandidates();
        resolveTransformersWithPriority();
    }

    private void loadTransformersDependent(List<String> dependentClassCandidates) {
        TransformerBytesManager dependentBytesManager = null;
        try {
            dependentBytesManager = new TransformerBytesManager(byteStringMapTriple -> {
                if (!dependentClassCandidates.isEmpty()) {
                    String name = byteStringMapTriple.getMiddle()
                            .substring(0, byteStringMapTriple.getMiddle().length() - 6)
                            .replace('/', '.');
                    if (dependentClassCandidates.remove(name))
                        byteStringMapTriple
                                .getRight()
                                .put(name, byteStringMapTriple.getLeft());
                }
            });
        } catch (IOException e) { e.printStackTrace(); }
        if (dependentBytesManager != null) {
            Map<String, byte[]> bytes = dependentBytesManager.getTransformerBytes();
            ByteClassLoader loader = new ByteClassLoader(getClass().getClassLoader());
            bytes.forEach(loader::defineClass);
        }
    }

    /**
     * Read, check and load transformers
     */
    private List<String> loadTransformers() {
        TransformerBytesManager bytesManager = null;
        List<String> dependentClassCandidates = new ArrayList<>();
        try {
            bytesManager = new TransformerBytesManager(byteStringMapTriple -> {
                List<String> value = new CheckHookClassVisitor().getResult(byteStringMapTriple.getLeft());
                if (value != null) {
                    String name = byteStringMapTriple.getMiddle()
                            .substring(0, byteStringMapTriple.getMiddle().length() - 6)
                            .replace('/', '.');
                    byteStringMapTriple
                            .getRight()
                            .put(name, byteStringMapTriple.getLeft());
                    dependentClassCandidates.addAll(value);
                }
            });
        } catch (IOException e) { e.printStackTrace(); }
        if (bytesManager != null) {
            Map<String, byte[]> bytes = bytesManager.getTransformerBytes();
            ByteClassLoader loader = new ByteClassLoader(getClass().getClassLoader());
            bytes.forEach((name, currBytes) -> transformerCandidates.add(loader.defineClass(name, currBytes)));
        }
        return dependentClassCandidates;
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
            closed = true;
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

    public final void resolveTransformersWithPriority() {
        if (!closed) LOGGER.log(Level.ERROR, "Resolve candidates of " + getClass().getCanonicalName() + " before resolvePriority!");
        transformers.sort(Comparator.comparing(AbstractMousecoreTransformer::getHookPriority));
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
            LOGGER.log(Level.FATAL, "Candidates is Unresolved! Resolve candidates before call \"apply\" method! Return default value.");
            return basicClass;
        }
        byte[] clazz = basicClass;
        for (AbstractMousecoreTransformer transformer : transformers) clazz = transformer.transform(name, transformedName, basicClass);
        return clazz;
    }
}