package ru.spbau.mit.pilyugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Collections {

    public static <A, R> List<R> map(Iterable<A> iterable, Function1<? super A, R> function) {
        List<R> result = new ArrayList<>();
        for (A element : iterable) {
            result.add(function.apply(element));
        }
        return result;
    }

    public static <A> List<A> filter(Iterable<A> iterable, Predicate<? super A> predicate) {
        List<A> result = new ArrayList<>();
        for (A element : iterable) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    public static <A> List<A> takeWhile(Iterable<A> iterable, Predicate<? super A> predicate) {
        List<A> result = new ArrayList<>();
        for (A element : iterable) {
            if (!predicate.apply(element)) {
                break;
            }
            result.add(element);
        }
        return result;
    }

    public static <A> List<A> takeUnless(Iterable<A> iterable, Predicate<? super A> predicate) {
        return takeWhile(iterable, predicate.not());
    }

    public static <A, R> R foldl(Iterable<A> iterable, Function2<? super R, ? super A, R> function, R initialValue) {
        R result = initialValue;
        for (A element : iterable) {
            result = function.apply(result, element);
        }
        return result;
    }

    public static <A, R> R foldr(Iterable<A> iterable, Function2<? super A, ? super R, R> function, R initialValue) {
        return processFoldr(iterable.iterator(), function, initialValue);
    }

    private static <A, R> R processFoldr(Iterator<? extends A> iterator,
                                         Function2<? super A, ? super R, R> function,
                                         R initialValue) {
        if (!iterator.hasNext()) {
            return initialValue;
        }
        return function.apply(iterator.next(), processFoldr(iterator, function, initialValue));
    }
}
