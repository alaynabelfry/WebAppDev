CREATE DEFINER = CURRENT_USER TRIGGER `mydb`.`groups_AFTER_DELETE` AFTER DELETE ON `groups` FOR EACH ROW
BEGIN

DELETE FROM users_groups
	WHERE users_groups.group_id = OLD.group_id;

END
