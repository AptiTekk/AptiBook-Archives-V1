--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET search_path = public, pg_catalog;
SET default_tablespace = '';
SET default_with_oids = false;

--
-- Name: asset; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE asset (
    id integer NOT NULL,
    availabilityend time without time zone,
    availabilitystart time without time zone,
    image oid,
    name character varying(255),
    needsapproval boolean,
    tenant_id integer NOT NULL,
    assetcategory_id integer,
    owner_id integer
);


--
-- Name: asset_tag; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE asset_tag (
    assets_id integer NOT NULL,
    tags_id integer NOT NULL
);


--
-- Name: assetcategory; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE assetcategory (
    id integer NOT NULL,
    name character varying(255),
    tenant_id integer NOT NULL
);


--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
	

--
-- Name: notification; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE notification (
    id bigint NOT NULL,
    body character varying(2048),
    creation timestamp without time zone,
    notif_read boolean,
    subject character varying(255),
    tenant_id integer NOT NULL,
    user_id integer
);


--
-- Name: permission; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE permission (
    id integer NOT NULL,
    descriptor character varying(255),
    tenant_id integer NOT NULL
);


--
-- Name: permission_user; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE permission_user (
    permissions_id integer NOT NULL,
    users_id integer NOT NULL
);


--
-- Name: permission_usergroup; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE permission_usergroup (
    permissions_id integer NOT NULL,
    usergroups_id integer NOT NULL
);


--
-- Name: property; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE property (
    id integer NOT NULL,
    propertykey character varying(255),
    propertyvalue character varying(255),
    tenant_id integer NOT NULL
);


--
-- Name: reservation; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE reservation (
    id integer NOT NULL,
    date date,
    datecreated timestamp without time zone,
    status character varying(255),
    timeend time without time zone,
    timestart time without time zone,
    title character varying(32),
    tenant_id integer NOT NULL,
    asset_id integer,
    user_id integer
);


--
-- Name: reservationdecision; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE reservationdecision (
    id integer NOT NULL,
    approved boolean NOT NULL,
    comment character varying(512),
    tenant_id integer NOT NULL,
    reservation_id integer NOT NULL,
    user_id integer NOT NULL,
    usergroup_id integer NOT NULL
);


--
-- Name: reservationfield; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE reservationfield (
    id integer NOT NULL,
    description text,
    largefield boolean,
    name character varying(32),
    tenant_id integer NOT NULL,
    assetcategory_id integer,
    "Order" integer
);


--
-- Name: reservationfieldentry; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE reservationfieldentry (
    id integer NOT NULL,
    content text,
    tenant_id integer NOT NULL,
    field_id integer,
    reservation_id integer
);


--
-- Name: tag; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE tag (
    id integer NOT NULL,
    name character varying(255),
    tenant_id integer NOT NULL,
    assetcategory_id integer NOT NULL
);


--
-- Name: tenant; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE tenant (
    id integer NOT NULL,
    slug character varying(32) NOT NULL,
    subscriptionid integer NOT NULL
);


--
-- Name: user; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE "user" (
    id integer NOT NULL,
    email character varying(255),
    firstname character varying(255),
    lastname character varying(255),
    location character varying(255),
    password bytea,
    phonenumber character varying(255),
    username character varying(255),
    tenant_id integer NOT NULL
);


--
-- Name: user_usergroup; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE user_usergroup (
    users_id integer NOT NULL,
    usergroups_id integer NOT NULL
);


--
-- Name: usergroup; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE usergroup (
    id integer NOT NULL,
    name character varying(255),
    tenant_id integer NOT NULL,
    parent_id integer
);


--
-- Name: asset_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY asset
    ADD CONSTRAINT asset_pkey PRIMARY KEY (id);


--
-- Name: assetcategory_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY assetcategory
    ADD CONSTRAINT assetcategory_pkey PRIMARY KEY (id);


--
-- Name: notification_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY notification
    ADD CONSTRAINT notification_pkey PRIMARY KEY (id);


--
-- Name: permission_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY permission
    ADD CONSTRAINT permission_pkey PRIMARY KEY (id);


--
-- Name: permission_user_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY permission_user
    ADD CONSTRAINT permission_user_pkey PRIMARY KEY (permissions_id, users_id);


--
-- Name: permission_usergroup_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY permission_usergroup
    ADD CONSTRAINT permission_usergroup_pkey PRIMARY KEY (permissions_id, usergroups_id);


--
-- Name: property_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY property
    ADD CONSTRAINT property_pkey PRIMARY KEY (id);


--
-- Name: reservation_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY reservation
    ADD CONSTRAINT reservation_pkey PRIMARY KEY (id);


--
-- Name: reservationdecision_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY reservationdecision
    ADD CONSTRAINT reservationdecision_pkey PRIMARY KEY (id);


--
-- Name: reservationfield_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY reservationfield
    ADD CONSTRAINT reservationfield_pkey PRIMARY KEY (id);


--
-- Name: reservationfieldentry_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY reservationfieldentry
    ADD CONSTRAINT reservationfieldentry_pkey PRIMARY KEY (id);


--
-- Name: tag_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (id);


--
-- Name: tenant_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY tenant
    ADD CONSTRAINT tenant_pkey PRIMARY KEY (id);


--
-- Name: uk_8u3gwjh0fofw4v64trrlk0i1p; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY tenant
    ADD CONSTRAINT uk_8u3gwjh0fofw4v64trrlk0i1p UNIQUE (slug);


