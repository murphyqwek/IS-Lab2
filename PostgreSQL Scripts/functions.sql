CREATE OR REPLACE FUNCTION get_ticket_with_max_type()
RETURNS SETOF ticket
LANGUAGE sql
STABLE
AS $$
    SELECT *
    FROM ticket
    WHERE type IS NOT NULL
    ORDER BY CASE type
        WHEN 'CHEAP' THEN 1
        WHEN 'BUDGETARY' THEN 2
        WHEN 'USUAL' THEN 3
        WHEN 'VIP' THEN 4
        ELSE 0
    END DESC
    LIMIT 1;
$$;


CREATE OR REPLACE FUNCTION count_tickets_with_venue_less_than(
    p_venue_id integer
)
RETURNS bigint
LANGUAGE sql
STABLE
AS $$
    SELECT COUNT(*)
    FROM ticket
    WHERE venue_id IS NOT NULL
      AND venue_id < p_venue_id;
$$;

CREATE OR REPLACE FUNCTION get_unique_ticket_venues()
RETURNS SETOF venue
LANGUAGE sql
STABLE
AS $$
    SELECT v.*
    FROM venue v
    WHERE EXISTS (
        SELECT 1
        FROM ticket t
        WHERE t.venue_id = v.id
    )
    ORDER BY v.id;
$$;

CREATE OR REPLACE FUNCTION copy_ticket_as_vip(
    p_ticket_id integer
)
RETURNS SETOF ticket
LANGUAGE sql
VOLATILE
AS $$
    INSERT INTO ticket (
        name,
        coordinates_id,
        creation_date,
        person_id,
        event_id,
        price,
        type,
        discount,
        number,
        venue_id
    )
    SELECT
        name,
        coordinates_id,
        CURRENT_TIMESTAMP,
        person_id,
        event_id,
        price * 2,
        'VIP',
        discount,
        number,
        venue_id
    FROM ticket
    WHERE id = p_ticket_id
    RETURNING *;
$$;


CREATE OR REPLACE FUNCTION copy_ticket_with_discount(
    p_ticket_id integer,
    p_discount integer
)
RETURNS SETOF ticket
LANGUAGE plpgsql
VOLATILE
AS $$
BEGIN
    IF p_discount < 1 OR p_discount > 100 THEN
        RAISE EXCEPTION 'Discount must be between 1 and 100';
    END IF;

    RETURN QUERY
    INSERT INTO ticket (
        name,
        coordinates_id,
        creation_date,
        person_id,
        event_id,
        price,
        type,
        discount,
        number,
        venue_id
    )
    SELECT
        t.name,
        t.coordinates_id,
        CURRENT_TIMESTAMP,
        t.person_id,
        t.event_id,

        (
            t.price +
            ROUND(t.price * p_discount / 100.0)
        )::integer,

        t.type,
        p_discount,
        t.number,
        t.venue_id
    FROM ticket t
    WHERE t.id = p_ticket_id
    RETURNING *;
END;
$$;