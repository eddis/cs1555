select distinct product.auction_id as suggested_auction, count(distinct bidlog.bidder) as num_bid_friends
from product join bidlog
on product.auction_id = bidlog.auction_id
where status = 'underauction' and bidder in (
	select distinct bidder
	from bidlog bl1
	where not exists (
		select distinct auction_id
		from bidlog cust_bidlog
		where bidder = ? and not exists (
			select distinct bidder
			from bidlog bl2
			where bl1.bidder = bl2.bidder and bl2.auction_id = cust_bidlog.auction_id
		)
	)
) and product.auction_id not in (
	select distinct auction_id
	from bidlog
	where bidder = ?
)
group by product.auction_id
order by count(distinct bidlog.bidder) desc;
