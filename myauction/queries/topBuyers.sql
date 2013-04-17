select login as top_k
from (
    select distinct customer.login
    from customer join product
    on customer.login = product.buyer
    group by customer.login
    having count(*) > 0
    order by Buying_Amount(login, ?) desc
)
where rownum <= ?
order by rownum