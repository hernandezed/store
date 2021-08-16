package com.edderna.store.service;

import com.edderna.store.domain.Customer;
import com.edderna.store.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Customer}.
 */
@Service
@Transactional
public class CustomerService {

    private final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Save a customer.
     *
     * @param customer the entity to save.
     * @return the persisted entity.
     */
    public Mono<Customer> save(Customer customer) {
        log.debug("Request to save Customer : {}", customer);
        return customerRepository.save(customer);
    }

    /**
     * Partially update a customer.
     *
     * @param customer the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Customer> partialUpdate(Customer customer) {
        log.debug("Request to partially update Customer : {}", customer);

        return customerRepository
            .findById(customer.getId())
            .map(
                existingCustomer -> {
                    if (customer.getName() != null) {
                        existingCustomer.setName(customer.getName());
                    }
                    if (customer.getEmail() != null) {
                        existingCustomer.setEmail(customer.getEmail());
                    }
                    if (customer.getPhone() != null) {
                        existingCustomer.setPhone(customer.getPhone());
                    }
                    if (customer.getAddressLine1() != null) {
                        existingCustomer.setAddressLine1(customer.getAddressLine1());
                    }
                    if (customer.getAddressLine2() != null) {
                        existingCustomer.setAddressLine2(customer.getAddressLine2());
                    }
                    if (customer.getCity() != null) {
                        existingCustomer.setCity(customer.getCity());
                    }

                    return existingCustomer;
                }
            )
            .flatMap(customerRepository::save);
    }

    /**
     * Get all the customers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Customer> findAll(Pageable pageable) {
        log.debug("Request to get all Customers");
        return customerRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of customers available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return customerRepository.count();
    }

    /**
     * Get one customer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Customer> findOne(Long id) {
        log.debug("Request to get Customer : {}", id);
        return customerRepository.findById(id);
    }

    /**
     * Delete the customer by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Customer : {}", id);
        return customerRepository.deleteById(id);
    }
}