--
-- Name: uk_lchql10t6pd0ytjviwri33jwt; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY tenant
    ADD CONSTRAINT uk_lchql10t6pd0ytjviwri33jwt UNIQUE (subscriptionid);


--
-- Name: user_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY "user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- Name: usergroup_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY usergroup
    ADD CONSTRAINT usergroup_pkey PRIMARY KEY (id);


--
-- Name: fk_asset_assetcategory; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY asset
    ADD CONSTRAINT fk_asset_assetcategory FOREIGN KEY (assetcategory_id) REFERENCES assetcategory(id);


--
-- Name: fk_asset_tag_asset; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY asset_tag
    ADD CONSTRAINT fk_asset_tag_asset FOREIGN KEY (assets_id) REFERENCES asset(id);


--
-- Name: fk_asset_tag_tag; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY asset_tag
    ADD CONSTRAINT fk_asset_tag_tag FOREIGN KEY (tags_id) REFERENCES tag(id);


--
-- Name: fk_asset_tenant; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY asset
    ADD CONSTRAINT fk_asset_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_asset_usergroup; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY asset
    ADD CONSTRAINT fk_asset_usergroup FOREIGN KEY (owner_id) REFERENCES usergroup(id);


--
-- Name: fk_assetcategory_tenant; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY assetcategory
    ADD CONSTRAINT fk_assetcategory_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_notification_tenant; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY notification
    ADD CONSTRAINT fk_notification_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_notification_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY notification
    ADD CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES "user"(id);


--
-- Name: fk_permission_tenant; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY permission
    ADD CONSTRAINT fk_permission_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_permission_user_permission; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY permission_user
    ADD CONSTRAINT fk_permission_user_permission FOREIGN KEY (permissions_id) REFERENCES permission(id);


--
-- Name: fk_permission_user_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY permission_user
    ADD CONSTRAINT fk_permission_user_user FOREIGN KEY (users_id) REFERENCES "user"(id);


--
-- Name: fk_permission_usergroup_permission; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY permission_usergroup
    ADD CONSTRAINT fk_permission_usergroup_permission FOREIGN KEY (permissions_id) REFERENCES permission(id);


--
-- Name: fk_permission_usergroup_usergroup; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY permission_usergroup
    ADD CONSTRAINT fk_permission_usergroup_usergroup FOREIGN KEY (usergroups_id) REFERENCES usergroup(id);


--
-- Name: fk_property_tenant; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY property
    ADD CONSTRAINT fk_property_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_reservation_asset; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reservation
    ADD CONSTRAINT fk_reservation_asset FOREIGN KEY (asset_id) REFERENCES asset(id);


--
-- Name: fk_reservation_tenant; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reservation
    ADD CONSTRAINT fk_reservation_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_reservation_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reservation
    ADD CONSTRAINT fk_reservation_user FOREIGN KEY (user_id) REFERENCES "user"(id);


--
-- Name: fk_reservationdecision_reservation; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reservationdecision
    ADD CONSTRAINT fk_reservationdecision_reservation FOREIGN KEY (reservation_id) REFERENCES reservation(id);


--
-- Name: fk_reservationdecision_tenant; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reservationdecision
    ADD CONSTRAINT fk_reservationdecision_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_reservationdecision_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reservationdecision
    ADD CONSTRAINT fk_reservationdecision_user FOREIGN KEY (user_id) REFERENCES "user"(id);


--
-- Name: fk_reservationdecision_usergroup; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reservationdecision
    ADD CONSTRAINT fk_reservationdecision_usergroup FOREIGN KEY (usergroup_id) REFERENCES usergroup(id);


--
-- Name: fk_reservationfield_assetcategory; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reservationfield
    ADD CONSTRAINT fk_reservationfield_assetcategory FOREIGN KEY (assetcategory_id) REFERENCES assetcategory(id);


--
-- Name: fk_reservationfield_tenant; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reservationfield
    ADD CONSTRAINT fk_reservationfield_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_reservationfieldentry_reservation; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reservationfieldentry
    ADD CONSTRAINT fk_reservationfieldentry_reservation FOREIGN KEY (reservation_id) REFERENCES reservation(id);


--
-- Name: fk_reservationfieldentry_reservationfield; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reservationfieldentry
    ADD CONSTRAINT fk_reservationfieldentry_reservationfield FOREIGN KEY (field_id) REFERENCES reservationfield(id);


--
-- Name: fk_reservationfieldentry_tenant; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY reservationfieldentry
    ADD CONSTRAINT fk_reservationfieldentry_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_tag_assetcategory; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tag
    ADD CONSTRAINT fk_tag_assetcategory FOREIGN KEY (assetcategory_id) REFERENCES assetcategory(id);


--
-- Name: fk_tag_tenant; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY tag
    ADD CONSTRAINT fk_tag_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_user_tenant; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY "user"
    ADD CONSTRAINT fk_user_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_user_usergroup_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_usergroup
    ADD CONSTRAINT fk_user_usergroup_user FOREIGN KEY (users_id) REFERENCES "user"(id);


--
-- Name: fk_user_usergroup_usergroup; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY user_usergroup
    ADD CONSTRAINT fk_user_usergroup_usergroup FOREIGN KEY (usergroups_id) REFERENCES usergroup(id);


--
-- Name: fk_usergroup_tenant; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY usergroup
    ADD CONSTRAINT fk_usergroup_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);


--
-- Name: fk_usergroup_usergroup; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY usergroup
    ADD CONSTRAINT fk_usergroup_usergroup FOREIGN KEY (parent_id) REFERENCES usergroup(id);

--
-- PostgreSQL database dump complete
--

