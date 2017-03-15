package com;

import java.util.*;


/**
 * @author vladimir.dolzhenko@gmail.com
 * @since 2017-03-16
 */
public class CollisionsDump {
    public static void main(String[] args) {
        Map<String, String> map = getMap("username");
        Map<String, String> map2 = getMap("ABCD");

        Set<String> strings = map.keySet();
        if (strings.size() < 10) {
            throw new IllegalStateException("strings:" + strings.size());
        }

        Set<String> strings2 = map2.keySet();
        if (strings2.size() < 3)
            throw new IllegalStateException("strings2:" + strings2.size());

        System.out.println(filterGood(strings));
        System.out.println(filterGood(strings2));

        Map<String, String> m = new HashMap<>();
        int i = 0;
        for (String s : strings) {
            if (i ++ > 10){
                break;
            }
            m.put(s, s);
        }
        /*
        i = 0;
        for (String s : strings2) {
            if (i ++ > 5){
                break;
            }
            m.put(s, s);
        }
        */
        Foo foo = new Foo(m);
        System.out.println(foo);
    }

    private static List<String> filterGood(Collection<String> values) {
        final List<String> goodValues = new ArrayList<>();
        for (String s : values) {
            boolean good = true;
            for(int k = 0, len = s.length(); k < len; k++){
                char ch = s.charAt(k);
                good &= ch > ' ' && ch < 127;
                if (!good) break;
            }
            if (good){
                goodValues.add(s);
            }
        }
        Collections.sort(goodValues);
        return goodValues;
    }

    private static Map<String, String> getMap(String key) {
        Map<String, String> map = new HashMap<>();
        map.put(key, key);
        int hash = key.hashCode();
        lookupCollisions0(map, key, 0, false, hash);
//        lookupCollisions0(map, key, 0, true, hash);
        lookupCollisions(map, key, 0, false, hash);
        lookupCollisions(map, key, 0, true, hash);
        lookupCollisions2(map, key, 0, true, hash);
        lookupCollisions2(map, key, 0, false, hash);
        return map;
    }

    static void lookupCollisions2(Map<String, String> map, String key, int depth, boolean plus, final int hash) {
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

    static void lookupCollisions(Map<String, String> map, String key, int depth, boolean plus, final int hash) {
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

    static void lookupCollisions0(Map<String, String> map, String key, int depth, boolean plus, final int hash) {
        if (depth > key.length()) {
            return;
        }

        for (int i = 0; i < key.length() - 1; i++) {
            char[] chars = key.toCharArray();
            chars[i] += plus ? 1 : -1;
            chars[i + 1] -= plus ? 31 : -31;

            if (!validChar(chars[i])
                    || !validChar(chars[i + 1])) {
                return;
            }

            String s = new String(chars);

            if (s.hashCode() != hash) {
                throw new RuntimeException(
                        String.format("expected 0x%08X - was 0x%08X",
                                hash, s.hashCode()));
            }
            //            System.out.printf("%s 0x%08X\n", s, s.hashCode());
            lookupCollisions0(map, s, depth + 1, plus, hash);
            lookupCollisions(map, s, depth, plus, hash);

            map.put(s, s);
        }
    }
    private static boolean validChar(char ch) {
        return ch >= '0' && ch < 127 && ch != '=';
    }

    private static class Foo {
        private final Map<String, String> map;

        public Foo(Map<String, String> map) {
            this.map = map;
        }
    }
}
