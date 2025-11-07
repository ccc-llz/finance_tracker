package com.elec5619p5g1.finance_tracker.Controllers;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elec5619p5g1.finance_tracker.DTO.DialogFormDataError;
import com.elec5619p5g1.finance_tracker.DTO.Transaction.CreateRecurrentTransactionDTO;
import com.elec5619p5g1.finance_tracker.DTO.Transaction.CreateTransactionDTO;
import com.elec5619p5g1.finance_tracker.DTO.Transaction.CreateTransactionOptionsDTO;
import com.elec5619p5g1.finance_tracker.DTO.Transaction.CreateTransactionRequest;
import com.elec5619p5g1.finance_tracker.DTO.Transaction.TransactionLogDTO;
import com.elec5619p5g1.finance_tracker.DTO.Transaction.TransactionLogRequest;
import com.elec5619p5g1.finance_tracker.DTO.Transaction.TransactionSummary;
import com.elec5619p5g1.finance_tracker.Entity.Account;
import com.elec5619p5g1.finance_tracker.Entity.Category;
import com.elec5619p5g1.finance_tracker.Entity.Ledger;
import com.elec5619p5g1.finance_tracker.Enum.CurrencyType;
import com.elec5619p5g1.finance_tracker.Enum.RecurrenceIntervalUnit;
import com.elec5619p5g1.finance_tracker.Enum.TransactionType;
import com.elec5619p5g1.finance_tracker.Security.SecurityUtils;
import com.elec5619p5g1.finance_tracker.Services.AccountService;
import com.elec5619p5g1.finance_tracker.Services.CategoryService;
import com.elec5619p5g1.finance_tracker.Services.LedgerService;
import com.elec5619p5g1.finance_tracker.Services.RecurringTransactionService;
import com.elec5619p5g1.finance_tracker.Services.TransactionService;
import com.elec5619p5g1.finance_tracker.Services.UserService;
import com.elec5619p5g1.finance_tracker.Utility.DataUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transaction")

