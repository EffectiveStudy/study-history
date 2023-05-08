package org.example;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

// ordinal 인덱싱 대신 EnumMap을 사용하라
public class Item37 {

    // 배열이나 리스트에서 원소를 꺼낼 때 ordinal 메서드로 인덱스를 얻을 수 있다.
    // ANNUAL = 한해살이
    // PERENNIAL = 여러해살이
    // BIENNIAL = 두해살이
    enum LifeCycle {ANNUAL, PERENNIAL, BIENNIAL}

    final String name;
    final LifeCycle lifeCycle;

    public Item37(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }

    @Override
    public String toString() {
        return name;
    }

    // 여러 식물들이 존재하는 정원이 있다.
    // 생애주기별로 식물을 그룹화하여 관리하고 출력하는 로직을 개발해본다.
    static Item37[] garden = new Item37[]{
            new Item37("a", LifeCycle.ANNUAL),
            new Item37("b", LifeCycle.BIENNIAL),
            new Item37("c", LifeCycle.ANNUAL),
            new Item37("d", LifeCycle.PERENNIAL),
            new Item37("e", LifeCycle.PERENNIAL)};

    public static void main(String[] args) {
        //ordinal();
        enummap();
        //stream();
    }

    // ordinal 함수를 사용하는 방식
    private static void ordinal() {
        // 배열은 제네릭과 호환되지 않으니 비검사 형변환을 수행해야 한다.
        Set<Item37>[] item37s = (Set<Item37>[]) new Set[LifeCycle.values().length];
        // 정확한 인덱스 보장이 어렵다.
        for (int i = 0; i < item37s.length; i++) {
            item37s[i] = new HashSet<>();
        }
        for (Item37 i : garden) {
            item37s[i.lifeCycle.ordinal()].add(i);
        }
        // 두 데이터의 배열 순환이 정확함을 보장할 수 없다.
        // 출력 결과를 직접 구성해야 한다.
        for (int i = 0; i < item37s.length; i++) {
            System.out.printf("%s: %s%n", Item37.LifeCycle.values()[i], item37s[i]);
        }
    }

    // EnumMap을 사용하는 방식
    // EnumMap = 열거 타입을 키로 사용하도록 설계한 아주 빠른 Map 구현체
    // 더 짧고 명료하다.
    private static void enummap() {
        // 형변환도 없다.
        Map<LifeCycle, Set<Item37>> item37Map = new EnumMap<>(Item37.LifeCycle.class);
        // 인덱스 보장이 잘 된다.
        for (Item37.LifeCycle lc : Item37.LifeCycle.values())
            item37Map.put(lc, new HashSet<>());
        for (Item37 p : garden)
            item37Map.get(p.lifeCycle).add(p);
        // 자체적으로 출력용 문자열을 제공한다.
        System.out.println(item37Map);
    }
    // 성능이 왜 비슷할까?
    // EnumMap도 내부적으로는 배열을 사용하기 때문이다.
    // 제네릭 + 배열을 활용하여 Map으로 감싸 타입 안전성과 배열의 성능을 모두 얻어 냈다.

    // Stream을 사용하는 방식
    private static void stream() {
        // EnumMap을 사용하지 않아 EnumMap의 성능 이점이 사라진다.
        // 자체적으로 Map을 구성한다.
        System.out.println(Arrays.stream(garden).collect(groupingBy(p -> p.lifeCycle)));

        // 람다를 활용하여 EnumMap을 사용하도록 구성한다.
        // stream()을 사용하면 EnumMap만 사용했을 때와는 살짝 다르게 동작한다.
        // EnumMap 버전은 언제나 식물의 생애주기당 하나씩의 중첩 맵을 만들지만 스트림 버전에서는 해당 생애주기에 속하는 식물이 있을 때만 만든다.
        // 즉, 가지고 있는 Enum 갯수 만큼의 중첩맵이 있게 된다.
        System.out.println(Arrays.stream(garden)
                .collect(groupingBy(p -> p.lifeCycle, () -> new EnumMap<>(LifeCycle.class), toSet())));
    }

    public enum Phase {
        SOLID, LIQUID, GAS, PLASMA;

        public enum Transition1 {
            MELT, FREEZE, BOIL, CONDENSE, SUBLIME, DEPOSIT;

            private static final Transition1[][] TRANSITIONS = {
                    { null, MELT, SUBLIME },
                    { FREEZE, null, BOIL },
                    { DEPOSIT, CONDENSE, null }
            };

            public static Transition1 from(Phase from, Phase to) {
                // ordinal을 이용하여 이중배열의 인덱스로 사용하여 데이터를 조회한다.
                // 인덱스 보장이 너무 어렵다.
                // 새로운 타입이 추가되거나 순서가 바뀌면 큰일난다.
                // => 각 원소의 인덱스가 변경되기 때문에 ordinal() 결과값도 달라지기 때문이다.
                return TRANSITIONS[from.ordinal()][to.ordinal()];
            }

        }

        public enum Transition2 {
            MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
            BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
            SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID),

            IONIZE(GAS, PLASMA), DEIONIZE(PLASMA, GAS);

            private final Phase from;
            private final Phase to;

            Transition2(Phase from, Phase to) {
                this.from = from;
                this.to = to;
            }

            // EnumMap을 사용하자.
            // EnumMap을 통해 초기화를 하니 인덱스 기반의 세팅이 모두 사라진다.
            // 그렇다는 것은 인덱스 보장의 필요성이 없다는것이다.
            // 이중 배열을 표현하기 위해서 groupingBy()와 toMap()을 활용했다.
            private static final Map<Phase, Map<Phase, Transition2>>
                    m = Stream.of(values()).collect(groupingBy(t -> t.from,
                        () -> new EnumMap<>(Phase.class),
                        toMap(t -> t.to, t -> t,
                            (x, y) -> y, () -> new EnumMap<>(Phase.class))));

            public static Transition2 from(Phase from, Phase to) {
                return m.get(from).get(to);
            }

        }

    }

}
