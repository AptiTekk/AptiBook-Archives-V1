--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = ON;
SET check_function_bodies = FALSE;
SET client_min_messages = WARNING;

--
-- Name: asset; Type: TABLE; Schema: public; Tablespace:
--

CREATE TABLE asset (
  id               INTEGER NOT NULL,
  name             TEXT,
  needsapproval    BOOLEAN,
  tenant_id        INTEGER NOT NULL,
  assetcategory_id INTEGER,
  image_id         INTEGER,
  owner_id         INTEGER
);

--
-- Name: asset_tag; Type: TABLE; Schema: public; Tablespace:
--

CREATE TABLE asset_tag (
  assets_id INTEGER NOT NULL,
  tags_id   INTEGER NOT NULL
);

--
-- Name: assetcategory; Type: TABLE; Schema: public; Tablespace:
--

CREATE TABLE assetcategory (
  id        INTEGER NOT NULL,
  name      TEXT,
  tenant_id INTEGER NOT NULL
);

--
-- Name: file; Type: TABLE; Schema: public; Tablespace:
--

CREATE TABLE file (
  id        INTEGER NOT NULL,
  data      BYTEA,
  tenant_id INTEGER NOT NULL
);

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; 
--

DROP SEQUENCE IF EXISTS hibernate_sequence;

CREATE SEQUENCE hibernate_sequence
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

--
-- Name: notification; Type: TABLE; Schema: public; Tablespace:
--

CREATE TABLE notification (
  id         INTEGER NOT NULL,
  body       TEXT,
  creation   TIMESTAMP WITH TIME ZONE,
  notif_read BOOLEAN,
  subject    TEXT,
  tenant_id  INTEGER NOT NULL,
  user_id    INTEGER
);

--
-- Name: permission; Type: TABLE; Schema: public; Tablespace:
--

CREATE TABLE permission (
  id         INTEGER NOT NULL,
  descriptor TEXT,
  tenant_id  INTEGER NOT NULL
);

--
-- Name: property; Type: TABLE; Schema: public; Tablespace:
--

CREATE TABLE property (
  id            INTEGER NOT NULL,
  propertykey   TEXT,
  propertyvalue TEXT,
  tenant_id     INTEGER NOT NULL
);

--
-- Name: reservation; Type: TABLE; Schema: public; Tablespace:
--

CREATE TABLE reservation (
  id          INTEGER NOT NULL,
  datecreated TIMESTAMP WITH TIME ZONE,
  endtime     TIMESTAMP WITH TIME ZONE,
  starttime   TIMESTAMP WITH TIME ZONE,
  status      TEXT,
  title       TEXT,
  tenant_id   INTEGER NOT NULL,
  asset_id    INTEGER,
  user_id     INTEGER
);

--
-- Name: reservationdecision; Type: TABLE; Schema: public; Tablespace:
--

CREATE TABLE reservationdecision (
  id             INTEGER NOT NULL,
  approved       BOOLEAN NOT NULL,
  comment        TEXT,
  tenant_id      INTEGER NOT NULL,
  reservation_id INTEGER NOT NULL,
  user_id        INTEGER NOT NULL,
  usergroup_id   INTEGER NOT NULL
);

--
-- Name: reservationfield; Type: TABLE; Schema: public; Tablespace:
--

CREATE TABLE reservationfield (
  id               INTEGER NOT NULL,
  description      TEXT,
  multiline        BOOLEAN NOT NULL,
  required         BOOLEAN NOT NULL,
  title            TEXT,
  tenant_id        INTEGER NOT NULL,
  assetcategory_id INTEGER
);

--
-- Name: reservationfield_reservationfieldentry; Type: TABLE; Schema: public; Tablespace:
--

CREATE TABLE reservationfield_reservationfieldentry (
  reservationfield_id        INTEGER NOT NULL,
  reservationfieldentries_id INTEGER NOT NULL
);

--
-- Name: reservationfieldentry; Type: TABLE; Schema: public; Tablespace:
--

CREATE TABLE reservationfieldentry (
  id             INTEGER NOT NULL,
  content        TEXT,
  tenant_id      INTEGER NOT NULL,
  field_id       INTEGER,
  reservation_id INTEGER
);

--
-- Name: tag; Type: TABLE; Schema: public; Tablespace:
--

CREATE TABLE tag (
  id               INTEGER NOT NULL,
  name             TEXT,
  tenant_id        INTEGER NOT NULL,
  assetcategory_id INTEGER NOT NULL
);

--
-- Name: tenant; Type: TABLE; Schema: public; Tablespace:
--

CREATE TABLE tenant (
  id              INTEGER                NOT NULL,
  active          BOOLEAN                NOT NULL,
  slug            TEXT NOT NULL,
  subscriptionid  INTEGER                NOT NULL,
  timesetinactive TIMESTAMP WITH TIME ZONE
);

--
-- Name: user; Type: TABLE; Schema: public; Tablespace:
--

