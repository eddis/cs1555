select name as top_k
from (
    select name
    from category
    where parent_category is null
    group by name
    order by Product_Count(name, ?) desc
)
where rownum <= ?
order by rownum