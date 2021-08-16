package com.edderna.store.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.edderna.store.domain.Order;
import com.edderna.store.domain.enumeration.OrderStatus;
import com.edderna.store.repository.rowmapper.CustomerRowMapper;
import com.edderna.store.repository.rowmapper.InvoiceRowMapper;
import com.edderna.store.repository.rowmapper.OrderRowMapper;
import com.edderna.store.repository.rowmapper.UserRowMapper;
import com.edderna.store.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Order entity.
 */
@SuppressWarnings("unused")
class OrderRepositoryInternalImpl implements OrderRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final InvoiceRowMapper invoiceMapper;
    private final UserRowMapper userMapper;
    private final CustomerRowMapper customerMapper;
    private final OrderRowMapper orderMapper;

    private static final Table entityTable = Table.aliased("jhi_order", EntityManager.ENTITY_ALIAS);
    private static final Table invoiceIdTable = Table.aliased("invoice", "invoiceId");
    private static final Table userTable = Table.aliased("jhi_user", "e_user");
    private static final Table customerTable = Table.aliased("customer", "customer");

    public OrderRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        InvoiceRowMapper invoiceMapper,
        UserRowMapper userMapper,
        CustomerRowMapper customerMapper,
        OrderRowMapper orderMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.invoiceMapper = invoiceMapper;
        this.userMapper = userMapper;
        this.customerMapper = customerMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    public Flux<Order> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Order> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Order> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = OrderSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(InvoiceSqlHelper.getColumns(invoiceIdTable, "invoiceId"));
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(CustomerSqlHelper.getColumns(customerTable, "customer"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(invoiceIdTable)
            .on(Column.create("invoice_id_id", entityTable))
            .equals(Column.create("id", invoiceIdTable))
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable));

        String select = entityManager.createSelect(selectFrom, Order.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<Order> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Order> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Order process(Row row, RowMetadata metadata) {
        Order entity = orderMapper.apply(row, "e");
        entity.setInvoiceId(invoiceMapper.apply(row, "invoiceId"));
        entity.setUser(userMapper.apply(row, "user"));
        entity.setCustomer(customerMapper.apply(row, "customer"));
        return entity;
    }

    @Override
    public <S extends Order> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Order> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Order with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Order entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class OrderSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("placed_date", table, columnPrefix + "_placed_date"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("code", table, columnPrefix + "_code"));
        columns.add(Column.aliased("invoice_id", table, columnPrefix + "_invoice_id"));

        columns.add(Column.aliased("invoice_id_id", table, columnPrefix + "_invoice_id_id"));
        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        columns.add(Column.aliased("customer_id", table, columnPrefix + "_customer_id"));
        return columns;
    }
}
