create table users (
	userID int not null auto_increment,
	username varchar(30) not null,
    userPassword varchar(30) not null,
    primary key (userID)
);

create table signs (
	signID int not null auto_increment,
	sign_title varchar(100),
    sign_text varchar(1000),
    sign_coord varchar(30),
    primary key (signID)
);

create table visits (
	visitID int unique not null auto_increment,
    signID int not null,
    userID int not null,
    foreign key (userID) references users (userID),
    foreign key (signID) references signs (signID),
    primary key (userID, signID)
);

