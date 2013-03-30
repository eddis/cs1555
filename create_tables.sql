create table customer(
	login varchar2(10) not null,
	password varchar2(10) not null,
	name varchar2(20),
	address varchar2(30),
	email varchar2(20) not null,
	--
	constraint pk_customer_login primary key (login)
		initially immediate deferrable,
	constraint ak_customer_email unique(email)
		initially immediate deferrable
);

create table administrator(
	login varchar2(10) not null,
	password varchar2(10) not null,
	name varchar2(20),
	address varchar2(30),
	email varchar2(20) not null,
	--
	constraint pk_administrator_login primary key (login)
		initially immediate deferrable,
	constraint ak_administrator_email unique(email)
		initially immediate deferrable
);

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
	constraint pk_product_auction_id primary key (auction_id)
		initially immediate deferrable,
	constraint fk_product_seller foreign key (seller) references customer(login)
		initially immediate deferrable
	constraint fk_product_buyer foreign key (buyer) references customer(login)
		initially immediate deferrable
);

create table bidlog(
	bidsn int not null,
	auction_id int not null,
	bidder varchar2(10) not null,
	bid_time date not null,
	amount int not null,
	--
	constraint pk_bidlog_bidsn primary key (bidsn)
		initially immediate deferrable,
	constraint fk_bidlog_auction_id foreign key (auction_id) references product(auction_id)
		initially immediate deferrable,
	constraint fk_bidlog_bidder foreign key (bidder) references customer(login)
		initially immediate deferrable
);