-- ## Drop pre-existing tables
drop table customer cascade constraints;
drop table administrator cascade constraints;
drop table product cascade constraints;
drop sequence product_auction_id_sequence;
drop table bidlog cascade constraints;
drop sequence bidlog_bidsn_sequence;
drop table category cascade constraints;
drop table belongs_to cascade constraints;
drop table system_time cascade constraints;

-- ## Create the database tables
create table customer(
	login varchar2(10) not null,
	password varchar2(10) not null,
	name varchar2(20),
	address varchar2(30),
	email varchar2(20) not null,
	--
	constraint pk_customer primary key (login)
		initially immediate deferrable,
	constraint ak_customer_email unique (email)
		initially immediate deferrable
);

create table administrator(
	login varchar2(10) not null,
	password varchar2(10) not null,
	name varchar2(20),
	address varchar2(30),
	email varchar2(20) not null,
	--
	constraint pk_administrator primary key (login)
		initially immediate deferrable,
	constraint ak_administrator_email unique (email)
		initially immediate deferrable
);

-- define a sequence for generating auction id numbers
create sequence product_auction_id_sequence start with 0 increment by 1 minvalue 0 nomaxvalue;

create table product(
	auction_id int not null,
	name varchar2(20) not null,
	description varchar2(30),
	seller varchar2(10) not null,
	start_date date not null,
	number_of_days int not null,
	min_price int not null,
	status varchar2(15) not null,
	buyer varchar2(10),
	sell_date date,
	amount int,
	--
	constraint pk_product primary key (auction_id)
		initially immediate deferrable,
	constraint fk_product_seller foreign key (seller) references customer(login)
		initially immediate deferrable,
	constraint fk_product_buyer foreign key (buyer) references customer(login)
		initially immediate deferrable
);

-- define a sequence for generating bidsn numbers
create sequence bidlog_bidsn_sequence start with 0 increment by 1 minvalue 0 nomaxvalue;

create table bidlog(
	bidsn int not null,
	auction_id int not null,
	bidder varchar2(10) not null,
	bid_time date not null,
	amount int not null,
	--
	constraint pk_bidlog primary key (bidsn)
		initially immediate deferrable,
	constraint fk_bidlog_auction_id foreign key (auction_id) references product(auction_id)
		initially immediate deferrable,
	constraint fk_bidlog_bidder foreign key (bidder) references customer(login)
		initially immediate deferrable
);	
-- use a trigger to enforce all new bids being > previous amount
create or replace trigger tri_checkHighestBid
before insert
on bidlog
for each row
declare
	old_highest int;
begin
	select max(amount)
	into old_highest 
	from product 
	where product.auction_id = :new.auction_id;

	if :new.amount <= old_highest and old_highest is not null then
		raise_application_error(-20000, 'Invalid bid amount');
	end if;
end;
/

create table category(
	name varchar2(20) not null,
	parent_category varchar2(20),
	--
	constraint pk_category primary key (name)
		initially immediate deferrable,
	constraint fk_category_parent_category foreign key (parent_category) references category(name)
		initially immediate deferrable
);

create table belongs_to(
	auction_id int not null,
	category varchar2(20) not null,
	--
	constraint pk_belongs_to primary key (auction_id, category)
		initially immediate deferrable,
	constraint fk_belongs_to_auction_id foreign key (auction_id) references product(auction_id)
		initially immediate deferrable,
	constraint fk_belongs_to_category foreign key (category) references category(name)
		initially immediate deferrable
);

create table system_time(
	current_time date not null
);

-- Insert sample data
insert into customer values ('user0', 'password', 'Harris', '4153 Scarlett Drive', 'harris@gmail.com');
insert into customer values ('user1', 'password', 'Ferris', '4154 Scarlett Drive', 'ferris@gmail.com');
insert into customer values ('user2', 'password', 'Paris', '4155 Scarlett Drive', 'paris@gmail.com');
insert into customer values ('user3', 'password', 'Joel', '4156 Scarlett Drive', 'joel@gmail.com');
insert into customer values ('user4', 'password', 'Marissa', '4157 Scarlett Drive', 'marissa@gmail.com');
commit;

insert into administrator values ('admin', 'root', 'Bobby', '6810 SENSQ', 'admin@gmail.com');
insert into administrator values ('admin2', 'root', 'Timmy', '6810 SENSQ', 'admin2@gmail.com');
commit;

