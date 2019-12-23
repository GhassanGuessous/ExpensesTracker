package com.guess.expensestracker.web.rest;

import com.guess.expensestracker.entity.Expense;
import com.guess.expensestracker.service.ExpenseService;
import com.guess.expensestracker.web.rest.errors.BadRequestAlertException;
import com.guess.expensestracker.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ExpenseResource {

    private final Logger log = LoggerFactory.getLogger(ExpenseResource.class);

    private static final String ENTITY_NAME = "expense";

    private ExpenseService expenseService;

    public ExpenseResource(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/expenses")
    public ResponseEntity<Expense> createIncome(@RequestBody Expense expense) throws URISyntaxException {
        log.debug("REST request to save Expense : {}", expense);
        if(expense.getId() != null) {
            throw new BadRequestAlertException("A new expense cannot have already an Id!", ENTITY_NAME);
        }
        Expense result = expenseService.save(expense);
        return ResponseEntity.created(new URI("/api/expenses/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    @PutMapping("/expenses")
    public ResponseEntity<Expense> updateIncome(@RequestBody Expense expense) {
        log.debug("REST request to update Expense : {}", expense);
        if(expense.getId() == null) {
            throw new BadRequestAlertException("Invalid Id!", ENTITY_NAME);
        }
        Expense result = expenseService.save(expense);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/expenses")
    public ResponseEntity<List<Expense>> getAllIncomes() {
        log.debug("REST request to get all expenses");
        List<Expense> expenses = expenseService.findAll();
        return ResponseEntity.ok().body(expenses);
    }

    @GetMapping("/expenses/{id}")
    public ResponseEntity<Expense> getIncome(@PathVariable Long id) {
        log.debug("REST request to get Expense : {}", id);
        Optional<Expense> expense = expenseService.findOne(id);
        if(expense.isPresent()) {
            return ResponseEntity.ok().body(expense.get());
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
        log.debug("REST request to delete Expense : {}", id);
        expenseService.delete(id);
        return ResponseEntity.ok().build();
    }
}
