package com.sucy.skill.api.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Handles measuring the size of strings and squaring them up</p>
 * <p>Units for sizes are based off of the space between characters</p>
 * <p>(e.g. size 4 = the size of 4 of the spaces between characters)</p>
 */
public class TextSizer {

    /**
     * Gets the size of the message
     *
     * @param message message to measure
     * @return        message size
     */
    public static int measureString(String message) {

        // Make sure the string isn't null
        if (message == null)
            throw new IllegalArgumentException("Invalid string - null");

        byte boldBonus = 0;
        boolean skip = false;

        int index = 0;
        int size = 0;
        for (char c : message.toCharArray()) {
            if (skip) {
                skip = false;
                index++;
                continue;
            }

            // Chat colors don't add to the size, but bold adds 1 to all characters affected
            if (c == ChatColor.COLOR_CHAR) {
                skip = true;
                if (message.length() > index + 1) {
                    if (message.charAt(index + 1) == 'l')
                        boldBonus = 1;
                    else if (message.charAt(index + 1) == 'r')
                        boldBonus = 0;
                }
            }

            // Add the size of each character other than what's used for chat colors
            else {
                if (lengths.containsKey(c)) size += lengths.get(c) + boldBonus;
                else size += 6 + boldBonus;
            }

            index++;
        }

        return size;
    }

    /**
     * Expands a string to meet the desired size
     *
     * @param message message to expand
     * @param size    desired size
     * @param front   whether or not to add to the front of the string
     * @return        the resulting message
     * @throws IllegalArgumentException when the string is either too large or just one pixel too small
     */
    public static String expand(String message, int size, boolean front) {

        // Make sure the message isn't null
        if (message == null)
            throw new IllegalArgumentException("Invalid string - null");

        // Get the length of the message
        int currentSize = measureString(message);

        // Already the correct size
        if (currentSize == size) return message;

        // Too large of a string
        if (currentSize > size)
            throw new IllegalArgumentException("Invalid string - larger than desired size");

        // Can't match the size when it is one pixel away
        if (currentSize == size - 1)
            throw new IllegalArgumentException("Invalid string - one pixel off and unable to match desired size");

        // Expand the string
        if (front) return expandFront(message, currentSize, size);
        else return expandBack(message, currentSize, size);
    }

    /**
     * Expands the messages so that they are all the same size
     * Note: this adds a space to the end of the longest string to ensure it can square them all
     *
     * @param messages messages to expand
     * @param front    whether or not to add to the front
     * @return         list of expanded strings
     */
    public static List<String> expand(List<String> messages, boolean front) {

        // Don't worry about empty lists
        if (messages.size() == 0)
            return messages;

        // Get the maximum size
        int maxSize = 0;
        for (String message : messages) {
            int size = measureString(message);
            if (size > maxSize) maxSize = size;
        }

        // Add a space to make sure it doesn't break
        maxSize += 4;

        // Expand each of the strings
        ArrayList<String> result = new ArrayList<String>();
        for (String message : messages) {
            result.add(expand(message, maxSize, front));
        }

        return result;
    }

    /**
     * Expands a string by adding to the end of the string
     *
     * @param message     message to expand
     * @param currentSize current size of the message
     * @param size        desired size of the message
     * @return            expanded message
     */
    private static String expandBack(String message, int currentSize, int size) {

        while (currentSize < size - 3 && currentSize != size - 5) {
            message += ' ';
            currentSize += 4;
        }
        if (currentSize < size - 2) {
            message += ChatColor.BLACK + "'";
            currentSize += 3;
        }
        if (currentSize < size - 1) {
            message += ChatColor.BLACK + "`";
        }

        return message + ChatColor.RESET;
    }

    /**
     * Expands a string by adding to the front of the string
     *
     * @param message     message to expand
     * @param currentSize current size of the message
     * @param size        desired size of the message
     * @return            expanded message
     */
    private static String expandFront(String message, int currentSize, int size) {

        while (currentSize < size - 3 && currentSize != size - 5) {
            message = " " + message;
            currentSize += 4;
        }
        if ((size - currentSize) % 2 == 1) {
            message = ChatColor.BLACK + "'" + ChatColor.RESET + message;
            currentSize += 3;
        }
        if ((size - currentSize) % 4 == 2) {
            message = ChatColor.BLACK + "`" + ChatColor.RESET + message;
            currentSize += 2;
        }

        return message;
    }

