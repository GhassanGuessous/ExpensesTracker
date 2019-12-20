package com.guess.expensestracker.web.rest;

import com.guess.expensestracker.ExpensesTrackerApplication;
import com.guess.expensestracker.entity.Income;
import com.guess.expensestracker.repository.IncomeRepository;
import com.guess.expensestracker.service.IncomeService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExpensesTrackerApplication.class)
@AutoConfigureMockMvc()
public class IncomeResourceIntTest {

    private static final String DEFAULT_NOTE = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Autowired
    private EntityManager em;

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private Validator validator;

    private MockMvc restIncomeMockMvc;

    private Income income;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final IncomeResource activityResource = new IncomeResource(incomeService);
        this.restIncomeMockMvc = MockMvcBuilders.standaloneSetup(activityResource)
                .setConversionService(createFormattingConversionService())
                .setMessageConverters(jacksonMessageConverter)
                .setValidator(validator).build();
    }

    public static Income createEntity(EntityManager em) {
        Income income = new Income();
        income.setNote(DEFAULT_NOTE);
        return income;
    }

    @Before
    public void initTest() {
        income = createEntity(em);
    }

    @Test
    @Transactional
    public void createIncome() throws Exception {
        int databaseSizeBeforeCreate = incomeRepository.findAll().size();

        // Create the Income
        restIncomeMockMvc.perform(post("/api/incomes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(income)))
                .andExpect(status().isCreated());

        // Validate the Income in the database
        List<Income> incomes = incomeRepository.findAll();
        assertThat(incomes).hasSize(databaseSizeBeforeCreate + 1);
        Income testIncome = incomes.get(incomes.size() - 1);
        assertThat(testIncome.getNote()).isEqualTo(DEFAULT_NOTE);
    }

    @Test
    @Transactional
    public void createIncomeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = incomeRepository.findAll().size();

        income.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restIncomeMockMvc.perform(post("/api/incomes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(income)))
                .andExpect(status().isBadRequest());

        // Validate the Income in the database
        List<Income> incomes = incomeRepository.findAll();
        assertThat(incomes).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllCategories() throws Exception {
        // Initialize the database
        incomeRepository.saveAndFlush(income);

        // Get all the incomes
        restIncomeMockMvc.perform(get("/api/incomes?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(income.getId().intValue())))
                .andExpect(jsonPath("$.[*].note").value(hasItem(income.getNote().toString())));
    }

    @Test
    @Transactional
    public void getIncome() throws Exception {
        // Initialize the database
        incomeRepository.saveAndFlush(income);

        // Get the income
        restIncomeMockMvc.perform(get("/api/incomes/{id}", income.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(income.getId().intValue()))
                .andExpect(jsonPath("$.note").value(income.getNote().toString()));
    }

    @Test
    @Transactional
    public void getNonExistingIncome() throws Exception {
        // Get the income
        restIncomeMockMvc.perform(get("/api/incomes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateIncome() throws Exception {
        incomeService.save(income);

        int databaseSizeBeforeUpdate = incomeRepository.findAll().size();

        Income updatedIncome = incomeRepository.findById(income.getId()).get();
        em.detach(updatedIncome);
        updatedIncome.setNote(UPDATED_NAME);

        restIncomeMockMvc.perform(put("/api/incomes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedIncome)))
                .andExpect(status().isOk());

        List<Income> incomes = incomeRepository.findAll();
        assertThat(incomes).hasSize(databaseSizeBeforeUpdate);
        Income testIncome = incomes.get(incomes.size() - 1);
        assertThat(testIncome.getNote()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingIncome() throws Exception {
        int databaseSizeBeforeUpdate = incomeRepository.findAll().size();

        restIncomeMockMvc.perform(put("/api/incomes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(income)))
                .andExpect(status().isBadRequest());

        List<Income> incomes = incomeRepository.findAll();
        assertThat(incomes).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deteleIncome() throws Exception {
        incomeService.save(income);

        int databaseSizeBeforeDelete = incomeRepository.findAll().size();

        restIncomeMockMvc.perform(delete("/api/incomes/{id}", income.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        List<Income> incomes = incomeRepository.findAll();
        assertThat(incomes).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVErifier() throws Exception {
//        TestUtil.equalsVerifier(Income.class);
        Income category1 = new Income();
        category1.setId(1L);
        Income category2 = new Income();
        category2.setId(category1.getId());
        assertThat(category1).isEqualTo(category2);
        category2.setId(2L);
        assertThat(category1).isNotEqualTo(category2);
        category1.setId(null);
        assertThat(category1).isNotEqualTo(category2);
    }
}
