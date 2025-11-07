package com.elec5619p5g1.finance_tracker.Controllers;

import com.elec5619p5g1.finance_tracker.DTO.Accounts.AccountsListDTO;
import com.elec5619p5g1.finance_tracker.DTO.Accounts.PreCreateAccountContextDTO;
import com.elec5619p5g1.finance_tracker.DTO.DialogFormDataError;
import com.elec5619p5g1.finance_tracker.DTO.Accounts.CreateAccountRequest;
import com.elec5619p5g1.finance_tracker.Entity.Account;
import com.elec5619p5g1.finance_tracker.Enum.CurrencyType;
import com.elec5619p5g1.finance_tracker.Security.SecurityUtils;
import com.elec5619p5g1.finance_tracker.Services.AccountService;
import com.elec5619p5g1.finance_tracker.Services.UserService;
import com.elec5619p5g1.finance_tracker.Utility.DataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService accountService;
    private final UserService userService;
    private final DataUtils dataUtils;

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest createAccountRequest) {
        long userId = SecurityUtils.getCurrentUserId();
        try {
            if(accountService.ifUserOwnedAccountWithNameExists(userId, createAccountRequest.getName())) {
                DialogFormDataError error = new DialogFormDataError("name", "Duplicate names of accounts owned by yourself. ");
                return ResponseEntity.badRequest().body(error);
            }

            CurrencyType currency;
            try {
                currency = dataUtils.getCurrencyType(createAccountRequest.getCurrency());
            } catch (IllegalArgumentException e) {
                DialogFormDataError error = new DialogFormDataError("currency", "Invalid currency unit. ");
                return ResponseEntity.badRequest().body(error);
            }

            BigDecimal balance;
            try {
                if(createAccountRequest.getBalance() == null) {
                    balance = BigDecimal.ZERO;
                } else if (!dataUtils.isValidNumber(createAccountRequest.getBalance())) {
                    throw new IllegalArgumentException();
                } else {
                    balance = dataUtils.parseAmount(createAccountRequest.getBalance());
                }
            } catch (Exception e) {
                DialogFormDataError error = new DialogFormDataError("balance", "Invalid balance value. ");
                return ResponseEntity.badRequest().body(error);
            }

            boolean isCard = false;
            String cardNumber = null;
            if (createAccountRequest.isCard()) {
                try {
                    if (!dataUtils.isValidCardNumber(createAccountRequest.getCardNumber())) {
                        throw new IllegalArgumentException();
                    } else {
                        isCard = true;
                        cardNumber = createAccountRequest.getCardNumber();
                    }
                } catch (Exception e) {
                    System.out.println("hahahaha");
                    DialogFormDataError error = new DialogFormDataError("cardNumber", "Invalid card number. ");
                    return ResponseEntity.badRequest().body(error);
                }
            }
            Account newAccount = accountService.create(userId, createAccountRequest.getName(), BigDecimal.ZERO, CurrencyType.AUD, false, null);
            newAccount.setBalance(balance);
            newAccount.setCurrency(currency);
            newAccount.setCard(isCard);
            newAccount.setCardNumber(cardNumber);
            newAccount = accountService.save(newAccount);

            return ResponseEntity.ok(newAccount);
        } catch (Exception e) {
            log.error("Unexpected error when creating account for {} with request: {}", userId, createAccountRequest);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/create-context")
    public ResponseEntity<?> precreationContext() {
        long userId = SecurityUtils.getCurrentUserId();
        try {
            List<CurrencyType> userCurrencyList = userService.getUserCurrencies(userId);
            PreCreateAccountContextDTO dto = new PreCreateAccountContextDTO();
            for(CurrencyType currency : userCurrencyList) {
                dto.addInfo(currency.getShortCode(), currency.name());
            }
            return ResponseEntity.ok().body(dto);
        } catch (Exception e) {
            log.error("Unexpected error when creating account context");
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<AccountsListDTO> getAccountsList() {
        long userId = SecurityUtils.getCurrentUserId();
        try {
            AccountsListDTO accountsListDTO = new AccountsListDTO();

            List<Account> userAccounts = userService.getUserAccounts(userId);

            for(Account account : userAccounts) {
                accountsListDTO.addItem(
                        account.getId(),
                        account.getName(),
                        account.getBalance(),
                        dataUtils.getCurrencySymbol(account.getCurrency()),
                        account.getExpense(),
                        account.getIncome(),
                        account.isCard(),
                        account.getCardNumber(),
                        account.isActive()
                );
            }

            return ResponseEntity.ok(accountsListDTO);
        }  catch (Exception e) {
            log.error("Unexpected error when getting account list for user {}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/toggleActivation")
    public ResponseEntity<Boolean> toggleActivation(@RequestParam long accountId) {
        long userId = SecurityUtils.getCurrentUserId();
        try {
            Account account = accountService.findById(accountId).orElseThrow();
            if(account.getOwner().getId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            boolean result = accountService.setActive(accountId, !account.isActive());
            return ResponseEntity.ok(result);
        }  catch (Exception e) {
            log.error("Unexpected error when toggle activation of account {}", accountId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
