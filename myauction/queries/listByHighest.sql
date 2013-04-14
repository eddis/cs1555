select product.auction_id, name, description, amount, min_price, start_date, number_of_days, seller
from product join belongs_to
on product.auction_id = belongs_to.auction_id
where status = 'underauction' and category = ?
order by amount desc nulls last