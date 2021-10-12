package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTests {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();

        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);

        Item item = new Item();
        item.setId(1L);
        item.setName("TestItem");
        item.setPrice(new BigDecimal(9.99));
        item.setDescription("This is a Test Item");
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
        when(itemRepository.findByName("TestItem")).thenReturn(Collections.singletonList(item));

        Item item1 = new Item();
        item1.setId(2L);
        item1.setName("TestItem1");
        item1.setPrice(new BigDecimal(19.99));
        item1.setDescription("This is another Test Item");
        when(itemRepository.findById(2L)).thenReturn(java.util.Optional.of(item1));
        when(itemRepository.findByName("TestItem1")).thenReturn(Collections.singletonList(item1));

        List<Item> itemList = new ArrayList();
        itemList.add(item);
        itemList.add(item1);
        when(itemRepository.findAll()).thenReturn(itemList);
    }


    @Test
    public void getItems_HappyPath() {
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> itemList = response.getBody();
        assertNotNull(itemList);
        assertEquals(2, itemList.size());
    }

    @Test
    public void getItemById_HappyPath() {
        ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item item = response.getBody();
        assertNotNull(item);
    }

    @Test
    public void getItemById_IdError() {
        ResponseEntity<Item> response = itemController.getItemById(3L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getItemByName_HappyPath() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName("TestItem1");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> itemList = response.getBody();
        assertNotNull(itemList);
        assertEquals(1, itemList.size());
    }

    @Test
    public void getItemByName_NameError() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName("TestItem_Wrong");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

}
