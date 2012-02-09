# public schema
 
# --- !Ups

DROP TABLE IF EXISTS "site";
CREATE TABLE "site" (
	"address" varchar(255) DEFAULT NULL::character varying,
	"city" varchar(255) DEFAULT NULL::character varying,
	"state" varchar(255) DEFAULT NULL::character varying,
	"zip" varchar(255) DEFAULT NULL::character varying,
	"county" varchar(255) DEFAULT NULL::character varying,
	"latitude" float8,
	"longitude" float8,
	"id" varchar(255) NOT NULL,
	"name" varchar(255) DEFAULT NULL::character varying,
	"zipcode" varchar(255) DEFAULT NULL::character varying,
	"geohash" text
)
WITH (OIDS=FALSE);

INSERT INTO "site" VALUES ('102 NICHOLS AVENUE', 'SYRACUSE', 'NY', '13206', 'ONONDAGA', '43.068842', '-76.104558', '0', null, null, 'dr9vh0qq5q09bf56303mm5b');
INSERT INTO "site" VALUES ('421 SPENCER STREET', 'SYRACUSE', 'NY', '13204', 'ONONDAGA', '43.056611', '-76.164611', '0', null, null, 'dr9ufy4wgyjc03yehuzrkuz');
INSERT INTO "site" VALUES ('116 EAST CASTLE STREET', 'SYRACUSE', 'NY', '13205', 'ONONDAGA', '43.033056', '-76.1475', '0', null, null, 'dr9ug1zjdxhdhb63c59qn7p');
INSERT INTO "site" VALUES ('117 SOUTHVIEW ROAD', 'SOLVAY', 'NY', '13209', 'ONONDAGA', '43.050694', '-76.218083', '0', null, null, 'dr9uct5smzn0ubfus4ftsyz');
INSERT INTO "site" VALUES ('3229 E GENESEE STREET', 'SYRACUSE', 'NY', '13214', 'ONONDAGA', '43.043389', '-76.094633', '0', null, null, 'dr9uu7v9ft30u84mwyq66gz');
INSERT INTO "site" VALUES ('1266 OLD ROUTE 17', 'LIVINGSTON MANOR', 'NY', '12578', '', '41.9267', '-74.859', '0', null, null, 'dr6y8892dz7gkku5r1cn8hp');
INSERT INTO "site" VALUES ('1709 Union Avenue     ', 'Hazlet ', 'NJ', '7730', '', '40.4258', '-74.1586', '0', null, null, 'dr5mbrr3777fpm55j94rcnb');
INSERT INTO "site" VALUES ('CLUB DR', 'Helmetta', 'NJ', '8828', '', '40.3778', '74.425', '0', null, null, 'txhvtwd9spqr00pyqce9fuz');
INSERT INTO "site" VALUES ('WINCHESTER AVE.', 'Hempstead', 'NY', '11550', 'Nassau', '40.707321', '-73.625682', '0', null, null, 'dr5xsde3m68kq4u362bmm8z');
INSERT INTO "site" VALUES ('6 SEALEY AVE', 'HEMPSTEAD', 'NY', '11550', '', '40.713333333', '-73.630555556', '0', null, null, 'dr5xs7xu7eqmq99gsxy0wvz');

CREATE INDEX "site_geohash" ON "site" USING btree(geohash ASC NULLS LAST);
CREATE INDEX "site_lat_lng" ON "site" USING btree(latitude ASC NULLS LAST, longitude ASC NULLS LAST);
CREATE INDEX "site_latitude_longitude" ON "site" USING btree(latitude ASC NULLS LAST, longitude ASC NULLS LAST);


# --- !Downs

DROP TABLE IF EXISTS "site";