select login as top_k
from (
    select distinct customer.login
    from customer join bidlog
    on customer.login = bidlog.bidder
    group by customer.login
    having count(*) > 0
    order by Bid_Count(login, ?) desc
)
where rownum <= ?
order by rownum