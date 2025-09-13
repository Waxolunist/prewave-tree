TRUNCATE edge;

do $$
    begin
        for r in 1..100 loop -- generate 100 trees
                WITH nodes as (
                    SELECT n as from_id, n + m as to_id
                    FROM generate_series(r*100000, r*100000+99900) n
                             CROSS JOIN generate_series(1, 10) m
                    ORDER BY n, m) -- each tree has 99.900 nodes
                insert into edge (from_id, to_id) select from_id, to_id from nodes;
            end loop;
    end;
$$;

vacuum edge;