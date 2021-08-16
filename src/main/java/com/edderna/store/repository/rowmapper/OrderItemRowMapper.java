package com.edderna.store.repository.rowmapper;

import com.edderna.store.domain.OrderItem;
import com.edderna.store.domain.enumeration.OrderItemStatus;
import com.edderna.store.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link OrderItem}, with proper type conversions.
 */
@Service
public class OrderItemRowMapper implements BiFunction<Row, String, OrderItem> {

    private final ColumnConverter converter;

    public OrderItemRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link OrderItem} stored in the database.
     */
    @Override
    public OrderItem apply(Row row, String prefix) {
        OrderItem entity = new OrderItem();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setQuantity(converter.fromRow(row, prefix + "_quantity", Integer.class));
        entity.setTotalPrice(converter.fromRow(row, prefix + "_total_price", BigDecimal.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", OrderItemStatus.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        entity.setOrderId(converter.fromRow(row, prefix + "_order_id", Long.class));
        return entity;
    }
}
