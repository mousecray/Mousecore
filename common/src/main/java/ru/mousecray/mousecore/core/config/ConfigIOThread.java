/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.core.config;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

public class ConfigIOThread extends Thread {
    private static ConfigIOThread instance;
    ExecutorService service = Executors.newSingleThreadExecutor();
    private final Map<MouseConfigImpl, Callable<IOResult>> savePool      = new HashMap<>();
    private final Map<MouseConfigImpl, Callable<IOResult>> loadPool      = new HashMap<>();
    private final List<MouseConfigImpl>                    updatingQueue = new CopyOnWriteArrayList<>();
    private       long                                     sec           = 0;
    private       boolean                                  isWorking     = false;
    private       boolean                                  needStop      = false;

    public void registerConfig(MouseConfigImpl config) {
        if (!updatingQueue.contains(config)) updatingQueue.add(config);
    }

    public IOFuture loadIntermediately(MouseConfigImpl config) {
        return new IOFuture(false, config, service.submit(() -> {
            if (!Files.getLastModifiedTime(config.getPath().getAbsoluteFile().toPath()).equals(
                    config.parser.lastModTime)) {
                IOResult load = config.loadInternal();
                config.lastSyncSec = sec;
                return load;
            }
            return IOResult.createSkipped();
        }));
    }

    public IOFuture saveIntermediately(MouseConfigImpl config, boolean markUnloaded) {
        return new IOFuture(true, config, service.submit(() -> {
            if (config.hasUnsavedChanges()) {
                IOResult save = config.saveInternal(markUnloaded);
                config.lastSaveSec = sec;
                return save;
            }
            return IOResult.createSkipped();
        }));
    }

    public void addSaveAction(MouseConfigImpl config) {
        synchronized (savePool) {
            savePool.put(config, () -> {
                IOResult save = config.saveInternal(false);
                config.lastSaveSec = sec;
                return save;
            });
        }
    }

    public void addLoadAction(MouseConfigImpl config) {
        synchronized (loadPool) {
            loadPool.put(config, () -> {
                IOResult load = config.loadInternal();
                config.lastSyncSec = sec;
                return load;
            });
        }
    }

    @SuppressWarnings({ "BusyWait" }) @Override
    public void run() {
        while (!needStop) {
            ++sec;
            for (MouseConfigImpl config : updatingQueue) config.update(sec);
            synchronized (savePool) {
                if (!savePool.isEmpty()) {
                    for (Map.Entry<MouseConfigImpl, Callable<IOResult>> entry : savePool.entrySet()) {
                        if (entry != null) service.submit(() -> {
                            try {
                                if (entry.getKey().hasUnsavedChanges()) entry.getValue().call();
                            } catch (Exception e) {
                                try {
                                    MouseConfigImpl key = entry.getKey();
                                    //noinspection DataFlowIssue
                                    key.logger.atFatal(e)
                                            .withPrefix(LogPrefixes.CONFIG)
                                            .log("Error when save MouseConfigImpl '{1}'", key.getName());
                                } catch (Exception ignore) { }
                            }
                        });
                    }
                    savePool.clear();
                }
            }
            synchronized (loadPool) {
                if (!loadPool.isEmpty()) {
                    for (Iterator<Map.Entry<MouseConfigImpl, Callable<IOResult>>> iterator = loadPool.entrySet().iterator(); iterator.hasNext(); ) {
                        Map.Entry<MouseConfigImpl, Callable<IOResult>> entry = iterator.next();
                        if (entry != null && !entry.getKey().hasUnsavedChanges()) {
                            service.submit(() -> {
                                try {
                                    if (!Files.getLastModifiedTime(entry.getKey().path.getAbsoluteFile().toPath()).equals(
                                            entry.getKey().parser.lastModTime)) {
                                        entry.getValue().call();
                                    }
                                } catch (Exception e) {
                                    try {
                                        MouseConfigImpl key = entry.getKey();
                                        //noinspection DataFlowIssue
                                        key.logger.atFatal(e)
                                                .withPrefix(LogPrefixes.CONFIG)
                                                .log("Error when load MouseConfigImpl '{1}'", key.getName());
                                    } catch (Exception ignore) { }
                                }
                            });
                            iterator.remove();
                        }
                    }
                }
            }
            try { sleep(1000); } catch (InterruptedException e) { throw new RuntimeException(e); }
        }
        isWorking = false;
    }

