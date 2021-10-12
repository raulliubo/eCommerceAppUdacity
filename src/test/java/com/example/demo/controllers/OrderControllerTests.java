package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTests {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);

        Item item = new Item();
        item.setId(1L);
        item.setName("TestItem");
        item.setPrice(new BigDecimal(9.99));
        item.setDescription("This is a Test Item");

        List<Item> itemList = new ArrayList();
        itemList.add(item);


        Cart cart = new Cart();
        cart.setId(1L);

        User user = new User();
        user.setUsername("TestUser");
        user.setCart(cart);
        cart.setUser(user);
        cart.setItems(itemList);
        cart.setTotal(new BigDecimal(9.99));
        when(userRepository.findByUsername("TestUser")).thenReturn(user);

    }


    @Test
    public void submit_HappyPath() {
        ResponseEntity<UserOrder> response = orderController.submit("TestUser");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(1, order.getItems().size());
        assertTrue(order.getTotal().compareTo(new BigDecimal(9.99)) == 0);
    }

    @Test
    public void submit_UserError() {
        ResponseEntity<UserOrder> response = orderController.submit("TestUser_Wrong");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getOrdersForUser_HappyPath() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("TestUser");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<UserOrder> orderList = response.getBody();
        assertNotNull(orderList);
    }

    @Test
    public void getOrdersForUser_UserError() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("TestUser_Wrong");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

}
