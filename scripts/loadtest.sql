TRUNCATE edge;

do $$
    begin
        for r in 1..100 loop -- generate 100 trees
                WITH nodes as (SELECT n FROM generate_series(r*100000, r*100000+99998) n) -- each tree has 99.999 nodes
                insert into edge (from_id, to_id) select n, n + 1 from nodes;
            end loop;
    end;
$$;

vacuum edge;