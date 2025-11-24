package com.example.notiveserver.common.querydsl

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.PathBuilder
import org.springframework.data.domain.Pageable

fun Pageable.toOrderSpecifiers(root: EntityPathBase<*>): Array<OrderSpecifier<*>> {
    if (sort.isUnsorted) return emptyArray()

    val pathBuilder = PathBuilder(root.type, root.metadata)
    val specifiers = mutableListOf<OrderSpecifier<*>>()

    for (sortOrder in sort) {
        val direction = if (sortOrder.isAscending) Order.ASC else Order.DESC
        val path = pathBuilder.getComparable(sortOrder.property, Comparable::class.java)
        specifiers += OrderSpecifier(direction, path)
    }
    return specifiers.toTypedArray()
}