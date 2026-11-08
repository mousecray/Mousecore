/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import ru.mousecray.mousecore.api.anno.Fast;
import ru.mousecray.mousecore.api.anno.Slowly;
import ru.mousecray.mousecore.api.utils.MouseStrings;

public class MouseLogger {
    private final Logger logger;
    private final String prefixPattern;
    private final String prefixColors;

    public MouseLogger(Logger logger, String prefixPattern, ConsoleColor... prefixColors) {
        this.logger = logger;
        this.prefixPattern = prefixPattern;
        this.prefixColors = ConsoleColor.getColorString(prefixColors);
    }

    @Fast
    public void log(Level level, String prefix, ConsoleColor[] style, Throwable exception, String text, Object... args) {
        if (!logger.isEnabled(level)) return;

        String        message      = (args != null && args.length > 0) ? MouseStrings.format(text, args) : text;
        StringBuilder finalMessage = new StringBuilder();

        if (prefix != null && prefixPattern != null) {
            finalMessage.append(prefixColors)
                    .append(prefixPattern.replace("${value}", prefix))
                    .append(ConsoleColor.RESET)
                    .append(" ");
        }

        boolean hasStyle = style != null && style.length > 0;
        if (hasStyle) finalMessage.append(ConsoleColor.getColorString(style));

        finalMessage.append(message);

        if (hasStyle) finalMessage.append(ConsoleColor.RESET);

        if (exception != null) logger.log(level, finalMessage.toString(), exception);
        else logger.log(level, finalMessage.toString());
    }

    @Fast
    public void log(Level level, String prefix, ConsoleColor style, Throwable exception, String text, Object... args) {
        log(level, prefix, style != null ? new ConsoleColor[]{ style } : null, exception, text, args);
    }

    @Fast
    public void info(String text, Object... args) {
        log(Level.INFO, null, (ConsoleColor[]) null, null, text, args);
    }

    @Fast
    public void info(ConsoleColor style, String text, Object... args) {
        log(Level.INFO, null, style, null, text, args);
    }

    @Fast
    public void info(String prefix, ConsoleColor style, String text, Object... args) {
        log(Level.INFO, prefix, style, null, text, args);
    }

    @Fast
    public void info(String prefix, ConsoleColor[] style, String text, Object... args) {
        log(Level.INFO, prefix, style, null, text, args);
    }

    @Fast
    public void debug(String text, Object... args) {
        log(Level.DEBUG, null, (ConsoleColor[]) null, null, text, args);
    }

    @Fast
    public void debug(ConsoleColor style, String text, Object... args) {
        log(Level.DEBUG, null, style, null, text, args);
    }

    @Fast
    public void debug(String prefix, ConsoleColor style, String text, Object... args) {
        log(Level.DEBUG, prefix, style, null, text, args);
    }

    @Fast
    public void debug(String prefix, ConsoleColor[] style, String text, Object... args) {
        log(Level.DEBUG, prefix, style, null, text, args);
    }

    @Fast
    public void warn(String text, Object... args) {
        log(Level.WARN, null, (ConsoleColor[]) null, null, text, args);
    }

    @Fast
    public void warn(ConsoleColor style, String text, Object... args) {
        log(Level.WARN, null, style, null, text, args);
    }

    @Fast
    public void warn(String prefix, ConsoleColor style, String text, Object... args) {
        log(Level.WARN, prefix, style, null, text, args);
    }

    @Fast
    public void warn(String prefix, ConsoleColor[] style, String text, Object... args) {
        log(Level.WARN, prefix, style, null, text, args);
    }

    @Fast
    public void error(String text, Object... args) {
        log(Level.ERROR, null, (ConsoleColor[]) null, null, text, args);
    }

    @Fast
    public void error(ConsoleColor style, String text, Object... args) {
        log(Level.ERROR, null, style, null, text, args);
    }

    @Fast
    public void error(String prefix, ConsoleColor style, String text, Object... args) {
        log(Level.ERROR, prefix, style, null, text, args);
    }

    @Fast
    public void error(String prefix, ConsoleColor[] style, String text, Object... args) {
        log(Level.ERROR, prefix, style, null, text, args);
    }

    @Fast
    public void fatal(String prefix, Throwable throwable) {
        log(Level.FATAL, prefix, (ConsoleColor[]) null, throwable, throwable != null ? throwable.getMessage() : "null");
    }

    @Fast
    public void fatal(Throwable throwable, String text, Object... args) {
        log(Level.FATAL, null, (ConsoleColor[]) null, throwable, text, args);
    }

    @Fast
    public void fatal(String prefix, Throwable throwable, String text, Object... args) {
        log(Level.FATAL, prefix, (ConsoleColor[]) null, throwable, text, args);
    }

    @Fast
    public void fatal(String prefix, ConsoleColor style, Throwable throwable, String text, Object... args) {
        log(Level.FATAL, prefix, style, throwable, text, args);
    }

    @Fast
    public void fatal(String prefix, ConsoleColor[] style, Throwable throwable, String text, Object... args) {
        log(Level.FATAL, prefix, style, throwable, text, args);
    }

    private static final LogBuilder NO_OP = new LogBuilder(null, null) {
        @Override public LogBuilder withStyle(ConsoleColor... style)   { return this; }
        @Override public LogBuilder withPrefix(String prefix)          { return this; }
        @Override public LogBuilder withException(Throwable exception) { return this; }
        @Override public void log(String text, Object... args)         { }
    };

    @Slowly
    public LogBuilder at(Level level) {
        if (!logger.isEnabled(level)) return NO_OP;
        return new LogBuilder(this, level);
    }

    @Slowly public LogBuilder atInfo()             { return at(Level.INFO); }
    @Slowly public LogBuilder atWarn()             { return at(Level.WARN); }
    @Slowly public LogBuilder atError()            { return at(Level.ERROR); }
    @Slowly public LogBuilder atDebug()            { return at(Level.DEBUG); }
    @Slowly public LogBuilder atFatal(Throwable e) { return at(Level.FATAL).withStyle(ConsoleColor.RED_BG).withException(e); }

    public static class LogBuilder {
        private final MouseLogger    parent;
        private final Level          level;
        private       ConsoleColor[] style     = null;
        private       String         prefix    = null;
        private       Throwable      exception = null;

        private LogBuilder(MouseLogger parent, Level level) {
            this.parent = parent;
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

        public LogBuilder withException(Throwable exception) {
            this.exception = exception;
            return this;
        }

        public void log(String text, Object... args) { parent.log(level, prefix, style, exception, text, args); }
    }
}