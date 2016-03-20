package ru.spbau.mit.pilyugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Collections {

    public static <A, R> List<R> map(Function1<? super A, ? extends R> function, Iterable<? extends A> iterable) {
        List<R> result = new ArrayList<>();
        for (A element : iterable) {
            result.add(function.apply(element));
        }
        return result;
    }

    public static <A> List<A> filter(Predicate<? super A> predicate, Iterable<? extends A> iterable) {
        List<A> result = new ArrayList<>();
        for (A element : iterable) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    public static <A> List<A> takeWhile(Predicate<? super A> predicate, Iterable<? extends A> iterable) {
        List<A> result = new ArrayList<>();
        for (A element : iterable) {
            if (!predicate.apply(element)) {
                break;
            }
            result.add(element);
        }
        return result;
    }

    public static <A> List<A> takeUnless(Predicate<? super A> predicate, Iterable<? extends A> iterable) {
        return takeWhile(Predicate.not(predicate), iterable);
    }

    public static <A, R> R foldl(Function2<? super R, ? super A, ? extends R> function,
                                 R initialValue, Iterable<? extends A> iterable) {
        R result = initialValue;
        for (A element : iterable) {
            result = function.apply(result, element);
        }
        return result;
    }

    public static <A, R> R foldr(Function2<? super A, ? super R, ? extends R> function,
                                 R initialValue, Iterable<? extends A> iterable) {
        return processFoldr(function, initialValue, iterable.iterator());
    }

    private static <A, R> R processFoldr(Function2<? super A, ? super R, ? extends R> function,
                                         R initialValue, Iterator<? extends A> iterator) {
        if (!iterator.hasNext()) {
            return initialValue;
        }
        return function.apply(iterator.next(), processFoldr(function, initialValue, iterator));
    }
}
