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