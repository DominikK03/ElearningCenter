package pl.dominik.elearningcenter.interfaces.rest.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.dominik.elearningcenter.application.admin.dto.AdminStatsDTO;
import pl.dominik.elearningcenter.application.admin.query.GetAdminStatsQueryHandler;
import pl.dominik.elearningcenter.application.user.command.AdjustUserBalanceCommand;
import pl.dominik.elearningcenter.application.user.command.AdjustUserBalanceCommandHandler;
import pl.dominik.elearningcenter.application.user.command.BalanceAdjustmentType;
import pl.dominik.elearningcenter.interfaces.rest.common.AckResponse;
import pl.dominik.elearningcenter.interfaces.rest.user.request.AdjustBalanceRequest;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final GetAdminStatsQueryHandler getAdminStatsQueryHandler;
    private final AdjustUserBalanceCommandHandler adjustUserBalanceCommandHandler;

    public AdminController(
            GetAdminStatsQueryHandler getAdminStatsQueryHandler,
            AdjustUserBalanceCommandHandler adjustUserBalanceCommandHandler
    ) {
        this.getAdminStatsQueryHandler = getAdminStatsQueryHandler;
        this.adjustUserBalanceCommandHandler = adjustUserBalanceCommandHandler;
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDTO> getStats() {
        return ResponseEntity.ok(getAdminStatsQueryHandler.handle());
    }

    @PostMapping("/users/{userId}/balance-adjustments")
    public ResponseEntity<AckResponse> adjustUserBalance(
            @PathVariable Long userId,
            @RequestBody AdjustBalanceRequest request
    ) {
        BalanceAdjustmentType type = BalanceAdjustmentType.valueOf(request.type().toUpperCase());
        AdjustUserBalanceCommand command = new AdjustUserBalanceCommand(
                userId,
                request.amount(),
                type,
                request.reason()
        );
        adjustUserBalanceCommandHandler.handle(command);
        return ResponseEntity.ok(AckResponse.success("User balance adjusted successfully"));
    }
}
