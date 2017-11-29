
    alter table payYourself.account 
        drop 
        foreign key FKB9D38A2DAD1DA871;

    alter table payYourself.account 
        drop 
        foreign key FKB9D38A2DB6B10B57;

    alter table payYourself.beneficiary 
        drop 
        foreign key FKDE5136E5540969F5;

    alter table payYourself.beneficiary_aud_details 
        drop 
        foreign key FKAAADC959B979B7EB;

    alter table payYourself.beneficiary_usd_details 
        drop 
        foreign key FK63F8DE2FB979B7EB;

    alter table payYourself.chain_links 
        drop 
        foreign key FKFEBDB4BB153A76A0;

    alter table payYourself.level_one 
        drop 
        foreign key FK9D4D172BAD1DA871;

    alter table payYourself.pycurrency 
        drop 
        foreign key FK16C5E51A70F7F4BD;

    alter table payYourself.trade 
        drop 
        foreign key FK697F164F445063;

    alter table payYourself.trade 
        drop 
        foreign key FK697F164CC82DEAF;

    alter table payYourself.trade_beneficiary 
        drop 
        foreign key FK19A2908A7C5487FE;

    alter table payYourself.trade_beneficiary 
        drop 
        foreign key FK19A2908AB3E8A60F;

    alter table payYourself.tradelinks 
        drop 
        foreign key FK707E9CD5C7270CA;

    alter table payYourself.tradelinks 
        drop 
        foreign key FK707E9CD5A49E457E;

    alter table payYourself.trader 
        drop 
        foreign key FKCC663B8E689B0E5C;

    alter table payYourself.trader 
        drop 
        foreign key FKCC663B8EEF077A8F;

    alter table payYourself.trader 
        drop 
        foreign key FKCC663B8E1F76E338;

    alter table payYourself.trader 
        drop 
        foreign key FKCC663B8E738B3C78;

    alter table payYourself.trader_beneficiaries 
        drop 
        foreign key FKEB449F52F873FBF6;

    alter table payYourself.trader_beneficiaries 
        drop 
        foreign key FKEB449F52C3C8BB7F;

    alter table payYourself.trader_group_traders 
        drop 
        foreign key FK1FD4A1F4D5EF31DD;

    alter table payYourself.trader_group_traders 
        drop 
        foreign key FK1FD4A1F4F873FBF6;

    alter table payYourself.tradergroup 
        drop 
        foreign key FKA977C3B1689B0E5C;

    alter table payYourself.tradergroup 
        drop 
        foreign key FKA977C3B1738B3C78;

    alter table payYourself.transaction 
        drop 
        foreign key FK7FA0D2DE7D6D3B14;

    alter table payYourself.transaction 
        drop 
        foreign key FK7FA0D2DE1F76E338;

    alter table payYourself.transaction 
        drop 
        foreign key FK7FA0D2DE1419A783;

    alter table payYourself.transaction 
        drop 
        foreign key FK7FA0D2DED1205922;

    alter table payYourself.transaction 
        drop 
        foreign key FK7FA0D2DE57EC21DE;

    alter table payYourself.user_beneficiaries 
        drop 
        foreign key FK7569A0CF2A63D7F0;

    alter table payYourself.user_beneficiaries 
        drop 
        foreign key FK7569A0CF1F76E338;

    drop table if exists payYourself.account;

    drop table if exists payYourself.beneficiary;

    drop table if exists payYourself.beneficiary_aud_details;

    drop table if exists payYourself.beneficiary_details;

    drop table if exists payYourself.beneficiary_usd_details;

    drop table if exists payYourself.chain_links;

    drop table if exists payYourself.chains;

    drop table if exists payYourself.currencytypes;

    drop table if exists payYourself.level_one;

    drop table if exists payYourself.level_two;

    drop table if exists payYourself.period;

    drop table if exists payYourself.products;

    drop table if exists payYourself.pycurrency;

    drop table if exists payYourself.registrationinfo;

    drop table if exists payYourself.tasksettings;

    drop table if exists payYourself.trade;

    drop table if exists payYourself.trade_beneficiary;

    drop table if exists payYourself.tradelinks;

    drop table if exists payYourself.trader;

    drop table if exists payYourself.trader_beneficiaries;

    drop table if exists payYourself.trader_group_traders;

    drop table if exists payYourself.tradergroup;

    drop table if exists payYourself.transaction;

    drop table if exists payYourself.user_beneficiaries;

    drop table if exists payYourself.user_data;

    drop table if exists payYourself.user_role;

    create table payYourself.account (
        id INTEGER not null unique,
        symbol VARCHAR(20),
        level_two_id INTEGER,
        level_one_id INTEGER,
        primary key (id)
    );

    create table payYourself.beneficiary (
        beneficiary_id BIGINT not null,
        name VARCHAR(30),
        type VARCHAR(3),
        details bigint unique,
        primary key (beneficiary_id)
    );

    create table payYourself.beneficiary_aud_details (
        details_id bigint not null unique,
        account_number VARCHAR(30),
        bsb_number VARCHAR(8),
        primary key (details_id)
    );

    create table payYourself.beneficiary_details (
        details_id bigint not null auto_increment,
        primary key (details_id)
    );

    create table payYourself.beneficiary_usd_details (
        details_id bigint not null unique,
        account_number VARCHAR(30),
        routing_number VARCHAR(30),
        primary key (details_id)
    );

    create table payYourself.chain_links (
        linkid integer not null,
        elt tinyblob,
        idx integer not null,
        primary key (linkid, idx)
    );

    create table payYourself.chains (
        chainid integer not null,
        primary key (chainid)
    );

    create table payYourself.currencytypes (
        id BIGINT not null unique,
        code VARCHAR(3) not null,
        ratetobase BIGINT not null,
        symbol VARCHAR(2) not null,
        date DATE not null,
        base BIT default false,
        primary key (id)
    );

    create table payYourself.level_one (
        id INTEGER not null unique,
        symbol VARCHAR(20),
        code VARCHAR(4),
        level_two_id INTEGER,
        primary key (id)
    );

    create table payYourself.level_two (
        id INTEGER not null unique,
        symbol VARCHAR(20),
        code VARCHAR(4),
        primary key (id)
    );

    create table payYourself.period (
        id INTEGER not null unique,
        symbol VARCHAR(20),
        primary key (id)
    );

    create table payYourself.products (
        id INT not null,
        fee FLOAT,
        active BIT comment 'Denotes if product is actively being used in system.',
        primary key (id)
    );

    create table payYourself.pycurrency (
        currencyid BIGINT not null unique,
        value BIGINT,
        currencycode VARCHAR(5),
        primary key (currencyid)
    );

    create table payYourself.registrationinfo (
        registrationid integer not null,
        username VARCHAR(20) not null unique,
        firstname VARCHAR(50) not null,
        lastname VARCHAR(50) not null,
        password VARCHAR(20) default 'password' not null,
        email VARCHAR(50) not null,
        primary key (registrationid)
    );

    create table payYourself.tasksettings (
        taskid INTEGER not null unique,
        allowablemismatch FLOAT,
        computetradeinterval INTEGER,
        stepsize FLOAT,
        maxgroupsizebeforedecimation INTEGER,
        status VARCHAR(20),
        run BIT,
        primary key (taskid)
    );

    create table payYourself.trade (
        tradeid BIGINT not null unique,
        buyergroup BIGINT,
        sellergroup BIGINT,
        datecreated DATE,
        status VARCHAR(20),
        primary key (tradeid)
    );

    create table payYourself.trade_beneficiary (
        id BIGINT not null,
        currencyid BIGINT,
        beneificary_id BIGINT,
        primary key (id)
    );

    create table payYourself.tradelinks (
        linkid integer not null,
        buyersellcurrencymismatch FLOAT,
        buyergroupid INTEGER,
        sellergroupid INTEGER,
        buyerbuycurrencymismatch FLOAT,
        primary key (linkid)
    );

    create table payYourself.trader (
        traderid BIGINT not null unique,
        tradergroupid INTEGER,
        username VARCHAR(20),
        currencytobuyid BIGINT,
        currencytosellid BIGINT,
        status VARCHAR(20),
        primary key (traderid)
    );

    create table payYourself.trader_beneficiaries (
        traderid bigint not null,
        id bigint not null,
        idx integer not null,
        primary key (traderid, idx)
    );

    create table payYourself.trader_group_traders (
        groupid bigint not null,
        traderid bigint not null,
        idx integer not null,
        primary key (groupid, idx)
    );

    create table payYourself.tradergroup (
        groupid BIGINT not null,
        currencytosellid BIGINT,
        currencytobuyid BIGINT,
        primary key (groupid)
    );

    create table payYourself.transaction (
        id BIGINT not null unique,
        credit BIT,
        date DATE,
        period_id INTEGER,
        account_id INTEGER,
        pycurrency_id BIGINT,
        username VARCHAR(20),
        journal_id BIGINT,
        primary key (id)
    );

    create table payYourself.user_beneficiaries (
        username varchar(20) not null,
        beneficiary_id bigint not null,
        beneficiary_pos integer not null,
        primary key (username, beneficiary_pos)
    );

    create table payYourself.user_data (
        username VARCHAR(20) not null unique,
        firstname VARCHAR(20) not null,
        lastname VARCHAR(30) not null,
        password VARCHAR(20) default 'password' not null,
        email VARCHAR(30) not null,
        cantrade BIT,
        primary key (username)
    );

    create table payYourself.user_role (
        username VARCHAR(20) not null unique,
        role VARCHAR(20) not null,
        primary key (username)
    );

    alter table payYourself.account 
        add index FKB9D38A2DAD1DA871 (level_two_id), 
        add constraint FKB9D38A2DAD1DA871 
        foreign key (level_two_id) 
        references payYourself.level_two (id);

    alter table payYourself.account 
        add index FKB9D38A2DB6B10B57 (level_one_id), 
        add constraint FKB9D38A2DB6B10B57 
        foreign key (level_one_id) 
        references payYourself.level_one (id);

    alter table payYourself.beneficiary 
        add index FKDE5136E5540969F5 (details), 
        add constraint FKDE5136E5540969F5 
        foreign key (details) 
        references payYourself.beneficiary_details (details_id);

    alter table payYourself.beneficiary_aud_details 
        add index FKAAADC959B979B7EB (details_id), 
        add constraint FKAAADC959B979B7EB 
        foreign key (details_id) 
        references payYourself.beneficiary_details (details_id);

    alter table payYourself.beneficiary_usd_details 
        add index FK63F8DE2FB979B7EB (details_id), 
        add constraint FK63F8DE2FB979B7EB 
        foreign key (details_id) 
        references payYourself.beneficiary_details (details_id);

    alter table payYourself.chain_links 
        add index FKFEBDB4BB153A76A0 (linkid), 
        add constraint FKFEBDB4BB153A76A0 
        foreign key (linkid) 
        references payYourself.chains (chainid);

    alter table payYourself.level_one 
        add index FK9D4D172BAD1DA871 (level_two_id), 
        add constraint FK9D4D172BAD1DA871 
        foreign key (level_two_id) 
        references payYourself.level_two (id);

    alter table payYourself.pycurrency 
        add index FK16C5E51A70F7F4BD (currencycode), 
        add constraint FK16C5E51A70F7F4BD 
        foreign key (currencycode) 
        references payYourself.currencytypes (id);

    alter table payYourself.trade 
        add index FK697F164F445063 (sellergroup), 
        add constraint FK697F164F445063 
        foreign key (sellergroup) 
        references payYourself.tradergroup (groupid);

    alter table payYourself.trade 
        add index FK697F164CC82DEAF (buyergroup), 
        add constraint FK697F164CC82DEAF 
        foreign key (buyergroup) 
        references payYourself.tradergroup (groupid);

    alter table payYourself.trade_beneficiary 
        add index FK19A2908A7C5487FE (beneificary_id), 
        add constraint FK19A2908A7C5487FE 
        foreign key (beneificary_id) 
        references payYourself.beneficiary (beneficiary_id);

    alter table payYourself.trade_beneficiary 
        add index FK19A2908AB3E8A60F (currencyid), 
        add constraint FK19A2908AB3E8A60F 
        foreign key (currencyid) 
        references payYourself.pycurrency (currencyid);

    alter table payYourself.tradelinks 
        add index FK707E9CD5C7270CA (buyergroupid), 
        add constraint FK707E9CD5C7270CA 
        foreign key (buyergroupid) 
        references payYourself.tradergroup (groupid);

    alter table payYourself.tradelinks 
        add index FK707E9CD5A49E457E (sellergroupid), 
        add constraint FK707E9CD5A49E457E 
        foreign key (sellergroupid) 
        references payYourself.tradergroup (groupid);

    alter table payYourself.trader 
        add index FKCC663B8E689B0E5C (currencytosellid), 
        add constraint FKCC663B8E689B0E5C 
        foreign key (currencytosellid) 
        references payYourself.pycurrency (currencyid);

    alter table payYourself.trader 
        add index FKCC663B8EEF077A8F (tradergroupid), 
        add constraint FKCC663B8EEF077A8F 
        foreign key (tradergroupid) 
        references payYourself.tradergroup (groupid);

    alter table payYourself.trader 
        add index FKCC663B8E1F76E338 (username), 
        add constraint FKCC663B8E1F76E338 
        foreign key (username) 
        references payYourself.user_data (username);

    alter table payYourself.trader 
        add index FKCC663B8E738B3C78 (currencytobuyid), 
        add constraint FKCC663B8E738B3C78 
        foreign key (currencytobuyid) 
        references payYourself.pycurrency (currencyid);

    alter table payYourself.trader_beneficiaries 
        add index FKEB449F52F873FBF6 (traderid), 
        add constraint FKEB449F52F873FBF6 
        foreign key (traderid) 
        references payYourself.trader (traderid);

    alter table payYourself.trader_beneficiaries 
        add index FKEB449F52C3C8BB7F (id), 
        add constraint FKEB449F52C3C8BB7F 
        foreign key (id) 
        references payYourself.trade_beneficiary (id);

    alter table payYourself.trader_group_traders 
        add index FK1FD4A1F4D5EF31DD (groupid), 
        add constraint FK1FD4A1F4D5EF31DD 
        foreign key (groupid) 
        references payYourself.tradergroup (groupid);

    alter table payYourself.trader_group_traders 
        add index FK1FD4A1F4F873FBF6 (traderid), 
        add constraint FK1FD4A1F4F873FBF6 
        foreign key (traderid) 
        references payYourself.trader (traderid);

    alter table payYourself.tradergroup 
        add index FKA977C3B1689B0E5C (currencytosellid), 
        add constraint FKA977C3B1689B0E5C 
        foreign key (currencytosellid) 
        references payYourself.pycurrency (currencyid);

    alter table payYourself.tradergroup 
        add index FKA977C3B1738B3C78 (currencytobuyid), 
        add constraint FKA977C3B1738B3C78 
        foreign key (currencytobuyid) 
        references payYourself.pycurrency (currencyid);

    alter table payYourself.transaction 
        add index FK7FA0D2DE7D6D3B14 (account_id), 
        add constraint FK7FA0D2DE7D6D3B14 
        foreign key (account_id) 
        references payYourself.account (id);

    alter table payYourself.transaction 
        add index FK7FA0D2DE1F76E338 (username), 
        add constraint FK7FA0D2DE1F76E338 
        foreign key (username) 
        references payYourself.user_data (username);

    alter table payYourself.transaction 
        add index FK7FA0D2DE1419A783 (pycurrency_id), 
        add constraint FK7FA0D2DE1419A783 
        foreign key (pycurrency_id) 
        references payYourself.pycurrency (currencyid);

    alter table payYourself.transaction 
        add index FK7FA0D2DED1205922 (period_id), 
        add constraint FK7FA0D2DED1205922 
        foreign key (period_id) 
        references payYourself.period (id);

    alter table payYourself.transaction 
        add index FK7FA0D2DE57EC21DE (journal_id), 
        add constraint FK7FA0D2DE57EC21DE 
        foreign key (journal_id) 
        references payYourself.account (id);

    alter table payYourself.user_beneficiaries 
        add index FK7569A0CF2A63D7F0 (beneficiary_id), 
        add constraint FK7569A0CF2A63D7F0 
        foreign key (beneficiary_id) 
        references payYourself.beneficiary (beneficiary_id);

    alter table payYourself.user_beneficiaries 
        add index FK7569A0CF1F76E338 (username), 
        add constraint FK7569A0CF1F76E338 
        foreign key (username) 
        references payYourself.user_data (username);
