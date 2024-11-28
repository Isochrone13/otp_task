import java.util.Comparator;
import java.util.List;

public class Collections {

    // Метод 1: binarySearch для List с Comparable
    public static <T extends Comparable<? super T>> int binarySearch(List<? extends T> list, T key) {
        return java.util.Collections.binarySearch(list, key);
    }

    // Метод 2: binarySearch для List с Comparator
    public static <T> int binarySearch(List<? extends T> list, T key, Comparator<? super T> c) {
        return java.util.Collections.binarySearch(list, key, c);
    }
}