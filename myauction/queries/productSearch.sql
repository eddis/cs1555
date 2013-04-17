select product.auction_id, name, description, status, amount, min_price, start_date, number_of_days, seller
from product
where description LIKE ? and description LIKE ?