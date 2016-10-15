--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: file; Type: TABLE; Schema: public; Tablespace: 
--

CREATE TABLE file (
  id integer NOT NULL,
  data bytea,
  tenant_id integer NOT NULL
);

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; 
--

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
  id integer NOT NULL,
  body character varying(255),
  creation timestamp without time zone,
  notif_read boolean,
  subject character varying(255),
  tenant_id integer NOT NULL,
  user_id integer
);

--
-- Name: permission; Type: TABLE; Schema: public; Tablespace: 
--

CREATE TABLE permission (
  id integer NOT NULL,
  descriptor character varying(255),
  tenant_id integer NOT NULL
);

--
-- Name: property; Type: TABLE; Schema: public; Tablespace: 
--

CREATE TABLE property (
  id integer NOT NULL,
  propertykey character varying(255),
  propertyvalue character varying(255),
  tenant_id integer NOT NULL
);

--
-- Name: reservation; Type: TABLE; Schema: public; Tablespace: 
--

CREATE TABLE reservation (
  id integer NOT NULL,
  datecreated timestamp without time zone,
  endtime timestamp without time zone,
  starttime timestamp without time zone,
  status character varying(255),
  title character varying(255),
  tenant_id integer NOT NULL,
  resource_id integer,
  user_id integer
);

--
-- Name: reservationdecision; Type: TABLE; Schema: public; Tablespace: 
--

CREATE TABLE reservationdecision (
  id integer NOT NULL,
  approved boolean NOT NULL,
  comment character varying(255),
  tenant_id integer NOT NULL,
  reservation_id integer NOT NULL,
  user_id integer NOT NULL,
  usergroup_id integer NOT NULL
);

--
-- Name: reservationfield; Type: TABLE; Schema: public; Tablespace: 
--

CREATE TABLE reservationfield (
  id integer NOT NULL,
  description character varying(255),
  multiline boolean NOT NULL,
  required boolean NOT NULL,
  title character varying(255),
  tenant_id integer NOT NULL,
  resourcecategory_id integer
);

--
-- Name: reservationfield_reservationfieldentry; Type: TABLE; Schema: public; Tablespace: 
--

CREATE TABLE reservationfield_reservationfieldentry (
  reservationfield_id integer NOT NULL,
  reservationfieldentries_id integer NOT NULL
);

--
-- Name: reservationfieldentry; Type: TABLE; Schema: public; Tablespace: 
--

CREATE TABLE reservationfieldentry (
  id integer NOT NULL,
  content character varying(255),
  tenant_id integer NOT NULL,
  field_id integer,
  reservation_id integer
);

--
-- Name: resource; Type: TABLE; Schema: public; Tablespace: 
--

CREATE TABLE resource (
  id integer NOT NULL,
  name character varying(255),
  needsapproval boolean,
  tenant_id integer NOT NULL,
  image_id integer,
  owner_id integer,
  resourcecategory_id integer
);

--
-- Name: resource_tag; Type: TABLE; Schema: public; Tablespace: 
--

CREATE TABLE resource_tag (
  resources_id integer NOT NULL,
  tags_id integer NOT NULL
);

--
-- Name: resourcecategory; Type: TABLE; Schema: public; Tablespace: 
--

CREATE TABLE resourcecategory (
  id integer NOT NULL,
  name character varying(255),
  tenant_id integer NOT NULL
);

--
-- Name: tag; Type: TABLE; Schema: public; Tablespace: 
--

CREATE TABLE tag (
  id integer NOT NULL,
  name character varying(255),
  tenant_id integer NOT NULL,
  resourcecategory_id integer NOT NULL
);

--
-- Name: tenant; Type: TABLE; Schema: public; Tablespace: 
--

CREATE TABLE tenant (
  id integer NOT NULL,
  active boolean NOT NULL,
  slug character varying(255) NOT NULL,
  subscriptionid integer NOT NULL,
  timesetinactive timestamp without time zone
);

--
-- Name: user; Type: TABLE; Schema: public; Tablespace: 
--

CREATE TABLE "user" (
  id integer NOT NULL,
  emailaddress character varying(255),
  firstname character varying(255),
  hashedpassword character varying(255),
  lastname character varying(255),
  location character varying(255),
  notificationtypesettings character varying(255),
  phonenumber character varying(255),
  userstate character varying(255),
  verificationcode character varying(255),
  verified boolean NOT NULL,
  tenant_id integer NOT NULL
);

--
-- Name: user_permission; Type: TABLE; Schema: public; Tablespace: 
--

CREATE TABLE user_permission (
  users_id integer NOT NULL,
  permissions_id integer NOT NULL
);

--
-- Name: user_usergroup; Type: TABLE; Schema: public; Tablespace: 
--

CREATE TABLE user_usergroup (
  users_id integer NOT NULL,
  usergroups_id integer NOT NULL
);

--
-- Name: usergroup; Type: TABLE; Schema: public; Tablespace: 
--

CREATE TABLE usergroup (
  id integer NOT NULL,
  name character varying(255),
  tenant_id integer NOT NULL,
  parent_id integer
);

--
-- Name: usergroup_permission; Type: TABLE; Schema: public; Tablespace: 
--

CREATE TABLE usergroup_permission (
  usergroups_id integer NOT NULL,
  permissions_id integer NOT NULL
);

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
-- Name: resource_pkey; Type: CONSTRAINT; Schema: public; Tablespace: 
--

ALTER TABLE ONLY resource
  ADD CONSTRAINT resource_pkey PRIMARY KEY (id);


--
-- Name: resourcecategory_pkey; Type: CONSTRAINT; Schema: public; Tablespace: 
--

