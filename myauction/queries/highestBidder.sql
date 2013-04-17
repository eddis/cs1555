select bidlog.bidder as highest_bidder
from product join bidlog 
on product.auction_id = bidlog.auction_id and product.amount = bidlog.amount
where product.auction_id = ?
