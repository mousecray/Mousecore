/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.core.config.io;

import ru.mousecray.mousecore.api.config.io.ConfigIOResultType;
import ru.mousecray.mousecore.api.config.io.IOFuture;
import ru.mousecray.mousecore.api.log.ConsoleColor;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.core.config.MouseConfigImpl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class IOFutureImpl implements IOFuture {
    private final String               operationType;
    private final String               operationTypeUpperCase;
    private final String               operationTypeCompl;
    private final MouseConfigImpl      config;
    private final Future<IOResultImpl> result;

    IOFutureImpl(boolean saving, MouseConfigImpl config, Future<IOResultImpl> result) {
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

    @Override
    public IOResultImpl waitResult() {
        try {
            IOResultImpl result = this.result.get();
            if (result.getType() != ConfigIOResultType.SUCCESS &&
                    result.getType() != ConfigIOResultType.NOT_FOUND) {
                if (config.logger != null) {
                    config.logger.atError()
                            .withPrefix(LogPrefixes.CONFIG)
                            .withStyle(ConsoleColor.RED_BG)
                            .log("MouseConfig '{0}' cannot be '{1}", config.getName(), operationTypeCompl);
                }
            }
            return result;
        } catch (InterruptedException e) {
            if (config.logger != null) {
                config.logger.atFatal(e)
                        .withPrefix(LogPrefixes.CONFIG)
                        .log("During {0} of MouseConfig '{1}' an error occurred", operationType, config.getName());
            }
            return IOResultImpl.createError(e);
        } catch (ExecutionException e) {
            if (config.logger != null) {
                config.logger.atFatal(e)
                        .withPrefix(LogPrefixes.CONFIG)
                        .log("{0} of MouseConfig '{1}' was interrupted", operationTypeUpperCase, config.getName());
            }
            return IOResultImpl.createError(e);
        }
    }
}
