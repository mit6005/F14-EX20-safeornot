import java.util.*;

/* Tiny thread safety argument examples.
 * Note: explanations identify most salient issues but may not be exhaustive.
 */

//// 1

class StaticFieldInClass {
    private static Map<Integer, Result> cache = new HashMap<>();
    // Thread safety argument:
    //   "cache" is confined to StaticFieldInClass
    
    // ... use "cache" in methods ...
}

//// Problem: any thread can call public methods that use cache

//// 2

class FieldInClass {
    private Map<Integer, Result> cache;
    // Thread safety argument:
    //   "cache" is confined to FieldInClass
    
    public FieldInClass() {
        cache = new HashMap<>();
    }
    
    // ... use "cache" in public methods ...
}

//// Problem: same

//// 3

class LocalVariable {
    // Thread safety argument:
    //   "cache" is confined to the current thread
    
    public Result computeSomething() {
        Map<Integer, Result> cache = new HashMap<>();
        
        // ... use "cache" here ...
        
        return Result.ofComputingSomething;
    }
}

//// Incomplete: need to show that the ... code doesn't create aliases of cache
////             or create threads that can see cache

//// 4

class FinalLocalVariable {
    // Thread safety argument:
    // "cache" is confined to the current thread
    
    public int computeSomething() {
        final Map<Integer, Boolean> cache = new HashMap<>();
        
        // ... use "cache" here ...
        
        return Result.ofComputingSomeMoreThings;
    }
}

//// Incomplete: same.

//// 5

class Singleton {
    private static ImportantObject important;
    // Invariant: we never create more than one ImportantObject
    // Thread safety argument:
    //   ImportantObjects are threadsafe
    
    public static ImportantObject getImportantObject() {
        if (important == null) {
            important = new ImportantObject();
        }
        return important;
    }
}

// A threadsafe important object.
class ImportantObject { /* ... */ }

//// Incomplete/problem: race condition jeopardizes invariant.

//// 6

class FinalFieldInClass {
    private final Map<Integer, Result> cache;
    // Thread safety argument:
    //   "cache" is immutable
    
    public FinalFieldInClass() {
        cache = new HashMap<>();
    }
    
    // ... use "cache" in methods ...
}

//// Problem: cache reference is immutable, but its value is not.

//// 7

// An immutable named thing.
class ImmutableNamedThing {
    private String name;
    private int kindOfThing;
    // Thread safety argument:
    //   all objects in rep are immutable,
    //   and this class is immutable
    
    // ... use name and kindOfThing in methods ...
}

//// Incomplete: would need to show variables are not reassigned,
////             best strategy is to make the compiler prove it by using final.

//// 8

// An immutable result.
class ImmutableResult {
    private final int data;
    private final Map<Integer, Result> cache;
    // Thread safety argument: this class is immutable
    
    public ImmutableResult(int data) {
        this.data = data;
        this.cache = new HashMap<>();
    }
    public Result getResult() {
        // do computations using "data"
        // look up intermediate results in "cache"
        // store stuff in "cache" so we won't have to compute it again
        return Result.ofComputingSomething;
    }
}

//// Problem: not immutable in strict sense required for thread safety.

//// 9

class ImmutableListField {
    private final List<Result> results;
    // Thread safety argument:
    //   "results" is immutable
    
    public ImmutableListField() {
        results = Collections.unmodifiableList(new ArrayList<>());
    }
    public boolean resultsConsistent() {
        for (Result r : results) {
            // check r for consistency
        }
        return true;
    }
}

//// Almost correct: relies on safety of concurrent calls to observers of ArrayList,
////                 which is true but should be stated.

//// 10

class ThreadsafeFieldInClass {
    private final Map<Integer, Result> cache;
    private final Helper helper;
    // Thread safety argument:
    //   "cache" is safe for concurrency
    
    public ThreadsafeFieldInClass() {
        Map<Integer, Result> cache = new HashMap<>();
        this.cache = Collections.synchronizedMap(cache);
        // share the cache with a helper object
        this.helper = new Helper(cache);
    }
    
    // ... use cache in methods ...
}

//// Problem: Helper has reference to inner HashMap, which is not synchronized.

//// 11

// An immutable result.
class ThreadsafeImmutableResult {
    private final int data;
    private final Map<Integer, Result> cache;
    // Thread safety argument:
    //   "data" is immutable, and "cache" is safe for concurrency
    
    public ThreadsafeImmutableResult(int data) {
        this.data = data;
        this.cache = Collections.synchronizedMap(new HashMap<>());
    }
    public Result getResult() {
        // do computations using "data"
        // look up intermediate results in "cache"
        // store stuff in "cache" so we won't have to compute it again
        return Result.ofComputingSomething;
    }
}

//// Almost correct: should state that references are immutable.

//// 12

class SynchronizedListField {
    private final List<Result> results;
    // Thread safety argument: "results" is safe for concurrency
    
    public SynchronizedListField() {
        results = Collections.synchronizedList(new ArrayList<>());
    }
    public void addResult(Result r) {
        results.add(r);
    }
    public boolean resultsConsistent() {
        for (Result r : results) {
            // check r for consistency
        }
        return true;
    }
}

//// Incomplete/problem: unsafe iteration.

////////////////////////////////////////

class Result {
    static Result ofComputingSomething;
    static int ofComputingSomeMoreThings;
}

class Helper {
    Helper(Map<Integer, Result> cache) { }
}
