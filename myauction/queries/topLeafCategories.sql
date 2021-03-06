select name as top_k
from (
    select name
    from category
    where name not in (
        select distinct parent_category
        from category
        where parent_category is not null
    ) and Product_Count(name, ?) > 0
    group by name
    order by Product_Count(name, ?) desc
)
where rownum <= ?
order by rownum