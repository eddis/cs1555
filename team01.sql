-- ## Drop pre-existing tables
drop table customer cascade constraints;
drop table administrator cascade constraints;
drop table product cascade constraints;
drop sequence product_auction_id_sequence;
drop table bidlog cascade constraints;
drop sequence bidlog_bidsn_sequence;
drop table category cascade constraints;
drop table belongs_to cascade constraints;

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
create sequence product_auction_id_sequence start with 0 increment by 1 nomaxvalue;

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
create sequence bidlog_bidsn_sequence start with 0 increment by 1 nomaxvalue;

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
		initially immediate deferrable,
	--
	constraint check_highest_bid
		check (
			case when (select amount from product where product.auction_id = auction_id) is null then
				amount >= (select min_price from product where product.auction_id = auction_id)
			else
				amount > (select amount from product where product.auction_id = auction_id)
			end
		)
);	

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
	
insert into administrator values ('admin', 'admin', 'Bobby', '6810 SENSQ', 'admin@gmail.com');
insert into administrator values ('admin2', 'admin', 'Timmy', '6810 SENSQ', 'admin2@gmail.com');

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

insert into system_time values (to_date('04/03/2013 11:59:00', 'MM/DD/YYYY HH24:MI:SS'));

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

insert into belongs_to values (0, 'Monitors');
insert into belongs_to values (1, 'Monitors');
insert into belongs_to values (2, 'Fiction books');

-- ## SQL Statements
--
-- # Customer Interface
--
-- * Browsing Products (by highest bid)
--    - User-selected category: 'Books'
--    - User is sorting by highest bid amount
select auction_id, name, description, amount, min_price, start_date, number_of_days, seller
from product join belongs_to
on product.auction_id = belongs_to.auction_id
where status = 'underauction' and category = 'Books'
order by amount desc;

-- * Browsing Products (alphabetically)
--    - User-selected category: 'Books'
--    - User is sorting by product name
select auction_id, name, description, amount, min_price, start_date, number_of_days, seller
from product join belongs_to
on product.auction_id = belongs_to.auction_id
where status = 'underauction' and category = 'Books'
order by name asc;

-- * Searching for product by text
--    - let keyword 1 = 'Marchetta'
--    - let keyword 2 = ''
select auction_id, name, description, status, amount, min_price, start_date, number_of_days, seller
from product
where description LIKE '%Marchetta%' and description LIKE '%%';

-- * Putting products up for auction
--    - We assume the procedure will require the following additional parameters:
--       seller, min_price
create or replace procedure put_product(seller in varchar2, name in varchar2, description in varchar2, category in varchar2, min_price in int, num_days in int)
is
	cur_time date;
	cur_category varchar2;
begin
	-- grab the current time
	select current_time
	into cur_time
	from system_time;

    -- add the product
    insert into product values (
    	product_auction_id_sequence.nextval, 
    	name,
    	description,
    	seller,
    	cur_time,
    	num_days,
    	min_price,
    	'underauction',
    	null,
    	null,
    	null
    );

    commit;

    -- categorize the product by visiting all parent categories up the chain
	cur_category = category;
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

	commit;
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

	commit;
end;
/

-- Test the triggers
select * from system_time;
insert into bidlog values (bidlog_bidsn_sequence.nextval, 2, 'user3', to_date('04/03/2013 11:59:00', 'MM/DD/YYYY HH24:MI:SS'), 1);
select * from system_time;
insert into bidlog values (bidlog_bidsn_sequence.nextval, 2, 'user4', to_date('04/03/2013 11:59:05', 'MM/DD/YYYY HH24:MI:SS'), 1);
select * from system_time;

-- * Selling products
-- TODO

-- * Suggestions
--    - Customer X: 'user1'
-- FIXME
select distinct auction_id
from product join bidlog
on product.auction_id = bidlog.auction_id
where status = 'underauction' and bidder in (
	select distinct bidder
	from bidlog
	where auction_id in (
		select distinct auction_id
		from bidlog
		where bidder = 'user1'
	)
);

-- # Administrator Interface
--
-- * New customer registration
create or replace procedure create_user(login in varchar2, password in varchar2, name in varchar2, address in varchar2, email in varchar2, is_admin in int)
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

-- * Statistics

-- # Additional functional requirements
--
create or replace trigger tri_closeAuctions
after update of current_time
on system_time
for each row
begin
	update product
	set status = 'close'
	where status = 'underauction' and sell_date <= :new.current_time;

	commit;
end;
/