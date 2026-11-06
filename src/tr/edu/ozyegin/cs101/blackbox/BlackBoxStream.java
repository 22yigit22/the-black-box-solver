package tr.edu.ozyegin.cs101.blackbox;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BlackBoxStream {
    public static Stream<BlackBox> streamBlackBoxes() {
        return IntStream.range(0, 1 << 24) // 0 to 16,777,215
                .mapToObj(bits -> {
                    int a = (bits >> 18) & 0x3F;
                    int b = (bits >> 12) & 0x3F;
                    int c = (bits >> 6) & 0x3F;
                    int d = bits & 0x3F;
                    if (a < b && b < c && c < d) {
                        return new BlackBox(Location.locationFromLinearPosition(a),
                                            Location.locationFromLinearPosition(b),
                                            Location.locationFromLinearPosition(c),
                                            Location.locationFromLinearPosition(d));
                    } else {
                        return null;
                    }
                })
                .filter(arr -> arr != null);
    }
}

