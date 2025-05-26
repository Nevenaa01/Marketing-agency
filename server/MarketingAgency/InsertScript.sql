INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');


INSERT INTO public.permissions(
	id, name)
	VALUES 
	(1, 'CREATE_ADVERTISEMENT_REQUEST'),
	(2, 'UPDATE_USER_PROFILE'),
	(3, 'DELETE_ADVERTISEMENT_REQUEST'),
    (4, 'READ_OWN_REQUESTS'),
	(5, 'READ_USER_INFORMATION'),
	(6, 'CREATE_ADVERTISEMENT'),
	(7, 'READ_OWN_ADVERTISEMENT'),
	(8, 'READ_ADVERTISEMENT_FOR_YOU'),
	(9, 'READ_ALL_REQUESTS'),
	(10, 'READ_ALL_USERS'),
	(11, 'READ_PENDING_USERS'),
	(12, 'READ_USER_BY_ID'),
	(13, 'UPDATE_USER'),
	(14, 'DELETE_USER'),
	(15, 'DENY_USERREGISTRATION'),
	(16, 'GENERATE_ACTIVATION_LINK'),
	(17, 'DELETE_ALL_MY_DATA');


INSERT INTO public.roles_permissions(
	role_id, permission_id)
	VALUES (1, 1),
		   (1, 2),
		   (1, 3),
           (1, 4),
		   (1, 5),
		   (1, 17),
		   (2, 6),
		   (2, 7),
		   (1, 8),
		   (2, 9),
		   (2, 17),
		   (3, 1),
		   (3, 2),
		   (3, 3),
		   (3, 4),
		   (3, 5),
		   (3, 6),
		   (3, 7),
		   (3, 8),
		   (3, 9),
		   (3, 10),
		   (3, 11),
		   (3, 12),
		   (3, 13),
		   (3, 14),
		   (3, 15),
		   (3, 16);