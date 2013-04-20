select product.auction_id
from customer join product 
on customer.login = product.seller 
where customer.login = ? and product.status = 'close'