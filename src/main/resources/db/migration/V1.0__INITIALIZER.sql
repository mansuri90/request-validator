create sequence hibernate_sequence start with 1 increment by 1;

CREATE TABLE `customer` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `active` tinyint(1) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
);
--INSERT INTO `customer` VALUES (1,'Big News Media Corp',1),(2,'Online Mega Store',1),(3,'Nachoroo Delivery',0),(4,'Euro Telecom Group',1);

CREATE TABLE `ip_blacklist` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `ip` bigint(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_ip` (`ip`)
);
--INSERT INTO `ip_blacklist` (`ip`) VALUES (0),(2130706433),(4294967295);

CREATE TABLE `ua_blacklist` (
  `user_agent` varchar(255) NOT NULL,
  PRIMARY KEY (`user_agent`)
);
--INSERT INTO `ua_blacklist` VALUES ('A6-Indexer'),('Googlebot-News'),('Googlebot');

create table hourly_stats (
   `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
    date date,
    `request_count` bigint(20) unsigned NOT NULL DEFAULT '0',
    `invalid_count` bigint(20) unsigned NOT NULL DEFAULT '0',
    time time,
    `customer_id` bigint(11) unsigned NOT NULL,
    primary key (id),
    UNIQUE KEY `unique_customer_date_time` (`customer_id`,`date`,`time`),
    CONSTRAINT `hourly_stats_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);