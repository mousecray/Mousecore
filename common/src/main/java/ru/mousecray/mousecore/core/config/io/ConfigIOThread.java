/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.io;

import ru.mousecray.mousecore.api.config.io.IOFuture;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.core.config.MouseConfigImpl;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConfigIOThread extends Thread {
    private static ConfigIOThread instance;
    ExecutorService service = Executors.newSingleThreadExecutor();
    private final Map<MouseConfigImpl, Callable<IOResultImpl>> savePool      = new HashMap<>();
    private final Map<MouseConfigImpl, Callable<IOResultImpl>> loadPool      = new HashMap<>();
    private final List<MouseConfigImpl>                        updatingQueue = new CopyOnWriteArrayList<>();
    private       long                                         sec           = 0;
    private       boolean                                      isWorking     = false;
    private       boolean                                      needStop      = false;

    public void registerConfig(MouseConfigImpl config) {
        if (!updatingQueue.contains(config)) updatingQueue.add(config);
    }

    public IOFuture loadIntermediately(MouseConfigImpl config) {
        return new IOFutureImpl(false, config, service.submit(() -> {
            if (!Files.getLastModifiedTime(config.getPath().getAbsoluteFile().toPath()).equals(
                    config.parser.lastModTime)) {
                IOResultImpl load = config.loadInternal();
                config.lastSyncSec = sec;
                return load;
            }
            return IOResultImpl.createSkipped();
        }));
    }

    public IOFuture saveIntermediately(MouseConfigImpl config, boolean markUnloaded) {
        return new IOFutureImpl(true, config, service.submit(() -> {
            if (config.hasUnsavedChanges()) {
                IOResultImpl save = config.saveInternal(markUnloaded);
                config.lastSaveSec = sec;
                return save;
            }
            return IOResultImpl.createSkipped();
        }));
    }

    public void addSaveAction(MouseConfigImpl config) {
        synchronized (savePool) {
            savePool.put(config, () -> {
                IOResultImpl save = config.saveInternal(false);
                config.lastSaveSec = sec;
                return save;
            });
        }
    }

    public void addLoadAction(MouseConfigImpl config) {
        synchronized (loadPool) {
            loadPool.put(config, () -> {
                IOResultImpl load = config.loadInternal();
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
                    for (Map.Entry<MouseConfigImpl, Callable<IOResultImpl>> entry : savePool.entrySet()) {
                        if (entry != null) service.submit(() -> {
                            try {
                                if (entry.getKey().hasUnsavedChanges()) entry.getValue().call();
                            } catch (Exception e) {
                                try {
                                    MouseConfigImpl key = entry.getKey();
                                    //noinspection DataFlowIssue
                                    key.logger.atFatal(e)
                                            .withPrefix(LogPrefixes.CONFIG)
                                            .log("Error when save MouseConfig '{1}'", key.getName());
                                } catch (Exception ignore) { }
                            }
                        });
                    }
                    savePool.clear();
                }
            }
            synchronized (loadPool) {
                if (!loadPool.isEmpty()) {
                    for (Iterator<Map.Entry<MouseConfigImpl, Callable<IOResultImpl>>> iterator = loadPool.entrySet().iterator(); iterator.hasNext(); ) {
                        Map.Entry<MouseConfigImpl, Callable<IOResultImpl>> entry = iterator.next();
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
                                                .log("Error when load MouseConfig '{1}'", key.getName());
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

}