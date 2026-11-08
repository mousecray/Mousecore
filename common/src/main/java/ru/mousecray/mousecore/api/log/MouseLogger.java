/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import java.util.Arrays;

public class MouseLogger {
    private final Logger logger;
    private final String prefixPattern, prefixColors;

    public MouseLogger(Logger logger, String prefixPattern, ConsoleColor... prefixColors) {
        this.logger = logger;
        this.prefixPattern = prefixPattern;
        this.prefixColors = ConsoleColor.getColorString(prefixColors);
    }

    public LogBuilder at(Level level)      { return new LogBuilder(level); }
    public LogBuilder atInfo()             { return new LogBuilder(Level.INFO); }
    public LogBuilder atWarn()             { return new LogBuilder(Level.WARN); }
    public LogBuilder atError()            { return new LogBuilder(Level.ERROR); }
    public LogBuilder atFatal(Exception e) { return new LogBuilder(Level.FATAL).withStyle(ConsoleColor.RED_BG).withException(e); }
    public LogBuilder atDebug()            { return new LogBuilder(Level.DEBUG); }

    public class LogBuilder {
        private final Level          level;
        private       ConsoleColor[] style     = new ConsoleColor[0];
        private       String         prefix    = null;
        private       Exception      exception = null;

        private LogBuilder(Level level) {
            this.level = level;
        }

        public LogBuilder withStyle(ConsoleColor... style) {
            this.style = style;
            return this;
        }

        public LogBuilder withPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public LogBuilder withException(Exception exception) {
            this.exception = exception;
            return this;
        }

        public void log(String text, Object... args) {
            String message = MouseStrings.format(text, args);

            StringBuilder finalMessage = new StringBuilder();

            if (prefix != null) {
                finalMessage.append(prefixColors)
                        .append(prefixPattern.replace("${value}", prefix))
                        .append(ConsoleColor.RESET)
                        .append(" ");
            }

            boolean hasStyle = style != null && style.length > 0;
            if (hasStyle) finalMessage.append(ConsoleColor.getColorString(style));

            finalMessage.append(message);

            if (hasStyle) finalMessage.append(ConsoleColor.RESET);

            if (exception != null) {
                finalMessage.append(System.lineSeparator())
                        .append("Exception: ")
                        .append(exception.getLocalizedMessage())
                        .append(System.lineSeparator())
                        .append("Stack trace: ")
                        .append(Arrays.toString(exception.getStackTrace()));
            }

            logger.log(level, finalMessage.toString());
        }
    }
}