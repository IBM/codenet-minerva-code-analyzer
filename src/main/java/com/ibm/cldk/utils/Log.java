/*
Copyright IBM Corporation 2023, 2024

Licensed under the Apache Public License 2.0, Version 2.0 (the "License");
you may not use this file except in compliance with the License.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.ibm.cldk.utils;

import java.time.LocalDateTime;

/**
 * The type Log.
 */
public class Log {
    /**
     * The constant ANSI_RESET.
     */
    public static final String ANSI_RESET  = "\u001B[0m";
    /**
     * The constant ANSI_BLACK.
     */
    public static final String ANSI_BLACK  = "\u001B[30m";
    /**
     * The constant ANSI_RED.
     */
    public static final String ANSI_RED    = "\u001B[31m";
    /**
     * The constant ANSI_GREEN.
     */
    public static final String ANSI_GREEN  = "\u001B[32m";
    /**
     * The constant ANSI_YELLOW.
     */
    public static final String ANSI_YELLOW = "\u001B[33m";
    /**
     * The constant ANSI_BLUE.
     */
    public static final String ANSI_BLUE   = "\u001B[34m";
    /**
     * The constant ANSI_PURPLE.
     */
    public static final String ANSI_PURPLE = "\u001B[35m";
    /**
     * The constant ANSI_CYAN.
     */
    public static final String ANSI_CYAN   = "\u001B[36m";
    /**
     * The constant ANSI_WHITE.
     */
    public static final String ANSI_WHITE  = "\u001B[37m";
    private static boolean verbose = true;

    /**
     * Set verbose setting to on or off.
     *
     * @param val True or false.
     */
    public static final void setVerbosity(boolean val) {
        verbose = val;
    }

    /**
     * Is verbosity turned on/off
     *
     * @return Boolean boolean
     */
    public static final boolean isVerbose() {
        return verbose;
    }

    /**
     * Info.
     *
     * @param msg the msg
     */
    public static final void info(String msg) {
        toConsole(msg, ANSI_PURPLE, "INFO");
    }

    /**
     * Done.
     *
     * @param msg the msg
     */
    public static final void done(String msg) {
        toConsole(msg, ANSI_GREEN, "DONE");
    }

    /**
     * Debug.
     *
     * @param msg the msg
     */
    public static final void debug(String msg) {
        toConsole(msg, ANSI_YELLOW, "DEBUG");
    }

    /**
     * Warn.
     *
     * @param msg the msg
     */
    public static final void warn(String msg) {
        toConsole(msg, ANSI_YELLOW, "WARN");
    }

    /**
     * Error.
     *
     * @param msg the msg
     */
    public static final void error(String msg) {
        toConsole(msg, ANSI_RED, "ERROR");
    }

    /**
     * Print log message to console
     *
     * @param msg to print to console
     */
    private static void toConsole(String msg, String ansi_color, String Level) {
        if (isVerbose()) {
            LocalDateTime localDateTime = LocalDateTime.now();
            System.out.println(
                    ANSI_CYAN + localDateTime + ANSI_RESET + ansi_color + "\t[" + Level + "]\t" + ANSI_RESET + msg);
        }
    }
}