    /**
     * <p>Splits the list of messages into a number of columns equal to the number of
     * alignments provided. Columns from left to right will have the provided alignments
     * in order. The returned list contains the rows resulting from the arrangement.</p>
     *
     * <p>The vertical parameter refers to what order the messages are added to columns.
     * If true, then messages will appear in the following pattern assuming 3 columns
     * and 8 elements:</p>
     * <p>0 3 6</p>
     * <p>1 4 7</p>
     * <p>2 5</p>
     * <p>If set to false, the messages will appear in this pattern instead:</p>
     * <p>0 1 2</p>
     * <p>3 4 5</p>
     * <p>6 7</p>
     *
     * @param messages   messages to split into columns
     * @param vertical   whether or not to apply messages vertically
     * @param alignments the alignments for each column
     * @return           the arranged message rows
     */
    public static List<String> split(List<String> messages, boolean vertical, TextAlignment... alignments) {
        int columnCount = alignments.length;
        int rowCount = (messages.size() + columnCount - 1) / columnCount;

        // Initialize rows
        String[] rows = new String[rowCount];
        for (int i = 0; i < rowCount; i++) {
            rows[i] = "";
        }

        // Retrieve the columns
        for (int i = 0; i < columnCount; i++) {

            // Retrieve a column
            List<String> column = new ArrayList<String>();
            for (int j = 0; j < rowCount && i * rowCount + j < messages.size(); j++) {
                int index = j * columnCount + i;
                if (vertical) {
                    index = j + i * rowCount;
                }
                column.add(messages.get(index));
            }

            // Arrange the column
            column = expand(column, alignments[i] == TextAlignment.RIGHT);

            // Append the column elements to the rows
            for (int j = 0; i < column.size(); j++) {
                rows[j] += column.get(j);
            }
        }

        return Arrays.asList(rows);
    }

    /**
     * Splits a string to fit within the given size, breaking it up by word
     *
     * @param message message to split
     * @param maxSize maximum size of each line
     * @return        resulting lines
     */
    public static List<String> split(String message, int maxSize) {
        List<String> result = new ArrayList<String>();
        if (measureString(message) <= maxSize) {
            result.add(message);
            return result;
        }
        String[] pieces = message.contains(" ") ? message.split(" ") : new String[] { message };

        String current = "";
        for (int i = 0; i < pieces.length; i++) {
            if (current.length() == 0) {
                while (measureString(pieces[i]) <= maxSize) {
                    char[] chars = pieces[i].toCharArray();
                    String temp = "";
                    int index = 0;
                    while (measureString(temp) <= maxSize) {
                        temp += chars[index++];
                    }
                    result.add(temp.substring(0, temp.length() - 1));
                    pieces[i] = pieces[i].substring(index - 1);
                }
                current += pieces[i];
                continue;
            }

            String temp = current + pieces[i];
            if (measureString(temp) > maxSize) {
                result.add(current);
                current = pieces[i];
            }
            else current = temp;
        }

        result.add(current);
        return result;
    }

    /**
     * Creates a line that is the maximum size allowed in the chat box using the given info
     *
     * @param begin beginning string
     * @param end   ending string
     * @param fill  string to fill with
     * @return      maximum size string line
     */
    public static String createLine(String begin, String end, String fill) {
        int startingSize = measureString(begin) + measureString(end);
        int fillCount = (320 - startingSize) / measureString(fill);
        for (int i = 0; i < fillCount; i++) {
            begin += fill + "";
        }
        return begin + end;
    }

    /**
     * The lengths for each character
     */
    private static final HashMap<Character, Byte> lengths = new HashMap<Character, Byte>() {{

        // Length 6 + 1
        put('~', (byte)7);
        put('@', (byte)7);

        // All not-included characters are size 5 + 1

        // Length 4 + 1
        put('f', (byte)5);
        put('k', (byte)5);
        put('"', (byte)5);
        put('<', (byte)5);
        put('>', (byte)5);
        put('{', (byte)5);
        put('}', (byte)5);
        put('(', (byte)5);
        put(')', (byte)5);
        put('*', (byte)5);

        // length 3 + 1
        put('I', (byte)4);
        put('t', (byte)4);
        put(' ', (byte)4);
        put('[', (byte)4);
        put(']', (byte)4);

        // length 2 + 1
        put('l', (byte)3);
        put('\'', (byte)3);

        // length 1 + 1
        put('|', (byte)2);
        put('.', (byte)2);
        put(';', (byte)2);
        put(':', (byte)2);
        put('!', (byte)2);
        put('`', (byte)2);
        put(',', (byte)2);
        put('i', (byte)2);
    }};
}
