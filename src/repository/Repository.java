package repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A tiny generic, in-memory CRUD store keyed by an ID type.
 * Demonstrates GENERICS (type parameters T and ID) so the same class
 * can safely store Book-by-ISBN and Member-by-memberId without any
 * casting or code duplication.
 *
 * @param <T>  the entity type stored
 * @param <ID> the type of the entity's unique identifier
 */
public class Repository<T, ID> {

    private final Map<ID, T> store = new HashMap<>();

    public void save(ID id, T entity) {
        store.put(id, entity);
    }

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(store.get(id));
    }

    public boolean existsById(ID id) {
        return store.containsKey(id);
    }

    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    public void deleteById(ID id) {
        store.remove(id);
    }

    public int count() {
        return store.size();
    }
}