insert into category values ('Books', null);
insert into category values ('Textbooks', 'Books');
insert into category values ('Fiction books', 'Books');
insert into category values ('Magazines', 'Books');
insert into category values ('Computer Science', 'Textbooks');
insert into category values ('Math', 'Textbooks');
insert into category values ('Philosophy', 'Textbooks');
insert into category values ('Computer Related', null);
insert into category values ('Desktop PCs', 'Computer Related');
insert into category values ('Laptops', 'Computer Related');
insert into category values ('Monitors', 'Computer Related');
commit;

insert into system_time values (to_date('04/03/2013 11:59:00', 'MM/DD/YYYY HH24:MI:SS'));
commit;

insert into product values (product_auction_id_sequence.nextval, 
						    '17 inch monitor', '17 inches', 
						    'user0', to_date('04/03/2013 11:58:00', 'MM/DD/YYYY HH24:MI:SS'), 2, 100, 
						    'underauction', null, null, null);
insert into product values (product_auction_id_sequence.nextval, 
						    '19 inch monitor', '19 inches', 
						    'user0', to_date('04/03/2013 11:58:00', 'MM/DD/YYYY HH24:MI:SS'), 2, 0, 
						    'underauction', null, null, null);
insert into product values (product_auction_id_sequence.nextval, 
						    'Jellicoe Road', 'Melina Marchetta', 
						    'user3', to_date('04/03/2013 11:55:00', 'MM/DD/YYYY HH24:MI:SS'), 7, 1, 
						    'underauction', null, null, null);
commit;

insert into belongs_to values (0, 'Monitors');
insert into belongs_to values (0, 'Computer Related');
insert into belongs_to values (1, 'Monitors');
insert into belongs_to values (1, 'Computer Related');
insert into belongs_to values (2, 'Fiction books');
insert into belongs_to values (2, 'Books');
commit;

-- ## SQL Statements
--
-- # Customer Interface
--
-- * Browsing Products (by highest bid)
--    - User-selected category: 'Books'
--    - User is sorting by highest bid amount
select product.auction_id, name, description, amount, min_price, start_date, number_of_days, seller
from product join belongs_to
on product.auction_id = belongs_to.auction_id
where status = 'underauction' and category = 'Books'
order by amount desc nulls last;

-- * Browsing Products (alphabetically)
--    - User-selected category: 'Books'
--    - User is sorting by product name
select product.auction_id, name, description, amount, min_price, start_date, number_of_days, seller
from product join belongs_to
on product.auction_id = belongs_to.auction_id
where status = 'underauction' and category = 'Books'
order by name asc;

-- * Searching for product by text
--    - let keyword 1 = 'Marchetta'
--    - let keyword 2 = ''
select product.auction_id, name, description, status, amount, min_price, start_date, number_of_days, seller
from product
where description LIKE '%Marchetta%' and description LIKE '%%';

-- * Putting products up for auction
--    - We assume the procedure will require the following additional parameters:
--       seller, min_price
create or replace procedure put_product(seller in varchar2, name in varchar2, description in varchar2, category in varchar2, min_price in int, num_days in int)
is
	cur_time date;
	cur_category varchar2(20);
	id int;
begin
	-- grab the current time
	select current_time
	into cur_time
	from system_time;

	-- save the next auction id
	id := product_auction_id_sequence.nextval;

    -- add the product
    insert into product values (id, name, description, seller, cur_time, num_days, min_price, 'underauction', null, null, null);

    commit;

    -- categorize the product by visiting all parent categories up the chain
	cur_category := category;
	loop
		exit when cur_category IS null;
		
		insert into belongs_to values (id, cur_category);
		
		select parent_category
		into cur_category
		from category
		where name = cur_category;
	end loop;

	commit;
end;
/

-- Test the procedure
call put_product('user1', 'Design Patterns', 'Gang of Four', 'Computer Science', 10, 7);
call put_product('user4', 'Rolling Stone', 'Music, man.', 'Magazines', 2, 2);

-- * Bidding on products
create or replace trigger tri_bidTimeUpdate
after insert
on bidlog
for each row
begin
	-- advance the clock 5 seconds
	update system_time
	set current_time = current_time + interval '5' second;
