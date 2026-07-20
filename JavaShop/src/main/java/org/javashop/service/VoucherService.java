package org.javashop.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.javashop.Exceptions.NotEnoughPointsException;
import org.javashop.Exceptions.VoucherAlreadyExistsException;
import org.javashop.Exceptions.VoucherNotFoundException;
import org.javashop.dto.voucherDTO.VoucherDto;
import org.javashop.mapper.VoucherMapper;
import org.javashop.models.Voucher;
import org.javashop.repo.VoucherRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.*;

@Service
@AllArgsConstructor
public class VoucherService {
    private static final Map<Integer, Integer> POINTS_TO_DISCOUNT = Map.of(
            100, 10,
            150, 15,
            200, 20,
            250, 25
    );
    private static final TemporalAmount VOUCHER_MAX_DAYS = Period.ofDays(7);

    private final VoucherRepository repository;
    private final VoucherMapper mapper;

    public VoucherDto addVoucher(Voucher voucher) {
        if (repository.findAll().contains(voucher)){
            throw new VoucherAlreadyExistsException();
        }
        repository.save(voucher);
        return  mapper.toRespone(voucher);
    }

    public VoucherDto update(Long id, Voucher voucher) {
        if (repository.findById(id).isEmpty()){
            throw new VoucherNotFoundException();
        }
        repository.save(voucher);
        return mapper.toRespone(voucher);
    }

    public void delete(Long id) {
        Voucher voucherToDelete = repository.findById(id).orElseThrow(VoucherNotFoundException::new);
        repository.delete(voucherToDelete);
    }

    public void delete(String voucherName) {
        Voucher voucherToDelete = repository.findByVoucherName(voucherName).orElseThrow(VoucherNotFoundException::new);
        repository.delete(voucherToDelete);
    }

    public List<VoucherDto> findAll() {
        return repository.findAll().stream().map(mapper::toRespone).toList();
    }

    public Optional<VoucherDto> findById(Long id) {
        return repository.findById(id).map(mapper::toRespone);
    }

    public Optional<VoucherDto> findByName(String voucherName) {
        return repository.findByVoucherName(voucherName).map(mapper::toRespone);
    }

    public boolean validateVoucher(@NonNull Voucher voucher) {
        if (!repository.findAll().contains(voucher)) throw new VoucherNotFoundException();
        return !voucher.getExpirationDate().isBefore(LocalDate.now());
    }

    public Voucher generateVoucher(int points) {
        String name = Long.toHexString(UUID.randomUUID().getMostSignificantBits()).substring(0, 8).toUpperCase();
        Integer discount = POINTS_TO_DISCOUNT.get(points);
        if (discount == null) throw new NotEnoughPointsException("Not enough points");
        return new Voucher(name, LocalDate.now().plus(VOUCHER_MAX_DAYS), discount);
    }

    public Map<Integer, Integer> getPointsToDiscount() {
        return Collections.unmodifiableMap(POINTS_TO_DISCOUNT);
    }

}
