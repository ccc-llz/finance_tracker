package com.elec5619p5g1.finance_tracker.Controllers;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elec5619p5g1.finance_tracker.DTO.DialogFormDataError;
import com.elec5619p5g1.finance_tracker.DTO.Ledgers.CheckInvitingUserAvailabilityResponse;
import com.elec5619p5g1.finance_tracker.DTO.Ledgers.CreateLedgerRequest;
import com.elec5619p5g1.finance_tracker.DTO.Ledgers.CreateLedgerResponse;
import com.elec5619p5g1.finance_tracker.DTO.Ledgers.LedgerCollaboratorInvitationUpdateRequest;
import com.elec5619p5g1.finance_tracker.DTO.Ledgers.LedgersCollaboratorsItem;
import com.elec5619p5g1.finance_tracker.DTO.Ledgers.LedgersInvitationListDTO;
import com.elec5619p5g1.finance_tracker.DTO.Ledgers.LedgersListDTO;
import com.elec5619p5g1.finance_tracker.DTO.Ledgers.PreCreateLedgerContextDTO;
import com.elec5619p5g1.finance_tracker.Entity.Ledger;
import com.elec5619p5g1.finance_tracker.Entity.User;
import com.elec5619p5g1.finance_tracker.Entity.UserLedgerPermission;
import com.elec5619p5g1.finance_tracker.Enum.CurrencyType;
import com.elec5619p5g1.finance_tracker.Enum.LedgerInvitationStatus;
import com.elec5619p5g1.finance_tracker.Enum.LedgerType;
import com.elec5619p5g1.finance_tracker.Exceptions.InvalidInvitedUserException;
import com.elec5619p5g1.finance_tracker.Exceptions.UserPermissionDenied;
import com.elec5619p5g1.finance_tracker.Security.SecurityUtils;
import com.elec5619p5g1.finance_tracker.Services.LedgerService;
import com.elec5619p5g1.finance_tracker.Services.UserService;
import com.elec5619p5g1.finance_tracker.Utility.DataUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/ledger")
public class LedgerController {
    private final LedgerService ledgerService;
    private final UserService userService;
    private final DataUtils dataUtils;

