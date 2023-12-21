package com.techelevator.dao;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransfersDao;
import com.techelevator.tenmo.model.Account;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
//@Transactional  // Rollbacks the transactions after each test
public class JdbcAccountDaoIntegrationTest extends BaseDaoTests {


    private AccountDao accountDao;
    private Account account1 = new Account(1, 1001);
    private Account account2 = new Account(2, 1002);

    @Before
    public void setup() {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            accountDao = new JdbcAccountDao(jdbcTemplate); // Initialize the class-level accountDao variable
        }


    @Test
    public void getBalanceByAccountId_should_return_balance_when_account_exists() {
        // Arrange: Assuming account_id 1 is created in your test data
        BigDecimal expectedBalance = BigDecimal.valueOf(1000);

        // Act: Call the method to get the balance
        BigDecimal actualBalance = accountDao.getBalanceByAccountId(1);

        // Assert: Verify the balance matches the expected value
        assertNotNull(actualBalance);
        assertEquals("expected balance of 1000", 0, expectedBalance.compareTo(actualBalance));
    }
}