public class TransactionController {
    private final TransactionService transactionService;
    private final RecurringTransactionService recurringTransactionService;
    private final UserService userService;
    private final DataUtils dataUtils;
    private final LedgerService ledgerService;
    private final AccountService accountService;
    private final CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<?> createTransaction(@RequestBody CreateTransactionRequest createTransactionRequest) {
        long userId = SecurityUtils.getCurrentUserId();
        log.info("Creating transaction, {}", createTransactionRequest);
        // Category Validate
        Category category;
        try {
            if (!userService.getUserAccessibleCategories(userId).stream().anyMatch(c -> c.getId() == dataUtils.parseId(createTransactionRequest.getCategory()))) {
                throw new IllegalArgumentException();
            } else {
                category = categoryService.findById(dataUtils.parseId(createTransactionRequest.getCategory()));
            }
        } catch (Exception e) {
            DialogFormDataError dialogFormDataError = new DialogFormDataError("category", "Invalid category selection for this transaction");
            log.error("Error creating transaction: category error, {}", e.getMessage());
            return ResponseEntity.badRequest().body(dialogFormDataError);
        }

        // Type Validate
        TransactionType type;
        try {
            type = dataUtils.getTransactionType(createTransactionRequest.getType());
        } catch (Exception e) {
            DialogFormDataError dialogFormDataError = new DialogFormDataError("type", "Invalid type selection for this transaction");
            log.error("Error creating transaction: type error, {}", e.getMessage());
            return ResponseEntity.badRequest().body(dialogFormDataError);
        }

        // Money Amount Validate
        BigDecimal amount;
        try {
            amount = dataUtils.parseAmount(createTransactionRequest.getAmount());
        } catch (Exception e) {
            DialogFormDataError dialogFormDataError = new DialogFormDataError("amount", "Invalid money amount for the new transaction. ");
            log.error("Error creating transaction: amount error, {}", e.getMessage());
            return ResponseEntity.badRequest().body(dialogFormDataError);
        }

        // Ledger Option Validate
        Ledger ledger;
        try {
            if (!userService.getUserEditableLedgers(userId).stream().anyMatch(l -> l.getId() == dataUtils.parseId(createTransactionRequest.getLedger()))){
                throw new IllegalArgumentException();
            } else {
                ledger = ledgerService.findById(dataUtils.parseId(createTransactionRequest.getLedger())).orElseThrow();
            }
        }  catch (Exception e) {
            DialogFormDataError dialogFormDataError = new DialogFormDataError("ledger", "Invalid ledger selection for the new transaction. ");
            log.error("Error creating transaction: ledger error, {}", e.getMessage());
            return ResponseEntity.badRequest().body(dialogFormDataError);
        }

        // Account Option Validate
        Account account;
        try {
            if (accountService.findByIdAndUserId(dataUtils.parseId(createTransactionRequest.getAccount()), userId).isEmpty()) {
                throw new IllegalArgumentException();
            } else {
                account = accountService.findById(dataUtils.parseId(createTransactionRequest.getAccount())).orElseThrow();
            }
        } catch (Exception e) {
            DialogFormDataError dialogFormDataError = new DialogFormDataError("account", "Invalid account selection for the new transaction. ");
            log.error("Error creating transaction: account error, {}", e.getMessage());
            return ResponseEntity.badRequest().body(dialogFormDataError);
        }

        // Currency Validate
        CurrencyType currency;
        try {
            currency = dataUtils.getCurrencyType(createTransactionRequest.getCurrency());
        }  catch (Exception e) {
            DialogFormDataError dialogFormDataError = new DialogFormDataError("currency", "Invalid currency selection for the new transaction. ");
            log.error("Error creating transaction: currency error, {}", e.getMessage());
            return ResponseEntity.badRequest().body(dialogFormDataError);
        }

        // Date Validate
        LocalDate transactionDate =  LocalDate.now();
        LocalDate recurStartDate = LocalDate.now();
        LocalDate recurEndDate = LocalDate.now().plusDays(1);
        int interval = 0;
        RecurrenceIntervalUnit intervalUnit = RecurrenceIntervalUnit.DAILY;
        if (!createTransactionRequest.isRecurrent()) {
            try {
                transactionDate = LocalDate.parse(createTransactionRequest.getDate());
            } catch (Exception e) {
                DialogFormDataError dialogFormDataError = new DialogFormDataError("date", "Invalid date for the new transaction. ");
                log.error("Error creating transaction: date error, {}", e.getMessage());
                return ResponseEntity.badRequest().body(dialogFormDataError);
            }
        } else {
            try {
                recurStartDate = LocalDate.parse(createTransactionRequest.getRecurStartDate());
            }  catch (Exception e) {
                DialogFormDataError dialogFormDataError = new DialogFormDataError("recurStartDate", "Invalid date for the new transaction. ");
                log.error("Error creating transaction: recurStartDate error, {}", e.getMessage());
                return ResponseEntity.badRequest().body(dialogFormDataError);
            }

            if (createTransactionRequest.isHasEndDate()) {
                try {
                    recurEndDate = LocalDate.parse(createTransactionRequest.getRecurEndDate());
                    if (!recurStartDate.isBefore(recurEndDate)) {
                        throw new IllegalArgumentException();
                    }
                } catch (Exception e) {
                    DialogFormDataError dialogFormDataError = new DialogFormDataError("recurEndDate", "Invalid date for the new transaction. ");
                    log.error("Error creating transaction: recurEndDate error, {}", e.getMessage());
                    return ResponseEntity.badRequest().body(dialogFormDataError);
                }
            }

            if (!createTransactionRequest.getInterval().matches("^[0-9]+$")) {
                DialogFormDataError dialogFormDataError = new DialogFormDataError("interval", "Invalid interval for the new transaction. ");
                log.error("Error creating transaction: interval error");
                return ResponseEntity.badRequest().body(dialogFormDataError);
            }
            interval = Integer.parseInt(createTransactionRequest.getInterval());
            if(interval <= 0) {
                DialogFormDataError dialogFormDataError = new DialogFormDataError("interval", "Invalid interval for the new transaction. ");
                log.error("Error creating transaction: Interval must be a positive integer");
                return ResponseEntity.badRequest().body(dialogFormDataError);
            }

            try {
                intervalUnit = dataUtils.getRecurrenceType(createTransactionRequest.getUnit());
            } catch (IllegalArgumentException e) {
                DialogFormDataError dialogFormDataError = new DialogFormDataError("unit", "Invalid unit selection for the new transaction. ");
                log.error("Error creating transaction: unit error, {}", e.getMessage());
                return ResponseEntity.badRequest().body(dialogFormDataError);
            }
        }

        log.info("Transaction is recurrent: {}", createTransactionRequest.isRecurrent());
        try {
            if(!createTransactionRequest.isRecurrent()) {
                CreateTransactionDTO dto = transactionService.createTransaction(userId, createTransactionRequest.getName(), category, account, type, currency, amount, transactionDate, ledger);
                log.info("Created transaction record for user {}, transaction id: {}", userId, dto.getTransactionId());
                return ResponseEntity.ok(dto);
            } else {
                CreateRecurrentTransactionDTO dto = recurringTransactionService.newRecurrentTransactionTemplate(userId, createTransactionRequest.getName(), ledger, category, account, type, currency, amount, intervalUnit, interval, recurStartDate, createTransactionRequest.isHasEndDate(), recurEndDate);
                log.info("Created recurrent  transaction record for user {}, template id: {}", userId, dto.getTemplateId());
                return ResponseEntity.ok(dto);
            }
        } catch (Exception e) {
            log.error("Error when creating transaction (isRecurrent:{}) for user {}: {}", createTransactionRequest.isRecurrent(), userId, e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/options")
    public ResponseEntity<CreateTransactionOptionsDTO> getTransactionOptions() {
        long userId = SecurityUtils.getCurrentUserId();

        try {
            CreateTransactionOptionsDTO dto = new CreateTransactionOptionsDTO();

            List<CurrencyType> userCurrencies = userService.getUserCurrencies(userId);
            for (CurrencyType c : userCurrencies) {
                dto.addOptions(CreateTransactionOptionsDTO.OptionsType.Currency, c.getShortCode(), c.name());
            }

            List<Category> userCategories = userService.getUserAccessibleCategories(userId);
            for (Category c : userCategories) {
                if (c.getTransactionType() == TransactionType.EXPENSE) {
                    dto.addOptions(CreateTransactionOptionsDTO.OptionsType.ExpenseCategory, String.valueOf(c.getId()), c.getName());
                } else {
                    dto.addOptions(CreateTransactionOptionsDTO.OptionsType.IncomeCategory, String.valueOf(c.getId()), c.getName());
                }
            }

            List<Account> userAccounts = userService.getUserAccounts(userId);
            for (Account a : userAccounts) {
                dto.addOptions(CreateTransactionOptionsDTO.OptionsType.Account, String.valueOf(a.getId()), a.getName());
            }

            List<Ledger> userLedgers = userService.getUserEditableLedgers(userId);
            for (Ledger l : userLedgers) {
                dto.addOptions(CreateTransactionOptionsDTO.OptionsType.Ledger, String.valueOf(l.getId()), l.getName(), l.getLedgerType().getCode());
            }

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("Error when getting transaction options for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<TransactionLogDTO> listTransactionLog(TransactionLogRequest transactionLogRequest) {
        long userId = SecurityUtils.getCurrentUserId();
        System.out.println(transactionLogRequest);
        try {
            TransactionLogDTO dto = transactionService.listTransactionLog(userId, transactionLogRequest);

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("Error when getting transaction logs for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<List<TransactionSummary>> getExpenseSummary(
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false) Long ledgerId
    ) {

        LocalDate start;
        LocalDate end;
       
        if (startDate == null || endDate == null) {
            // Default to current week: Monday → Sunday
            LocalDate now = LocalDate.now();
            start = now.with(DayOfWeek.MONDAY);
            end = now.with(DayOfWeek.SUNDAY);
        } else {
            start = LocalDate.parse(startDate);
            end = LocalDate.parse(endDate);
        }
       

        long userId = SecurityUtils.getCurrentUserId();

        long ledgerIdValue = ledgerId != null ? ledgerId : 0L;
        List<TransactionSummary> summary = transactionService.getExpenseSummaryByDateRange(userId, start, end, ledgerIdValue);

        return ResponseEntity.ok(summary);
    }

}
