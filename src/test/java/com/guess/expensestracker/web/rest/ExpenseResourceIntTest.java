package com.guess.expensestracker.web.rest;

import com.guess.expensestracker.ExpensesTrackerApplication;
import com.guess.expensestracker.entity.Expense;
import com.guess.expensestracker.repository.ExpenseRepository;
import com.guess.expensestracker.service.ExpenseService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.guess.expensestracker.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExpensesTrackerApplication.class)
@AutoConfigureMockMvc()
public class ExpenseResourceIntTest {

    private static final String DEFAULT_NOTE = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Autowired
    private EntityManager em;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private Validator validator;

    private MockMvc restExpenseMockMvc;

    private Expense expense;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ExpenseResource activityResource = new ExpenseResource(expenseService);
        this.restExpenseMockMvc = MockMvcBuilders.standaloneSetup(activityResource)
                .setConversionService(createFormattingConversionService())
                .setMessageConverters(jacksonMessageConverter)
                .setValidator(validator).build();
    }

    public static Expense createEntity(EntityManager em) {
        Expense expense = new Expense();
        expense.setNote(DEFAULT_NOTE);
        return expense;
    }

    @Before
    public void initTest() {
        expense = createEntity(em);
    }

    @Test
    @Transactional
    public void createExpense() throws Exception {
        int databaseSizeBeforeCreate = expenseRepository.findAll().size();

        // Create the Expense
        restExpenseMockMvc.perform(post("/api/expenses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(expense)))
                .andExpect(status().isCreated());

        // Validate the Expense in the database
        List<Expense> expenses = expenseRepository.findAll();
        assertThat(expenses).hasSize(databaseSizeBeforeCreate + 1);
        Expense testExpense = expenses.get(expenses.size() - 1);
        assertThat(testExpense.getNote()).isEqualTo(DEFAULT_NOTE);
    }

    @Test
    @Transactional
    public void createExpenseWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = expenseRepository.findAll().size();

        expense.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restExpenseMockMvc.perform(post("/api/expenses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(expense)))
                .andExpect(status().isBadRequest());

        // Validate the Expense in the database
        List<Expense> expenses = expenseRepository.findAll();
        assertThat(expenses).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllCategories() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get all the expenses
        restExpenseMockMvc.perform(get("/api/expenses?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(expense.getId().intValue())))
                .andExpect(jsonPath("$.[*].note").value(hasItem(expense.getNote().toString())));
    }

    @Test
    @Transactional
    public void getExpense() throws Exception {
        // Initialize the database
        expenseRepository.saveAndFlush(expense);

        // Get the expense
        restExpenseMockMvc.perform(get("/api/expenses/{id}", expense.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(expense.getId().intValue()))
                .andExpect(jsonPath("$.note").value(expense.getNote().toString()));
    }

    @Test
    @Transactional
    public void getNonExistingExpense() throws Exception {
        // Get the expense
        restExpenseMockMvc.perform(get("/api/expenses/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateExpense() throws Exception {
        expenseService.save(expense);

        int databaseSizeBeforeUpdate = expenseRepository.findAll().size();

        Expense updatedExpense = expenseRepository.findById(expense.getId()).get();
        em.detach(updatedExpense);
        updatedExpense.setNote(UPDATED_NAME);

        restExpenseMockMvc.perform(put("/api/expenses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedExpense)))
                .andExpect(status().isOk());

        List<Expense> expenses = expenseRepository.findAll();
        assertThat(expenses).hasSize(databaseSizeBeforeUpdate);
        Expense testExpense = expenses.get(expenses.size() - 1);
        assertThat(testExpense.getNote()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingExpense() throws Exception {
        int databaseSizeBeforeUpdate = expenseRepository.findAll().size();

        restExpenseMockMvc.perform(put("/api/expenses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(expense)))
                .andExpect(status().isBadRequest());

        List<Expense> expenses = expenseRepository.findAll();
        assertThat(expenses).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deteleExpense() throws Exception {
        expenseService.save(expense);

        int databaseSizeBeforeDelete = expenseRepository.findAll().size();

        restExpenseMockMvc.perform(delete("/api/expenses/{id}", expense.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        List<Expense> expenses = expenseRepository.findAll();
        assertThat(expenses).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVErifier() throws Exception {
//        TestUtil.equalsVerifier(Expense.class);
        Expense category1 = new Expense();
        category1.setId(1L);
        Expense category2 = new Expense();
        category2.setId(category1.getId());
        assertThat(category1).isEqualTo(category2);
        category2.setId(2L);
        assertThat(category1).isNotEqualTo(category2);
        category1.setId(null);
        assertThat(category1).isNotEqualTo(category2);
    }
}