ALTER TABLE ONLY resourcecategory
  ADD CONSTRAINT resourcecategory_pkey PRIMARY KEY (id);


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
-- Name: uk_reservationfieldentries_id; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY reservationfield_reservationfieldentry
  ADD CONSTRAINT uk_reservationfieldentries_id UNIQUE (reservationfieldentries_id);


--
-- Name: uk_slug; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY tenant
  ADD CONSTRAINT uk_slug UNIQUE (slug);


--
-- Name: uk_subscriptionid; Type: CONSTRAINT; Schema: public; Tablespace:
--

ALTER TABLE ONLY tenant
  ADD CONSTRAINT uk_subscriptionid UNIQUE (subscriptionid);


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
-- Name: fk_field_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationfieldentry
  ADD CONSTRAINT fk_field_id FOREIGN KEY (field_id) REFERENCES reservationfield(id);


--
-- Name: fk_image_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY resource
  ADD CONSTRAINT fk_image_id FOREIGN KEY (image_id) REFERENCES file(id);


--
-- Name: fk_owner_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY resource
  ADD CONSTRAINT fk_owner_id FOREIGN KEY (owner_id) REFERENCES usergroup(id);


--
-- Name: fk_parent_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY usergroup
  ADD CONSTRAINT fk_parent_id FOREIGN KEY (parent_id) REFERENCES usergroup(id);


--
-- Name: fk_permissions_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY user_permission
  ADD CONSTRAINT fk_permissions_id FOREIGN KEY (permissions_id) REFERENCES permission(id);


--
-- Name: fk_permissions_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY usergroup_permission
  ADD CONSTRAINT fk_permissions_id FOREIGN KEY (permissions_id) REFERENCES permission(id);


--
-- Name: fk_reservation_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationdecision
  ADD CONSTRAINT fk_reservation_id FOREIGN KEY (reservation_id) REFERENCES reservation(id);


--
-- Name: fk_reservation_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationfieldentry
  ADD CONSTRAINT fk_reservation_id FOREIGN KEY (reservation_id) REFERENCES reservation(id);


--
-- Name: fk_reservationfield_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationfield_reservationfieldentry
  ADD CONSTRAINT fk_reservationfield_id FOREIGN KEY (reservationfield_id) REFERENCES reservationfield(id);


--
-- Name: fk_reservationfieldentries_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationfield_reservationfieldentry
  ADD CONSTRAINT fk_reservationfieldentries_id FOREIGN KEY (reservationfieldentries_id) REFERENCES reservationfieldentry(id);


--
-- Name: fk_resource_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservation
  ADD CONSTRAINT fk_resource_id FOREIGN KEY (resource_id) REFERENCES resource(id);


--
-- Name: fk_resourcecategory_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationfield
  ADD CONSTRAINT fk_resourcecategory_id FOREIGN KEY (resourcecategory_id) REFERENCES resourcecategory(id);


--
-- Name: fk_resourcecategory_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY resource
  ADD CONSTRAINT fk_resourcecategory_id FOREIGN KEY (resourcecategory_id) REFERENCES resourcecategory(id);


--
-- Name: fk_resourcecategory_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY tag
  ADD CONSTRAINT fk_resourcecategory_id FOREIGN KEY (resourcecategory_id) REFERENCES resourcecategory(id);


--
-- Name: fk_resources_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY resource_tag
  ADD CONSTRAINT fk_resources_id FOREIGN KEY (resources_id) REFERENCES resource(id);


--
-- Name: fk_tags_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY resource_tag
  ADD CONSTRAINT fk_tags_id FOREIGN KEY (tags_id) REFERENCES tag(id);


--
-- Name: fk_tenant_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY file
  ADD CONSTRAINT fk_tenant_id FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_tenant_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY notification
  ADD CONSTRAINT fk_tenant_id FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_tenant_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY permission
  ADD CONSTRAINT fk_tenant_id FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_tenant_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY property
  ADD CONSTRAINT fk_tenant_id FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_tenant_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservation
  ADD CONSTRAINT fk_tenant_id FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_tenant_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationdecision
  ADD CONSTRAINT fk_tenant_id FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_tenant_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationfield
  ADD CONSTRAINT fk_tenant_id FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_tenant_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationfieldentry
  ADD CONSTRAINT fk_tenant_id FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_tenant_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY resource
  ADD CONSTRAINT fk_tenant_id FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_tenant_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY resourcecategory
  ADD CONSTRAINT fk_tenant_id FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_tenant_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY tag
  ADD CONSTRAINT fk_tenant_id FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_tenant_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY "user"
  ADD CONSTRAINT fk_tenant_id FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_tenant_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY usergroup
  ADD CONSTRAINT fk_tenant_id FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_user_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY notification
  ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES "user"(id);


--
-- Name: fk_user_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservation
  ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES "user"(id);


--
-- Name: fk_user_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationdecision
  ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES "user"(id);


--
-- Name: fk_usergroup_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY reservationdecision
  ADD CONSTRAINT fk_usergroup_id FOREIGN KEY (usergroup_id) REFERENCES usergroup(id);


--
-- Name: fk_usergroups_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY user_usergroup
  ADD CONSTRAINT fk_usergroups_id FOREIGN KEY (usergroups_id) REFERENCES usergroup(id);


--
-- Name: fk_usergroups_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY usergroup_permission
  ADD CONSTRAINT fk_usergroups_id FOREIGN KEY (usergroups_id) REFERENCES usergroup(id);


--
-- Name: fk_users_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY user_permission
  ADD CONSTRAINT fk_users_id FOREIGN KEY (users_id) REFERENCES "user"(id);


--
-- Name: fk_users_id; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY user_usergroup
  ADD CONSTRAINT fk_users_id FOREIGN KEY (users_id) REFERENCES "user"(id);

--
-- PostgreSQL database dump complete
--

