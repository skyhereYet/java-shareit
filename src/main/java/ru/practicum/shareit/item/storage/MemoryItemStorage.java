package ru.practicum.shareit.item.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class MemoryItemStorage implements ItemStorage {

    private static final Logger log = LoggerFactory.getLogger(MemoryItemStorage.class);
    private final HashMap<Integer, Item> itemStorage = new HashMap<>();
    private final HashMap<Integer, List<Integer>> userItemsStorage = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        itemStorage.put(item.getId(), item);
        if (userItemsStorage.containsKey(item.getOwner().getId())) {
            List<Integer> listItems = userItemsStorage.get(item.getId());
            userItemsStorage.put(item.getOwner().getId(), listItems);
        } else {
            userItemsStorage.put(item.getOwner().getId(), Arrays.asList(item.getId()));
        }
        log.info("Successfully add item - " + item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        itemStorage.put(item.getId(), item);
        log.info("Successfully update item - " + item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(int id) {
        log.info("Get item by id - " + id);
        if (itemStorage.containsKey(id)) {
            return Optional.of(itemStorage.get(id));
        }
        return Optional.empty();
    }

    @Override
    public List<Item> getItemsByUserId(int userId) {
        List<Integer> itemIds = userItemsStorage.get(userId);
        List<Item> itemsToReturn = new ArrayList<>();
        for (int itemId : itemIds) {
            itemsToReturn.add(itemStorage.get(itemId));
        }
        log.info("Get items list by user ID - " + userId + ", list - " + itemsToReturn);
        return itemsToReturn;
    }

    @Override
    public List<Item> getItemsBySubstring(String text) {
        List<Item> items = new ArrayList<>();
        for (Item item : itemStorage.values()) {
            if ((item.getName().toLowerCase().contains(text) || item.getDescription().toLowerCase().contains(text))
                    && item.getAvailable()) {
                items.add(item);
            }
        }
        return items;
    }
}
