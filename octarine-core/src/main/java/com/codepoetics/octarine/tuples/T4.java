package com.codepoetics.octarine.tuples;

import com.codepoetics.octarine.functions.F4;
import com.codepoetics.octarine.lenses.Lens;

import java.util.Objects;

public final class T4<A, B, C, D> {
    private final A a;
    private final B b;
    private final C c;
    private final D d;

    private T4(A a, B b, C c, D d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public static <A, B, C, D> Lens<T4<A, B, C, D>, A> first() {
        return Lens.of(
                T4::getFirst,
                T4::withFirst
        );
    }

    public static <A, B, C, D> Lens<T4<A, B, C, D>, B> second() {
        return Lens.of(
                T4::getSecond,
                T4::withSecond
        );
    }

    public static <A, B, C, D> Lens<T4<A, B, C, D>, C> third() {
        return Lens.of(
                T4::getThird,
                T4::withThird
        );
    }

    public static <A, B, C, D> Lens<T4<A, B, C, D>, D> fourth() {
        return Lens.of(
                T4::getFourth,
                T4::withFourth
        );
    }

    public static <A, B, C, D> T4<A, B, C, D> of(A a, B b, C c, D d) {
        return new T4<>(a, b, c, d);
    }

    public A getFirst() {
        return a;
    }

    public <A2> T4<A2, B, C, D> withFirst(A2 a2) {
        return T4.of(a2, b, c, d);
    }

    public B getSecond() {
        return b;
    }

    public <B2> T4<A, B2, C, D> withSecond(B2 b2) {
        return T4.of(a, b2, c, d);
    }

    public C getThird() {
        return c;
    }

    public <C2> T4<A, B, C2, D> withThird(C2 c2) {
        return T4.of(a, b, c2, d);
    }

    public D getFourth() {
        return d;
    }

    public <D2> T4<A, B, C, D2> withFourth(D2 d2) {
        return T4.of(a, b, c, d2);
    }

    public <R> R sendTo(F4<A, B, C, D, R> f) {
        return f.apply(a, b, c, d);
    }

    public <E> T5<A, B, C, D, E> push(E e) {
        return Tuple.of(a, b, c, d, e);
    }

    public T2<D, T3<A, B, C>> pop() {
        return Tuple.of(d, Tuple.of(a, b, c));
    }

    public T2<A, T3<B, C, D>> shift() {
        return Tuple.of(a, Tuple.of(b, c, d));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof T4)) {
            return false;
        }
        T4 other = (T4) o;
        return Objects.equals(a, other.a) &&
                Objects.equals(b, other.b) &&
                Objects.equals(c, other.c) &&
                Objects.equals(d, other.d);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c, d);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s, %s, %s)", a, b, c, d);
    }
}
