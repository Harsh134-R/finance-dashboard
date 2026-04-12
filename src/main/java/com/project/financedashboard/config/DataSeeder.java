package com.project.financedashboard.config;

import com.project.financedashboard.entity.User;
import com.project.financedashboard.enums.Role;
import com.project.financedashboard.enums.UserStatus;
import com.project.financedashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.financedashboard.entity.Transaction;
import com.project.financedashboard.enums.Category;
import com.project.financedashboard.enums.TransactionType;
import com.project.financedashboard.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final TransactionRepository transactionRepository;


    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            seedUser("Admin User",    "admin@project.com",
                    "admin123",    Role.ADMIN);
            seedUser("Analyst User",  "analyst@project.com",
                    "analyst123",  Role.ANALYST);
            seedUser("Viewer User",   "viewer@project.com",
                    "viewer123",   Role.VIEWER);
            User admin = userRepository.findByEmail("admin@project.com").orElseThrow();
            seedTransactions(admin);
            log.info("===Seeding complete. Use below credentials to test ===");
            log.info("ADMIN =  admin@project.com    / admin123");
            log.info("ANALYST = analyst@project.com  / analyst123");
            log.info("VIEWER  =  viewer@project.com   / viewer123");
        };
    }

    private void seedUser(String fullName, String email,
                          String password, Role role) {
        if (!userRepository.existsByEmail(email)) {
            User user = User.builder()
                    .fullName(fullName)
                    .email(email)
                    .passwordHash(passwordEncoder.encode(password))
                    .role(role)
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(user);
            log.info("Seeded user: {} ({})", email, role);
        }
    }

    private void seedTransactions(User admin) {
        if (transactionRepository.count() == 0) {
            transactionRepository.saveAll(List.of(
                    // NORMAL DATA
                    buildTx(admin, "85000.00", TransactionType.INCOME,
                            Category.SALARY, LocalDate.now().minusDays(5),
                            "Monthly salary"),

                    buildTx(admin, "12000.00", TransactionType.INCOME,
                            Category.INVESTMENT, LocalDate.now().minusDays(10),
                            "Stock dividends"),

                    buildTx(admin, "15000.00", TransactionType.EXPENSE,
                            Category.RENT, LocalDate.now().minusDays(3),
                            "Office rent"),

                    //  UTILITIES BASE
                    buildTx(admin, "4000.00", TransactionType.EXPENSE,
                            Category.UTILITIES, LocalDate.now().minusDays(10),
                            "Electricity bill"),

                    buildTx(admin, "4200.00", TransactionType.EXPENSE,
                            Category.UTILITIES, LocalDate.now().minusDays(8),
                            "Water bill"),

                    // FOOD BASE
                    buildTx(admin, "3000.00", TransactionType.EXPENSE,
                            Category.FOOD, LocalDate.now().minusDays(6),
                            "Lunch"),

                    buildTx(admin, "3200.00", TransactionType.EXPENSE,
                            Category.FOOD, LocalDate.now().minusDays(5),
                            "Snacks"),

                    buildTx(admin, "3500.00", TransactionType.EXPENSE,
                            Category.FOOD, LocalDate.now().minusDays(4),
                            "Dinner"),

                    //  ENTERTAINMENT BASE
                    buildTx(admin, "4000.00", TransactionType.EXPENSE,
                            Category.ENTERTAINMENT, LocalDate.now().minusDays(15),
                            "Team outing"),

                    buildTx(admin, "4500.00", TransactionType.EXPENSE,
                            Category.ENTERTAINMENT, LocalDate.now().minusDays(12),
                            "Movies"),


                    //  WARNING ANOMALY
                    buildTx(admin, "12000.00", TransactionType.EXPENSE,
                            Category.FOOD, LocalDate.now().minusDays(2),
                            "Luxury dinner (high expense)"),


                    // CRITICAL ANOMALY
                    buildTx(admin, "60000.00", TransactionType.EXPENSE,
                            Category.ENTERTAINMENT, LocalDate.now(),
                            "Corporate party (extreme expense)"),


                    // ANOTHER CRITICAL
                    buildTx(admin, "100000.00", TransactionType.EXPENSE,
                            Category.UTILITIES, LocalDate.now(),
                            "Unexpected server bill spike")
            ));
            log.info("Seeded sample transactions");
        }
    }

    private Transaction buildTx(User user, String amount,
                                TransactionType type, Category category,
                                LocalDate date, String notes) {
        return Transaction.builder()
                .createdBy(user)
                .amount(new BigDecimal(amount))
                .type(type)
                .category(category)
                .transactionDate(date)
                .notes(notes)
                .isDeleted(false)
                .build();
    }
}