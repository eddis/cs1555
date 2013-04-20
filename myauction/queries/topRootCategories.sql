select name as top_k
from (
    select name
    from category
    where parent_category is null and Product_Count(name, ?) > 0
    group by name
    order by Product_Count(name, ?) desc
)
where rownum <= ?
order by rownum