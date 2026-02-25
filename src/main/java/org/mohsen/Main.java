package org.mohsen;

import java.util.HashMap;
import java.util.Map;

public class Main {
    static void main() {
        User user1 = new User("ali");
        User user2 = new User("ali");

        boolean result = user1.equals(user2);
        System.out.printf("result: %s\n", result);

        int user1HashCode = user1.hashCode();
        int user2HashCode = user2.hashCode();

        System.out.printf("user1HashCode: %d, user2HashCode: %d\n", user1HashCode, user2HashCode);

        Map<User, String> map = new HashMap<>();
        map.put(user1, "ali");
        map.put(user2, "ali۱");

        System.out.printf("map: %s\n", map.size());
        System.out.printf("map: %s\n", map.get(user2));
    }
}
