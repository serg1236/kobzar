/*
 * This file is generated by jOOQ.
 */
package com.sdakhniy.kobzar.model;


import org.jooq.Sequence;
import org.jooq.impl.Internal;
import org.jooq.impl.SQLDataType;


/**
 * Convenience access to all sequences in public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Sequences {

    /**
     * The sequence <code>public.id_sequence</code>
     */
    public static final Sequence<Long> ID_SEQUENCE = Internal.createSequence("id_sequence", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);
}
