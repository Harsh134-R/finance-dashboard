package com.zorvyn.financedashboard.controller;

import com.zorvyn.financedashboard.dto.response.ApiResponse;
import com.zorvyn.financedashboard.dto.response.CategorySummaryResponse;
import com.zorvyn.financedashboard.dto.response.DashboardOverviewResponse;
import com.zorvyn.financedashboard.dto.response.MonthlyTrendResponse;
import com.zorvyn.financedashboard.dto.response.TransactionResponse;
import com.zorvyn.financedashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard",
        description = "Analytics and summary data - Analyst and Admin only")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(
            summary = "Financial overview",
            description = "Returns total income, total expense, " +
                    "net balance, and transaction counts"
    )
    public ResponseEntity<ApiResponse<DashboardOverviewResponse>> getOverview() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Overview retrieved successfully",
                        dashboardService.getOverview()));
    }

    @GetMapping("/category-wise")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(
            summary = "Category wise breakdown",
            description = "Total amount and count grouped by category and type"
    )
    public ResponseEntity<ApiResponse<List<CategorySummaryResponse>>>
    getCategoryWise() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Category summary retrieved successfully",
                        dashboardService.getCategoryWise()));
    }

    @GetMapping("/recent-activity")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(
            summary = "Recent transactions",
            description = "Latest transactions — default 10, max 50 via ?limit=N"
    )
    public ResponseEntity<ApiResponse<List<TransactionResponse>>>
    getRecentActivity(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Recent activity retrieved successfully",
                        dashboardService.getRecentActivity(limit)));
    }

    @GetMapping("/monthly-trends")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(
            summary = "Monthly trends",
            description = "Income vs expense breakdown grouped by month and year"
    )
    public ResponseEntity<ApiResponse<List<MonthlyTrendResponse>>>
    getMonthlyTrends() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Monthly trends retrieved successfully",
                        dashboardService.getMonthlyTrends()));
    }
}