end;
/

create or replace trigger tri_updateHighBid
after insert
on bidlog
for each row
begin
	update product
	set amount = :new.amount
	where auction_id = :new.auction_id;
end;
/

-- Test the triggers
select to_char(current_time, 'MM/DD/YYYY HH24:MI:SS') from system_time;
insert into bidlog values (bidlog_bidsn_sequence.nextval, 2, 'user4', to_date('04/03/2013 11:59:00', 'MM/DD/YYYY HH24:MI:SS'), 1);
commit;
select to_char(current_time, 'MM/DD/YYYY HH24:MI:SS') from system_time;
insert into bidlog values (bidlog_bidsn_sequence.nextval, 2, 'user1', to_date('04/03/2013 11:59:05', 'MM/DD/YYYY HH24:MI:SS'), 2);
commit;
select to_char(current_time, 'MM/DD/YYYY HH24:MI:SS') from system_time;
insert into bidlog values (bidlog_bidsn_sequence.nextval, 2, 'user4', to_date('04/03/2013 11:59:10', 'MM/DD/YYYY HH24:MI:SS'), 2);
commit;
select to_char(current_time, 'MM/DD/YYYY HH24:MI:SS') from system_time;

insert into bidlog values (bidlog_bidsn_sequence.nextval, 0, 'user4', to_date('04/03/2013 11:59:15', 'MM/DD/YYYY HH24:MI:SS'), 180);
commit;
insert into bidlog values (bidlog_bidsn_sequence.nextval, 1, 'user4', to_date('04/03/2013 11:59:20', 'MM/DD/YYYY HH24:MI:SS'), 185);
commit;

-- * Selling products
create or replace function Second_Highest_Bid(auction_id in int)
	return int
is
	second_highest_bid_bidsn int;
	second_highest_bid_amount int;
begin
	select max(amount) 
	into second_highest_bid_amount
	from bidlog
	where bidlog.auction_id = auction_id and amount != (
		select max(amount) 
		from bidlog
		where bidlog.auction_id = auction_id
	);

	select bidsn
	into second_highest_bid_bidsn
	from bidlog
	where bidlog.auction_id = auction_id and amount = second_highest_bid_amount;

	return second_highest_bid_bidsn;
end;
/

create or replace procedure sell_product(auction_id in int)
is
	num_bids int;
	winning_bid_bidsn int;
	winning_bid_bidder varchar2(10);
	winning_bid_amount int;
begin
	select count(bidsn)
	into num_bids
	from bidlog
	where bidlog.auction_id = auction_id;

	if num_bids = 1 then
		select bidsn into winning_bid_bidsn from bidlog where bidlog.auction_id = auction_id;
	else
		winning_bid_bidsn := Second_Highest_Bid(auction_id);
	end if;

	select amount into winning_bid_amount from bidlog where bidsn = winning_bid_bidsn;
	select bidder into winning_bid_bidder from bidlog where bidsn = winning_bid_bidsn;

	update product 
	set status = 'sold', 
	    buyer = winning_bid_bidder, 
	    sell_date = start_date + number_of_days, 
	    amount = winning_bid_amount
	where product.auction_id = auction_id;

	commit;
end;
/

create or replace procedure withdraw_product(auction_id in int)
is
begin
	update product
	set status = 'withdrawn'
	where product.auction_id = auction_id;

	commit;
end;
/

-- * Suggestions
--    - Customer X: 'user1'
select distinct product.auction_id as suggested_auction, count(distinct bidlog.bidder) as num_bid_friends
from product join bidlog
on product.auction_id = bidlog.auction_id
where status = 'underauction' and bidder in (
	select distinct bidder
	from bidlog bl1
	where not exists (
		select distinct auction_id
		from bidlog cust_bidlog
		where bidder = 'user1' and not exists (
			select distinct bidder
			from bidlog bl2
			where bl1.bidder = bl2.bidder and bl2.auction_id = cust_bidlog.auction_id
		)
	)
) and product.auction_id not in (
	select distinct auction_id
	from bidlog
	where bidder = 'user1'
)
group by product.auction_id
order by count(distinct bidlog.bidder) desc;

-- # Administrator Interface
--
-- * New customer registration
create or replace procedure create_user(login in varchar2, password in varchar2, 
										name in varchar2, address in varchar2, 
										email in varchar2, is_admin in int)
