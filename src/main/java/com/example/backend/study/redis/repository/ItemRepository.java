package com.example.backend.study.redis.repository;

import com.example.backend.study.redis.dto.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 *  Data Source
 *  데이터베이스 or 외부 API or 연산  을 통해 원본 데이터 반환
 */
@Slf4j
@Repository
public class ItemRepository {

    // 키(Long)를 기준으로 내림차순으로 정렬
    private final ConcurrentSkipListMap<Long, Item> database = new ConcurrentSkipListMap<>(Comparator.reverseOrder());

    public Optional<Item> read(Long itemId) {
        log.info("[ItemRepository.read] itemId={}", itemId);
        return Optional.ofNullable(database.get(itemId));
    }

    // 페이지 조회
    public List<Item> readAll(Long page, Long pageSize) {
        log.info("[ItemRepository.readAll] page={} pageSize={}", page, pageSize);

        return database.values().stream()
            .skip((page - 1) * pageSize)
            .limit(pageSize)
            .toList();
    }

    // 무한 스크롤 방식, 데이터 커서
    public List<Item> readAllInfiniteScroll(Long lastItemId, Long pageSize) {
        log.info("[ItemRepository.readAllInfiniteScroll] lastItemId={}, pageSize={}", lastItemId, pageSize);

        // 첫 페이지 조회
        if(lastItemId == null) {
            return database.values().stream()
                .limit(pageSize)
                .toList();
        }

        // null 이 아니면 첫페이지가 아니기 때문에 기준점을 찾아야함.
        // 내림차순 정렬을 했기 때문에 tailMap 을 사용, lastItemId 다음에있는 데이터들부터 접근 가능.
        return database.tailMap(lastItemId, false).values().stream()
            .limit(pageSize)
            .toList();
    }

    public Item create(Item item) {
        log.info("[ItemRepository.create] item={}", item);
        database.put(item.getItemId(), item);
        return item;
    }

    public Item update(Item item) {
        log.info("[ItemRepository.update] item={}", item);
        database.put(item.getItemId(), item);
        return item;
    }

    public void delete(Item item) {
        log.info("ItemRepository.delete] item={}", item);
        database.remove(item.getItemId());
    }

    public long count() {
        log.info("ItemRepository.count]");
        return database.size();
    }

}
