package org.javashop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javashop.interfaces.PaymentStrategy;
import org.javashop.interfaces.Validator;
import org.javashop.models.PaymentResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private final Map<String, PaymentStrategy> methods;
    private final List<Validator> validators;
    private final List<Consumer<PaymentResult>> listeners = new ArrayList<>();

    public void onPayment(Consumer<PaymentResult> listener) {
        listeners.add(listener);
    }

    private PaymentResult notifyListeners(PaymentResult result) {
        listeners.forEach(l -> l.accept(result));
        return result;
    }

    public PaymentResult pay(String method, BigDecimal amount, String customerId, String description) {
        String message = validators.stream().map(validator -> validator.validate(amount,customerId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElse("");
        if (!message.isEmpty()) {
            return notifyListeners(new PaymentResult(customerId, amount, LocalDateTime.now(), message, false, method));
        }
        PaymentStrategy strategy = methods.get(method);
        if (strategy == null)
            return notifyListeners(new PaymentResult(customerId, amount, LocalDateTime.now(), "Payment method not found: " + method, false, method));

        try {
            strategy.pay(amount, customerId, description);
            return notifyListeners(new PaymentResult(customerId, amount, LocalDateTime.now(), "Payment successful", true, method));
        } catch (Exception e) {
            return notifyListeners(new PaymentResult(customerId, amount, LocalDateTime.now(), e.getMessage(), false, method));

        }
    }
}