is
begin
	if is_admin = 1 then
		insert into administrator values (login, password, name, address, email);
	else
		insert into customer values (login, password, name, address, email);
	end if;

	commit;
end;
/

-- * Update system date
--    - User-selected date: '04/03/2013 12:00:00'
update system_time set current_time = to_date('04/03/2013 12:00:00', 'MM/DD/YYYY HH24:MI:SS');
commit;

-- * Product statistics (all products)
select name, status, amount as highest_bid, highest_bidder
from (
	(
		select name, status, amount, buyer as highest_bidder
		from product
		where status = 'sold'
	) 
	union
	(
		select product.name, product.status, product.amount, bidlog.bidder as highest_bidder
		from product join bidlog
		on product.auction_id = bidlog.auction_id and product.amount = bidlog.amount
		where product.status != 'sold'
	)
);

-- * Product statistics (customer's products)
--    - Customer X: 'user1'
select name, status, amount as highest_bid, highest_bidder
from (
	(
		select name, status, amount, buyer as highest_bidder
		from product
		where seller = 'user1' and status = 'sold'
	) 
	union
	(
		select product.name, product.status, product.amount, bidlog.bidder as highest_bidder
		from product join bidlog
		on product.auction_id = bidlog.auction_id and product.amount = bidlog.amount
		where product.seller = 'user1' and product.status != 'sold'
	)
);

-- * Statistics
--
-- * Product_Count
create or replace function Product_Count(category in varchar, months in int)
	return int
is
	cur_time date;
	num_sold int;
begin
	-- grab the current time
	select current_time
	into cur_time
	from system_time;

	select count(product.auction_id)
	into num_sold
	from product join (
		select auction_id
		from belongs_to
		where belongs_to.category = category
	) category_product
	on product.auction_id = category_product.auction_id
	where product.status = 'sold' and product.sell_date >= add_months(cur_time, -months);

	return num_sold;
end;
/

-- * Bid_Count
create or replace function Bid_Count(user in varchar, months in int)
	return int
is
	cur_time date;
	num_bids int;
begin
	-- grab the current time
	select current_time
	into cur_time
	from system_time;

	select count(bidlog.bidsn)
	into num_bids
	from bidlog join customer
	on bidlog.bidder = customer.login
	where customer.login = user and bidlog.bid_time >= add_months(cur_time, -months);

	return num_bids;
end;
/

-- * Buying_Amount
create or replace function Buying_Amount(user in varchar, months in int)
	return int
is
	cur_time date;
	spent int;
begin
	-- grab the current time
	select current_time
	into cur_time
	from system_time;

	select sum(amount)
	into spent
	from product
	where buyer = user and sell_date >= add_months(cur_time, -months);

	return spent;
end;
/

-- * top k highest volume categories (leaf nodes)
--    - k = 2, months = 1
select name as highest_volume_category
from (
	select name
	from category
	where name not in (
		select distinct parent_category
		from category
		where parent_category is not null
	)
	group by name
	order by Product_Count(name, 1) desc
)
where rownum <= 2
order by rownum;

-- * top k highest volume categories (root nodes)
--    - k = 2, months = 1
select name as highest_volume_category
from (
	select name
	from category
	where parent_category is null
	group by name
	order by Product_Count(name, 1) desc
)
where rownum <= 2
order by rownum;

-- * top k most active bidders
--    - k = 2, months = 1
select login as most_active_bidder
from (
	select distinct customer.login
	from customer join bidlog
	on customer.login = bidlog.bidder
	group by customer.login
	having count(*) > 0
	order by Bid_Count(login, 1) desc
)
where rownum <= 2
order by rownum;

-- * top k most active buyers
--    - k = 2, months = 1
select login as most_active_bidder
from (
	select distinct customer.login
	from customer join product
	on customer.login = product.buyer
	group by customer.login
	having count(*) > 0
	order by Buying_Amount(login, 1) desc
)
where rownum <= 2
order by rownum;


-- # Additional functional requirements
create or replace trigger tri_closeAuctions
after update of current_time
on system_time
for each row
begin
	update product
	set status = 'close'
	where status = 'underauction' and start_date + number_of_days <= :new.current_time;
end;
/