CREATE TABLE "user" (
  id                       INTEGER NOT NULL,
  emailaddress             TEXT,
  firstname                TEXT,
  hashedpassword           TEXT,
  lastname                 TEXT,
  location                 TEXT,
  notificationtypesettings TEXT,
  phonenumber              TEXT,
  userstate                TEXT,
  verificationcode         TEXT,
  verified                 BOOLEAN NOT NULL,
  tenant_id                INTEGER NOT NULL
);

--
-- Name: user_permission; Type: TABLE; Schema: public; Tablespace:
--

CREATE TABLE user_permission (
  users_id       INTEGER NOT NULL,
  permissions_id INTEGER NOT NULL
);

--
-- Name: user_usergroup; Type: TABLE; Schema: public; Tablespace:
--

CREATE TABLE user_usergroup (
  users_id      INTEGER NOT NULL,
  usergroups_id INTEGER NOT NULL
);

--
-- Name: usergroup; Type: TABLE; Schema: public; Tablespace:
--

CREATE TABLE usergroup (
  id        INTEGER NOT NULL,
  name      TEXT,
  tenant_id INTEGER NOT NULL,
  parent_id INTEGER
);

--
-- Name: usergroup_permission; Type: TABLE; Schema: public; Tablespace:
--

CREATE TABLE usergroup_permission (
  usergroups_id  INTEGER NOT NULL,
  permissions_id INTEGER NOT NULL
);

--
-- Name: asset_pkey; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY asset
  ADD CONSTRAINT asset_pkey PRIMARY KEY (id);

--
-- Name: assetcategory_pkey; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY assetcategory
  ADD CONSTRAINT assetcategory_pkey PRIMARY KEY (id);

--
-- Name: file_pkey; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY file
  ADD CONSTRAINT file_pkey PRIMARY KEY (id);

--
-- Name: notification_pkey; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY notification
  ADD CONSTRAINT notification_pkey PRIMARY KEY (id);

--
-- Name: permission_pkey; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY permission
  ADD CONSTRAINT permission_pkey PRIMARY KEY (id);

--
-- Name: property_pkey; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY property
  ADD CONSTRAINT property_pkey PRIMARY KEY (id);

--
-- Name: reservation_pkey; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY reservation
  ADD CONSTRAINT reservation_pkey PRIMARY KEY (id);

--
-- Name: reservationdecision_pkey; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY reservationdecision
  ADD CONSTRAINT reservationdecision_pkey PRIMARY KEY (id);

--
-- Name: reservationfield_pkey; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY reservationfield
  ADD CONSTRAINT reservationfield_pkey PRIMARY KEY (id);

--
-- Name: reservationfieldentry_pkey; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY reservationfieldentry
  ADD CONSTRAINT reservationfieldentry_pkey PRIMARY KEY (id);

--
-- Name: tag_pkey; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY tag
  ADD CONSTRAINT tag_pkey PRIMARY KEY (id);

--
-- Name: tenant_pkey; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY tenant
  ADD CONSTRAINT tenant_pkey PRIMARY KEY (id);

--
-- Name: uk_reservationfield_reservationfieldentry_reservationfieldentries_id; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY reservationfield_reservationfieldentry
  ADD CONSTRAINT uk_reservationfield_reservationfieldentry_reservationfieldentries_id UNIQUE (reservationfieldentries_id);

--
-- Name: uk_tenant_slug; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY tenant
  ADD CONSTRAINT uk_tenant_slug UNIQUE (slug);

--
-- Name: uk_tenant_subscriptionid; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY tenant
  ADD CONSTRAINT uk_tenant_subscriptionid UNIQUE (subscriptionid);

--
-- Name: user_pkey; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY "user"
  ADD CONSTRAINT user_pkey PRIMARY KEY (id);

--
-- Name: usergroup_pkey; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY usergroup
  ADD CONSTRAINT usergroup_pkey PRIMARY KEY (id);

--
-- Name: fk_asset_assetcategory; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY asset
  ADD CONSTRAINT fk_asset_assetcategory FOREIGN KEY (assetcategory_id) REFERENCES assetcategory (id);

--
-- Name: fk_asset_file; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY asset
  ADD CONSTRAINT fk_asset_file FOREIGN KEY (image_id) REFERENCES file (id);

--
-- Name: fk_asset_tag_asset; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY asset_tag
  ADD CONSTRAINT fk_asset_tag_asset FOREIGN KEY (assets_id) REFERENCES asset (id);

--
-- Name: fk_asset_tag_tag; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY asset_tag
  ADD CONSTRAINT fk_asset_tag_tag FOREIGN KEY (tags_id) REFERENCES tag (id);

--
-- Name: fk_asset_tenant; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY asset
  ADD CONSTRAINT fk_asset_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id);

--
-- Name: fk_asset_usergroup; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY asset
  ADD CONSTRAINT fk_asset_usergroup FOREIGN KEY (owner_id) REFERENCES usergroup (id);

--
-- Name: fk_assetcategory_tenant; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY assetcategory
  ADD CONSTRAINT fk_assetcategory_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id);

--
-- Name: fk_file_tenant; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY file
  ADD CONSTRAINT fk_file_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id);

--
-- Name: fk_notification_tenant; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY notification
  ADD CONSTRAINT fk_notification_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id);