    @GetMapping("/check-user-available")
    public ResponseEntity<?> checkUserAvailable(@RequestParam String email) {
        long userId = SecurityUtils.getCurrentUserId();
        Optional<User> requestedUser = userService.findByEmail(email);
        if (requestedUser.isPresent() && requestedUser.get().getId() != userId && !email.equals("admin@admin.com")) {
            CheckInvitingUserAvailabilityResponse response = new CheckInvitingUserAvailabilityResponse();
            response.setEmail(email);

            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createLedger(@RequestBody CreateLedgerRequest createLedgerRequest) {
        long userId = SecurityUtils.getCurrentUserId();
        try {
            if(ledgerService.ifUserOwnedLedgerWithNameExists(userId, createLedgerRequest.getName())) {
                DialogFormDataError error = new DialogFormDataError("name", "Duplicate names of ledgers owned by yourself. ");
                return ResponseEntity.badRequest().body(error);
            }

            CurrencyType currency;
            try {
                currency = dataUtils.getCurrencyType(createLedgerRequest.getCurrency());
            } catch (IllegalArgumentException e) {
                DialogFormDataError error = new DialogFormDataError("currency", "Invalid currency unit. ");
                return ResponseEntity.badRequest().body(error);
            }

            BigDecimal budget;
            try {
                if(createLedgerRequest.getBudget() == null) {
                    budget = BigDecimal.ZERO;
                } else if (!dataUtils.isValidNumber(createLedgerRequest.getBudget())) {
                    throw new IllegalArgumentException();
                } else {
                    budget = dataUtils.parseAmount(createLedgerRequest.getBudget());
                }
            } catch (Exception e) {
                DialogFormDataError error = new DialogFormDataError("budget", "Invalid budget value. ");
                return ResponseEntity.badRequest().body(error);
            }

            Ledger newLedger = ledgerService.createNewLedger(userId, createLedgerRequest.getName(), createLedgerRequest.getType());
            newLedger.setBudget(budget);
            newLedger.setDefaultCurrency(currency);
            newLedger = ledgerService.save(newLedger);

            if (newLedger.getLedgerType() == LedgerType.COLLAB) {
                try {
                    for (String email : createLedgerRequest.getInvitedUsers()) {
                        User invitedUser = userService.findByEmail(email).orElseThrow();
                        if (invitedUser.getId() == userId || email.equals("admin@admin.com")) {
                            throw new InvalidInvitedUserException();
                        }
                        ledgerService.createUserLedgerPermission(userId, invitedUser.getId(), newLedger.getId());
                    }
                } catch (Exception e) {
                    ledgerService.deleteLedger(newLedger.getId());
                    DialogFormDataError error = new DialogFormDataError("invitedUser", "Invalid users to be invited. ");
                    return ResponseEntity.badRequest().body(error);
                }
            }

            CreateLedgerResponse response = new CreateLedgerResponse(newLedger.getId(), newLedger.getName(), newLedger.getLedgerType().name(), newLedger.getOwner().getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Unexpected error when creating ledger for {} with request: {}", userId, createLedgerRequest);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/create-context")
    public ResponseEntity<?> precreationContext() {
        long userId = SecurityUtils.getCurrentUserId();
        try {
            List<CurrencyType> userCurrencyList = userService.getUserCurrencies(userId);
            PreCreateLedgerContextDTO dto = new PreCreateLedgerContextDTO();
            for(CurrencyType currency : userCurrencyList) {
                dto.addInfo(currency.getShortCode(), currency.name());
            }

            return ResponseEntity.ok().body(dto);
        } catch (Exception e) {
            log.error("Unexpected error when creating ledger context");
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<LedgersListDTO> getLedgersList() {
        long userId = SecurityUtils.getCurrentUserId();
        try {
            LedgersListDTO ledgersListDTO = new LedgersListDTO();

            List<Ledger> userOwnedLedgers = userService.getUserOwnedLedgers(userId);
            List<Ledger> collaboratedEditableLedgers = userService.getUserCollaboratedEditableLedgers(userId);
            List<Ledger> collaboratedReadableLedgers = userService.getUserCollaboratedReadableLedgers(userId);

            for(Ledger ledger : userOwnedLedgers) {
                ledgersListDTO.addItem(
                        ledger.getId(),
                        ledger.getName(),
                        ledger.getBalance(),
                        dataUtils.getCurrencySymbol(ledger.getDefaultCurrency()),
                        ledger.getExpense(),
                        ledger.getIncome(),
                        false,
                        userService.getUserName(userId),
                        ledger.getLedgerType() == LedgerType.COLLAB,
                        userService.getUsernames(ledgerService.getActiveCollaboratedUsers(ledger.getId())),
                        ledger.isActive()
                );
            }

            for(Ledger ledger : collaboratedEditableLedgers) {
                ledgersListDTO.addItem(
                        ledger.getId(),
                        ledger.getName(),
                        ledger.getBalance(),
                        dataUtils.getCurrencySymbol(ledger.getDefaultCurrency()),
                        ledger.getExpense(),
                        ledger.getIncome(),
                        true,
                        userService.getUserName(ledger.getOwner().getId()),
                        true,
                        List.of(userService.getUserName(ledger.getOwner().getId())),
                        ledger.isActive()
                );
            }

            for(Ledger ledger : collaboratedReadableLedgers) {
                ledgersListDTO.addItem(
                        ledger.getId(),
                        ledger.getName(),
                        ledger.getBalance(),
                        dataUtils.getCurrencySymbol(ledger.getDefaultCurrency()),
                        ledger.getExpense(),
                        ledger.getIncome(),
                        true,
                        userService.getUserName(ledger.getOwner().getId()),
                        true,
                        List.of(userService.getUserName(ledger.getOwner().getId())),
                        ledger.isActive()
                );
            }

            return ResponseEntity.ok(ledgersListDTO);
        }  catch (Exception e) {
            log.error("Unexpected error when getting ledger list for user {}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/toggleActivation")
    public ResponseEntity<Boolean> toggleActivation(@RequestParam long ledgerId) {
        long userId = SecurityUtils.getCurrentUserId();
        try {
            Ledger ledger = ledgerService.findById(ledgerId).orElseThrow();
            if(ledger.getOwner().getId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            boolean result = ledgerService.setActive(ledgerId, !ledger.isActive());
            return ResponseEntity.ok(result);
        }  catch (Exception e) {
            log.error("Unexpected error when toggle activation of ledger {}", ledgerId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/invitations")
    public ResponseEntity<LedgersInvitationListDTO> getInvitations() {
        long userId = SecurityUtils.getCurrentUserId();

        try {
            LedgersInvitationListDTO invitationListDTO = new LedgersInvitationListDTO();
            List<UserLedgerPermission> invitations = ledgerService.getUserReceivedLedgerPermissions(userId);
            for(UserLedgerPermission invitation : invitations) {
                invitationListDTO.addInvitation(
                        invitation.getId(),
                        invitation.getInvitedBy().getUsername(),
                        invitation.getLedger().getName(),
                        invitation.getInvitedAt().format(DateTimeFormatter.ISO_LOCAL_DATE),
                        invitation.getPermissionType().name(),
                        invitation.getInvitationStatus().name());
            }

            return ResponseEntity.ok(invitationListDTO);
        } catch (Exception e) {
            log.error("Unexpected error when getting ledgers invitations for user {}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/invitation-badge")
    public ResponseEntity<Integer> getUnrespondedInvitationBadge() {
        long userId = SecurityUtils.getCurrentUserId();

        try {
            List<UserLedgerPermission> invitations = ledgerService.getUserReceivedLedgerPermissionsWithStatus(userId, LedgerInvitationStatus.PENDING);

            return ResponseEntity.ok(invitations.size());
        } catch (Exception e) {
            log.error("Unexpected error when getting ledgers invitations for user {}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/respond-to-invitations")
    public ResponseEntity<String> respondToInvitation(@RequestParam long invitationId, @RequestParam boolean isAccept) {
        long userId = SecurityUtils.getCurrentUserId();
        try {
            LedgerInvitationStatus status = ledgerService.setInvitationStatus(invitationId, userId, isAccept);
            String result = status.name();
            if(status == LedgerInvitationStatus.ACTIVE) result = "ACCEPTED";
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error when responding to invitation {}", invitationId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{ledgerId}/collaborators")
    public ResponseEntity<List<LedgersCollaboratorsItem>> getLedgerCollaborators(@PathVariable("ledgerId") long ledgerId) {
        long userId = SecurityUtils.getCurrentUserId();
        try {
            List<LedgersCollaboratorsItem> collaborators = ledgerService.getAllCollaborators(userId, ledgerId);
            return ResponseEntity.ok(collaborators);
        } catch (NoSuchElementException e) {
            log.error("Unable to get collaborators list for ledgerId {}, there is no such ledger - ", ledgerId, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error when getting collaborators for ledger {}, {}: {}", ledgerId, e, e.getStackTrace());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{ledgerId}/collaborators/{email}")
    public ResponseEntity<String> getLedgerCollaborators(@PathVariable("ledgerId") long ledgerId, @PathVariable("email") String email) {
        long userId = SecurityUtils.getCurrentUserId();
        try {
            String confirmedEmail = ledgerService.checkEmailToInviteForLedgerAvailable(email, ledgerId, userId);
            return ResponseEntity.ok(confirmedEmail);
        } catch (InvalidInvitedUserException e) {
            return ResponseEntity.badRequest().body("Invalid Invitation Email");
        } catch (NoSuchElementException e) {
            log.error("Unable to invite collaborators list for ledgerId {}, there is no such ledger - ", ledgerId, e);
            return ResponseEntity.badRequest().body("Ledger not found");
        } catch (UserPermissionDenied e) {
            log.debug("UserPermissionDenied for ledgerId {} - email {} - operator {}", ledgerId, email, userId, e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("Unexpected error when checking collaborator {} availability for ledger {}, {}: {}", email, ledgerId, e, e.getStackTrace());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{ledgerId}/collaborators")
    public ResponseEntity<Boolean> inviteCollaborators(@PathVariable("ledgerId") long ledgerId, @RequestBody List<String> inviteUsers) {
        long userId = SecurityUtils.getCurrentUserId();
        try {
            boolean result = ledgerService.inviteUsersForExistingLedger(userId, ledgerId, inviteUsers);
            return ResponseEntity.ok(result);
        } catch (UserPermissionDenied e) {
            log.error("User {} invite collaborators for ledger {} failed, permission denied", userId, ledgerId, e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (InvalidInvitedUserException|NoSuchElementException e) {
            log.error("User {} invite collaborators for ledger {} failed", userId, ledgerId, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error when inviting collaborator {} for ledger {}, {}: {}", inviteUsers, ledgerId, e, e.getStackTrace());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{ledgerId}/collaborators/{username}")
    public ResponseEntity<?> updateInvitation(@PathVariable("ledgerId") long ledgerId, @PathVariable("username") String username, @RequestBody LedgerCollaboratorInvitationUpdateRequest request) {
        long userId = SecurityUtils.getCurrentUserId();
        try {
            if (request.getOperation().equals("REVOKE") || request.getOperation().equals("RETRY")) {
                LedgersCollaboratorsItem item = ledgerService.updateLedgerCollaboratorInvitation(ledgerId, userId, username, request.getOperation());
                return ResponseEntity.ok(item);
            } else if (request.getOperation().equals("SET")) {
                LedgersCollaboratorsItem item = ledgerService.alterLedgerCollaborationPermission(ledgerId, userId, username, request.getPermission());
                return ResponseEntity.ok(item);
            }
            return ResponseEntity.badRequest().build();
        } catch (InvalidInvitedUserException|NoSuchElementException e) {
            return ResponseEntity.badRequest().body("Invalid Collaboration Entry");
        } catch (UserPermissionDenied e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("Unexpected error when modifying collaboration entry for username {} for ledger {}, {}: {}", username, ledgerId, e, e.getStackTrace());
            return ResponseEntity.internalServerError().build();
        }
    }
}
