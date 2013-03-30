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
	min_price int not null,
	number_of_days int not null,
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
		initially immediate deferrable
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

-- define a sequence for generating id numbers
create sequence system_time_id start with 0 increment by 1 nomaxvalue;

create table system_time(
	id int not null,
	current_time date not null,
	--
	constraint pk_system_time primary key (id)
		initially immediate deferrable
);

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
    	min_price,
    	num_days,
    	'underauction',
    	NULL,
    	NULL,
    	NULL
    );

    commit;

    -- categorize the product by visiting all parent categories up the chain
	cur_category = category;
	loop
		exit when cur_category IS NULL;
		
		insert into belongs_to values (id, cur_category);
		
		select parent_category
		into cur_category
		from category
		where name = cur_category;
	end loop;

	commit;
end;
/

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
	-- advance the clock 5 seconds
	update system_time
	set current_time = current_time + interval '5' second;
end;
/