drop table if exists auth_cache;
 
create table auth_cache(
	userid 					varchar(50) not null,
	deviceId				varchar(50) not null,
	lastauthenticateddate	int,
	lastauthenticatedtime	int,
    authenticationtype		varchar(20),
	
	primary key(userid,deviceId)
	);

