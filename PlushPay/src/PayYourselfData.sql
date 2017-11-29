
	INSERT INTO payYourself.currencytypes (id,code,ratetobase,symbol,date,base)
	VALUES (0,"USD",10000,"$","2007-12-31 23:59:59:99999",true);
   
	INSERT INTO payYourself.currencytypes (id,code,ratetobase,symbol,date)
	VALUES (1,"AUD",8829,"$","2007-12-31 23:59:59:99999");

   
	INSERT INTO payYourself.user_role (username, role)
	VALUES ("tpacker", "user");
   
	INSERT INTO payYourself.user_role (username, role)
	VALUES ("admin", "administrator");
	
	INSERT INTO payYourself.user_role (username, role)
	VALUES ("manager", "manager");

	INSERT INTO payYourself.user_role (username, role)
	VALUES ("jallemann", "accounting");

	-- Setup User tpacker
	INSERT INTO payYourself.user_data (username, firstname, lastname, password,email,cantrade)
	VALUES ("tpacker", "Terry", "Packer", "password","terry@plushpay.com",true);

	INSERT INTO payYourself.beneficiary (beneficiary_id,name,type,details)
	VALUES (1,"Terry's USD Bene","USD",1);
	
	INSERT INTO payYourself.beneficiary (beneficiary_id,name,type,details)
	VALUES (2,"Terry's AUD Bene","AUD",2);
	
	INSERT INTO payYourself.beneficiary (beneficiary_id,name,type,details)
	VALUES (3,"Terry's Other USD Bene","USD",3);
	
	INSERT INTO payYourself.beneficiary_details (details_id)
	VALUES (1);

	INSERT INTO payYourself.beneficiary_details (details_id)
	VALUES (2);
	
	INSERT INTO payYourself.beneficiary_details (details_id)
	VALUES (3);
	
	INSERT INTO payYourself.beneficiary_usd_details (details_id,account_number,routing_number)
	VALUES (1,"323-4445-7866","322-457");

	INSERT INTO payYourself.beneficiary_aud_details (details_id,account_number,bsb_number)
	VALUES (2,"233-222-441","55-331");
	
	INSERT INTO payYourself.beneficiary_usd_details (details_id,account_number,routing_number)
	VALUES (3,"323-4445-7866","322-457");
	
	INSERT INTO payYourself.user_beneficiaries (username,beneficiary_id,beneficiary_pos)
	VALUES ("tpacker",1,0);

	INSERT INTO payYourself.user_beneficiaries (username,beneficiary_id,beneficiary_pos)
	VALUES ("tpacker",2,1);

	INSERT INTO payYourself.user_beneficiaries (username,beneficiary_id,beneficiary_pos)
	VALUES ("tpacker",3,2);
	
	-- Setup user admin
	INSERT INTO payYourself.user_data (username, firstname, lastname, password,email,cantrade)
	VALUES ("admin", "Terry", "Packer", "password","admin@plushpay.com",true);

	INSERT INTO payYourself.beneficiary (beneficiary_id,name,type,details)
	VALUES (4,"Admin's USD Bene","USD",4);
	
	INSERT INTO payYourself.beneficiary (beneficiary_id,name,type,details)
	VALUES (5,"Admin's AUD Bene","AUD",5);
	
	INSERT INTO payYourself.beneficiary_details (details_id)
	VALUES (4);

	INSERT INTO payYourself.beneficiary_details (details_id)
	VALUES (5);
	
	INSERT INTO payYourself.beneficiary_usd_details (details_id,account_number,routing_number)
	VALUES (4,"323-4445-7866","322-457");

	INSERT INTO payYourself.beneficiary_aud_details (details_id,account_number,bsb_number)
	VALUES (5,"233-222-441","55-331");

	INSERT INTO payYourself.user_beneficiaries (username,beneficiary_id,beneficiary_pos)
	VALUES ("admin",4,0);

	INSERT INTO payYourself.user_beneficiaries (username,beneficiary_id,beneficiary_pos)
	VALUES ("admin",5,1);
	
	
	
	-- Setup user manager
	INSERT INTO payYourself.user_data (username, firstname, lastname, password,email,cantrade)
	VALUES ("manager", "Terry", "Packer", "password","manager@plushpay.com",true);

	INSERT INTO payYourself.beneficiary (beneficiary_id,name,type,details)
	VALUES (6,"Manager's USD Bene","USD",6);
	
	INSERT INTO payYourself.beneficiary (beneficiary_id,name,type,details)
	VALUES (7,"Manager's AUD Bene","AUD",7);
	
	INSERT INTO payYourself.beneficiary_details (details_id)
	VALUES (6);

	INSERT INTO payYourself.beneficiary_details (details_id)
	VALUES (7);
	
	INSERT INTO payYourself.beneficiary_usd_details (details_id,account_number,routing_number)
	VALUES (6,"323-4445-7866","322-457");

	INSERT INTO payYourself.beneficiary_aud_details (details_id,account_number,bsb_number)
	VALUES (7,"233-222-441","55-331");

	INSERT INTO payYourself.user_beneficiaries (username,beneficiary_id,beneficiary_pos)
	VALUES ("manager",6,0);

	INSERT INTO payYourself.user_beneficiaries (username,beneficiary_id,beneficiary_pos)
	VALUES ("manager",7,1);
	
	
	-- Setup user: jallemann
	INSERT INTO payYourself.user_data (username, firstname, lastname, password,email,cantrade)
	VALUES ("jallemann", "Jeanne", "Allemann", "password","jallemann@plushpay.com",true);

	INSERT INTO payYourself.beneficiary (beneficiary_id,name,type,details)
	VALUES (8,"Jeanne's USD Bene","USD",8);
	
	INSERT INTO payYourself.beneficiary (beneficiary_id,name,type,details)
	VALUES (9,"Jeanne's AUD Bene","AUD",9);
	
	INSERT INTO payYourself.beneficiary_details (details_id)
	VALUES (8);

	INSERT INTO payYourself.beneficiary_details (details_id)
	VALUES (9);
	
	INSERT INTO payYourself.beneficiary_usd_details (details_id,account_number,routing_number)
	VALUES (8,"323-4445-7866","322-457");

	INSERT INTO payYourself.beneficiary_aud_details (details_id,account_number,bsb_number)
	VALUES (9,"233-222-441","55-331");

	INSERT INTO payYourself.user_beneficiaries (username,beneficiary_id,beneficiary_pos)
	VALUES ("jallemann",8,0);

	INSERT INTO payYourself.user_beneficiaries (username,beneficiary_id,beneficiary_pos)
	VALUES ("jallemann",9,1);
	

	INSERT INTO payYourself.tasksettings (taskid,allowablemismatch,computetradeinterval,stepsize,maxgroupsizebeforedecimation,status,run)
	VALUES (1,.05,5000,.01,100,"Status",true);