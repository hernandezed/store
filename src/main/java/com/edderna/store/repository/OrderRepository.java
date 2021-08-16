package com.edderna.store.repository;

import com.edderna.store.domain.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderRepository extends R2dbcRepository<Order, Long>, OrderRepositoryInternal {
    Flux<Order> findAllBy(Pageable pageable);

    @Query("SELECT * FROM jhi_order entity WHERE entity.invoice_id_id = :id")
    Flux<Order> findByInvoiceId(Long id);

    @Query("SELECT * FROM jhi_order entity WHERE entity.invoice_id_id IS NULL")
    Flux<Order> findAllWhereInvoiceIdIsNull();

    @Query("SELECT * FROM jhi_order entity WHERE entity.user_id = :id")
    Flux<Order> findByUser(Long id);

    @Query("SELECT * FROM jhi_order entity WHERE entity.user_id IS NULL")
    Flux<Order> findAllWhereUserIsNull();

    @Query("SELECT * FROM jhi_order entity WHERE entity.customer_id = :id")
    Flux<Order> findByCustomer(Long id);

    @Query("SELECT * FROM jhi_order entity WHERE entity.customer_id IS NULL")
    Flux<Order> findAllWhereCustomerIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Order> findAll();

    @Override
    Mono<Order> findById(Long id);

    @Override
    <S extends Order> Mono<S> save(S entity);
}

interface OrderRepositoryInternal {
    <S extends Order> Mono<S> insert(S entity);
    <S extends Order> Mono<S> save(S entity);
    Mono<Integer> update(Order entity);

    Flux<Order> findAll();
    Mono<Order> findById(Long id);
    Flux<Order> findAllBy(Pageable pageable);
    Flux<Order> findAllBy(Pageable pageable, Criteria criteria);
}
