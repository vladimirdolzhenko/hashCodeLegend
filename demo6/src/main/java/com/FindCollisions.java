package com;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author vladimir.dolzhenko@gmail.com
 * @since 2017-01-10
 */
public class FindCollisions {
    public static void main(String[] args) throws IOException {
        if (false) {
            System.out.println(Integer.MAX_VALUE);

            int k = 2 * (31 * 31 * 31 * 31 * 31 * 31 * 2 + 31 * 31 * 31 * 31 * 31 * 13 + 31 * 31 * 31 * 9 + 31 * 31 * 30 + 31 * 12) + 4;
            System.out.println(-900 + k);
            return;
        }

        // username

        Map<String, String> map = new LinkedHashMap<String, String>();
        Map<String, String> map2 = new LinkedHashMap<String, String>();

        String key = "username";
        int hashCode = key.hashCode();

        System.out.printf("%s 0x%08X\n", key, key.hashCode());
//        map.put(key, key);

        lookupCollisions2(map2, key, 0, true, hashCode);
        lookupCollisions2(map2, key, 0, false, hashCode);

        lookupCollisions(map, key, 0, true, hashCode);
        lookupCollisions(map, key, 0, false, hashCode);

        writeFile(map, "username.txt");

        map.putAll(map2);
        writeFile(map, "username0.txt");

        System.out.println(map.size());
        String v = map.get(key);
        System.out.println(v);
    }

    private static void writeFile(Map<String, String> map, String pathname) throws IOException {
        File file = new File(pathname);
        FileWriter writer = new FileWriter(file, false);
        try {

            for (String s : map.keySet()) {
                writer.append(s).append('\n');
            }
            writer.flush();
        } finally {
            writer.close();
        }
    }

    private static void lookupCollisions2(Map<String, String> map, String key, int depth, boolean plus, final int hash) {
        if (depth > key.length()) {
            return;
        }

        for (int i = 1; i < key.length() - 1; i++) {
            char[] chars = key.toCharArray();
            chars[chars.length - (i + 2)] += plus ? 1 : -1;
            //chars[chars.length - (i + 1)] -= plus ? 31 : -31;
            chars[chars.length - i] -= plus ? 31 * 31 : -31 * 31;

            if (!validChar(chars[chars.length - (i + 2)])
                || !validChar(chars[chars.length - i])) {
                return;
            }

            String s = new String(chars);

            if (s.hashCode() != hash) {
                /*/
                throw new RuntimeException(
                        String.format("expected 0x%08X - was 0x%08X",
                                      hash, s.hashCode()));
                                      /*/
                return;
                //*/
            }
            //            System.out.printf("%s 0x%08X\n", s, s.hashCode());
            lookupCollisions(map, s, depth + 1, plus, hash);
            lookupCollisions2(map, s, depth + 1, plus, hash);

            map.put(s, s);
        }
    }

    private static void lookupCollisions(Map<String, String> map, String key, int depth, boolean plus, final int hash) {
        if (depth > key.length()) {
            return;
        }

        for (int i = 1; i < key.length(); i++) {
            char[] chars = key.toCharArray();
            chars[chars.length - (i + 1)] += plus ? 1 : -1;
            chars[chars.length - i] -= plus ? 31 : -31;

            if (!validChar(chars[chars.length - (i + 1)])
                || !validChar(chars[chars.length - i])) {
                return;
            }

            String s = new String(chars);

            if (s.hashCode() != hash) {
                throw new RuntimeException(
                        String.format("expected 0x%08X - was 0x%08X",
                                      hash, s.hashCode()));
            }
            //            System.out.printf("%s 0x%08X\n", s, s.hashCode());
            lookupCollisions(map, s, depth + 1, plus, hash);

            map.put(s, s);
        }
    }

    private static boolean validChar(char ch) {
        return ch > ' ' && ch < 0xFFFF && ch != '=';
    }
}