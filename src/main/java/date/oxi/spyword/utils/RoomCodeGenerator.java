package date.oxi.spyword.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RoomCodeGenerator {
    private Set<String> usedCodes = new HashSet<>();
    private Random random = new Random();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public String generateUniqueCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (usedCodes.contains(code));

        usedCodes.add(code);
        return code;
    }

    private String generateRandomCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            char character = CHARACTERS.charAt(random.nextInt(CHARACTERS.length()));
            code.append(character);
        }
        return code.toString();
    }
}