--
-- Name: fk_notification_user; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY notification
  ADD CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES "user" (id);

--
-- Name: fk_permission_tenant; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY permission
  ADD CONSTRAINT fk_permission_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id);

--
-- Name: fk_property_tenant; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY property
  ADD CONSTRAINT fk_property_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id);

--
-- Name: fk_reservation_asset; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservation
  ADD CONSTRAINT fk_reservation_asset FOREIGN KEY (asset_id) REFERENCES asset (id);

--
-- Name: fk_reservation_tenant; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservation
  ADD CONSTRAINT fk_reservation_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id);

--
-- Name: fk_reservation_user; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservation
  ADD CONSTRAINT fk_reservation_user FOREIGN KEY (user_id) REFERENCES "user" (id);

--
-- Name: fk_reservationdecision_reservation; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationdecision
  ADD CONSTRAINT fk_reservationdecision_reservation FOREIGN KEY (reservation_id) REFERENCES reservation (id);

--
-- Name: fk_reservationdecision_tenant; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationdecision
  ADD CONSTRAINT fk_reservationdecision_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id);

--
-- Name: fk_reservationdecision_user; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationdecision
  ADD CONSTRAINT fk_reservationdecision_user FOREIGN KEY (user_id) REFERENCES "user" (id);

--
-- Name: fk_reservationdecision_usergroup; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationdecision
  ADD CONSTRAINT fk_reservationdecision_usergroup FOREIGN KEY (usergroup_id) REFERENCES usergroup (id);

--
-- Name: fk_reservationfield_assetcategory; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationfield
  ADD CONSTRAINT fk_reservationfield_assetcategory FOREIGN KEY (assetcategory_id) REFERENCES assetcategory (id);

--
-- Name: fk_reservationfield_reservationfieldentry_reservationfield; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationfield_reservationfieldentry
  ADD CONSTRAINT fk_reservationfield_reservationfieldentry_reservationfield FOREIGN KEY (reservationfield_id) REFERENCES reservationfield (id);

--
-- Name: fk_reservationfield_reservationfieldentry_reservationfieldentry; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationfield_reservationfieldentry
  ADD CONSTRAINT fk_reservationfield_reservationfieldentry_reservationfieldentry FOREIGN KEY (reservationfieldentries_id) REFERENCES reservationfieldentry (id);

--
-- Name: fk_reservationfield_tenant; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationfield
  ADD CONSTRAINT fk_reservationfield_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id);

--
-- Name: fk_reservationfieldentry_reservation; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationfieldentry
  ADD CONSTRAINT fk_reservationfieldentry_reservation FOREIGN KEY (reservation_id) REFERENCES reservation (id);

--
-- Name: fk_reservationfieldentry_reservationfield; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationfieldentry
  ADD CONSTRAINT fk_reservationfieldentry_reservationfield FOREIGN KEY (field_id) REFERENCES reservationfield (id);

--
-- Name: fk_reservationfieldentry_tenant; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationfieldentry
  ADD CONSTRAINT fk_reservationfieldentry_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id);

--
-- Name: fk_tag_assetcategory; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY tag
  ADD CONSTRAINT fk_tag_assetcategory FOREIGN KEY (assetcategory_id) REFERENCES assetcategory (id);

--
-- Name: fk_tag_tenant; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY tag
  ADD CONSTRAINT fk_tag_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id);

--
-- Name: fk_user_permission_permission; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY user_permission
  ADD CONSTRAINT fk_user_permission_permission FOREIGN KEY (permissions_id) REFERENCES permission (id);

--
-- Name: fk_user_permission_user; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY user_permission
  ADD CONSTRAINT fk_user_permission_user FOREIGN KEY (users_id) REFERENCES "user" (id);

--
-- Name: fk_user_tenant; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY "user"
  ADD CONSTRAINT fk_user_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id);

--
-- Name: fk_user_usergroup_user; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY user_usergroup
  ADD CONSTRAINT fk_user_usergroup_user FOREIGN KEY (users_id) REFERENCES "user" (id);

--
-- Name: fk_user_usergroup_usergroup; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY user_usergroup
  ADD CONSTRAINT fk_user_usergroup_usergroup FOREIGN KEY (usergroups_id) REFERENCES usergroup (id);

--
-- Name: fk_usergroup_permission_permission; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY usergroup_permission
  ADD CONSTRAINT fk_usergroup_permission_permission FOREIGN KEY (permissions_id) REFERENCES permission (id);

--
-- Name: fk_usergroup_permission_usergroup; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY usergroup_permission
  ADD CONSTRAINT fk_usergroup_permission_usergroup FOREIGN KEY (usergroups_id) REFERENCES usergroup (id);

--
-- Name: fk_usergroup_tenant; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY usergroup
  ADD CONSTRAINT fk_usergroup_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id);

--
-- Name: fk_usergroup_usergroup; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY usergroup
  ADD CONSTRAINT fk_usergroup_usergroup FOREIGN KEY (parent_id) REFERENCES usergroup (id);

--
-- PostgreSQL database dump complete
--

