package com.sucy.skill.api.util;

import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Formats strings into various forms
 */
public class TextFormatter {

    /**
     * Regex string for finding color patterns
     */
    private static final String COLOR_REGEX = "([0-9a-fl-orA-FL-OR])";

    /**
     * Formats text into individual words
     * (e.g. This Would Be A Result)
     *
     * @param string string to format
     * @return       formatted string
     */
    public static String format(String string) {
        if (string == null || string.length() == 0)
            return string;

        String[] pieces = split(string);
        String result = pieces[0].substring(0, 1).toUpperCase() + pieces[0].substring(1).toLowerCase();
        for (int i = 1; i < pieces.length; i++) {
            result += " " + pieces[i].substring(0, 1).toUpperCase() + pieces[i].substring(1).toLowerCase();
        }
        return result;
    }

    /**
     * Formats text into lower camel case form
     * (e.g. thisWouldBeAResult
     *
     * @param string string to be formatted
     * @return       formatted string
     */
    public static String formatLowerCamel(String string) {
        if (string == null || string.length() == 0)
            return string;

        String[] pieces = split(string);
        String result = pieces[0].toLowerCase();
        for (int i = 1; i < pieces.length; i++)
            result += pieces[i].substring(0, 1).toUpperCase() + pieces[i].substring(1).toLowerCase();
        return result;
    }

    /**
     * Formats the string into upper camel case form
     * (e.g. ThisWouldBeAResult)
     *
     * @param string string to be formatted
     * @return       formatted string
     */
    public static String formatUpperCamel(String string) {
        if (string == null || string.length() == 0)
            return string;

        String[] pieces = split(string);
        String result = "";
        for (String piece : pieces)
            result += " " + piece.substring(0, 1).toUpperCase() + piece.substring(1).toLowerCase();
        return result.substring(1);
    }

    /**
     * Formats a decimal number
     *
     * @param number   number to format
     * @param decimals how many decimal places should be used
     * @param commas   whether or not to add commas (e.g. 1,210,321)
     * @return         formatted number
     */
    public static String formatNumber(double number, int decimals, boolean commas) {
        String formatString = commas ? "#,###,###,##0" : "#########0";
        if (decimals >= 1) formatString += ".0";
        for (int i = 1; i < decimals; i++) formatString += "0";
        return new DecimalFormat(formatString).format(number);
    }

    /**
     * Colors a string using & as the color indicator
     *
     * @param string string to color
     * @return       colored string
     */
    public static String colorString(String string) {
        return colorString(string, '&');
    }

    /**
     * Colors a string using the given color indicator
     *
     * @param string string to color
     * @param token  color indicator
     * @return       colored string
     */
    public static String colorString(String string, char token) {
        return string.replaceAll(token + COLOR_REGEX, ChatColor.COLOR_CHAR + "$1");
    }

    /**
     * Colors a list of strings using & as the color indicator
     *
     * @param list string list
     * @return     colored string list
     */
    public static List<String> colorStringList(List<String> list) {
        return colorStringList(list, '&');
    }

    /**
     * Colors a list of strings with the given color indicator
     *
     * @param list  string list
     * @param token color indicator
     * @return     colored string list
     */
    public static List<String> colorStringList(List<String> list, char token) {
        ArrayList<String> copy = new ArrayList<String>();
        for (String string : list) {
            copy.add(colorString(string));
        }
        return copy;
    }

    /**
     * Splits a string
     *
     * @param string string to split
     * @return       split string
     */
    private static String[] split(String string) {
        if (string.contains(" "))
            return string.split(" ");
        if (string.contains("_"))
            return string.split("_");
        else return new String[] { string };
    }
}
