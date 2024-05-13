package com.sismics.docs.core.dao.jpa;

import com.sismics.docs.BaseTransactionalTest;
import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.core.util.TransactionUtil;
import com.sismics.docs.core.util.authentication.InternalAuthenticationHandler;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the persistance layer.
 * 
 * @author jtremeaux
 */
public class TestJpa extends BaseTransactionalTest {
    @Test
    public void testJpa() throws Exception {
        // Create a user
        UserDao userDao = new UserDao();
        User user = new User();
        user.setUsername("username");
        user.setPassword("12345678");
        user.setEmail("toto@docs.com");
        user.setRoleId("admin");
        user.setStorageQuota(10L);
        String id = userDao.create(user, "me");
        
        TransactionUtil.commit();

        // Search a user by his ID
        user = userDao.getById(id);
        Assert.assertNotNull(user);
        Assert.assertEquals("toto@docs.com", user.getEmail());

        // Authenticate using the database
        Assert.assertNotNull(new InternalAuthenticationHandler().authenticate("username", "12345678"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        User user = new User();
        UserDao userDao = new UserDao();
        user.setUsername("deleteTestUser");
        user.setPassword("passwordToDelete");
        user.setEmail("deleteTest@docs.com");
        user.setRoleId("user");
        user.setStorageQuota(20L);
        String userId = userDao.create(user, "creatorId");

        TransactionUtil.commit();
        User createdUser = userDao.getById(userId);
        Assert.assertNotNull("The user should exist before deletion", createdUser);
        userDao.delete(createdUser.getUsername(), "deleterId");
        TransactionUtil.commit();
        User deletedUser = userDao.getById(userId);
        Assert.assertNotNull("The user should not be null even after deletion", deletedUser);
        Assert.assertNotNull("The deleteDate should be set after deletion", deletedUser.getDeleteDate());
    }
}