    public void stopWork()     { needStop = true; }
    public boolean isWorking() { return isWorking; }

    public void startWork() {
        isWorking = true;
        needStop = false;
        sec = 0;
        savePool.clear();
        loadPool.clear();
        start();
    }

    private ConfigIOThread() { startWork(); }

    public static ConfigIOThread getInstance() {
        if (instance == null) instance = new ConfigIOThread();
        return instance;
    }

    public static final class IOFuture {
        private final String           operationType;
        private final String           operationTypeUpperCase;
        private final String           operationTypeCompl;
        private final MouseConfigImpl  config;
        private final Future<IOResult> result;

        private IOFuture(boolean saving, MouseConfigImpl config, Future<IOResult> result) {
            if (saving) {
                operationType = "saving";
                operationTypeUpperCase = "Saving";
                operationTypeCompl = "Saved";
            } else {
                operationType = "loading";
                operationTypeUpperCase = "Loading";
                operationTypeCompl = "Loaded";
            }
            this.config = config;
            this.result = result;
        }

        public IOResult waitResult() {
            try {
                IOResult result = this.result.get();
                if (result.getType() != IOResult.ConfigIOResultType.SUCCESS &&
                        result.getType() != IOResult.ConfigIOResultType.NOT_FOUND) {
                    if (config.logger != null) {
                        config.logger.atError()
                                .withPrefix(LogPrefixes.CONFIG)
                                .withStyle(ConsoleColor.RED_BG)
                                .log("MouseConfigImpl '{0}' cannot be '{1}", config.getName(), operationTypeCompl);
                    }
                }
                return result;
            } catch (InterruptedException e) {
                if (config.logger != null) {
                    config.logger.atFatal(e)
                            .withPrefix(LogPrefixes.CONFIG)
                            .log("During {0} of MouseConfigImpl '{1}' an error occurred", operationType, config.getName());
                }
                return IOResult.createError(e);
            } catch (ExecutionException e) {
                if (config.logger != null) {
                    config.logger.atFatal(e)
                            .withPrefix(LogPrefixes.CONFIG)
                            .log("{0} of MouseConfigImpl '{1}' was interrupted", operationTypeUpperCase, config.getName());
                }
                return IOResult.createError(e);
            }
        }
    }

    @ParametersAreNonnullByDefault
    @MethodReturnsNonnullByDefault
    public static class IOResult {
        @Nonnull private final  ConfigIOResultType type;
        @Nullable private final Exception          error;

        private IOResult(ConfigIOResultType type, @Nullable Exception error) {
            this.type = type;
            this.error = error;
        }

        public static IOResult createSuccess() {
            return new IOResult(ConfigIOResultType.SUCCESS, null);
        }

        public static IOResult createNotFound() {
            return new IOResult(ConfigIOResultType.NOT_FOUND, null);
        }

        public static IOResult createSkipped() {
            return new IOResult(ConfigIOResultType.SKIPPED, null);
        }

        public static IOResult createError(Exception error) {
            return new IOResult(ConfigIOResultType.ERROR, Objects.requireNonNull(error));
        }

        public ConfigIOResultType getType()   { return type; }
        @Nullable public Exception getError() { return error; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IOResult that)) return false;
            return type == that.type && Objects.equals(error, that.error);
        }

        @Override public int hashCode()    { return Objects.hash(type, error); }
        @Override public String toString() { return "IOResult{type=" + type + ", error=" + error + '}'; }

        public enum ConfigIOResultType {
            SUCCESS, ERROR, NOT_FOUND, SKIPPED
        }
    }
}