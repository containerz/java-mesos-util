/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.elodina.mesos.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Strings {
    public static String capitalize(String s) {
        if (s.isEmpty()) return s;
        char[] chars = s.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static String join(Iterable<?> objects) { return join(objects, ","); }

    public static String join(Iterable<?> objects, String separator) {
        String result = "";

        for (Object object : objects)
            result += object + separator;

        if (result.length() > 0)
            result = result.substring(0, result.length() - separator.length());

        return result;
    }

    public static Map<String, String> parseMap(String s) { return parseMap(s, false); }

    public static Map<String, String> parseMap(String s, boolean nullValues) { return parseMap(s, ',', '=', nullValues); }

    public static Map<String, String> parseMap(String s, char entrySep) { return parseMap(s, entrySep, '=', false); }

    public static Map<String, String> parseMap(String s, char entrySep, char valueSep) { return parseMap(s, entrySep, valueSep, false); }

    public static Map<String, String> parseMap(String s, char entrySep, char valueSep, boolean nullValues) {
        Map<String, String> result = new LinkedHashMap<>();
        if (s == null) return result;

        for (String entry : splitEscaped(s, entrySep, false)) {
            if (entry.trim().isEmpty()) throw new IllegalArgumentException(s);

            List<String> pair = splitEscaped(entry, valueSep, true);
            String key = pair.get(0).trim();
            String value = pair.size() > 1 ? pair.get(1).trim() : null;

            if (value == null && !nullValues) throw new IllegalArgumentException(s);
            result.put(key, value);
        }

        return result;
    }

    private static List<String> splitEscaped(String s, char sep, boolean unescape) {
        List<String> parts = new ArrayList<>();

        boolean escaped = false;
        String part = "";
        for (char c : s.toCharArray()) {
            if (c == '\\' && !escaped) escaped = true;
            else if (c == sep && !escaped) {
                parts.add(part);
                part = "";
            } else {
                if (escaped && !unescape) part += "\\";
                part += c;
                escaped = false;
            }
        }

        if (escaped) throw new IllegalArgumentException("open escaping");
        if (!part.equals("")) parts.add(part);

        return parts;
    }

    public static String formatMap(Map<String, ?> map) { return formatMap(map, ',', '='); }

    public static String formatMap(Map<String, ?> map, char entrySep, char valueSep) {
        String s = "";

        for (String k : map.keySet()) {
            Object v = map.get(k);
            if (!s.isEmpty()) s += entrySep;
            s += escape(k, entrySep, valueSep);
            if (v != null) s += valueSep + escape("" + v, entrySep, valueSep);
        }

        return s;
    }

    private static String escape(String s, char entrySep, char valueSep) {
        String result = "";

        for (char c : s.toCharArray()) {
            if (c == entrySep || c == valueSep || c == '\\') result += "\\";
            result += c;
        }

        return result;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean isFloat(String s) {
        try {
            Float.parseFloat(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean isBigDecimal(String s) {
        try {
            new BigDecimal(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static final String HEX_CHARS = "0123456789ABCDEF";

    public static String formatHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(2 * bytes.length);

        for (byte b : bytes)
            hex.append(HEX_CHARS.charAt((b & 0xF0) >> 4)).append(HEX_CHARS.charAt((b & 0x0F)));

        return hex.toString();
    }

    public static byte[] parseHex(String s) throws IllegalArgumentException {
        if (s.length() % 2 == 1) throw new IllegalArgumentException(s);

        char[] chars = s.toUpperCase().toCharArray();
        byte[] bytes = new byte[chars.length / 2];

        for (int i = 0, j = 0; i < chars.length; i += 2, j++) {
            int high = HEX_CHARS.indexOf(chars[i]);
            if (high == -1) throw new IllegalArgumentException(s);

            int low = HEX_CHARS.indexOf(chars[i+1]);
            if (low == -1) throw new IllegalArgumentException(s);

            bytes[j] = (byte)(high << 4 | low);
        }

        return bytes;
    }
}
